package com.ansi.scilla.web.response.ticketSearch;

import java.io.Serializable;

import com.ansi.scilla.web.response.MessageResponse;

/**
 * Used to return a single code to the client
 * 
 * @author ggroce
 *
 */
public class TicketSearchResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private TicketSearchRecord ticketSearch;

	public TicketSearchResponse() {
		super();
	}

	public TicketSearchResponse(TicketSearchRecord ticketSearch) {
		super();
		this.ticketSearch = ticketSearch;
	}

	public TicketSearchRecord getTicketSearch() {
		return ticketSearch;
	}
	
	
}
