package com.ansi.scilla.web.common.response;

import com.ansi.scilla.common.exceptions.InvalidValueException;
import com.ansi.scilla.common.utils.ErrorLevel;
import com.ansi.scilla.web.common.utils.ApplicationWebObject;

public abstract class MessageResponse extends ApplicationWebObject {
	private static final long serialVersionUID = 1L;
	
	private WebMessages webMessages;

	public MessageResponse() {
		super();
	}

	public MessageResponse(WebMessages webMessages) {
		super();
		this.webMessages = webMessages;
	}

	public WebMessages getWebMessages() {
		return webMessages;
	}

	public void setWebMessages(WebMessages webMessages) {
		this.webMessages = webMessages;
	}
	
	protected ResponseCode makeResponseCode(ErrorLevel errorLevel) throws InvalidValueException {
		ResponseCode responseCode = null;
		
		switch (errorLevel) {
		case ERROR:
			responseCode = ResponseCode.EDIT_FAILURE;
			break;
		case OK:
			responseCode = ResponseCode.SUCCESS;
			break;
		case WARNING:
			responseCode = ResponseCode.EDIT_WARNING;
			break;
		default:
			throw new InvalidValueException(errorLevel.name());
		}
		return responseCode;
	}
}
