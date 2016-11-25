package com.ansi.scilla.web.exceptions;

public abstract class ScillaException extends Exception {

	private static final long serialVersionUID = 1L;

	public ScillaException() {
		super();
	}

	public ScillaException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ScillaException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScillaException(String message) {
		super(message);
	}

	public ScillaException(Throwable cause) {
		super(cause);
	}

}
