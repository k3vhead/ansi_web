package com.ansi.scilla.web.payroll.common;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;

/**
 * Validation message list from validating a timesheet employee
 * 
 * @author dclewis
 *
 */
public class PayrollValidation extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	private ResponseCode responseCode;
	private WebMessages webMessages;
	public PayrollValidation() {
		super();
	}
	public PayrollValidation(ResponseCode responseCode, WebMessages webMessages) {
		this();
		this.responseCode = responseCode;
		this.webMessages = webMessages;
	}
	public ResponseCode getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}
	public WebMessages getWebMessages() {
		return webMessages;
	}
	public void setWebMessages(WebMessages webMessages) {
		this.webMessages = webMessages;
	}
	
	
}
