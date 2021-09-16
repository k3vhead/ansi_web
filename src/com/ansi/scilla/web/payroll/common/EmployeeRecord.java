package com.ansi.scilla.web.payroll.common;

import java.sql.Connection;
import java.util.Calendar;

import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.PayrollEmployee;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.thewebthing.commons.db2.RecordNotFoundException;

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
//	// etc. etc.
//	
	
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
		//this.setTerminationDate(spreadSheetRow[COL_TERMINATION_DATE]);
		this.unionMember = spreadSheetRow[COL_UNION_MEMBER];
		this.unionCode = spreadSheetRow[COL_UNION_CODE];
		this.unionRate = spreadSheetRow[COL_UNION_RATE];
		this.processDate = spreadSheetRow[COL_PROCESS_DATE];
		
		// etc. etc. etc.
		
	}
	

	public EmployeeRecord(Connection conn, Integer employeeCode) throws RecordNotFoundException, Exception {
		super();
		PayrollEmployee employee = new PayrollEmployee();
		employee.setEmployeeCode(employeeCode);
		employee.selectOne(conn);
		this.employeeCode = employeeCode;
		this.companyCode = employee.getCompanyCode();
		this.divisionId = employee.getDivisionId();
		this.firstName = employee.getEmployeeFirstName();
		this.lastName = employee.getEmployeeLastName();
		this.middleInitial = employee.getEmployeeMi();
		this.departmentDescription = employee.getDeptDescription();
		this.status = employee.getEmployeeStatus();
		if ( employee.getEmployeeTerminationDate() != null ) {
			this.setTerminationDate(DateUtils.toCalendar(employee.getEmployeeTerminationDate()));
		}
		this.notes = employee.getNotes();
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

//	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
//	public Calendar getTerminationDate() {
//		return terminationDate;
//	}
//	
//	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
//	public void setTerminationDate(Calendar terminationDate) {
//		this.terminationDate = terminationDate;
//	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
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
