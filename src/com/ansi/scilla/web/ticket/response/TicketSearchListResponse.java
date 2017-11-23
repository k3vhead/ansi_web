package com.ansi.scilla.web.ticket.response;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ansi.scilla.common.queries.TicketSearch;
import com.ansi.scilla.web.common.response.MessageResponse;

/** 
 * Used to return a list of "code" objects to the client
 * 
 * @author ggroce
 *
 */
public class TicketSearchListResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<TicketSearchRecord> ticketSearchList;

	public TicketSearchListResponse() {
		super();
	}
	/**
	 * create a list of all code table records in the database, sorted by
	 * table, field, display value
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public TicketSearchListResponse(Connection conn) throws Exception {
		List<TicketSearch> ticketSearchList = TicketSearch.select(conn);
		this.ticketSearchList = new ArrayList<TicketSearchRecord>();
		for ( TicketSearch record : ticketSearchList ) {
			this.ticketSearchList.add(new TicketSearchRecord(record));
		}
	}
	
	public TicketSearchListResponse(Connection conn, String queryTerm) throws Exception {
		List<TicketSearch> ticketSearchList = TicketSearch.select(conn, queryTerm);
		this.ticketSearchList = new ArrayList<TicketSearchRecord>();
		for ( TicketSearch record : ticketSearchList ) {
			this.ticketSearchList.add(new TicketSearchRecord(record));
		}
	}
	
	public TicketSearchListResponse(Connection conn, String queryTerm, String[] sortField) throws Exception {
		List<TicketSearch> ticketSearchList = TicketSearch.select(conn, queryTerm, sortField);
		this.ticketSearchList = new ArrayList<TicketSearchRecord>();
		for ( TicketSearch record : ticketSearchList ) {
			this.ticketSearchList.add(new TicketSearchRecord(record));
		}
	}
	
	public TicketSearchListResponse(Connection conn, Integer quoteId) throws Exception {
		TicketSearch ticketSearch = TicketSearch.select(conn, quoteId);
		TicketSearchRecord record = new TicketSearchRecord(ticketSearch);
		this.ticketSearchList = Arrays.asList(new TicketSearchRecord[] { record });
	}

	public List<TicketSearchRecord> getTicketSearchList() {
		return ticketSearchList;
	}

	public void setTicketSearchList(List<TicketSearchRecord> ticketSearchList) {
		this.ticketSearchList = ticketSearchList;
	}
	
	
	
}
