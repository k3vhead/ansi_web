package com.ansi.scilla.web.ticket.request;

import java.sql.Connection;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class TicketAssignmentRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String WASHER_ID = "washerId";
	public static final String TICKET_ID = "ticketId";
		
	private Integer washerId;
	private Integer ticketId;
	
	public Integer getWasherId() {
		return washerId;
	}
	public void setWasherId(Integer washerId) {
		this.washerId = washerId;
	}
	public Integer getTicketId() {
		return ticketId;
	}
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	
	
	public WebMessages validateAdd(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateWasherId(conn, webMessages, WASHER_ID, this.washerId, true);
		RequestValidator.validateTicketId(conn, webMessages, TICKET_ID, this.ticketId, true);
		return webMessages;
	}
	
}
