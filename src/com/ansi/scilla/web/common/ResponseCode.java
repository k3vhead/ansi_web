package com.ansi.scilla.web.common;

import javax.servlet.http.HttpServletResponse;

public enum ResponseCode {
	EXPIRED_LOGIN(HttpServletResponse.SC_FORBIDDEN),
	INVALID_LOGIN(HttpServletResponse.SC_FORBIDDEN),

	SUCCESS(HttpServletResponse.SC_OK);

	private final Integer statusCode;
	
	ResponseCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	public Integer statusCode() {
		return statusCode;
	}
}
