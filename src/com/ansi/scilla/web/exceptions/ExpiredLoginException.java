package com.ansi.scilla.web.exceptions;

public class ExpiredLoginException extends ScillaException {

	private static final long serialVersionUID = 1L;

	public ExpiredLoginException() {
		super();
	}

	public ExpiredLoginException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ExpiredLoginException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpiredLoginException(String message) {
		super(message);
	}

	public ExpiredLoginException(Throwable cause) {
		super(cause);
	}


	
}
