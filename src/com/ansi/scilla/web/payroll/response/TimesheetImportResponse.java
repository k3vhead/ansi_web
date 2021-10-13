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
	
	private List<TimesheetRecord> timesheetRecords = new ArrayList<TimesheetRecord>();

	private String operationsManagerName;
	private String division;
	private String weekEnding;
	private RequestDisplay request;
	
	//private List<TimesheetRecord> timesheetRecords = new ArrayList<TimesheetRecord>();
	
	public enum WprCols{
		EMPLOYEE_ROW  	("B")		
		,EmployeeName 	("D")
		,RegularHours 	("F")
		,RegularPay		("H")
		,Expenses 		("J")
		,OTHours 		("L")
		,OTPay 			("N")
		,VacHours 		("P")
		,VacPay			("R")
		,HolHours 		("T")
		,HolPay  		("V")
		,GrossPay 		("X")
		,ExpSubmitted 	("Z")		
		,ExpAllowed 	("AB")
		,Volume 		("AD")
		,DL 			("AF")
		,Prod 			("AH")
		,Division		("L3")
		,OM_Name		("O3")
		,WeekEnding		("X3");
		
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
		
		this.request = new RequestDisplay(conn, request);
		this.timesheetRecords = Arrays.asList( new TimesheetRecord[] {
				new SampleRecord1(),
				new SampleRecord2()
		} );
		
	}	
	
	public TimesheetImportResponse(Connection conn, InputStream inputStream) throws Exception {
		this();
		logger.log(Level.DEBUG, "Building a response object");
		parseODSFile(inputStream);
		
	}

	public RequestDisplay getRequest() {
		return request;
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

	

	public List<TimesheetRecord> getTimesheetRecords() {
		return timesheetRecords;
	}

	public void setTimesheetRecords(List<TimesheetRecord> timesheetRecords) {
		this.timesheetRecords = timesheetRecords;
	}

	public String getOperationsManagerName() {
		return operationsManagerName;
	}

	public void setOperationsManagerName(String operationsManagerName) {
		this.operationsManagerName = operationsManagerName;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getWeekEnding() {
		return weekEnding;
	}

	public void setWeekEnding(String weekEnding) {
		this.weekEnding = weekEnding;
	}

	
		
    public void parseODSFile(InputStream odsFile) throws Exception {

    	Boolean isDebugMode=true;
    	    	
    	SpreadsheetDocument speadsheetDocument = null;
		speadsheetDocument = SpreadsheetDocument.loadDocument(odsFile);
		
		
		List<Table> tables = speadsheetDocument.getTableList();
			
		// tests that tables are present */
		if(isDebugMode) {	
			System.out.println("Tables Found " + tables.size() );
			for(int i=0; i < tables.size();i++) {
				Table table = tables.get(i);	
				System.out.println("Tab Name - " + table.getTableName().toString());
			}
		}
		
		Table summary_tab = tables.get(2);

		// tests that division, om name and week ending were properly plucked. 
		if(isDebugMode) {	
			System.out.println("Divsion - " + summary_tab.getCellByPosition("L3").getDisplayText());
			System.out.println("OM Name - " + summary_tab.getCellByPosition("O3").getDisplayText());
			System.out.println("Week Ending - " + summary_tab.getCellByPosition("X3").getDisplayText());
		}
		
		
		String cellAddr="";
		for(Integer sdRow=6; sdRow<39;sdRow++) {
			if(isDebugMode) {	 // spew some data for each row to debug.. 
				System.out.println("Row: " + sdRow);
				System.out.println("Loc: " + WprCols.EmployeeName.cellLocation()	+ sdRow.toString());
			}
			
			TimesheetRecord timeSheetRecord = new TimesheetRecord();
			
			timeSheetRecord.setEmployeeName(summary_tab.getCellByPosition		(WprCols.EmployeeName.cellLocation()	+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setDirectLabor(summary_tab.getCellByPosition		(WprCols.DL.cellLocation()				+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setExpenses(summary_tab.getCellByPosition			(WprCols.Expenses.cellLocation()		+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setExpensesAllowed(summary_tab.getCellByPosition	(WprCols.ExpAllowed.cellLocation()		+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setExpensesSubmitted(summary_tab.getCellByPosition	(WprCols.ExpSubmitted.cellLocation()	+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setGrossPay(summary_tab.getCellByPosition			(WprCols.GrossPay.cellLocation()		+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setHolidayHours(summary_tab.getCellByPosition		(WprCols.HolPay.cellLocation()			+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setHolidayPay(summary_tab.getCellByPosition			(WprCols.OTPay.cellLocation()			+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setOtHours(summary_tab.getCellByPosition			(WprCols.OTHours.cellLocation()			+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setOtPay(summary_tab.getCellByPosition				(WprCols.OTPay.cellLocation()			+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setProductivity(summary_tab.getCellByPosition		(WprCols.Prod.cellLocation() 			+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setRegularHours(summary_tab.getCellByPosition		(WprCols.RegularHours.cellLocation()	+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setRegularPay(summary_tab.getCellByPosition			(WprCols.RegularPay.cellLocation()		+ sdRow.toString()).getDisplayText());
			//TimeRecRow.setState(smry.getCellByPosition						(wpr_cols.EmployeeName+sdRow.toString()).getDisplayText());
			timeSheetRecord.setVacationHours(summary_tab.getCellByPosition		(WprCols.VacHours.cellLocation()		+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setVacationPay(summary_tab.getCellByPosition		(WprCols.VacPay.cellLocation()			+ sdRow.toString()).getDisplayText());
			timeSheetRecord.setVolume(summary_tab.getCellByPosition				(WprCols.Volume.cellLocation()			+ sdRow.toString()).getDisplayText());
			
			timesheetRecords.add(timeSheetRecord);
			
//			if(isDebugMode) {	
//				System.out.println("Employee Name for Row " + sdRow + " is " + TimeRecRow.getEmployeeName());
//				System.out.println("Volume for Row " + sdRow + " is " + TimeRecRow.getVolume());
//			}
		}
		this.setDivision(cellAddr); 
//		summary_tab.getCellByPosition(WprCols.Volume.cellLocation()			+ sdRow.toString()).getDisplayText());
		
		
		
		TimesheetImportResponse oTimeSheetImportResponse = new TimesheetImportResponse();
		oTimeSheetImportResponse.setEmployeeRecordList(timesheetRecords);

		// don't see any place to set division, week_ending or om_name
		// oTimeSheetImportResponse.
		//smry.getCellByPosition		(wpr_cols.EmployeeName	+ sdRow.toString()).getDisplayText());		
		
		/* debug msgs */
		System.out.println("Rows stored = " + timesheetRecords.size());
		/* debug msgs */


    }
	
	public class RequestDisplay extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private String div;
		private Calendar payrollDate;
		private String state;
		private String city;
		private String timesheetFile;
		
		public RequestDisplay(Connection conn, TimesheetImportRequest request) throws Exception {
			super();
			Division division = new Division();
			division.setDivisionId(request.getDivisionId());
			division.selectOne(conn);
			this.div = division.getDivisionDisplay();
			
			this.payrollDate = request.getPayrollDate();
			this.state = request.getState();
			this.city = request.getCity();
			this.timesheetFile = request.getTimesheetFile().getName();
		}

		public String getDiv() {
			return div;
		}

		public void setDiv(String div) {
			this.div = div;
		}
		
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		public Calendar getPayrollDate() {
			return payrollDate;
		}
		
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		public void setPayrollDate(Calendar payrollDate) {
			this.payrollDate = payrollDate;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getTimesheetFile() {
			return timesheetFile;
		}

		public void setTimesheetFile(String timesheetFile) {
			this.timesheetFile = timesheetFile;
		}
		

		
		
		
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
