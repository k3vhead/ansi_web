package com.ansi.scilla.web.payroll.request;

import java.sql.Connection;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.payroll.EmployeeStatus;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class EmployeeRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String SELECTED_EMPLOYEE_CODE = "selectedEmployeeCode";
	public static final String EMPLOYEE_CODE = "employeeCode";
	public static final String COMPANY_CODE ="companyCode";
	public static final String DIVISION_ID="divisionId";
	public static final String FIRST_NAME="firstName";
	public static final String LAST_NAME="lastName";
	public static final String MIDDLE_INITIAL="middleInitial";
	public static final String DEPARTMENT_DESCRIPTION="departmentDescription";
	public static final String STATUS="status";
	public static final String TERMINATION_DATE="terminationDate";
	public static final String UNION_MEMBER = "unionMember";
	public static final String UNION_CODE = "unionCode";
	public static final String UNION_RATE = "unionRate";
	public static final String PROCESS_DATE = "processDate";
	public static final String NOTES="notes";
	
	private Integer selectedEmployeeCode;
	private Integer employeeCode;
	private String companyCode;
	private Integer divisionId;
	private String firstName;
	private String lastName;
	private String middleInitial;
	private String departmentDescription;
	private String status;
	private Calendar terminationDate;
	private Integer unionMember;
	private String unionCode;
	private Double unionRate;
	private Calendar processDate;
	private String notes;

	
	public Integer getSelectedEmployeeCode() {
		return selectedEmployeeCode;
	}
	public void setSelectedEmployeeCode(Integer selectedEmployeeCode) {
		this.selectedEmployeeCode = selectedEmployeeCode;
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
		this.companyCode = StringUtils.trimToNull(companyCode);
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
		this.firstName = StringUtils.trimToNull(firstName);
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = StringUtils.trimToNull(lastName);
	}
	public String getMiddleInitial() {
		return middleInitial;
	}
	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = StringUtils.trimToNull(middleInitial);
	}
	public String getDepartmentDescription() {
		return departmentDescription;
	}
	public void setDepartmentDescription(String departmentDescription) {
		this.departmentDescription = StringUtils.trimToNull(departmentDescription);
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = StringUtils.trimToNull(status);
	}
	public Calendar getTerminationDate() {
		return terminationDate;
	}
	public void setTerminationDate(Calendar terminationDate) {
		this.terminationDate = terminationDate;
	}
	public Integer getUnionMember() {
		return unionMember;
	}
	public void setUnionMember(Integer unionMember) {
		this.unionMember = unionMember;
	}
	public String getUnionCode() {
		return unionCode;
	}
	public void setUnionCode(String unionCode) {
		this.unionCode = StringUtils.trimToNull(unionCode);
	}
	public Double getUnionRate() {
		return unionRate;
	}
	public void setUnionRate(Double unionRate) {
		this.unionRate = unionRate;
	}
	public Calendar getProcessDate() {
		return processDate;
	}
	public void setProcessDate(Calendar processDate) {
		this.processDate = processDate;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = StringUtils.trimToNull(notes);
	}
	
	
	public WebMessages validateAdd(Connection conn) throws Exception {
		WebMessages webMessages = validateUpdate(conn);
		RequestValidator.validateInteger(webMessages, EMPLOYEE_CODE, employeeCode, null, null, true);
		return webMessages;
	}
	
	
	public WebMessages validateUpdate(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, DIVISION_ID, this.divisionId, true);
		RequestValidator.validateString(webMessages, "name", this.firstName, 40, true, "Name");
		RequestValidator.validateString(webMessages, "name", this.lastName, 40, true, "Name");
		RequestValidator.validateString(webMessages, "name", this.middleInitial, 2, false, "Middle Initial");
		RequestValidator.validateString(webMessages, DEPARTMENT_DESCRIPTION, this.departmentDescription, 45, true, null);
		RequestValidator.validateString(webMessages, NOTES, this.notes, 512, false, null);
		RequestValidator.validateCompanyCode(conn, webMessages, COMPANY_CODE, companyCode, true, null);
		RequestValidator.validateEmployeeStatus(webMessages, STATUS, status, true);
		if ( this.unionMember != null && this.unionMember.intValue() == 1 ) {
			RequestValidator.validateString(webMessages, UNION_CODE, unionCode, 45, true, null);
			RequestValidator.validateNumber(webMessages, UNION_RATE, unionRate, 0.01D, null, true, null);			
		}
		RequestValidator.validateDate(webMessages, PROCESS_DATE, processDate, true, null, null);
		validateTerminationDate(webMessages);

		return webMessages;
	}
	
	
	private void validateTerminationDate(WebMessages webMessages) {
		if ( ! webMessages.containsKey(STATUS)) {
			EmployeeStatus employeeStatus = EmployeeStatus.valueOf(status);
			if ( employeeStatus.equals(EmployeeStatus.ACTIVE)) {
				if ( terminationDate != null ) {
					webMessages.addMessage(TERMINATION_DATE, "Employee is active");
				}
			} else if ( employeeStatus.equals(EmployeeStatus.TERMINATED) ){
				RequestValidator.validateDate(webMessages, TERMINATION_DATE, terminationDate, true, null, null);
			} else {
				//this should never happen, because we already validated status
				throw new RuntimeException("Invalid employee status was not caught");
			}
			
		}		
	}
	
	
}
