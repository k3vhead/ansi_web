package com.ansi.scilla.web.common.response;

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
}
