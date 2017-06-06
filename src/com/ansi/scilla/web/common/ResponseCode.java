package com.ansi.scilla.web.common;

import javax.servlet.http.HttpServletResponse;

public enum ResponseCode {
	EXPIRED_LOGIN(HttpServletResponse.SC_FORBIDDEN),
	INVALID_LOGIN(HttpServletResponse.SC_FORBIDDEN),
	
	EDIT_FAILURE(HttpServletResponse.SC_OK),
	EDIT_WARNING(HttpServletResponse.SC_OK),
	
	SYSTEM_FAILURE(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),

	SUCCESS(HttpServletResponse.SC_OK);

	
	private final Integer statusCode;
	
	ResponseCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	public Integer statusCode() {
		return statusCode;
	}
}
