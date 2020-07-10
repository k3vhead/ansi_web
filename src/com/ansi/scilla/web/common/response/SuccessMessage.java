package com.ansi.scilla.web.common.response;

public final class SuccessMessage extends WebMessages {

	private static final long serialVersionUID = 1L;

	public SuccessMessage() {
		super();
		super.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
	}
}
