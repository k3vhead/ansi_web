package com.ansi.scilla.web.response.invoice;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.ansi.scilla.common.queries.InvoiceSearch;
import com.ansi.scilla.common.queries.ReportQuery;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thewebthing.commons.db2.DBColumn;

public class InvoiceLookupResponseItem extends ReportQuery {

	private static final long serialVersionUID = 1L;

	public static final String INVOICE_ID = "invoice_id";
	public static final String FLEETMATICS_INVOICE_NBR = "fleetmatics_invoice_nbr";
	public static final String INVOICE_AMOUNT = "invoice_amount";
	public static final String INVOICE_TAX = "invoice_tax";
	public static final String INVOICE_TOTAL = "invoice_total";
	public static final String INVOICE_PAID = "invoice_paid";
	public static final String INVOICE_BALANCE = "invoice_balance";
	public static final String TICKET_COUNT = "ticket_count";
	public static final String INVOICE_DATE = "invoice_date";
	public static final String BILL_TO_NAME = "bill_to_name";
	public static final String DIV = "div";
	public static final String PRINT_COUNT = "print_count";
	
	/*
	 invoice.invoice_id, sum(ticket.act_price_per_cleaning) as invoice_total, " + 
		" count(ticket.ticket_id) as ticket_count, ticket.invoice_date, bill_to.bill_to_name, " + 
		" concat(division.division_nbr, '-', division.division_code) as div
	 */
	private Integer invoiceId;
	private String fleetmaticsInvoiceNbr;
	private BigDecimal invoiceAmount;
	private BigDecimal invoiceTax;
	private BigDecimal invoiceTotal;
	private BigDecimal invoicePaid;
	private BigDecimal invoiceBalance;
	private Integer ticketCount;
	private Date invoiceDate;
	private String billToName;
	private String div;
	private Integer printCount;
	
	
	public InvoiceLookupResponseItem() {
		super();
	}
	public InvoiceLookupResponseItem(InvoiceSearch invoiceSearch) {
		this();
		this.invoiceId = invoiceSearch.getInvoiceId();
		this.fleetmaticsInvoiceNbr = invoiceSearch.getFleetmaticsInvoiceNbr();
		this.invoiceAmount = invoiceSearch.getInvoiceAmount();
		this.invoiceTax = invoiceSearch.getInvoiceTax();
		this.invoiceTotal = invoiceSearch.getInvoiceTotal();
		this.invoicePaid = invoiceSearch.getInvoicePaid();
		this.invoiceBalance = invoiceSearch.getInvoiceBalance();
		this.ticketCount = invoiceSearch.getTicketCount();
		this.invoiceDate = invoiceSearch.getInvoiceDate();
		this.billToName = invoiceSearch.getBillToName();
		this.div = invoiceSearch.getDiv();
		this.printCount=invoiceSearch.getPrintCount();		
	}
	
	public InvoiceLookupResponseItem(ResultSet rs) throws SQLException {
		this();
		this.invoiceId = rs.getInt(INVOICE_ID);
		this.fleetmaticsInvoiceNbr = rs.getString(FLEETMATICS_INVOICE_NBR);
		this.invoiceAmount = rs.getBigDecimal(INVOICE_AMOUNT);
		this.invoiceTax = rs.getBigDecimal(INVOICE_TAX);
		this.invoiceTotal = rs.getBigDecimal(INVOICE_TOTAL);
		this.invoicePaid = rs.getBigDecimal(INVOICE_PAID);
		this.invoiceBalance = rs.getBigDecimal(INVOICE_BALANCE);
		this.ticketCount = rs.getInt(TICKET_COUNT);
		this.invoiceDate = rs.getDate(INVOICE_DATE);
		this.billToName = rs.getString(BILL_TO_NAME);
		this.div = rs.getString(DIV);
		this.printCount=rs.getInt(PRINT_COUNT);
	}
	@DBColumn(INVOICE_ID)
	public Integer getInvoiceId() {
		return invoiceId;
	}
	@DBColumn(INVOICE_ID)
	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}
	@DBColumn(FLEETMATICS_INVOICE_NBR)
	public String getFleetmaticsInvoiceNbr() {
		return fleetmaticsInvoiceNbr;
	}
	@DBColumn(FLEETMATICS_INVOICE_NBR)
	public void setFleetmaticsInvoiceNbr(String fleetmaticsInvoiceNbr) {
		this.fleetmaticsInvoiceNbr = fleetmaticsInvoiceNbr;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	@DBColumn(INVOICE_AMOUNT)
	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}
	@DBColumn(INVOICE_AMOUNT)
	public void setInvoiceAmount(BigDecimal invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	@DBColumn(INVOICE_TAX)
	public BigDecimal getInvoiceTax() {
		return invoiceTax;
	}
	@DBColumn(INVOICE_TAX)
	public void setInvoiceTax(BigDecimal invoiceTax) {
		this.invoiceTax = invoiceTax;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	@DBColumn(INVOICE_TOTAL)
	public BigDecimal getInvoiceTotal() {
		return invoiceTotal;
	}
	@DBColumn(INVOICE_TOTAL)
	public void setInvoiceTotal(BigDecimal invoiceTotal) {
		this.invoiceTotal = invoiceTotal;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	@DBColumn(INVOICE_PAID)
	public BigDecimal getInvoicePaid() {
		return invoicePaid;
	}
	@DBColumn(INVOICE_PAID)
	public void setInvoicePaid(BigDecimal invoicePaid) {
		this.invoicePaid = invoicePaid;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	@DBColumn(INVOICE_BALANCE)
	public BigDecimal getInvoiceBalance() {
		return invoiceBalance;
	}
	@DBColumn(INVOICE_BALANCE)
	public void setInvoiceBalance(BigDecimal invoiceBalance) {
		this.invoiceBalance = invoiceBalance;
	}
	@DBColumn(TICKET_COUNT)
	public Integer getTicketCount() {
		return ticketCount;
	}
	@DBColumn(TICKET_COUNT)
	public void setTicketCount(Integer ticketCount) {
		this.ticketCount = ticketCount;
	}
	@JsonSerialize(using=AnsiDateFormatter.class)
	@DBColumn(INVOICE_DATE)
	public Date getInvoiceDate() {
		return invoiceDate;
	}
	@DBColumn(INVOICE_DATE)
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	@DBColumn(BILL_TO_NAME)
	public String getBillToName() {
		return billToName;
	}
	@DBColumn(BILL_TO_NAME)
	public void setBillToName(String billToName) {
		this.billToName = billToName;
	}
	@DBColumn(DIV)
	public String getDiv() {
		return div;
	}
	@DBColumn(DIV)
	public void setDiv(String div) {
		this.div = div;
	}
	public Integer getPrintCount() {
		return printCount;
	}
	public void setPrintCount(Integer printCount) {
		this.printCount = printCount;
	}
	
	
}
