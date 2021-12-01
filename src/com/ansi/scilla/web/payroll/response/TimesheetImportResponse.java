package com.ansi.scilla.web.payroll.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

//import com.ansi.scilla.common.ApplicationObject;
//import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.payroll.request.TimesheetImportRequest;
//import com.fasterxml.jackson.annotation.JsonFormat;	

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
	private String fileName;
	
		
	public TimesheetImportResponse() {	
		super();
	}

	public TimesheetImportResponse(Connection conn, TimesheetImportRequest request) throws IOException, Exception {
		this(conn, request.getTimesheetFile());
	}
	
	public TimesheetImportResponse(Connection conn, String fileName) throws FileNotFoundException, Exception {
		this(conn, new File(fileName));
	}
	
	public TimesheetImportResponse(Connection conn, File file) throws FileNotFoundException, Exception {
		this();
		this.fileName = file.getAbsolutePath();
		parseODSFile(new FileInputStream(file));
	}
	
	public TimesheetImportResponse(Connection conn, FileItem file) throws IOException, Exception {
		this();
		this.fileName = file.getName();
		parseODSFile(file.getInputStream());
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
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
	public void parseODSFile(InputStream inputStream) throws Exception {    	    	
		logger.log(Level.DEBUG,"Inside Parser.. ");
		
    	SpreadsheetDocument speadsheetDocument = null;
    	speadsheetDocument = SpreadsheetDocument.loadDocument(inputStream);
		List<Table> tables = speadsheetDocument.getTableList();
					
		Table table = tables.get(2);
		for(Integer row=6; row<39;row++) {			
			timesheetRecords.add(new TimesheetRecord(table, row));
		}		
		this.setEmployeeRecordList(timesheetRecords);	
		this.setDivision(table.getCellByPosition(TimesheetRecord.WprCols.DIVISION.cellLocation()).getDisplayText());
		this.setWeekEnding(table.getCellByPosition(TimesheetRecord.WprCols.WEEK_ENDING.cellLocation()).getDisplayText());
		this.setOperationsManagerName(table.getCellByPosition(TimesheetRecord.WprCols.OPERATIONS_MANAGER_NAME.cellLocation()).getDisplayText());
		this.setState(table.getCellByPosition(TimesheetRecord.WprCols.STATE.cellLocation()).getDisplayText());
		this.setCity(table.getCellByPosition(TimesheetRecord.WprCols.CITY.cellLocation()).getDisplayText());

		logger.log(Level.DEBUG,"Division = " + this.getDivision());
		logger.log(Level.DEBUG,"Week Ending = " + this.getWeekEnding());
		logger.log(Level.DEBUG,"OM Name = " + this.getOperationsManagerName());
		logger.log(Level.DEBUG,"State = " + this.getState());
		logger.log(Level.DEBUG,"City = " + this.getCity());
				
		logger.log(Level.DEBUG,"Rows stored = " + this.getEmployeeRecordList().size());
    }
}