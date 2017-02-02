package com.ansi.scilla.web.response.ticket;

import java.sql.Connection;
import java.util.Date;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.response.MessageResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TicketDRVResponse extends MessageResponse {

	private Date startDate;
	private Date endDate;
	private Integer ticketCount;
	private Division division;
	private Date runDate;
	private Double totalVolume;
	private Double totalDL;
	
	public TicketDRVResponse(Connection conn, Integer divisionId, Date startDate) throws RecordNotFoundException, Exception{
		division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);
	}
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Integer getTicketCount() {
		return ticketCount;
	}
	public void setTicketCount(Integer ticketCount) {
		this.ticketCount = ticketCount;
	}
	public Division getDivision() {
		return division;
	}
	public void setDivision(Division division) {
		this.division = division;
	}
	public Date getRunDate() {
		return runDate;
	}
	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}
	public Double getTotalVolume() {
		return totalVolume;
	}
	public void setTotalVolume(Double totalVolume) {
		this.totalVolume = totalVolume;
	}
	public Double getTotalDL() {
		return totalDL;
	}
	public void setTotalDL(Double totalDL) {
		this.totalDL = totalDL;
	}
	
	
}
