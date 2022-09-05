package com.ansi.scilla.web.bcr.response;

import java.util.HashMap;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;

public class BcrSplitClaimValidationResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private WebMessages webMessages;
	private Boolean expenseError;
	private WebMessages expenseMessages;
	private Boolean laborError;
	private HashMap<Integer, WebMessages> laborMessages;
	
	public BcrSplitClaimValidationResponse() {
		super();
	}
	
	public BcrSplitClaimValidationResponse(WebMessages webMessages, WebMessages expenseMessages, HashMap<Integer, WebMessages> laborMessages) {
		this();
		this.webMessages = webMessages;
		this.expenseError = ! expenseMessages.isEmpty();
		this.expenseMessages = expenseMessages;
		this.laborError = ! laborMessages.isEmpty();
		this.laborMessages = laborMessages;
	}



	public WebMessages getWebMessages() {
		return webMessages;
	}
	public void setWebMessages(WebMessages webMessages) {
		this.webMessages = webMessages;
	}
	public WebMessages getExpenseMessages() {
		return expenseMessages;
	}
	public void setExpenseMessages(WebMessages expenseMessages) {
		this.expenseMessages = expenseMessages;
	}
	public HashMap<Integer, WebMessages> getLaborMessages() {
		return laborMessages;
	}
	public void setLaborMessages(HashMap<Integer, WebMessages> laborMessages) {
		this.laborMessages = laborMessages;
	}	
	public Boolean getExpenseError() {
		return expenseError;
	}
	public void setExpenseError(Boolean expenseError) {
		this.expenseError = expenseError;
	}
	public Boolean getLaborError() {
		return laborError;
	}
	public void setLaborError(Boolean laborError) {
		this.laborError = laborError;
	}

	public ResponseCode responseCode() {
		ResponseCode responseCode = ResponseCode.SUCCESS;
		if ( ! this.webMessages.isEmpty() ) { responseCode = ResponseCode.EDIT_FAILURE; }
		if ( ! this.expenseMessages.isEmpty() ) { responseCode = ResponseCode.EDIT_FAILURE; }
		for ( WebMessages laborMessage : this.laborMessages.values() ) {
			if ( ! laborMessage.isEmpty() ) { responseCode = ResponseCode.EDIT_FAILURE; }
		}
		
		return responseCode;
	}
}
