package com.ansi.scilla.web.payroll.common;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.PayrollEmployee;
import com.ansi.scilla.common.payroll.common.EmployeeStatus;

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
//	private static final Integer COL_PROCESS_DATE = 11;
	
	
	// If you change this list of fields, make sure you modify the "equals()" method as well.
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
//	private String processDate;
	private String recordStatus;
	private List<String> fieldList = new ArrayList<String>();
	private String rowId;
	
	
	public EmployeeRecord() {
		super();
		this.rowId = makeRowId();
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
//		this.processDate = spreadSheetRow[COL_PROCESS_DATE];
		this.rowId = makeRowId();
	}
	



	public EmployeeRecord(ResultSet rs) throws SQLException {
		super();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
		Object terminationDate = rs.getObject(PayrollEmployee.EMPLOYEE_TERMINATION_DATE);
		Object unionMember = rs.getObject(PayrollEmployee.UNION_MEMBER);
		Object unionRate = rs.getObject(PayrollEmployee.UNION_RATE);
//		Object processDate = rs.getObject(PayrollEmployee.PROCESS_DATE);
		
		this.employeeCode = String.valueOf(rs.getInt(PayrollEmployee.EMPLOYEE_CODE));
		this.companyCode = rs.getString(PayrollEmployee.COMPANY_CODE);
		this.divisionId = String.valueOf(rs.getInt(PayrollEmployee.DIVISION));
		this.firstName = rs.getString(PayrollEmployee.EMPLOYEE_FIRST_NAME);
		this.lastName = rs.getString(PayrollEmployee.EMPLOYEE_LAST_NAME);
		this.departmentDescription = rs.getString(PayrollEmployee.DEPT_DESCRIPTION);
		this.status = rs.getString(PayrollEmployee.EMPLOYEE_STATUS);
		this.terminationDate = terminationDate == null ? null : sdf.format((java.sql.Date)terminationDate);
		this.unionMember = unionMember == null ? null : "Yes";
		this.unionCode = rs.getString(PayrollEmployee.UNION_CODE);
		this.unionRate = unionRate == null ? null : String.valueOf(unionRate);
//		this.processDate = processDate == null ? null : sdf.format((java.sql.Date)processDate);
		this.rowId = makeRowId();
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

//	public String getProcessDate() {
//		return processDate;
//	}

//	public void setProcessDate(String processDate) {
//		this.processDate = processDate;
//	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	
	public List<String> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<String> fieldList) {
		this.fieldList = fieldList;
	}
	
	public String getRowId() {
		return this.rowId;
	}

	@Override
	public boolean equals(Object obj) {		
		boolean matches = true;
		if ( obj.getClass().getName().equals(this.getClass().getName()) ) {			
			EmployeeRecord rec = (EmployeeRecord)obj;
			matches = compareInt("employeeCode", this.employeeCode, rec.getEmployeeCode()) ? matches : false;
			matches = compareString("companyCode", this.companyCode, rec.getCompanyCode()) ? matches : false;
			matches = compareInt("divisionId", this.divisionId, rec.getDivisionId()) ? matches : false;
			matches = compareString("firstName", this.firstName, rec.getFirstName()) ? matches : false;
			matches = compareString("lastName", this.lastName, rec.getLastName()) ? matches : false;
			matches = compareString("departmentDescription", this.departmentDescription, rec.getDepartmentDescription()) ? matches : false;
			matches = compareStatus("status", this.status, rec.getStatus()) ? matches : false;
			try {
				matches = compareDate("terminationDate", this.terminationDate, rec.getTerminationDate(), "MM/dd/yy") ? matches : false;
			} catch ( ParseException e) {
				fieldList.add("terminationDate");
				matches = false;
			}
			matches = compareBoolean("unionMember", this.unionMember, rec.getUnionMember()) ? matches : false;
			matches = compareString("unionCode", this.unionCode, rec.getUnionCode()) ? matches : false;
			matches = compareDollar("unionRate", this.unionRate, rec.getUnionRate()) ? matches : false;
//			try {
//				matches = compareDate("processDate", this.processDate, rec.getProcessDate(), "MM/dd/yy") ? matches : false;
//			} catch ( ParseException e) {
//				fieldList.add("processDate");
//				matches = false;
//			}
		} else {
			fieldList.add("classname");
			matches = false;
		}
		return matches;
	}


	private Boolean compareInt(String fieldName, String value1, String value2) {	
		Boolean matches = nullCheck(fieldName, value1, value2);
		if ( matches == null) {
			if ( value1.equalsIgnoreCase(value2) ) {
				matches = true;
			} else {
				fieldList.add(fieldName);
				matches = false;
			}
		}
		return matches;
	}

	private Boolean compareBoolean(String fieldName, String value1, String value2)  {
		Boolean matches = nullCheck(fieldName, value1, value2);
		if ( matches == null) {
			List<String> trueValueList = Arrays.asList(new String[] {"1","Yes","yes","true","True"});
			Boolean true1 = trueValueList.contains(value1);
			Boolean true2 = trueValueList.contains(value2);
			if ( true1.equals(true2) ) {
				matches = true;
			} else {
				matches = false;
				fieldList.add(fieldName);
			}
		}
		return matches;
	}

	private Boolean compareStatus(String fieldName, String value1, String value2)  {
		Boolean matches = nullCheck(fieldName, value1, value2);
		HashMap<String, EmployeeStatus> lookup = new HashMap<String, EmployeeStatus>();
		for ( EmployeeStatus employeeStatus : EmployeeStatus.values() ) {
			lookup.put(employeeStatus.display(), employeeStatus);
		}
		if ( matches == null) {
			EmployeeStatus status1 = lookup.containsKey(value1) ? lookup.get(value1) : EmployeeStatus.valueOf(value1);
			EmployeeStatus status2 = lookup.containsKey(value2) ? lookup.get(value2) : EmployeeStatus.valueOf(value2);
			if ( status1 != null && status2 != null ) {
				if ( status1.equals(status2)) {
					matches = true;
				} else {
					matches = false;
					fieldList.add(fieldName);
				}
			} else {
				fieldList.add(fieldName);
				matches = false;
			}
		}
		return matches;
	}

	private Boolean compareString(String fieldName, String value1, String value2)  {
		Boolean matches = nullCheck(fieldName, value1, value2);
		if ( matches == null) {
			if ( value1.equalsIgnoreCase(value2) ) {
				matches = true;
			} else {
				matches = false;
				fieldList.add(fieldName);
			}		
		}
		return matches;
	}

	private Boolean compareDollar(String fieldName, String value1, String value2)  {
		Boolean matches = nullCheck(fieldName, value1, value2);
		if ( matches == null) {
			Float float1 = value1.startsWith("$") ? Float.valueOf(value1.substring(1)) : Float.valueOf(value1);
			Float float2 = value2.startsWith("$") ? Float.valueOf(value2.substring(1)) : Float.valueOf(value2);
			if ( float1.equals(float2)) {
				matches = true;
			} else {
				matches = false;
				fieldList.add(fieldName);				
			}
		}
		return matches;
	}

	private Boolean compareDate(String fieldName, String value1, String value2, String dateFormat) throws ParseException {
		Boolean matches = nullCheck(fieldName, value1, value2);
		if ( matches == null) {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			Date date1 = sdf.parse(value1);
			Date date2 = sdf.parse(value2);
			if ( DateUtils.isSameDay(date1, date2)) {
				matches = true;
			} else {
				matches = false;
				fieldList.add(fieldName);				
			}
		}
		return matches;
	}

	/**
	 * 
	 * @param fieldName
	 * @param value1
	 * @param value2
	 * @return true if match, false if mismatch, null if needs another check
	 */
	private Boolean nullCheck(String fieldName, String value1, String value2) {
		Boolean matches = null;
		if ( isBlank(value1) && isBlank(value2) ) {
			// both are null - match
			matches = true;
		} else if (isBlank(value1) && ! isBlank(value2)) {
			// value1 is null & value2 is not --> mismatch
			fieldList.add(fieldName);
			matches = false; 
		} else if ( isBlank(value2) && ! isBlank(value1) ) {
			// value2 is null & value1 is not --> mismatch
			fieldList.add(fieldName);
			matches = false;
		} else {
			matches = null;
		}	
		return matches;
	}
	
	private boolean isBlank(String text) {
		return StringUtils.isBlank(text) || text.length() == 0 || text.equals("");
	}
	
	private String makeRowId() {
		List<String> source = new ArrayList<String>();
		source.add(employeeCode == null ? "" : String.valueOf(employeeCode));
		source.add(companyCode == null ? "" : companyCode);
		source.add(divisionId == null ? "" : String.valueOf(firstName));
		source.add(firstName == null ? "" : firstName);
		source.add(lastName == null ? "" : lastName);
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(StringUtils.join(source, "").getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			return number.toString(16);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
		
}
