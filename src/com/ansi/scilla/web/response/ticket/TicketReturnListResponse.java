package com.ansi.scilla.web.response.ticket;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.web.response.MessageResponse;

/** 
 * Used to return a list of "ticketReturn" objects to the client
 * 
 * @author ggroce
 *
 */
public class TicketReturnListResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<TicketRecord> ticketReturnList;

	public TicketReturnListResponse() {
		super();
	}
	/**
	 * create a list of all ticket table records in the database, sorted by
	 * table, field, display value
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public TicketReturnListResponse(Connection conn) throws Exception {
		List<Ticket> ticketList = Ticket.cast(new Ticket().selectAll(conn));
		this.ticketReturnList = new ArrayList<TicketRecord>();
		for ( Ticket ticket : ticketList ) {
			this.ticketReturnList.add(new TicketRecord(ticket));
		}
	}
	
	public TicketReturnListResponse(Connection conn, Integer ticketId) throws Exception {
		Ticket key = new Ticket();
		key.setTicketId(ticketId);
		List<Ticket> ticketList = Ticket.cast(key.selectSome(conn));
		this.ticketReturnList = new ArrayList<TicketRecord>();
		for ( Ticket ticket : ticketList ) {
			this.ticketReturnList.add(new TicketRecord(ticket));
		}
	}

	public List<TicketRecord> getDivisionList() {
		return ticketReturnList;
	}

	public void setTicketReturnList(List<TicketRecord> ticketReturnList) {
		this.ticketReturnList = ticketReturnList;
	}
	
	
	
}
