package com.ansi.scilla.web.claims.response;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.web.claims.common.ClaimTicketItem;
import com.ansi.scilla.web.claims.query.TicketAssignmentQuery;
import com.ansi.scilla.web.common.response.MessageResponse;

public class ClaimTicketResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private List<ClaimTicketItem> claims;
	
	public ClaimTicketResponse() {
		super();
	}
	
	private ClaimTicketResponse(ResultSet rs) throws SQLException {
		this();
		this.claims = new ArrayList<ClaimTicketItem>();
		while ( rs.next() ) {
			claims.add(new ClaimTicketItem(rs)) ;
		}
	}

	public List<ClaimTicketItem> getClaims() {
		return claims;
	}
	public void setClaims(List<ClaimTicketItem> claims) {
		this.claims = claims;
	}


	public static ClaimTicketResponse makeFromTicket(Connection conn, Integer ticketId) throws SQLException {
		ResultSet rs = TicketAssignmentQuery.makeTicketQuery(conn, ticketId);
		return new ClaimTicketResponse(rs);
	}
	
	public static ClaimTicketResponse makeFromWasher(Connection conn, Integer washerId) throws SQLException {
		ResultSet rs = TicketAssignmentQuery.makeWasherQuery(conn, washerId);
		return new ClaimTicketResponse(rs);
	}

	
}
