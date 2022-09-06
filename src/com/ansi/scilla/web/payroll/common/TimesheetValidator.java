package com.ansi.scilla.web.payroll.common;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.ApplicationProperties;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.payroll.validator.common.PayrollErrorType;
import com.ansi.scilla.common.payroll.validator.common.PayrollMessage;
import com.ansi.scilla.common.payroll.validator.common.ValidatorUtils;
import com.ansi.scilla.common.utils.ApplicationProperty;
import com.ansi.scilla.common.utils.ErrorLevel;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.thewebthing.commons.db2.RecordNotFoundException;

/**
 * Validates payroll timesheet record for:
 * * Unknown employee name alias
 * * Expenses <= 20% of gross pay (for this period)
 * 
 * 
 * * Excessive expense reimbursement per ANSI Policy
 * * Non-Washer
 * * Minimum pay exception
 * * Expenses <= 20% of gross pay (YTD)
 * 
 * 
 * @author dclewis
 *
 */
public class TimesheetValidator {

	
	
	private static final String EMPLOYEE_SQL = 
			"select count(*) as record_count from (\n" + 
			"	select pe.employee_code, pe.division, pe.division_id, pe.employee_first_name, pe.employee_last_name, pe.employee_mi, --, ea.employee_name\n" + 
			"		case\n" + 
			"		when pe.employee_mi is null then\n" + 
			"			lower(concat(pe.employee_first_name, ' ', pe.employee_last_name)) \n" + 
			"		else \n" + 
			"			lower(concat(pe.employee_first_name, ' ', pe.employee_mi, ' ', pe.employee_last_name))\n" + 
			"		end as employee_full_name\n" + 
			"	from payroll_employee pe ) employee\n" + 
			"where employee.employee_full_name = ?";
	
	private static final String EMPLOYEE_ALIAS_SQL = 
			"select count(*) as record_count from employee_alias where lower(employee_name)=?";
	
	private static final String YTD_EXPENSE_SQL =
			"select isnull(sum(pw.gross_pay),0) as ytd_gross_pay,\n" +
			"   isnull(sum(pw.expenses),0) as ytd_expenses, \n" + 
			"	isnull(sum(pw.expenses_submitted),0) as ytd_expenses_submitted, \n" + 
			"	isnull(sum(pw.expenses_allowed),0) as ytd_expenses_allowed \n" + 
			"from payroll_worksheet pw\n" + 
			"where pw.week_ending>=? and employee_code=?";
	
	
	
	public static boolean validateEmployeeName(Connection conn, WebMessages webMessages, String fieldName, String value, String label) throws SQLException, RecordNotFoundException {
		boolean isValid = true;
		
		if ( StringUtils.isBlank(value) ) {
			String message = StringUtils.isBlank(label) ? "Required Value" : label + " is Required";
			webMessages.addMessage(fieldName, message);
			isValid = false;
		} else {
			isValid = isEmployeeName(conn, value);
			
			if ( isValid == false ) {
				isValid = isEmployeeAlias(conn, value);				
			}
			
			if ( isValid == false ) {
				String message = StringUtils.isBlank(label) ? "Invalid Value" : label + " is Invalid";
				webMessages.addMessage(fieldName, message);
			}
		}
		
		return isValid;
	}
	
	
	public static boolean isEmployeeName(Connection conn, String value) throws SQLException, RecordNotFoundException {
		boolean isValid;
		PreparedStatement psEmployee = conn.prepareStatement(EMPLOYEE_SQL);
		psEmployee.setString(1, value.toLowerCase());
		ResultSet rsEmployee = psEmployee.executeQuery();
		if ( rsEmployee.next() ) {
			isValid = rsEmployee.getInt("record_count") > 0;
		} else {
			throw new RecordNotFoundException();
		}
		rsEmployee.close();
		return isValid;
	}


