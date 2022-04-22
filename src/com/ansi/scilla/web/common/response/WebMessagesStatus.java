package com.ansi.scilla.web.common.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;

/**
 * All the beauty of webmessages, plus the ability to differentiate between error messages and warnings
 * @author dclewis
 *
 */
public class WebMessagesStatus extends ApplicationObject {

	private static final long serialVersionUID = 1L;
	private WebMessages webMessages;
	private ResponseCode responseCode;
	
	public WebMessagesStatus() {
		super();
	}
	
	public WebMessagesStatus(WebMessages webMessages, ResponseCode responseCode) {
		this();
		this.webMessages = webMessages;
		this.responseCode = responseCode;
	}

	public WebMessages getWebMessages() {
		return webMessages;
	}

	public void setWebMessages(WebMessages webMessages) {
		this.webMessages = webMessages;
	}

	public ResponseCode getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}

	public void addMessage(String key, String message) {
		if ( this.webMessages == null ) {
			this.webMessages = new WebMessages();
		}
		webMessages.addMessage(key, message);
	}
	
	public void addAllMessages(WebMessages webMessages) {
		if ( this.webMessages == null ) {
			this.webMessages = new WebMessages();
		}
		this.webMessages.addAllMessages(webMessages);
	}
}
