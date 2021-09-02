package com.ansi.scilla.web.payroll.common;

import com.ansi.scilla.common.ApplicationObject;

public class EmployeeRecord extends ApplicationObject {

	private static final Integer COL_COMPANY_CODE = 0;
	// etc. etc.
	
	
	private String companyCode;
	private String division;
	private String employeeCode;
	private String firstName;
	// etc. etc
	
	
	public EmployeeRecord(String[] spreadSheetRow) {
		super();
		this.companyCode = spreadSheetRow[COL_COMPANY_CODE];
		// etc. etc. etc.
		
	}
}
