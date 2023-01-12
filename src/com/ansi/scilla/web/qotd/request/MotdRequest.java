package com.ansi.scilla.web.qotd.request;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class MotdRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String MESSAGE = "message";
	
	
	private String message;

	public MotdRequest() {
		super();
	}

	public MotdRequest(String message) {
		this();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public WebMessages validate() {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateString(webMessages, MESSAGE, this.message, 1024, true, null);
		return webMessages;
	}

	

}
