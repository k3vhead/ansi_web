package com.ansi.scilla.web.payroll.response;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.payroll.common.EmployeeRecord;
import com.ansi.scilla.web.payroll.request.EmployeeImportRequest;

public class EmployeeImportResponse extends MessageResponse {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<EmployeeRecord> employeeList;

	public EmployeeImportResponse() {
		super();
	}
	
	public EmployeeImportResponse(Connection conn, EmployeeImportRequest uploadRequest) {
		this();
	}

	public List<EmployeeRecord> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<EmployeeRecord> employeeList) {
		this.employeeList = employeeList;
	}
	
	

}
