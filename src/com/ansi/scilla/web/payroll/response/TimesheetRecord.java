package com.ansi.scilla.web.payroll.response;

import com.ansi.scilla.common.ApplicationObject;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;



public class TimesheetRecord extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	public static final String CITY = "city";
	public static final String DIRECT_LABOR = "directLabor";
	public static final String DIVISION_ID = "divisionId";
	public static final String EMPLOYEE_CODE = "employeeCode";
	public static final String EMPLOYEE_NAME = "employeeName";
	public static final String EXPENSES = "expenses";
	public static final String EXPENSES_ALLOWED = "expensesAllowed";
	public static final String EXPENSES_SUBMITTED = "expensesSubmitted";
	public static final String GROSS_PAY = "grossPay";
	public static final String HOLIDAY_HOURS = "holidayHours";
	public static final String HOLIDAY_PAY = "holidayPay";
	public static final String OT_HOURS = "otHours";
	public static final String OT_PAY = "otPay";
	public static final String PAYROLL_WORKSHEETCOL = "payrollWorksheetcol";
	public static final String PRODUCTIVITY = "productivity";
	public static final String REGULAR_HOURS = "regularHours";
	public static final String REGULAR_PAY = "regularPay";
	public static final String STATE = "state";
	public static final String VACATION_HOURS = "vacationHours";
	public static final String VACATION_PAY = "vacationPay";
	public static final String VOLUME = "volume";
	public static final String WEEK_ENDING = "weekEnding";
	
	private String row;
	private String employeeName;
	private String expenses;
	private String expensesAllowed;
	private String expensesSubmitted;
	private String grossPay;
	private String holidayHours;
	private String holidayPay;
	private String otHours;
	private String otPay;
	private String directLabor;
	private String productivity;
	private String regularHours;
	private String regularPay;
	private String state;
	private String vacationHours;
	private String vacationPay;
	private String volume;
	private Boolean errorsFound;
	
	public enum WprCols{
		EMPLOYEE_ROW  	("B")		
		,EMPLOYEE_NAME 	("D")
		,REGULAR_HOURS 	("F")
		,REGULAR_PAY	("H")
		,EXPENSES 		("J")
		,OT_HOURS 		("L")
		,OT_PAY 		("N")
		,VACATION_HOURS	("P")
		,VACATION_PAY	("R")
		,HOLIDAY_HOURS 	("T")
		,HOLIDAY_PAY  	("V")
		,GROSS_PAY 		("X")
		,EXPENSES_SUBMITTED 		("Z")		
		,EXPENSES_ALLOWED 			("AB")
		,VOLUME 		("AD")
		,DIRECT_LABOR 	("AF")
		,PRODUCTIVITY 	("AH")
		,CITY			("B4")
		,STATE			("F4")
		,DIVISION		("L3")
		,OPERATIONS_MANAGER_NAME	("O3")
		,WEEK_ENDING	("X3");
		
		private final String cellLocation;	
		 /**
	     * @param cellLocation
	     */
		private WprCols(final String cellLocation) {
	        this.cellLocation = cellLocation;
	    }

		public String cellLocation() {
			return this.cellLocation;
		}
	   
	}
	
	
	
	public TimesheetRecord() {
		super();
	}
	
	public TimesheetRecord(Table table, Integer ordinalRow) {
		super();
		
		String row = ordinalRow.toString();
		setEmployeeName(table.getCellByPosition			(WprCols.EMPLOYEE_NAME.cellLocation()+row).getDisplayText());
		setDirectLabor(table.getCellByPosition			(WprCols.DIRECT_LABOR.cellLocation()+row).getDisplayText());
		setExpenses(table.getCellByPosition				(WprCols.EXPENSES.cellLocation()+row).getDisplayText());
		setExpensesAllowed(table.getCellByPosition		(WprCols.EXPENSES_ALLOWED.cellLocation()+row).getDisplayText());
		setExpensesSubmitted(table.getCellByPosition	(WprCols.EXPENSES_SUBMITTED.cellLocation()+row).getDisplayText());
		setGrossPay(table.getCellByPosition				(WprCols.GROSS_PAY.cellLocation()+row).getDisplayText());
		setHolidayHours(table.getCellByPosition			(WprCols.HOLIDAY_HOURS.cellLocation()+row).getDisplayText());
		setHolidayPay(table.getCellByPosition			(WprCols.HOLIDAY_PAY.cellLocation()+row).getDisplayText());
		setOtHours(table.getCellByPosition				(WprCols.OT_HOURS.cellLocation()+row).getDisplayText());
		setOtPay(table.getCellByPosition				(WprCols.OT_PAY.cellLocation()+row).getDisplayText());
		setProductivity(table.getCellByPosition			(WprCols.PRODUCTIVITY.cellLocation()+row).getDisplayText());
		setRegularHours(table.getCellByPosition			(WprCols.REGULAR_HOURS.cellLocation()+row).getDisplayText());
		setRegularPay(table.getCellByPosition			(WprCols.REGULAR_PAY.cellLocation()+row).getDisplayText());
		setVacationHours(table.getCellByPosition		(WprCols.VACATION_HOURS.cellLocation()+row).getDisplayText());
		setVacationPay(table.getCellByPosition			(WprCols.VACATION_PAY.cellLocation()+row).getDisplayText());
		setVolume(table.getCellByPosition				(WprCols.VOLUME.cellLocation()+row).getDisplayText());
	}
	
	
	
	public String getRow() {
		return row;
	}
	public void setRow(String row) {
		this.row = row;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getExpenses() {
		return expenses;
	}
	public void setExpenses(String expenses) {
		this.expenses = expenses;
	}
	public String getExpensesAllowed() {
		return expensesAllowed;
	}
	public void setExpensesAllowed(String expensesAllowed) {
		this.expensesAllowed = expensesAllowed;
	}
	public String getExpensesSubmitted() {
		return expensesSubmitted;
	}
	public void setExpensesSubmitted(String expensesSubmitted) {
		this.expensesSubmitted = expensesSubmitted;
	}
	public String getGrossPay() {
		return grossPay;
	}
	public void setGrossPay(String grossPay) {
		this.grossPay = grossPay;
	}
	public String getHolidayHours() {
		return holidayHours;
	}
	public void setHolidayHours(String holidayHours) {
		this.holidayHours = holidayHours;
	}
	public String getHolidayPay() {
		return holidayPay;
	}
	public void setHolidayPay(String holidayPay) {
		this.holidayPay = holidayPay;
	}
	public String getOtHours() {
		return otHours;
	}
	public void setOtHours(String otHours) {
		this.otHours = otHours;
	}
	public String getOtPay() {
		return otPay;
	}
	public void setOtPay(String otPay) {
		this.otPay = otPay;
	}	
	public String getDirectLabor() {
		return directLabor;
	}
	public void setDirectLabor(String directLabor) {
		this.directLabor = directLabor;
	}
	public String getProductivity() {
		return productivity;
	}
	public void setProductivity(String productivity) {
		this.productivity = productivity;
	}
	public String getRegularHours() {
		return regularHours;
	}
	public void setRegularHours(String regularHours) {
		this.regularHours = regularHours;
	}
	public String getRegularPay() {
		return regularPay;
	}
	public void setRegularPay(String regularPay) {
		this.regularPay = regularPay;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getVacationHours() {
		return vacationHours;
	}
	public void setVacationHours(String vacationHours) {
		this.vacationHours = vacationHours;
	}
	public String getVacationPay() {
		return vacationPay;
	}
	public void setVacationPay(String vacationPay) {
		this.vacationPay = vacationPay;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}

	public Boolean getErrorsFound() {
		return errorsFound;
	}

	public void setErrorsFound(Boolean errorsFound) {
		this.errorsFound = errorsFound;
	}
	
	
}
