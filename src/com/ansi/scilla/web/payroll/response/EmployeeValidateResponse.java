package com.ansi.scilla.web.payroll.response;

import java.sql.Connection;
import java.text.SimpleDateFormat;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.PayrollEmployee;
import com.ansi.scilla.common.payroll.parser.employee.EmployeeImportRecord;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.payroll.request.EmployeeRequest;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class EmployeeValidateResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private EmployeeImportResponseRec employee;

	private EmployeeValidateResponse() {
		super();
	}

	public EmployeeValidateResponse(Connection conn, Integer employeeCode, EmployeeRequest employeeRequest, WebMessages webMessages) throws Exception {
		this();
		super.setWebMessages(webMessages);

		PayrollEmployee payrollEmployee = new PayrollEmployee();
		Division division = null;

		// only get a division if we have a good id
		if ( ! webMessages.containsKey(EmployeeRequest.DIVISION_ID)) {
			division = new Division();
			division.setDivisionId(employeeRequest.getDivisionId());
			division.selectOne(conn);
		}

		try {			
			payrollEmployee.setEmployeeCode(employeeCode);
			payrollEmployee.selectOne(conn);

			// set default values for employee
			EmployeeImportRecord employeeImportRecord = new EmployeeImportRecord(payrollEmployee);
			this.employee = new EmployeeImportResponseRec(employeeImportRecord);

			// override with validated data from request
			makeEmployee(employeeRequest, division);				
			employee.setRecordMatches( employee.ansiEquals(payrollEmployee) );
			employee.setNewEmployee(false);
		} catch (RecordNotFoundException e) {
			this.employee = new EmployeeImportResponseRec();			
			// override with validated data from request
			makeEmployee(employeeRequest, division);
			employee.setRecordMatches( false );
			employee.setNewEmployee(true);
		}
	}

	public EmployeeImportResponseRec getEmployee() {
		return employee;
	}

	public void setEmployee(EmployeeImportResponseRec employee) {
		this.employee = employee;
	}
	private void makeEmployee(EmployeeRequest employeeRequest, Division division) {
		Integer unionMember = employeeRequest.getUnionMember();
		Double unionRate = employeeRequest.getUnionRate();

		this.employee.setEmployeeCode( String.valueOf(employeeRequest.getEmployeeCode()) );
		this.employee.setCompanyCode( employeeRequest.getCompanyCode() );
		this.employee.setDivisionId( employeeRequest.getDivisionId() );
		this.employee.setFirstName( employeeRequest.getFirstName() );
		this.employee.setLastName( employeeRequest.getLastName() );
		//		this.employee.setMiddleInitial( ( employeeRequest.getMiddleInitial() ):    private String middleInitial;
		this.employee.setDepartmentDescription( employeeRequest.getDepartmentDescription() );
		this.employee.setStatus( employeeRequest.getStatus() );
		if ( employeeRequest.getTerminationDate() == null ) {
			this.employee.setTerminationDate( null );
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat(EmployeeImportRecord.EMPLOYEE_RECORD_DATE_FORMAT);
			this.employee.setTerminationDate( sdf.format(employeeRequest.getTerminationDate().getTime() ));
		}
		this.employee.setUnionMember( unionMember != null && unionMember.intValue()==1 ? "1" : "0" );
		this.employee.setUnionCode( employeeRequest.getUnionCode() );
		this.employee.setUnionRate( unionRate == null ? null : String.valueOf(unionRate) );
		//		this.employee.setProcessDate( employeeRequest.getProcessDate() );
		if ( division != null ) {
			this.employee.setDiv(division.getDivisionDisplay());
		}
		this.employee.setNotes(employeeRequest.getNotes());
	}



}
