package com.ansi.scilla.web.payroll.request;

import java.sql.Connection;
import java.util.Calendar;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.response.WebMessages;

public class EmployeeRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private String companyCode;
	private Integer divisionId;
	private String firstName;
	private String lastName;
	private String middleInitial;
	private String departmentDescription;
	private String status;
	private Calendar terminationDate;
	private String notes;

	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMiddleInitial() {
		return middleInitial;
	}
	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}
	public String getDepartmentDescription() {
		return departmentDescription;
	}
	public void setDepartmentDescription(String departmentDescription) {
		this.departmentDescription = departmentDescription;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Calendar getTerminationDate() {
		return terminationDate;
	}
	public void setTerminationDate(Calendar terminationDate) {
		this.terminationDate = terminationDate;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
	public WebMessages validateAdd(Connection conn) {
		WebMessages webMessages = new WebMessages();
		
		return webMessages;
	}
	
	public WebMessages validateUpdate(Connection conn, Integer employeeCode) {
		WebMessages webMessages = new WebMessages();
		return webMessages;
	}
	
	
}
