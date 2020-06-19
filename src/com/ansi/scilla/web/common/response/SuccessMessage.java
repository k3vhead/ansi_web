package com.ansi.scilla.web.common.response;

public final class SuccessMessage extends WebMessages {

	private static final long serialVersionUID = 1L;

	public SuccessMessage() {
		super();
		this.addMessage(GLOBAL_MESSAGE, "Success");
	}
}
