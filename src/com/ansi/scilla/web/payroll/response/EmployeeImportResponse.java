package com.ansi.scilla.web.payroll.response;

import java.util.List;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.payroll.common.EmployeeRecord;

public class EmployeeImportResponse extends MessageResponse {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<EmployeeRecord> employeeList;

	public List<EmployeeRecord> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<EmployeeRecord> employeeList) {
		this.employeeList = employeeList;
	}
	
	

}
