package com.ansi.scilla.web.response.ticketSearch;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.common.db.Division;
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
