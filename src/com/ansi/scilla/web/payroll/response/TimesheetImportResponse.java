package com.ansi.scilla.web.payroll.response;

import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.payroll.request.TimesheetImportRequest;
import com.fasterxml.jackson.annotation.JsonFormat;	

public class TimesheetImportResponse extends MessageResponse {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger logger = LogManager.getLogger(TimesheetImportResponse.class);	
	private String city;
	private String division;
	private String operationsManagerName;
	private String state;
	private List<TimesheetRecord> timesheetRecords = new ArrayList<TimesheetRecord>();
	private String weekEnding;
	
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
	public TimesheetImportResponse() {
		super();
	}
	public TimesheetImportResponse(Connection conn, TimesheetImportRequest request) throws Exception {
		this();			
		this.timesheetRecords = Arrays.asList( new TimesheetRecord[] {
				new SampleRecord1(),
				new SampleRecord2()
		} );
	}	
	public TimesheetImportResponse(Connection conn, InputStream inputStream) throws Exception {
		this();
		parseODSFile(inputStream);
		
	}
	public List<TimesheetRecord> getEmployeeRecordList() {
		return timesheetRecords;
	}
	public void setEmployeeRecordList(List<TimesheetRecord> employeeRecordList) {
		this.timesheetRecords = employeeRecordList;
	}

	public void addEmployeeRecord(TimesheetRecord record) {
		if ( this.timesheetRecords == null ) {
			this.timesheetRecords = new ArrayList<TimesheetRecord>();
		}
		this.timesheetRecords.add(record);
	}
	public String getCity() {
		return this.city;
	}
	public void setCity(String city) {	
		this.city = city;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getOperationsManagerName() {
		return operationsManagerName;
	}
	public void setOperationsManagerName(String operationsManagerName) {
		this.operationsManagerName = operationsManagerName;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public List<TimesheetRecord> getTimesheetRecords() {
		return timesheetRecords;
	}
	public void setTimesheetRecords(List<TimesheetRecord> timesheetRecords) {
		this.timesheetRecords = timesheetRecords;
	}
	public String getWeekEnding() {
		return weekEnding;
	}
	public void setWeekEnding(String weekEnding) {
		this.weekEnding = weekEnding;
	}

	public void parseODSFile(InputStream odsFile) throws Exception {    	    	
    	SpreadsheetDocument speadsheetDocument = null;
		speadsheetDocument = SpreadsheetDocument.loadDocument(odsFile);
		List<Table> tables = speadsheetDocument.getTableList();
					
		Table tableSummary = tables.get(2);
				
		for(Integer sdRow=6; sdRow<39;sdRow++) {			
			TimesheetRecord timeSheetRecord = new TimesheetRecord();
			
			timeSheetRecord.setEmployeeName(tableSummary.getCellByPosition		(WprCols.EMPLOYEE_NAME.cellLocation()	+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setDirectLabor(tableSummary.getCellByPosition		(WprCols.DIRECT_LABOR.cellLocation()				+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setExpenses(tableSummary.getCellByPosition			(WprCols.EXPENSES.cellLocation()		+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setExpensesAllowed(tableSummary.getCellByPosition	(WprCols.EXPENSES_ALLOWED.cellLocation()		+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setExpensesSubmitted(tableSummary.getCellByPosition	(WprCols.EXPENSES_SUBMITTED.cellLocation()	+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setGrossPay(tableSummary.getCellByPosition			(WprCols.GROSS_PAY.cellLocation()		+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setHolidayHours(tableSummary.getCellByPosition		(WprCols.HOLIDAY_HOURS.cellLocation()			+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setHolidayPay(tableSummary.getCellByPosition			(WprCols.HOLIDAY_PAY.cellLocation()			+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setOtHours(tableSummary.getCellByPosition			(WprCols.OT_HOURS.cellLocation()			+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setOtPay(tableSummary.getCellByPosition				(WprCols.OT_PAY.cellLocation()			+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setProductivity(tableSummary.getCellByPosition		(WprCols.PRODUCTIVITY.cellLocation() 			+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setRegularHours(tableSummary.getCellByPosition		(WprCols.REGULAR_HOURS.cellLocation()	+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setRegularPay(tableSummary.getCellByPosition			(WprCols.REGULAR_PAY.cellLocation()		+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setVacationHours(tableSummary.getCellByPosition		(WprCols.VACATION_HOURS.cellLocation()		+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setVacationPay(tableSummary.getCellByPosition		(WprCols.VACATION_PAY.cellLocation()			+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setVolume(tableSummary.getCellByPosition				(WprCols.VOLUME.cellLocation()			+ sdRow.toString()).getDisplayText());
			
			timesheetRecords.add(timeSheetRecord);
		}
		this.setEmployeeRecordList(timesheetRecords);
		this.setDivision(tableSummary.getCellByPosition(WprCols.DIVISION.cellLocation()).getDisplayText());
		this.setWeekEnding(tableSummary.getCellByPosition(WprCols.WEEK_ENDING.cellLocation()).getDisplayText());
		this.setOperationsManagerName(tableSummary.getCellByPosition(WprCols.OPERATIONS_MANAGER_NAME.cellLocation()).getDisplayText());
		this.setState(tableSummary.getCellByPosition(WprCols.STATE.cellLocation()).getDisplayText());
		this.setCity(tableSummary.getCellByPosition(WprCols.CITY.cellLocation()).getDisplayText());
		logger.log(Level.DEBUG,"Rows stored = " + this.getEmployeeRecordList().size());
    }

	public class SampleRecord1 extends TimesheetRecord {
		private static final long serialVersionUID = 1L;
		public SampleRecord1() {
			super();
			super.setRow("1");
			super.setEmployeeName("TYLER CORRADI");
			super.setRegularHours("8");
			super.setRegularPay("186.00");
			super.setExpenses("0.00");
			super.setGrossPay("186.00");
			super.setDirectLabor("186.00");
			super.setProductivity("100.00%");	
			super.setErrorsFound(true);
		}
	}
	public class SampleRecord2 extends TimesheetRecord {
		private static final long serialVersionUID = 1L;
		public SampleRecord2() {
			super();
			super.setRow("2");
			super.setEmployeeName("LEE KOCH");
			super.setExpenses("0.00");
			super.setVacationHours("8");
			super.setVacationPay("146.00");
			super.setGrossPay("146.00");
			super.setProductivity("100.00%");
			super.setErrorsFound(false);
		}
	}
}