package com.ansi.scilla.web.ticket.response;

import com.ansi.scilla.web.common.response.MessageResponse;

public class TicketAssignmentResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	public static final String WASHER_ID = "washerId";
	public static final String TICKET_ID = "ticketId";
		
	private Integer washerId;
	private Integer ticketId;
	
	public TicketAssignmentResponse() {
		super();
	}
	
	public TicketAssignmentResponse(Integer washerId, Integer ticketId) {
		this();
		this.washerId = washerId;
		this.ticketId = ticketId;
	}

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
	
}
