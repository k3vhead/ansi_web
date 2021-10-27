package com.ansi.scilla.web.payroll.request;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.response.WebMessages;

public class EmployeeImportRequest extends AbstractRequest {

	public EmployeeImportRequest(HttpServletRequest request) {
		super();
	}

	private static final long serialVersionUID = 1L;

	public WebMessages validate(Connection conn) {
		return new WebMessages();
	}

	
}
