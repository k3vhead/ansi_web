package com.ansi.scilla.web.payroll.response;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.division.common.CompanySelector;
import com.ansi.scilla.web.division.common.DivisionGroupSelectorItem;
import com.ansi.scilla.web.division.common.DivisionSelector;
import com.ansi.scilla.web.division.common.DivisionSelectorItem;
import com.ansi.scilla.web.payroll.common.EmployeeRecord;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class EmployeeResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private EmployeeRecord employee;
	private List<DivisionGroupSelectorItem> companyCodeList;
	private List<DivisionSelectorItem> divisionList;
	
	public EmployeeResponse() {
		super();
	}

	public EmployeeResponse(Connection conn, Integer employeeCode) throws RecordNotFoundException, Exception {
		super();
		this.employee = new EmployeeRecord(conn, employeeCode);
		this.companyCodeList = CompanySelector.getInstance(conn).getGroupList();
		this.divisionList = DivisionSelector.getInstance(conn).getDivisionList();	
	}

	

	public EmployeeRecord getEmployee() {
		return employee;
	}

	public void setEmployee(EmployeeRecord employee) {
		this.employee = employee;
	}

	public List<DivisionGroupSelectorItem> getCompanyCodeList() {
		return companyCodeList;
	}

	public void setCompanyCodeList(List<DivisionGroupSelectorItem> companyCodeList) {
		this.companyCodeList = companyCodeList;
	}

	public List<DivisionSelectorItem> getDivisionList() {
		return divisionList;
	}

	public void setDivisionList(List<DivisionSelectorItem> divisionList) {
		this.divisionList = divisionList;
	}









	
	
}
