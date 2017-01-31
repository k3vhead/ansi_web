package com.ansi.scilla.web.response.ticket;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.web.response.MessageResponse;

/**
 * Used to return a single ticket return set to the client
 * 
 * @author ggroce
 *
 */
public class TicketReturnResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private TicketReturnRecord ticket;

	public TicketReturnResponse() {
		super();
	}

	public TicketReturnResponse(TicketReturnRecord ticket) {
		super();
		this.ticket = ticket;
	}

	public TicketReturnResponse(Connection conn, Ticket ticket) throws IllegalAccessException, InvocationTargetException, SQLException {
		this();
		this.ticket = new TicketReturnRecord(ticket);
	}

	public TicketReturnRecord getTicket() {
		return ticket;
	}
	
	
}
