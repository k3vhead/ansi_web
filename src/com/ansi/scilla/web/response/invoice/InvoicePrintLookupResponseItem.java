package com.ansi.scilla.web.response.invoice;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.jsonFormat.AnsiIntegerFormatter;
import com.ansi.scilla.common.queries.ReportQuery;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thewebthing.commons.db2.DBColumn;

public class InvoicePrintLookupResponseItem extends ReportQuery {

	private static final long serialVersionUID = 1L;

	public static final String DIVISION_ID = "division_id";
	public static final String DIV = "div";
	public static final String INVOICE_COUNT = "invoice_count";
	public static final String TICKET_COUNT = "ticket_count";
	public static final String TAX_TOTAL = "tax_total";
	public static final String INVOICE_TOTAL = "invoice_total";
	
	private Integer divisionId;
	private String div;
	private Integer invoiceCount;
	private Integer ticketCount;
	private BigDecimal taxTotal;
	private BigDecimal invoiceTotal;
	
	public InvoicePrintLookupResponseItem(ResultSetMetaData rsmd, ResultSet rs) throws Exception {
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
	@DBColumn(INVOICE_COUNT)
	@JsonSerialize(using=AnsiIntegerFormatter.class)
	public Integer getInvoiceCount() {
		return invoiceCount;
	}
	@DBColumn(INVOICE_COUNT)
	public void setInvoiceCount(Integer invoiceCount) {
		this.invoiceCount = invoiceCount;
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
	@DBColumn(TAX_TOTAL)
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getTaxTotal() {
		return taxTotal;
	}
	@DBColumn(TAX_TOTAL)
	public void setTaxTotal(BigDecimal taxTotal) {
		this.taxTotal = taxTotal;
	}
	@DBColumn(INVOICE_TOTAL)
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getInvoiceTotal() {
		return invoiceTotal;
	}
	@DBColumn(INVOICE_TOTAL)
	public void setInvoiceTotal(BigDecimal invoiceTotal) {
		this.invoiceTotal = invoiceTotal;
	}
	
	
}
