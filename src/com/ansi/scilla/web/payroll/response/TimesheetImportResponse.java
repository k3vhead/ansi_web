package com.ansi.scilla.web.payroll.response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.payroll.parser.PayrollWorksheetEmployee;
import com.ansi.scilla.common.payroll.parser.PayrollWorksheetParser;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.payroll.request.TimesheetImportRequest;

public class TimesheetImportResponse extends MessageResponse {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final Logger logger = LogManager.getLogger(TimesheetImportResponse.class);	
	private String city;
	private String division;
	private String operationsManagerName;
	private String state;
	private List<PayrollWorksheetEmployee> timesheetRecords = new ArrayList<PayrollWorksheetEmployee>();
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
		logger.log(Level.DEBUG, "After - call to TimesheetImportResponse(Connection, String)");	
		logger.log(Level.DEBUG, "filename passed in was " + fileName);	
	}
	
	public TimesheetImportResponse(Connection conn, File file) throws FileNotFoundException, Exception {
		this(conn, new PayrollWorksheetParser(file));
//		this.fileName = file.getAbsolutePath();
//		parseODSFile(new FileInputStream(file));
	}
	
	public TimesheetImportResponse(Connection conn, FileItem file) throws IOException, Exception {
		this(conn, new PayrollWorksheetParser(file.getName(), file.getInputStream()));
		
//		this.fileName = file.getName();
//		parseODSFile(file.getInputStream());
	}
	
	private TimesheetImportResponse(Connection conn, PayrollWorksheetParser parser ) {
		this();
		this.city = parser.getCity();
		this.division = parser.getDivision();
		this.operationsManagerName = parser.getOperationsManagerName();
		this.state = parser.getState();
		this.timesheetRecords = parser.getTimesheetRecords();
		this.weekEnding = parser.getWeekEnding();
		this.fileName = parser.getFileName();
	}
	
	
	public List<PayrollWorksheetEmployee> getEmployeeRecordList() {
		return timesheetRecords;
	}
	public void setEmployeeRecordList(List<PayrollWorksheetEmployee> employeeRecordList) {
		this.timesheetRecords = employeeRecordList;
	}

	public void addEmployeeRecord(PayrollWorksheetEmployee record) {
		if ( this.timesheetRecords == null ) {
			this.timesheetRecords = new ArrayList<PayrollWorksheetEmployee>();
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
	public List<PayrollWorksheetEmployee> getTimesheetRecords() {
		return timesheetRecords;
	}
	public void setTimesheetRecords(List<PayrollWorksheetEmployee> timesheetRecords) {
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
}