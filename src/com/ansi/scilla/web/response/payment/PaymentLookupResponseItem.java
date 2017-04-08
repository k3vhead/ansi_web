package com.ansi.scilla.web.response.payment;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.ansi.scilla.common.queries.PaymentSearchResult;
import com.ansi.scilla.common.queries.ReportQuery;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thewebthing.commons.db2.DBColumn;

public class PaymentLookupResponseItem extends ReportQuery {

	private static final long serialVersionUID = 1L;

	public static final String PAYMENT_ID = "payment_id";
	public static final String PAYMENT_AMOUNT = "payment_amount";
	public static final String PAYMENT_DATE = "payment_date";
	public static final String PAYMENT_TYPE = "payment_type";
	public static final String PAYMENT_NOTE = "payment_note";
	public static final String CHECK_NBR = "check_nbr";
	public static final String CHECK_DATE = "check_date";
	public static final String TICKET_ID = "ticket_id";
	public static final String TICKET_AMOUNT = "ticket_amount";
	public static final String TICKET_TAX = "ticket_tax";
	public static final String TICKET_DIV = "ticket_div";
	public static final String INVOICE_ID = "invoice_id";
	public static final String BILL_TO_NAME = "bill_to_name";
	public static final String JOB_SITE_NAME = "job_site_name";
	
	/*
	 invoice.invoice_id, sum(ticket.act_price_per_cleaning) as invoice_total, " + 
		" count(ticket.ticket_id) as ticket_count, ticket.invoice_date, bill_to.bill_to_name, " + 
		" concat(division.division_nbr, '-', division.division_code) as div
	 */
	private Integer paymentId;
	private BigDecimal paymentAmount;
	private Date paymentDate;
	private String paymentType;
	private String paymentNote;
	private String checkNbr;
	private Date checkDate;
	private Integer ticketId;
	private BigDecimal ticketAmount;
	private BigDecimal ticketTax;
	private String ticketDiv;
	private Integer invoiceId;
	private String billToName;
	private String jobSiteName;
	
	
	public PaymentLookupResponseItem() {
		super();
	}
	public PaymentLookupResponseItem(ResultSet rs) throws SQLException {
		this();
		this.paymentId = rs.getInt(PAYMENT_ID);
		this.paymentAmount = rs.getBigDecimal(PAYMENT_AMOUNT);
		this.paymentDate = rs.getDate(PAYMENT_DATE);
		this.paymentType = rs.getString(PAYMENT_TYPE);
		this.paymentNote = rs.getString(PAYMENT_NOTE);
		this.checkNbr = rs.getString(CHECK_NBR);
		this.checkDate = rs.getDate(CHECK_DATE);
		this.ticketId = rs.getInt(TICKET_ID);
		this.ticketAmount = rs.getBigDecimal(TICKET_AMOUNT);
		this.ticketTax = rs.getBigDecimal(TICKET_TAX);
		this.ticketDiv = rs.getString(TICKET_DIV);
		this.invoiceId = rs.getInt(INVOICE_ID);
		this.billToName = rs.getString(BILL_TO_NAME);
		this.jobSiteName = rs.getString(JOB_SITE_NAME);
	}

	public PaymentLookupResponseItem(PaymentSearchResult result) throws IllegalAccessException, InvocationTargetException {
		this();
		BeanUtils.copyProperties(this, result);
	}
	
	
	@DBColumn(PAYMENT_ID)
	public Integer getPaymentId() {
		return paymentId;
	}
	@DBColumn(PAYMENT_ID)
	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	@DBColumn(PAYMENT_AMOUNT)
	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}
	@DBColumn(PAYMENT_AMOUNT)
	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	@JsonSerialize(using=AnsiDateFormatter.class)
	@DBColumn(PAYMENT_DATE)
	public Date getPaymentDate() {
		return paymentDate;
	}
	@DBColumn(PAYMENT_DATE)
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	@DBColumn(PAYMENT_TYPE)
	public String getPaymentType() {
		return paymentType;
	}
	@DBColumn(PAYMENT_TYPE)
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	@DBColumn(PAYMENT_NOTE)
	public String getPaymentNote() {
		return paymentNote;
	}
	@DBColumn(PAYMENT_NOTE)
	public void setPaymentNote(String paymentNote) {
		this.paymentNote = paymentNote;
	}
	@DBColumn(CHECK_NBR)
	public String getCheckNbr() {
		return checkNbr;
	}
	@DBColumn(CHECK_NBR)
	public void setCheckNbr(String checkNbr) {
		this.checkNbr = checkNbr;
	}
	@JsonSerialize(using=AnsiDateFormatter.class)
	@DBColumn(CHECK_DATE)
	public Date getCheckDate() {
		return checkDate;
	}
	@DBColumn(CHECK_DATE)
	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}
	@DBColumn(TICKET_ID)
	public Integer getTicketId() {
		return ticketId;
	}
	@DBColumn(TICKET_ID)
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	@DBColumn(TICKET_AMOUNT)
	public BigDecimal getTicketAmount() {
		return ticketAmount;
	}
	@DBColumn(TICKET_AMOUNT)
	public void setTicketAmount(BigDecimal ticketAmount) {
		this.ticketAmount = ticketAmount;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	@DBColumn(TICKET_TAX)
	public BigDecimal getTicketTax() {
		return ticketTax;
	}
	@DBColumn(TICKET_TAX)
	public void setTicketTax(BigDecimal ticketTax) {
		this.ticketTax = ticketTax;
	}
	@DBColumn(TICKET_DIV)
	public String getTicketDiv() {
		return ticketDiv;
	}
	@DBColumn(TICKET_DIV)
	public void setTicketDiv(String ticketDiv) {
		this.ticketDiv = ticketDiv;
	}
	@DBColumn(INVOICE_ID)
	public Integer getInvoiceId() {
		return invoiceId;
	}
	@DBColumn(INVOICE_ID)
	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}
	@DBColumn(BILL_TO_NAME)
	public String getBillToName() {
		return billToName;
	}
	@DBColumn(BILL_TO_NAME)
	public void setBillToName(String billToName) {
		this.billToName = billToName;
	}
	@DBColumn(JOB_SITE_NAME)
	public String getJobSiteName() {
		return jobSiteName;
	}
	@DBColumn(JOB_SITE_NAME)
	public void setJobSiteName(String jobSiteName) {
		this.jobSiteName = jobSiteName;
	}

	
	
}
