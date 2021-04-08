package com.ansi.scilla.web.bcr.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.TicketClaim;
import com.ansi.scilla.web.bcr.common.BcrTicket;
import com.ansi.scilla.web.bcr.common.BcrTicketSql;
import com.ansi.scilla.web.bcr.common.PassthruExpense;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class BcrTicketClaimResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	public static final String selectClause = "select detail.* , code.display_value as expense_type_display from (\n";	
	public static final String whereClause = 
			"\n) as detail"
			+ "\nleft outer join code on table_name='ticket_claim_passthru' and field_name='passthru_expense_type' and value=detail.passthru_expense_type"
			+ "\nwhere " + BcrTicketSql.TICKET_ID + "=?"
			+ "\nand " + BcrTicketSql.SERVICE_TYPE_ID + "=?"
			+ "\nand " + BcrTicketSql.CLAIM_WEEK + "=?";
	
	private Integer claimYear;
	private String claimWeek;
	private List<String> claimWeeks;
	private TicketData ticket;
	private List<BcrTicket> dlClaims;
	private List<PassthruExpense> expenses;
	private BcrTicketClaimSummary summary;
	
	
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
	private BcrTicketClaimResponse(Connection conn, Integer userId, List<SessionDivision> divisionList, Integer divisionId, Integer workYear, String workWeeks, Integer claimId) throws SQLException, RecordNotFoundException, Exception {
		this();
		this.claimYear = workYear;
		
		TicketClaim ticketClaim = makeClaim(conn, claimId);
		makeResponse(conn, userId, divisionList, divisionId, workYear, workWeeks, ticketClaim);
		makeSummary(conn, ticketClaim, divisionList, divisionId, workYear);
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

	public BcrTicketClaimSummary getSummary() {
		return summary;
	}

	public void setSummary(BcrTicketClaimSummary summary) {
		this.summary = summary;
	}

	/**
	 * Remove references to a particular claim id. (In essence, this means searching the D/L claims and expenses for the claim id)
	 * @param conn
	 * @param claimId
	 * @param ticketId
	 * @throws RecordNotFoundException
	 * @throws SQLException
	 */
	public void scrubClaim(Connection conn, TicketClaim ticketClaim) throws RecordNotFoundException, Exception {		
		CollectionUtils.filterInverse(this.expenses, new PassthruExpenseFilterByClaim(ticketClaim.getClaimId()));
		CollectionUtils.filterInverse(this.dlClaims, new BcrTicketFilterByClaim(ticketClaim.getClaimId()));
		this.ticket = new TicketData(conn, this.ticket.getTicketId(), ticketClaim.getServiceType());
	}

	
	
	private TicketClaim makeClaim(Connection conn, Integer claimId) throws Exception {
		TicketClaim ticketClaim = new TicketClaim();
		ticketClaim.setClaimId(claimId);
		ticketClaim.selectOne(conn);
		return ticketClaim;
	}

	
	
	private void makeResponse(Connection conn, Integer userId, List<SessionDivision> divisionList,
			Integer divisionId, Integer workYear, String workWeeks, TicketClaim ticketClaim) throws SQLException, RecordNotFoundException {
		
		Integer ticketId = ticketClaim.getTicketId();
		Integer serviceType = ticketClaim.getServiceType();
		this.ticket = new TicketData(conn, ticketId, serviceType);
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
		ps.setInt(4, ticketClaim.getServiceType());
		ps.setString(5, workYear + "-" + ticketClaim.getClaimWeek());

		logger.log(Level.DEBUG, "Division: " + divisionId);
		logger.log(Level.DEBUG, "workYear: " + workYear);
		logger.log(Level.DEBUG, "ticketId: " + ticketId);
		logger.log(Level.DEBUG, "getServiceType: " + ticketClaim.getServiceType());
		logger.log(Level.DEBUG, "claimWeek: " + workYear + "-" + ticketClaim.getClaimWeek());

		
		ResultSet rs = ps.executeQuery();
		this.dlClaims = new ArrayList<BcrTicket>();
		this.expenses = new ArrayList<PassthruExpense>();
		while ( rs.next() ) {			
			Integer claimId = rs.getInt("claim_id");
			Double passthruVolume = rs.getDouble("passthru_volume");
			String passthruExpenseType = rs.getString("expense_type_display");
			String passthruExpenseCode = rs.getString(BcrTicketSql.PASSTHRU_EXPENSE_TYPE);
			// D/L claims and passthru expense claims are stored separately in the ticket_claim table.
			// Each type goes into a separate list.
			if ( StringUtils.isBlank(passthruExpenseType)) {
				dlClaims.add(new BcrTicket(rs));
			} else {
				String notes = rs.getString("notes");
				expenses.add(new PassthruExpense(claimId, passthruVolume, passthruExpenseCode, passthruExpenseType, notes));
			}
		}
//		if ( dlClaims.isEmpty() ) {
//			throw new RecordNotFoundException(String.valueOf(ticketId));
//		}
		

	}
	
	
	
	
	private void makeSummary(Connection conn, TicketClaim ticketClaim, List<SessionDivision> divisionList,
			Integer divisionId, Integer workYear) throws Exception {
		this.summary = new BcrTicketClaimSummary(conn, ticketClaim, divisionList, divisionId, workYear);
		
	}

	public static BcrTicketClaimResponse fromClaim(Connection conn, Integer userId, List<SessionDivision> divisionList, Integer divisionId, Integer workYear, String workWeeks, Integer claimId) throws SQLException, RecordNotFoundException, Exception {
		return new BcrTicketClaimResponse(conn, userId, divisionList, divisionId, workYear, workWeeks, claimId);
	}
	
	
	
	public class TicketData extends ApplicationObject {
		private static final long serialVersionUID = 1L;

		private final String sql = "select ticket.ticket_id, ticket.job_id, ticket.ticket_type, ticket.ticket_status, \n" +
				"\taddress.name as job_site_name, job_tag.tag_id as service_tag_id, job_tag.abbrev,\n" +
				"\tticket.act_price_per_cleaning as total_volume,\n" +
				"\tticket.act_price_per_cleaning - isnull(ticket_claim_totals.claimed_total_volume,0.00)	as volume_remaining \n" +
				"from ticket\n" + 
				"inner join job on job.job_id=ticket.job_id\n" + 
				"inner join quote on quote.quote_id=job.quote_id\n" + 
				"inner join address on address.address_id=quote.job_site_address_id\n" + 
				"left outer join job_tag_xref xref on xref.job_id=job.job_id\n" + 
				"left outer join (\n" +
				BcrTicketSql.ticketTotalSubselect +
				") as ticket_claim_totals on ticket_claim_totals.ticket_id = ticket.ticket_id\n" +
				"inner join job_tag on job_tag.tag_id=xref.tag_id and job_tag.tag_type='SERVICE'\n" + 
				"where ticket.ticket_id=?\n";
		
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
		

		public TicketData(Connection conn, Integer ticketId, Integer serviceTag) throws RecordNotFoundException, SQLException {
			this();
			PreparedStatement ps = conn.prepareStatement(sql+" and job_tag.tag_id=?\n");
			Logger logger = LogManager.getLogger(BcrTicketClaimResponse.class);
			logger.log(Level.DEBUG, sql+" and job_tag.tag_id=?\n");
			ps.setInt(1,  ticketId);
			ps.setInt(2,  serviceTag);
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
	
	
	public class BcrTicketFilterByClaim implements Predicate<BcrTicket> {

		private Integer claimId;
		
		public BcrTicketFilterByClaim(Integer claimId) {
			super();
			this.claimId = claimId;
		}

		@Override
		public boolean evaluate(BcrTicket arg0) {
			return arg0.getClaimId().equals(claimId);
		}
		
	}
	
	public class PassthruExpenseFilterByClaim implements Predicate<PassthruExpense> {

		private Integer claimId;
		
		public PassthruExpenseFilterByClaim(Integer claimId) {
			super();
			this.claimId = claimId;
		}

		@Override
		public boolean evaluate(PassthruExpense arg0) {
			return arg0.getClaimId().equals(claimId);
		}
		
	}
	
	
}
