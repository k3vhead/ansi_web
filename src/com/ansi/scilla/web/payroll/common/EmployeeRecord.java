package com.ansi.scilla.web.payroll.common;

import com.ansi.scilla.common.ApplicationObject;

public class EmployeeRecord extends ApplicationObject {

	private static final long serialVersionUID = 1L;


	private static final Integer COL_COMPANY_CODE = 0;
	private static final Integer COL_DIVISION_ID = 1;
	private static final Integer COL_EMPLOYEE_CODE = 2;
	private static final Integer COL_FIRST_NAME = 3;
	private static final Integer COL_LAST_NAME = 4;
	private static final Integer COL_DEPARTMENT_DESCRIPTION = 5;
	private static final Integer COL_STATUS = 6;
	private static final Integer COL_TERMINATION_DATE = 7;
	private static final Integer COL_UNION_MEMBER= 8;
	private static final Integer COL_UNION_CODE = 9;
	private static final Integer COL_UNION_RATE = 10;
	private static final Integer COL_PROCESS_DATE = 11;
	
	
	private String employeeCode;
	private String companyCode;
	private String divisionId;
	private String firstName;
	private String lastName;
	private String departmentDescription;
	private String status;
	private String terminationDate;
	private String unionMember;
	private String unionCode;
	private String unionRate;
	private String processDate;
	
	public EmployeeRecord() {
		super();
	}
	
	public EmployeeRecord(String[] spreadSheetRow) {
		
		super();
		
		
		this.companyCode = spreadSheetRow[COL_COMPANY_CODE];
		this.divisionId = spreadSheetRow[COL_DIVISION_ID];
		this.employeeCode = spreadSheetRow[COL_EMPLOYEE_CODE];
		this.firstName = spreadSheetRow[COL_FIRST_NAME];
		this.lastName = spreadSheetRow[COL_LAST_NAME];
		this.departmentDescription = spreadSheetRow[COL_DEPARTMENT_DESCRIPTION];
		this.status = spreadSheetRow[COL_STATUS];
		this.terminationDate = spreadSheetRow[COL_TERMINATION_DATE];
		this.unionMember = spreadSheetRow[COL_UNION_MEMBER];
		this.unionCode = spreadSheetRow[COL_UNION_CODE];
		this.unionRate = spreadSheetRow[COL_UNION_RATE];
		this.processDate = spreadSheetRow[COL_PROCESS_DATE];
	}
	



	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(String divisionId) {
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

	public String getUnionCode() {
		return unionCode;
	}

	public void setUnionCode(String unionCode) {
		this.unionCode = unionCode;
	}

	public String getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(String terminationDate) {
		this.terminationDate = terminationDate;
	}

	public String getUnionMember() {
		return unionMember;
	}

	public void setUnionMember(String unionMember) {
		this.unionMember = unionMember;
	}

	public String getUnionRate() {
		return unionRate;
	}

	public void setUnionRate(String unionRate) {
		this.unionRate = unionRate;
	}

	public String getProcessDate() {
		return processDate;
	}

	public void setProcessDate(String processDate) {
		this.processDate = processDate;
	}
}
