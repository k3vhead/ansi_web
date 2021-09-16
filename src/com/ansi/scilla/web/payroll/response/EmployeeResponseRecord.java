package com.ansi.scilla.web.payroll.response;

import java.sql.Connection;
import java.util.Calendar;

import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.PayrollEmployee;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class EmployeeResponseRecord extends ApplicationObject {

	private static final long serialVersionUID = 1L;


	private Integer employeeCode;
	private String companyCode;
	private Integer divisionId;
	private String firstName;
	private String lastName;
	private String middleInitial;
	private String departmentDescription;
	private String status;
	private Calendar terminationDate;
	private String unionMember;
	private String unionCode;
	private String unionRate;
	private Calendar processDate;
	private String notes;
	
	public EmployeeResponseRecord() {
		super();
	}
	
	public EmployeeResponseRecord(Connection conn, Integer employeeCode) throws RecordNotFoundException, Exception {
		this();
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
			this.terminationDate = DateUtils.toCalendar(employee.getEmployeeTerminationDate());
		}
		this.notes = employee.getNotes();
	}

	public Integer getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(Integer employeeCode) {
		this.employeeCode = employeeCode;
	}

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

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public Calendar getTerminationDate() {
		return terminationDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public void setTerminationDate(Calendar terminationDate) {
		this.terminationDate = terminationDate;
	}

	public String getUnionMember() {
		return unionMember;
	}

	public void setUnionMember(String unionMember) {
		this.unionMember = unionMember;
	}

	public String getUnionCode() {
		return unionCode;
	}

	public void setUnionCode(String unionCode) {
		this.unionCode = unionCode;
	}

	public String getUnionRate() {
		return unionRate;
	}

	public void setUnionRate(String unionRate) {
		this.unionRate = unionRate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public Calendar getProcessDate() {
		return processDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public void setProcessDate(Calendar processDate) {
		this.processDate = processDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
