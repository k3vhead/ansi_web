package com.ansi.scilla.web.exceptions;

public class InvalidParameterException extends ScillaException {

	private static final long serialVersionUID = 1L;

	public InvalidParameterException() {
		super();
	}

	public InvalidParameterException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidParameterException(String message) {
		super(message);
	}

	public InvalidParameterException(Throwable cause) {
		super(cause);
	}

}
