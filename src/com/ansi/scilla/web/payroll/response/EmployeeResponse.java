package com.ansi.scilla.web.payroll.response;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.payroll.common.EmployeeRecord;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class EmployeeResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private EmployeeRecord employee;
	private List<String> companyCodeList;
	
	public EmployeeResponse() {
		super();
	}

	public EmployeeResponse(Connection conn, Integer employeeCode) throws RecordNotFoundException, Exception {
		super();
		this.employee = new EmployeeRecord(conn, employeeCode);
		makeCompanyCodeList(conn);
		
	}

	private void makeCompanyCodeList(Connection conn) {
		// TODO Auto-generated method stub
		
	}

	public EmployeeRecord getEmployee() {
		return employee;
	}

	public void setEmployee(EmployeeRecord employee) {
		this.employee = employee;
	}

	public List<String> getCompanyCodeList() {
		return companyCodeList;
	}

	public void setCompanyCodeList(List<String> companyCodeList) {
		this.companyCodeList = companyCodeList;
	}

	

	
	
}
