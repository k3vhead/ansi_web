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

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.TicketClaim;
import com.ansi.scilla.web.bcr.common.BcrTicket;
import com.ansi.scilla.web.bcr.common.BcrTicketSql;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class BcrTicketClaimResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private static final String selectClause = "select detail.* , code.display_value as expense_type_display from (\n";	
	private static final String whereClause = 
			"\n) as detail"
			+ "\nleft outer join code on table_name='ticket_claim_passthru' and field_name='passthru_expense_type' and value=detail.passthru_expense_type"
			+ "\nwhere ticket_id=?";
	
	private Integer claimYear;
	private String claimWeek;
	private List<String> claimWeeks;
	private TicketData ticket;
	private List<BcrTicket> dlClaims;
	private List<PassthruExpense> expenses;
	
	
	public BcrTicketClaimResponse() {
		super();
	}

	/**
	 * 
	 * @param conn
	 * @param userId
	 * @param divisionList
	 * @param divisionId
	 * @param workYear
	 * @param workWeeks comma-delimited list of claim weeks
	 * @param ticketId
	 * @throws SQLException
	 * @throws RecordNotFoundException
	 */
	public BcrTicketClaimResponse(Connection conn, Integer userId, List<SessionDivision> divisionList, Integer divisionId, Integer workYear, String workWeeks, Integer claimId) throws SQLException, RecordNotFoundException, Exception {
		this();
		this.claimYear = workYear;
		
		TicketClaim ticketClaim = makeClaim(conn, claimId);
		makeResponse(conn, userId, divisionList, divisionId, workYear, workWeeks, ticketClaim.getTicketId());
		String formattedClaimWeek = StringUtils.leftPad(String.valueOf(ticketClaim.getClaimWeek()), 2, '0'); // make sure week '4' is actually '04'
		this.claimWeek = ticketClaim.getClaimYear() + "-" + formattedClaimWeek;
		this.claimWeeks = new ArrayList<String>();
		for ( String week : StringUtils.split(workWeeks,",")) {
			claimWeeks.add( String.valueOf(workYear)+"-"+week);
		}
		if ( ! StringUtils.isBlank(claimWeek) && ! claimWeeks.contains(claimWeek)) {
			claimWeeks.add(claimWeek);
		}
		Collections.sort(claimWeeks);
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
		public TicketData getTicket() {
		return ticket;
	}

	public void setTicket(TicketData ticket) {
		this.ticket = ticket;
	}

	public List<BcrTicket> getDlClaims() {
		return dlClaims;
	}

	public void setDlClaims(List<BcrTicket> dlClaims) {
		this.dlClaims = dlClaims;
	}

	public List<PassthruExpense> getExpenses() {
		return expenses;
	}

	public void setExpenses(List<PassthruExpense> expenses) {
		this.expenses = expenses;
	}

	public String getClaimWeek() {
		return claimWeek;
	}

	public void setClaimWeek(String claimWeek) {
		this.claimWeek = claimWeek;
	}

	private TicketClaim makeClaim(Connection conn, Integer claimId) throws Exception {
		TicketClaim ticketClaim = new TicketClaim();
		ticketClaim.setClaimId(claimId);
		ticketClaim.selectOne(conn);
		return ticketClaim;
	}

	private void makeResponse(Connection conn, Integer userId, List<SessionDivision> divisionList,
			Integer divisionId, Integer workYear, String workWeeks, Integer ticketId) throws SQLException, RecordNotFoundException {
		
		this.ticket = new TicketData(conn, ticketId);
		String baseSql = BcrTicketSql.sqlSelectClause + BcrTicketSql.makeFilteredFromClause(divisionList) + BcrTicketSql.makeBaseWhereClause(workWeeks);
		String sql = selectClause + baseSql + whereClause;
		
		List<Integer> weekFilter = new ArrayList<Integer>();
		for ( String weekNum : StringUtils.split(workWeeks, ",")) {
			weekFilter.add(Integer.valueOf(weekNum));
		}
		Logger logger = LogManager.getLogger(this.getClass());
		logger.log(Level.DEBUG, sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, divisionId);
		ps.setInt(2, workYear);
		ps.setInt(3, ticketId);
		logger.log(Level.DEBUG, "division | workYear | workWeeks | ticketId: " + divisionId + " | "+workYear+" | "+ workWeeks + " | " + ticketId);
		ResultSet rs = ps.executeQuery();
		this.dlClaims = new ArrayList<BcrTicket>();
		this.expenses = new ArrayList<PassthruExpense>();
		while ( rs.next() ) {			
			Integer claimId = rs.getInt("claim_id");
			Double passthruVolume = rs.getDouble("passthru_volume");
			String passthruExpenseType = rs.getString("expense_type_display");
			// D/L claims and passthru expense claims are stored separately in the ticket_claim table.
			// Each type goes into a separate list.
			if ( StringUtils.isBlank(passthruExpenseType)) {
				dlClaims.add(new BcrTicket(rs));
			} else {
				expenses.add(new PassthruExpense(claimId, passthruVolume, passthruExpenseType));
			}
		}
		if ( dlClaims.isEmpty() ) {
			throw new RecordNotFoundException(String.valueOf(ticketId));
		}
		

	}
	
	public class TicketData extends ApplicationObject {
		private static final long serialVersionUID = 1L;

		private final String sql = "select ticket.ticket_id, ticket.job_id, ticket.ticket_type, ticket.ticket_status, \n" +
				"\taddress.name as job_site_name, job_tag.tag_id as service_tag_id, job_tag.abbrev,\n" +
				"\tjob.price_per_cleaning as total_volume,\n" +
				"\tjob.price_per_cleaning - isnull(ticket_claim_totals.claimed_total_volume,0.00)	as volume_remaining \n" +
				"from ticket\n" + 
				"inner join job on job.job_id=ticket.job_id\n" + 
				"inner join quote on quote.quote_id=job.quote_id\n" + 
				"inner join address on address.address_id=quote.job_site_address_id\n" + 
				"left outer join job_tag_xref xref on xref.job_id=job.job_id\n" + 
				"left outer join (\n" +
				BcrTicketSql.ticketTotalSubselect +
				") as ticket_claim_totals on ticket_claim_totals.ticket_id = ticket.ticket_id\n" +
				"inner join job_tag on job_tag.tag_id=xref.tag_id and job_tag.tag_type='SERVICE'\n" + 
				"where ticket.ticket_id=?";
		
		private Integer ticketId;
		private Integer jobId;
		private String ticketType;
		private String status;
		private String jobSiteName;
		private String serviceTagId;
		private String serviceTagAbbrev;
		private Double totalVolume;
		private Double volumeRemaining;
		
		private TicketData() {
			super();
		}
		
		public TicketData(Connection conn, Integer ticketId) throws RecordNotFoundException, SQLException {
			this();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1,  ticketId);
			ResultSet rs = ps.executeQuery();
			if ( rs.next() ) {
				this.ticketId = ticketId;
				this.jobId = rs.getInt("job_id");
				this.ticketType = rs.getString("ticket_type");
				this.status = rs.getString("ticket_status");
				this.jobSiteName = rs.getString("job_site_name");
				this.serviceTagId = rs.getString("service_tag_id");
				this.serviceTagAbbrev = rs.getString("abbrev");
				this.totalVolume = rs.getDouble("total_volume");
				this.volumeRemaining = rs.getDouble("volume_remaining");
			} else {
				throw new RecordNotFoundException();
			}
		}

		public Integer getTicketId() {
			return ticketId;
		}

		public Integer getJobId() {
			return jobId;
		}

		public String getTicketType() {
			return ticketType;
		}

		public String getStatus() {
			return status;
		}

		public String getJobSiteName() {
			return jobSiteName;
		}

		public String getServiceTagId() {
			return serviceTagId;
		}

		public String getServiceTagAbbrev() {
			return serviceTagAbbrev;
		}

		public Double getTotalVolume() {
			return totalVolume;
		}

		public void setTotalVolume(Double totalVolume) {
			this.totalVolume = totalVolume;
		}

		public Double getVolumeRemaining() {
			return volumeRemaining;
		}
		
	}
	
	
	public class PassthruExpense extends ApplicationObject {
		private static final long serialVersionUID = 1L;

		private Integer claimId;
		private Double passthruVolume;
		private String passthruExpenseType;
		
		private PassthruExpense() {
			super();
		}

		public PassthruExpense(Integer claimId, Double passthruVolume, String passthruExpenseType) {
			this();
			this.claimId = claimId;
			this.passthruVolume = passthruVolume;
			this.passthruExpenseType = passthruExpenseType;
		}

		public Integer getClaimId() {
			return claimId;
		}

		public Double getPassthruVolume() {
			return passthruVolume;
		}

		public String getPassthruExpenseType() {
			return passthruExpenseType;
		}
		
		
	}
}
