package com.ansi.scilla.web.payroll.response;

import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.payroll.common.EmployeeRecord;
import com.ansi.scilla.web.payroll.request.EmployeeImportRequest;	

public class EmployeeImportResponse extends MessageResponse {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger logger = LogManager.getLogger(EmployeeImportResponse.class);	

	private String division;

	private List<EmployeeRecord> employeeRecords = new ArrayList<EmployeeRecord>();
	
	
	public enum WprCols{
		COMPANY_CODE  			("B")		
		,DIVISION 				("D")
		,EMPLOYEE_CODE 			("F")
		,FIRST_NAME				("H")
		,LAST_NAME 				("J")
		,DEPARTMENT_DESCRIPTION ("L")
		,STATUS 				("N")
		,TERMINATION_DATE		("P")
		,UNION_MEMBER			("R")
		,UNION_CODE 			("T")
		,UNION_RATE  			("V")
		,PROCESS_DATE 			("X");
		
		
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
	public EmployeeImportResponse() {
		super();
	}
	public EmployeeImportResponse(Connection conn, EmployeeImportRequest request) throws Exception {
		this();			
		this.employeeRecords = Arrays.asList( new EmployeeRecord[] {
				new SampleRecord1(),
				new SampleRecord2()
		} );
	}	
	public EmployeeImportResponse(Connection conn, InputStream inputStream) throws Exception {
		this();
		parseODSFile(inputStream);
		
	}
	public List<EmployeeRecord> getEmployeeRecordList() {
		return employeeRecords;
	}
	public void setEmployeeRecordList(List<EmployeeRecord> employeeRecordList) {
		this.employeeRecords = employeeRecordList;
	}

	public void addEmployeeRecord(EmployeeRecord record) {
		if ( this.employeeRecords == null ) {
			this.employeeRecords = new ArrayList<EmployeeRecord>();
		}
		this.employeeRecords.add(record);
	}
	
	
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
//	public String getProcessDate() {
//		return processDate;
//	}
//	public void setProcessDate(String processDate) {
//		this.processDate = processDate;
//	}
//	public String getTerminationDate() {
//		return terminationDate;
//	}
//	public void setTerminationDate(String terminationDate) {
//		this.terminationDate = terminationDate;
//	}
	
	public void parseODSFile(InputStream odsFile) throws Exception {    	    	
    	SpreadsheetDocument speadsheetDocument = null;
		speadsheetDocument = SpreadsheetDocument.loadDocument(odsFile);
		List<Table> tables = speadsheetDocument.getTableList();
					
		Table tableSummary = tables.get(2);
				
		for(Integer sdRow=6; sdRow<39;sdRow++) {			
			EmployeeRecord employeeSheetRecord = new EmployeeRecord();
			
			
			employeeSheetRecord.setCompanyCode(tableSummary.getCellByPosition		(WprCols.COMPANY_CODE.cellLocation()	+ sdRow.toString()).getDisplayText());
			employeeSheetRecord.setEmployeeCode(tableSummary.getCellByPosition		(WprCols.EMPLOYEE_CODE.cellLocation()				+ sdRow.toString()).getDisplayText());
			employeeSheetRecord.setFirstName(tableSummary.getCellByPosition			(WprCols.FIRST_NAME.cellLocation()		+ sdRow.toString()).getDisplayText());
			employeeSheetRecord.setLastName(tableSummary.getCellByPosition	(WprCols.LAST_NAME.cellLocation()		+ sdRow.toString()).getDisplayText());
			employeeSheetRecord.setDepartmentDescription(tableSummary.getCellByPosition	(WprCols.DEPARTMENT_DESCRIPTION.cellLocation()	+ sdRow.toString()).getDisplayText());
			employeeSheetRecord.setStatus(tableSummary.getCellByPosition			(WprCols.STATUS.cellLocation()		+ sdRow.toString()).getDisplayText());
			employeeSheetRecord.setTerminationDate(tableSummary.getCellByPosition			(WprCols.TERMINATION_DATE.cellLocation()		+ sdRow.toString()).getDisplayText());
			employeeSheetRecord.setUnionMember(tableSummary.getCellByPosition			(WprCols.UNION_MEMBER.cellLocation()			+ sdRow.toString()).getDisplayText());
			employeeSheetRecord.setUnionCode(tableSummary.getCellByPosition			(WprCols.UNION_CODE.cellLocation()			+ sdRow.toString()).getDisplayText());
			employeeSheetRecord.setUnionRate(tableSummary.getCellByPosition				(WprCols.UNION_RATE.cellLocation()			+ sdRow.toString()).getDisplayText());
			employeeSheetRecord.setProcessDate(tableSummary.getCellByPosition			(WprCols.PROCESS_DATE.cellLocation()		+ sdRow.toString()).getDisplayText());

			employeeRecords.add(employeeSheetRecord);
		}
		this.setEmployeeRecordList(employeeRecords);
		this.setDivision(tableSummary.getCellByPosition(WprCols.DIVISION.cellLocation()).getDisplayText());
//		this.setProcessDate(tableSummary.getCellByPosition(WprCols.PROCESS_DATE.cellLocation()).getDisplayText());
//		this.setTerminationDate(tableSummary.getCellByPosition(WprCols.TERMINATION_DATE.cellLocation()).getDisplayText());
		
		logger.log(Level.DEBUG,"Rows stored = " + this.getEmployeeRecordList().size());
    }

	public class SampleRecord1 extends EmployeeRecord {
		private static final long serialVersionUID = 1L;
		public SampleRecord1() {
			super();
			super.setCompanyCode("1");
			super.setEmployeeCode("0000");
			super.setFirstName("TYLER ");
			super.setLastName("CORRADI");
			super.setDepartmentDescription("xxx");
			super.setStatus("active");
			super.setTerminationDate("10/23/20");
			super.setUnionMember("yes");
			super.setUnionCode("231");	
			super.setUnionRate("0.00");
			super.setProcessDate("10/23/20");
		}
	}
	public class SampleRecord2 extends EmployeeRecord {
		private static final long serialVersionUID = 1L;
		public SampleRecord2() {
			super();
//			super.setRow("2");
//			super.setEmployeeName("LEE KOCH");
//			super.setExpenses("0.00");
//			super.setVacationHours("8");
//			super.setVacationPay("146.00");
//			super.setGrossPay("146.00");
//			super.setProductivity("100.00%");
//			super.setErrorsFound(false);
		}
	}
}