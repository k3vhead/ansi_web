package com.ansi.scilla.web.payroll.response;


import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.payroll.parser.EmployeeImportParser;
import com.ansi.scilla.common.payroll.parser.EmployeeImportRecord;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.payroll.request.EmployeeImportRequest;
import com.ansi.scilla.web.payroll.request.EmployeeRequest;


public class EmployeeImportResponse extends MessageResponse {
	private static final long serialVersionUID = 1L;
	private Logger logger = LogManager.getLogger(EmployeeImportResponse.class);	

	private String fileName;
	private List<EmployeeImportRecord> employeeRecords = new ArrayList<EmployeeImportRecord>();
	
	
	
	public EmployeeImportResponse() {
		super();
	}
	
	public EmployeeImportResponse(Connection conn, EmployeeRequest request) throws Exception {
		this();					
	}
	
	public EmployeeImportResponse(Connection conn, String fileName, InputStream inputStream) throws Exception {
		this();
		EmployeeImportParser parser = new EmployeeImportParser(conn, fileName, inputStream);
		this.fileName = parser.getFileName();
		this.employeeRecords = parser.getEmployeeRecords();		
	}
	
	public EmployeeImportResponse(Connection conn, EmployeeImportRequest uploadRequest) throws Exception {		
		this(conn, uploadRequest.getEmployeeFile().getName(), uploadRequest.getEmployeeFile().getInputStream() );
	}
	
	
	

	public void addEmployeeRecord(EmployeeImportRecord record) {
		if ( this.employeeRecords == null ) {
			this.employeeRecords = new ArrayList<EmployeeImportRecord>();
		}
		this.employeeRecords.add(record);
	}
	public List<EmployeeImportRecord> getEmployeeRecords() {
		return employeeRecords;
	}
	public void setEmployeeRecords(List<EmployeeImportRecord> employeeRecords) {
		this.employeeRecords = employeeRecords;
	}
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/*
	public void parseCSVFile(InputStream csvFile) throws Exception {    
		
		CSVReader reader = new CSVReader(new InputStreamReader(csvFile));		
		List<String[]> recordList = reader.readAll();	

		recordList.remove(0);			
		int recordSize = recordList.size();
		reader.close();
		
		for ( int i = 0; i < recordSize; i++ ) {						
			EmployeeRecord rec = new EmployeeRecord(recordList.get(i));
			this.employeeRecords.add(rec);
//			logger.log(Level.DEBUG,rec);					
		}
		
    	
		
		logger.log(Level.DEBUG,"Rows stored = " + this.getEmployeeRecords().size());
    }
    */

	
}