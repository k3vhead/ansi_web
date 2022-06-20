package com.ansi.scilla.web.payroll.response;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.payroll.request.EmployeeRequest;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class EmployeeResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private EmployeeResponseRecord employee;
	
	public EmployeeResponse() {
		super();
	}

	public EmployeeResponse(Connection conn, Integer employeeCode) throws RecordNotFoundException, Exception {
		this();
		this.employee = new EmployeeResponseRecord(conn, employeeCode);
	}

	

	public EmployeeResponseRecord getEmployee() {
		return employee;
	}

	public void setEmployee(EmployeeResponseRecord employee) {
		this.employee = employee;
	}

}
