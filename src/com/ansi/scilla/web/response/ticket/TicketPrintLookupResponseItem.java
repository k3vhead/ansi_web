package com.ansi.scilla.web.response.ticket;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.jsonFormat.AnsiIntegerFormatter;
import com.ansi.scilla.common.queries.ReportQuery;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thewebthing.commons.db2.DBColumn;

public class TicketPrintLookupResponseItem extends ReportQuery {

	private static final long serialVersionUID = 1L;

	public static final String DIVISION_ID = "division_id";
	public static final String DIV = "div";
	public static final String TICKET_COUNT = "ticket_count";
	public static final String TOTAL_PPC = "total_ppc";
	
	private Integer divisionId;
	private String div;
	private Integer ticketCount;
	private BigDecimal totalPpc;
	
	public TicketPrintLookupResponseItem(ResultSetMetaData rsmd, ResultSet rs) throws Exception {
		super();
		super.rs2Object(this, rsmd, rs);
	}
	@DBColumn(DIVISION_ID)
	public Integer getDivisionId() {
		return divisionId;
	}
	@DBColumn(DIVISION_ID)
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	@DBColumn(DIV)
	public String getDiv() {
		return div;
	}
	@DBColumn(DIV)
	public void setDiv(String div) {
		this.div = div;
	}
	@DBColumn(TICKET_COUNT)
	@JsonSerialize(using=AnsiIntegerFormatter.class)
	public Integer getTicketCount() {
		return ticketCount;
	}
	@DBColumn(TICKET_COUNT)
	public void setTicketCount(Integer ticketCount) {
		this.ticketCount = ticketCount;
	}
	@DBColumn(TOTAL_PPC)
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getTotalPpc() {
		return totalPpc;
	}
	@DBColumn(TOTAL_PPC)
	public void setTotalPpc(BigDecimal totalPpc) {
		this.totalPpc = totalPpc;
	}
	
	
}
