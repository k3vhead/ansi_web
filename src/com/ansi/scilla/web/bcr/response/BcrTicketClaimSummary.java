package com.ansi.scilla.web.bcr.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.db.TicketClaim;
import com.ansi.scilla.web.bcr.common.BcrTicketSql;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class BcrTicketClaimSummary extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	public static final String selectClause = "select detail.claim_id, "
			+ "\tdetail.service_type_id,"
			+ "\tdetail.claim_year,"
			+ "\tdetail.claim_week, "
			+ "\tdetail.volume_claimed,"
			+ "\tdetail.passthru_volume, "
			+ "\tdetail.volume_remaining"
			+ " from (\n";	
	
	public static final String baseWhereClause =
			"\nwhere ticket.ticket_type in ('run','job') \n" + 			
			"  and ticket.act_division_id=? \n" + 
			" and ((\n" +
			"     ticket_claim.claim_year=?\n" + 
//			"     and ticket_claim.claim_week in ($CLAIMWEEKFILTER$)\n" +
			"   ) or (\n" +
			"     ticket.ticket_status in ('D','C')\n" +
//			" )\n" + 
			"   ) or ((isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim.passthru_expense_volume,0.00)) - isnull(invoice_totals.invoiced_amount,0.00) <> 0.00)\n" + 
			"		and (isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim.passthru_expense_volume,0.00) <> 0.00)\n" + 
			"   )  \n" 
			; 
	
	public static final String whereClause = 
			"\n) as detail" +
			"\n where detail.claim_id is not null and claim_week<=? and ticket_id=?\norder by detail.claim_week";
	
	private Integer ticketId;
	private Double totalVolume;
	private Double volumeClaimed;
	private Double expenseVolume;
	private Double volumeRemaining;
	
	public BcrTicketClaimSummary() {
		super();
	}
	
	
	
	public BcrTicketClaimSummary(Connection conn, TicketClaim ticketClaim, List<SessionDivision> divisionList, Integer divisionId, Integer workYear) throws Exception {
		this();

		this.ticketId = ticketClaim.getTicketId();
		
		Ticket ticket = new Ticket();
		ticket.setTicketId(this.ticketId);
		ticket.selectOne(conn);		
		this.totalVolume = ticket.getActPricePerCleaning().doubleValue();
		
		this.volumeClaimed = 0.0D;
		this.expenseVolume = 0.0D;
		this.volumeRemaining = 0.0D;
		
		String baseSql = BcrTicketSql.sqlSelectClause + 
				BcrTicketSql.makeFilteredFromClause(divisionList) +
				baseWhereClause;
		String sql = selectClause + baseSql + whereClause;		
		System.out.println(sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, divisionId);
		ps.setInt(2,  workYear);
		ps.setString(3, workYear + "-" + ticketClaim.getClaimWeek());
		ps.setInt(4, this.ticketId);
		ResultSet rs = ps.executeQuery();

		while ( rs.next() ) {
			this.volumeClaimed = this.volumeClaimed + rs.getBigDecimal("volume_claimed").doubleValue();
			this.expenseVolume = this.expenseVolume + rs.getBigDecimal("passthru_volume").doubleValue();
			// since we're sorting by claim week, the last row in the result has the volume remainin we want
			this.volumeRemaining = rs.getBigDecimal("volume_remaining").doubleValue(); 
		}
		rs.close();
	}
	
	

	public Integer getTicketId() {
		return ticketId;
	}

	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}

	public Double getTotalVolume() {
		return totalVolume;
	}
	public void setTotalVolume(Double totalVolume) {
		this.totalVolume = totalVolume;
	}
	public Double getVolumeClaimed() {
		return volumeClaimed;
	}
	public void setVolumeClaimed(Double volumeClaimed) {
		this.volumeClaimed = volumeClaimed;
	}
	public Double getExpenseVolume() {
		return expenseVolume;
	}
	public void setExpenseVolume(Double expenseVolume) {
		this.expenseVolume = expenseVolume;
	}
	public Double getVolumeRemaining() {
		return volumeRemaining;
	}
	public void setVolumeRemaining(Double volumeRemaining) {
		this.volumeRemaining = volumeRemaining;
	}
	
}
