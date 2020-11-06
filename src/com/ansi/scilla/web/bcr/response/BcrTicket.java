package com.ansi.scilla.web.bcr.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.jobticket.TicketStatus;

public class BcrTicket extends ApplicationObject {

	private static final long serialVersionUID = 1L;
	
	private static final String ticketSql = "SELECT         \n" + 
			"--	  division_nbr\n" + 
			"--	, 'fm:' + ticket.fleetmatics_id\n" + 
			"	  ticket.ticket_id \n" + 
			"	, address.name \n" + 
			"--	, address.address1 \n" + 
			"--	, address.city \n" + 
			"--	, '' as item_name_spacer\n" + 
			"--	, job.job_nbr \n" + 
			"--	, ticket.start_date \n" + 
			"	, ticket.ticket_status \n" + 
			"--	, '' as item_part_no_spacer\n" + 
			"--	, job.job_frequency \n" + 
			"--	, job.price_per_cleaning as item_price_ex_tax\n" + 
			"--	, '' as item_cost_price_spacer\n" + 
			"	, job.price_per_cleaning \n" + 
			"--	, job.invoice_style \n" + 
			"--	, (	\n" + 
			"--		select top 1 process_date \n" + 
			"--		from ticket \n" + 
			"--		where ticket.job_id = job.job_id \n" + 
			"--		and ticket.process_date is not null \n" + 
			"--		order by ticket.process_date desc\n" + 
			"--	  ) as last_run     \n" + 
			"--	, cast(division_nbr as varchar) + '-' + division_code as division\n" + 
			"--	, ticket.ticket_type\n" + 
			"--	, '11/02/2020' as PriorToDate \n" + 
			"--	, GetDate() as ReportCreatedDate \n" + 
			"--	, '03/28/2020' as FirstWorkDateInMonth\n" + 
			"--	, 5 as WeeksInMonth\n" + 
			"--	, '05/01/2020' as LastWorkDateInMonth\n" + 
			"--	, 1 as ValidWeek01\n" + 
			"--	, 2 as ValidWeek02\n" + 
			"--	, 3 as ValidWeek03\n" + 
			"--	, 4 as ValidWeek04\n" + 
			"--	, 5 as ValidWeek05\n" + 
			"	, job.budget \n" + 
			"FROM ticket \n" + 
			"INNER JOIN job ON job.job_id = ticket.job_id \n" + 
			"INNER JOIN quote ON quote.quote_id = job.quote_id \n" + 
			"INNER JOIN address ON address.address_id = quote.job_site_address_id \n" + 
			"INNER JOIN division ON division.division_id = ticket.act_division_id \n" + 
			"where \n" + 
			"	-- ticket.start_date <= EOMONTH(getdate())\n" + 
			"       ticket.start_date < ?\n" + 
			"   -- and division.division_nbr >= 22 AND DIVISION.division_nbr <= 89\n" + 
			"      and division.division_id=? \n" +
			"   and ticket_type = 'job' \n" + 
			"and ticket_status in ('"+ TicketStatus.DISPATCHED.code()+"','"+TicketStatus.NOT_DISPATCHED.code()+"')\n" + 
			"--order by division_nbr, ticket.start_date asc, address.name \n" + 
			"order by ticket_id";

	private String account;
	private Integer ticketNumber;
	private Integer claimWeek;
	private String type;
	private Float directLabor;
	private Float expenses;
	private Float totalDirectLabor;
	private Float totalVolume;
	private Float totalClaimed;
	private Float volumeRemaining;
	private String notes;
	private Float billedAmount;
	private Float diffClaimBilled;
	private String ticketStatus;
	private String employee;
	
	private BcrTicket() {
		super();
	}
	
	public BcrTicket(ResultSet rs) throws SQLException {
		this();
		this.account = rs.getString("name");
		this.ticketNumber = rs.getInt("ticket_id");
//		this.claimWeek = rs.getInt("columnIndex");
//		this.type = rs.getString("columnIndex");
//		this.directLabor = rs.getFloat("columnIndex");
//		this.expenses = rs.getFloat("columnIndex");
//		this.totalDirectLabor = rs.getFloat("columnIndex");
//		this.totalVolume = rs.getFloat("columnIndex");
//		this.totalClaimed = rs.getFloat("columnIndex");
//		this.volumeRemaining = rs.getFloat("columnIndex");
//		this.notes = rs.getString("columnIndex");
		this.billedAmount = rs.getFloat("price_per_cleaning");
//		this.diffClaimBilled = rs.getFloat("columnIndex");
		this.ticketStatus = rs.getString("ticket_status");
//		this.employee = rs.getString("columnIndex");
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getTicketNumber() {
		return ticketNumber;
	}

	public void setTicketNumber(Integer ticketNumber) {
		this.ticketNumber = ticketNumber;
	}

	public Integer getClaimWeek() {
		return claimWeek;
	}

	public void setClaimWeek(Integer claimWeek) {
		this.claimWeek = claimWeek;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Float getDirectLabor() {
		return directLabor;
	}

	public void setDirectLabor(Float directLabor) {
		this.directLabor = directLabor;
	}

	public Float getExpenses() {
		return expenses;
	}

	public void setExpenses(Float expenses) {
		this.expenses = expenses;
	}

	public Float getTotalDirectLabor() {
		return totalDirectLabor;
	}

	public void setTotalDirectLabor(Float totalDirectLabor) {
		this.totalDirectLabor = totalDirectLabor;
	}

	public Float getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(Float totalVolume) {
		this.totalVolume = totalVolume;
	}

	public Float getTotalClaimed() {
		return totalClaimed;
	}

	public void setTotalClaimed(Float totalClaimed) {
		this.totalClaimed = totalClaimed;
	}

	public Float getVolumeRemaining() {
		return volumeRemaining;
	}

	public void setVolumeRemaining(Float volumeRemaining) {
		this.volumeRemaining = volumeRemaining;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Float getBilledAmount() {
		return billedAmount;
	}

	public void setBilledAmount(Float billedAmount) {
		this.billedAmount = billedAmount;
	}

	public Float getDiffClaimBilled() {
		return diffClaimBilled;
	}

	public void setDiffClaimBilled(Float diffClaimBilled) {
		this.diffClaimBilled = diffClaimBilled;
	}

	public String getTicketStatus() {
		return ticketStatus;
	}

	public void setTicketStatus(String ticketStatus) {
		this.ticketStatus = ticketStatus;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}
	
	public static List<BcrTicket> makeTicketList(Connection conn, Calendar startDate, Integer divisionId) throws SQLException {
		List<BcrTicket> ticketList = new ArrayList<BcrTicket>();
		PreparedStatement ps = conn.prepareStatement(ticketSql);
		ps.setDate(1,  new java.sql.Date(startDate.getTime().getTime()));
		ps.setInt(2,  divisionId);
		
		ResultSet rs = ps.executeQuery();
		
		while ( rs.next() ) {
			ticketList.add(new BcrTicket(rs));
		}
		rs.close();
		
		return ticketList;
	}
}
