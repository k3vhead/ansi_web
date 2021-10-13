package com.ansi.scilla.web.payroll.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.ApplicationProperties;
import com.ansi.scilla.common.utils.ApplicationProperty;
import com.ansi.scilla.web.common.response.WebMessages;
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
	
	
	
	public static boolean validateEmployeeName(Connection conn, WebMessages webMessages, String fieldName, String value, String label) throws SQLException, RecordNotFoundException {
		boolean isValid = true;
		
		if ( StringUtils.isBlank(value) ) {
			String message = StringUtils.isBlank(label) ? "Required Value" : label + " is Required";
			webMessages.addMessage(fieldName, message);
			isValid = false;
		} else {
			PreparedStatement psEmployee = conn.prepareStatement(EMPLOYEE_SQL);
			psEmployee.setString(1, value.toLowerCase());
			ResultSet rsEmployee = psEmployee.executeQuery();
			if ( rsEmployee.next() ) {
				isValid = rsEmployee.getInt("record_count") > 0;
			} else {
				throw new RecordNotFoundException();
			}
			rsEmployee.close();
			
			if ( isValid == false ) {
				PreparedStatement psAlias = conn.prepareStatement(EMPLOYEE_ALIAS_SQL);
				psAlias.setString(1, value.toLowerCase());
				ResultSet rsAlias = psAlias.executeQuery();
				if ( rsAlias.next() ) {
					isValid = rsAlias.getInt("record_count") > 0;
				} else {
					throw new RecordNotFoundException();
				}
			}
			
			if ( isValid == false ) {
				String message = StringUtils.isBlank(label) ? "Invalid Value" : label + " is Invalid";
				webMessages.addMessage(fieldName, message);
			}
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
}
