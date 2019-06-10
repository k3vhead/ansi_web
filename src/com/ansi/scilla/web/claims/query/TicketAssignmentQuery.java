package com.ansi.scilla.web.claims.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ansi.scilla.common.ApplicationObject;

public class TicketAssignmentQuery extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	public static final String TICKET_ID = "ticket_id";
	public static final String WASHER_ID = "washer_id";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String CLAIM_ID = "claim_id";
	public static final String WORK_DATE = "work_date";
	public static final String VOLUME = "volume";
	public static final String DL_AMT = "dl_amt";
	public static final String HOURS = "hours";
	public static final String NOTES = "notes";
	
	private static final String sql = "select ticket_assignment.ticket_id, ticket_assignment.washer_id,\n" + 
			"		ansi_user.first_name, ansi_user.last_name,\n" + 
			"		ticket_claim.claim_id, ticket_claim.work_date, ticket_claim.volume, ticket_claim.dl_amt, ticket_claim.hours, ticket_claim.notes\n" + 
			"from ticket_assignment\n" + 
			"inner join ansi_user on ansi_user.user_id=ticket_assignment.washer_id\n" + 
			"left outer join ticket_claim on ticket_claim.ticket_id=ticket_assignment.ticket_id and ticket_claim.washer_id=ticket_assignment.washer_id\n";

	private static final String ticketQuery = 
			"where ticket_assignment.ticket_id=?\n" + 
			"order by ansi_user.last_name asc, ansi_user.first_name asc";
	
	private static final String washerQuery = 
			"where ticket_assignment.washer_id=?\n" + 
			"order by ticket_id asc";
	
	
	public static ResultSet makeTicketQuery(Connection conn, Integer ticketId) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(sql + ticketQuery);
		ps.setInt(1, ticketId);
		ResultSet rs = ps.executeQuery();
		return rs;
	}
	
	public static ResultSet makeWasherQuery(Connection conn, Integer washerId) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(sql + washerQuery);
		ps.setInt(1, washerId);
		ResultSet rs = ps.executeQuery();
		return rs;
	} 
}
