package com.ansi.scilla.web.bcr.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.bcr.common.BcrTicket;
import com.ansi.scilla.web.bcr.common.BcrTicketSql;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class BcrTicketResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private static final String selectClause = "select detail.* from (\n";	
	private static final String whereClause = "\n) as detail where ticket_id=?";
	
	private Integer claimYear;
	private List<String> claimWeeks;
	private BcrTicket ticket;
	
	public BcrTicketResponse(Connection conn, Integer userId, List<SessionDivision> divisionList, Integer divisionId, Integer workYear, String workWeek, Integer ticketId) throws SQLException, RecordNotFoundException {
		super();
		this.claimYear = workYear;
		this.claimWeeks = new ArrayList<String>();
		for ( String week : StringUtils.split(workWeek,",")) {
			claimWeeks.add( String.valueOf(workYear)+"-"+week);
		}
		Collections.sort(claimWeeks);
		makeResponse(conn, userId, divisionList, divisionId, workYear, workWeek, ticketId);
	}

	
	public Integer getClaimYear() {
		return claimYear;
	}
	public void setClaimYear(Integer claimYear) {
		this.claimYear = claimYear;
	}
	public List<String> getClaimWeeks() {
		return claimWeeks;
	}
	public void setClaimWeeks(List<String> claimWeeks) {
		this.claimWeeks = claimWeeks;
	}
	public BcrTicket getTicket() {
		return ticket;
	}
	public void setTicket(BcrTicket ticket) {
		this.ticket = ticket;
	}


	private void makeResponse(Connection conn, Integer userId, List<SessionDivision> divisionList,
			Integer divisionId, Integer workYear, String workWeek, Integer ticketId) throws SQLException, RecordNotFoundException {
		String baseSql = BcrTicketSql.sqlSelectClause + BcrTicketSql.makeFilteredFromClause(divisionList) + BcrTicketSql.makeBaseWhereClause(workWeek);
		String sql = selectClause + baseSql + whereClause;
		
		List<Integer> weekFilter = new ArrayList<Integer>();
		for ( String weekNum : StringUtils.split(workWeek, ",")) {
			weekFilter.add(Integer.valueOf(weekNum));
		}
		Logger logger = LogManager.getLogger(this.getClass());
		logger.log(Level.DEBUG, sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, divisionId);
		ps.setInt(2, workYear);
		ps.setInt(3, ticketId);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			this.ticket = new BcrTicket(rs);
		} else {
			throw new RecordNotFoundException(String.valueOf(ticketId));
		}
		

	}
}
