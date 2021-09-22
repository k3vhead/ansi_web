package com.ansi.scilla.web.payroll.response;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

	private RequestDisplay request;
	private List<TimesheetRecord> employeeRecordList = new ArrayList<TimesheetRecord>();
	
	public TimesheetImportResponse() {
		super();
	}
	
	public TimesheetImportResponse(Connection conn, TimesheetImportRequest request) throws Exception {
		this();	
		this.request = new RequestDisplay(conn, request);
	}


	public RequestDisplay getRequest() {
		return request;
	}


	public List<TimesheetRecord> getEmployeeRecordList() {
		return employeeRecordList;
	}


	public void setEmployeeRecordList(List<TimesheetRecord> employeeRecordList) {
		this.employeeRecordList = employeeRecordList;
	}
	
	public void addEmployeeRecord(TimesheetRecord record) {
		if ( this.employeeRecordList == null ) {
			this.employeeRecordList = new ArrayList<TimesheetRecord>();
		}
		this.employeeRecordList.add(record);
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
}
