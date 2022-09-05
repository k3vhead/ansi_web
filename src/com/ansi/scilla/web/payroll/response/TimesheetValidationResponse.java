package com.ansi.scilla.web.payroll.response;

import com.ansi.scilla.web.common.response.MessageResponse;

public class TimesheetValidationResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private TimesheetEmployee employee;

	public TimesheetEmployee getEmployee() {
		return employee;
	}

	public void setEmployee(TimesheetEmployee employee) {
		this.employee = employee;
	}
	
	

}
