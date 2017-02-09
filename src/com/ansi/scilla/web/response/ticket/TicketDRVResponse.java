package com.ansi.scilla.web.response.ticket;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.queries.TicketDRVQuery;
import com.ansi.scilla.web.response.MessageResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TicketDRVResponse extends MessageResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date startDate;
	private Date endDate;
	private Integer ticketCount;
	private Division division;
	private Date runDate;
	private BigDecimal totalVolume;
	private BigDecimal totalDL;
	private List<TicketDRVResponseItem> responseItemList;
	
	
	/*
	public TicketDRVResponse(Connection conn, Integer divisionId, Date startDate) throws RecordNotFoundException, Exception{
		division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);
	}
	*/
	
	public TicketDRVResponse() {
		super();
	}
	
	public TicketDRVResponse(Connection conn, Integer divisionId, Integer month, Integer year) throws RecordNotFoundException, Exception{
		this();
		division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);
		
		Calendar calendar = Calendar.getInstance();
		runDate = calendar.getTime();
		calendar.clear();
		calendar.set(year, month-1, 1);
		startDate = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
		endDate = calendar.getTime();
		
		responseItemList = new ArrayList<TicketDRVResponseItem>();
		List<TicketDRVQuery> queryList = TicketDRVQuery.makeMonthlyReport(conn, divisionId, month, year);
		ticketCount = queryList.size();
		totalVolume = new BigDecimal(0);
		totalDL = new BigDecimal(0);
		for(TicketDRVQuery query : queryList){
			TicketDRVResponseItem item = new TicketDRVResponseItem(query);
			responseItemList.add(item);
			totalVolume = totalVolume.add(query.getPricePerCleaning());
			totalDL = totalDL.add(query.getBudget());
		}
		
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public Date getStartDate() {
		return startDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public Date getEndDate() {
		return endDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
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
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy hh:mm:ss")
	public Date getRunDate() {
		return runDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy hh:mm:ss")
	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}
	public BigDecimal getTotalVolume() {
		return totalVolume;
	}
	public void setTotalVolume(BigDecimal totalVolume) {
		this.totalVolume = totalVolume;
	}
	public BigDecimal getTotalDL() {
		return totalDL;
	}
	public void setTotalDL(BigDecimal totalDL) {
		this.totalDL = totalDL;
	}

	public List<TicketDRVResponseItem> getResponseItemList() {
		return responseItemList;
	}

	public void setResponseItemList(List<TicketDRVResponseItem> responseItemList) {
		this.responseItemList = responseItemList;
	}

	
}
