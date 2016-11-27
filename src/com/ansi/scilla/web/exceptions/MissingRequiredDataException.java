package com.ansi.scilla.web.exceptions;

public class MissingRequiredDataException extends ScillaException {

	private static final long serialVersionUID = 1L;

	public MissingRequiredDataException() {

	}

	public MissingRequiredDataException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MissingRequiredDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingRequiredDataException(String message) {
		super(message);
	}

	public MissingRequiredDataException(Throwable cause) {
		super(cause);
	}

}