	public static boolean isEmployeeAlias(Connection conn, String value) throws SQLException, RecordNotFoundException {
		boolean isValid;
		PreparedStatement psAlias = conn.prepareStatement(EMPLOYEE_ALIAS_SQL);
		psAlias.setString(1, value.toLowerCase());
		ResultSet rsAlias = psAlias.executeQuery();
		if ( rsAlias.next() ) {
			isValid = rsAlias.getInt("record_count") > 0;
		} else {
			throw new RecordNotFoundException();
		}
		return isValid;
	}


	/**
	 * Payroll worksheet has "Expenses", "Expenses Submitted", "Expenses Allowed". 
	 * In the examples on hand, "Expenses" and "Submitted" match.
	 * How do we determine the 20% thing?
	 * 
	 * @param conn
	 * @return
	 * @throws Exception 
	 * @throws RecordNotFoundException 
	 */
	public static boolean validateExpenses(Connection conn, WebMessages webMessages, String fieldName, Double expensesAllowed, Double expensesSubmitted, Double grossPay) throws RecordNotFoundException, Exception {
		boolean isValid = true;
		
		if ( expensesSubmitted == null || expensesSubmitted.equals(0.0D)) {
			isValid = true;
		} else {
			ApplicationProperties applicationProperties = ApplicationProperty.get(conn, ApplicationProperty.EXPENSE_MAX);
			Double maxExpenseRate = applicationProperties.getValueFloat().doubleValue();
			String displayRate = String.valueOf( maxExpenseRate* 100 ) + "%";
			if ( expensesAllowed > (maxExpenseRate * grossPay) ) {
				webMessages.addMessage(fieldName, "Allowed expense is more than " + displayRate + " of gross pay");
				isValid = false;
			} else if ( expensesSubmitted > expensesAllowed ) {
				webMessages.addMessage(fieldName, "Expenses submitted greater than expenses allowed");
				isValid = false;
			}
		}
		return isValid;
	}
	
	
	public static boolean validateExpensesYTD(Connection conn, WebMessages webMessages, String fieldName, Integer employeeCode, Calendar workDay) throws RecordNotFoundException, Exception {
		boolean isValid = true;
		Calendar fiscalStart = AppUtils.getFiscalStart(conn, workDay);
		PreparedStatement ps = conn.prepareStatement(YTD_EXPENSE_SQL);
		ps.setDate(1, new java.sql.Date(fiscalStart.getTime().getTime()));
		ps.setInt(2, employeeCode);
		ResultSet rs = ps.executeQuery();
		if ( rs.next()) {
			Double expensesAllowed = rs.getDouble("ytd_expenses_allowed");
			Double expensesSubmitted = rs.getDouble("ytd_expenses_submitted");
			Double grossPay = rs.getDouble("ytd_gross_pay");
			validateExpenses(conn, webMessages, fieldName, expensesAllowed, expensesSubmitted, grossPay);
		} else {
			throw new RecordNotFoundException();
		}
		rs.close();
		return isValid;
	}
	
	
	public static PayrollMessage validateMinimumGovtPay(Division division, Double grossPay, Double expenses, Double regularHours, Double vacationHours, Double holidayHours) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Double minimumPay = division.getMinimumHourlyPay() == null ? 0.0D : division.getMinimumHourlyPay().doubleValue();
		PayrollMessage payrollErrorMessage = ValidatorUtils.validateMinimumGovtPay(division, grossPay, expenses, minimumPay, regularHours, vacationHours, holidayHours);
		return payrollErrorMessage;
	}
	
	
	
	public static PayrollMessage validateExcessExpense(Double maxExpenseRate, Double expensesSubmitted, Double grossPay) {
		PayrollMessage payrollMessage = new PayrollMessage(ErrorLevel.OK, "Expense pct is OK", PayrollErrorType.OK);
		if ( expensesSubmitted > (maxExpenseRate * grossPay)) {
			payrollMessage = new PayrollMessage(ErrorLevel.WARNING, "Expenses submitted exceed " + maxExpenseRate, PayrollErrorType.EXPENSES_EXCEED_ALLOWED);
		}
		return payrollMessage;
	}
	
	
}
