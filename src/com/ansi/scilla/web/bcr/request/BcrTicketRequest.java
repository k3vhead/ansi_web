package com.ansi.scilla.web.bcr.request;

import java.sql.Connection;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class BcrTicketRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String TICKET_ID = "ticketId";
	public static final String OLD_CLAIM_WEEK = "oldClaimWeek";
	public static final String NEW_CLAIM_WEEK = "newClaimWeek";
	
	private String oldClaimWeek;
	private String newClaimWeek;
	
	public String getOldClaimWeek() {
		return oldClaimWeek;
	}
	public void setOldClaimWeek(String oldClaimWeek) {
		this.oldClaimWeek = oldClaimWeek;
	}
	public String getNewClaimWeek() {
		return newClaimWeek;
	}
	public void setNewClaimWeek(String newClaimWeek) {
		this.newClaimWeek = newClaimWeek;
	}
	
	public WebMessages validateUpdateClaimWeek(Connection conn, Integer ticketId) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateTicketId(conn, webMessages, TICKET_ID, ticketId, true);
		RequestValidator.validateClaimWeek(webMessages, OLD_CLAIM_WEEK, this.oldClaimWeek, true);
		RequestValidator.validateClaimWeek(webMessages, NEW_CLAIM_WEEK, this.newClaimWeek, true);
		
		
		return webMessages;
	}
}
