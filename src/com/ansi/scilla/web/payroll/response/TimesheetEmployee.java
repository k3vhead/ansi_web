package com.ansi.scilla.web.payroll.response;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.payroll.parser.worksheet.PayrollWorksheetEmployee.FieldLocation;
import com.ansi.scilla.common.payroll.validator.common.PayrollMessage;
import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheetEmployee;
import com.ansi.scilla.common.utils.ErrorLevel;

/**
 * Represents an employee in a format suitable for return from a payroll servlet
 * 
 * @author dclewis
 *
 */
public class TimesheetEmployee extends ApplicationObject {

	private static final long serialVersionUID = 1L;
	
	private Integer row;
    private String employeeName;
    private Double regularHours;
    private Double regularPay;
    private Double expenses;
    private Double otHours;
    private Double otPay;
    private Double vacationHours;
    private Double vacationPay;
    private Double holidayHours;
    private Double holidayPay;
    private Double grossPay;
    private Double expensesSubmitted;
    private Double expensesAllowed;
    private Double volume;
    private Double directLabor;
    private Double productivity;
    
    private Integer divisionNbr;
    private Integer divisionId;
    private String divisionDisplay;
    private Integer employeeCode;
    private String vendorEmployeeCode;
    private ErrorLevel errorLevel;
	private HashMap<String, List<PayrollMessage>> messageList;
	private Double unionRate;
	private Integer standardDivisionId;
	private Integer standardCompanyId;
	
	public TimesheetEmployee(ValidatedWorksheetEmployee employee) {
		super();
		this.row = employee.getRow();
	    this.employeeName =
	    	StringUtils.isBlank(employee.getEmployeeFirstName()) && StringUtils.isBlank(employee.getEmployeeLastName()) ?
	    	employee.getEmployee().getEmployeeName() :
    		employee.getEmployeeFirstName() + " " + employee.getEmployeeLastName();
	    this.regularHours = employee.getValues().get(FieldLocation.REGULAR_HOURS.fieldName());
	    this.regularPay = employee.getValues().get(FieldLocation.REGULAR_PAY.fieldName());
	    this.expenses = employee.getValues().get(FieldLocation.EXPENSES.fieldName());
	    this.otHours = employee.getValues().get(FieldLocation.OT_HOURS.fieldName());
	    this.otPay = employee.getValues().get(FieldLocation.OT_PAY.fieldName());
	    this.vacationHours = employee.getValues().get(FieldLocation.VACATION_HOURS.fieldName());
	    this.vacationPay = employee.getValues().get(FieldLocation.VACATION_PAY.fieldName());
	    this.holidayHours = employee.getValues().get(FieldLocation.HOLIDAY_HOURS.fieldName());
	    this.holidayPay = employee.getValues().get(FieldLocation.HOLIDAY_PAY.fieldName());
	    this.grossPay = employee.getValues().get(FieldLocation.GROSS_PAY.fieldName());
	    this.expensesSubmitted = employee.getValues().get(FieldLocation.EXPENSES_SUBMITTED.fieldName());
	    this.expensesAllowed = employee.getValues().get(FieldLocation.EXPENSES_ALLOWED.fieldName());
	    this.volume = employee.getValues().get(FieldLocation.VOLUME.fieldName());
	    this.directLabor = employee.getValues().get(FieldLocation.DIRECT_LABOR.fieldName());
	    this.productivity = employee.getValues().get(FieldLocation.PRODUCTIVITY.fieldName());
	    
	    this.divisionNbr = employee.getDivision().getDivisionNbr();
	    this.divisionId = employee.getDivision().getDivisionId();
	    this.divisionDisplay = employee.getDivision().getDivisionDisplay();
	    this.employeeCode = employee.getEmployeeCode();
	    this.vendorEmployeeCode = employee.getVendorEmployeeCode();
	    this.errorLevel = employee.getErrorLevel();
		this.messageList = employee.getMessageList();
		this.unionRate = employee.getUnionRate();
		this.standardDivisionId = employee.getStandardDivisionId();
		this.standardCompanyId = employee.getStandardCompanyId();
	}

	public Integer getRow() {
		return row;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public Double getRegularHours() {
		return regularHours;
	}

	public Double getRegularPay() {
		return regularPay;
	}

	public Double getExpenses() {
		return expenses;
	}

	public Double getOtHours() {
		return otHours;
	}

	public Double getOtPay() {
		return otPay;
	}

	public Double getVacationHours() {
		return vacationHours;
	}

	public Double getVacationPay() {
		return vacationPay;
	}

	public Double getHolidayHours() {
		return holidayHours;
	}

	public Double getHolidayPay() {
		return holidayPay;
	}

	public Double getGrossPay() {
		return grossPay;
	}

	public Double getExpensesSubmitted() {
		return expensesSubmitted;
	}

	public Double getExpensesAllowed() {
		return expensesAllowed;
	}

	public Double getVolume() {
		return volume;
	}

	public Double getDirectLabor() {
		return directLabor;
	}

	public Double getProductivity() {
		return productivity;
	}

	public Integer getDivisionNbr() {
		return divisionNbr;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public String getDivisionDisplay() {
		return divisionDisplay;
	}

	public Integer getEmployeeCode() {
		return employeeCode;
	}

	public ErrorLevel getErrorLevel() {
		return errorLevel;
	}

	public HashMap<String, List<PayrollMessage>> getMessageList() {
		return messageList;
	}

	public Double getUnionRate() {
		return unionRate;
	}

	public Integer getStandardDivisionId() {
		return standardDivisionId;
	}

	public Integer getStandardCompanyId() {
		return standardCompanyId;
	}

	public String getVendorEmployeeCode() {
		return vendorEmployeeCode;
	}
	
}
