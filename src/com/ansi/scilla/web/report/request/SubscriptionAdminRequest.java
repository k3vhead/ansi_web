package com.ansi.scilla.web.report.request;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.response.WebMessages;

public class SubscriptionAdminRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Integer userId;
	private Integer divisionId;
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	
	public WebMessages validate() {
		WebMessages webMessages = new WebMessages();
		if ( userId == null ) {
			webMessages.addMessage("userId","User is Required");
		}
		if ( divisionId == null ) {
			webMessages.addMessage("divisionId","Division is Required");
		}
		return webMessages;
	}
}
