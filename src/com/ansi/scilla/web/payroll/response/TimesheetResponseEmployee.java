package com.ansi.scilla.web.payroll.response;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.response.ResponseCode;

public class TimesheetResponseEmployee extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	public ResponseCode responseCode;
	public TimesheetResponse data;
	public TimesheetResponseEmployee(ResponseCode responseCode, TimesheetResponse data) {
		super();
		this.responseCode = responseCode;
		this.data = data;
	}
	public String getResponseCode() {
		return responseCode.name();
	}
	public TimesheetResponse getData() {
		return data;
	}
}
