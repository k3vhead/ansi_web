package com.ansi.scilla.web.response.payment;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.ansi.scilla.common.queries.PaymentTotals;
import com.ansi.scilla.common.queries.ReportQuery;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thewebthing.commons.db2.DBColumn;

public class PaymentTotalsResponseItem extends ReportQuery {

	private static final long serialVersionUID = 1L;

	public static final String PAYMENT_ID = "payment_id";
	public static final String PAYMENT_AMOUNT = "payment_amount";
	public static final String PAYMENT_DATE = "payment_date";
	public static final String PAYMENT_TYPE = "payment_type";
	public static final String PAYMENT_NOTE = "payment_note";
	public static final String CHECK_NBR = "check_nbr";
	public static final String CHECK_DATE = "check_date";
	public static final String APPLIED_AMOUNT = "applied_amount";
	public static final String APPLIED_TAX_AMT = "applied_tax_amt";
	public static final String APPLIED_TOTAL = "applied_total";
	public static final String AVAILABLE = "available";
	
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
	private BigDecimal appliedAmount;
	private BigDecimal appliedTaxAmt;
	private BigDecimal appliedTotal;
	private BigDecimal available;
	
	
	public PaymentTotalsResponseItem() {
		super();
	}
	public PaymentTotalsResponseItem(ResultSet rs) throws SQLException {
		this();
		this.paymentId = rs.getInt(PAYMENT_ID);
		this.paymentAmount = rs.getBigDecimal(PAYMENT_AMOUNT);
		this.paymentDate = rs.getDate(PAYMENT_DATE);
		this.paymentType = rs.getString(PAYMENT_TYPE);
		this.paymentNote = rs.getString(PAYMENT_NOTE);
		this.checkNbr = rs.getString(CHECK_NBR);
		this.checkDate = rs.getDate(CHECK_DATE);
		this.appliedAmount = rs.getBigDecimal(APPLIED_AMOUNT);
		this.appliedTaxAmt = rs.getBigDecimal(APPLIED_TAX_AMT);
		this.appliedTotal = rs.getBigDecimal(APPLIED_TOTAL);
		this.available = rs.getBigDecimal(AVAILABLE);
	}
	public PaymentTotalsResponseItem(PaymentTotals paymentTotals) throws SQLException {
		this();
		this.paymentId = paymentTotals.getPaymentId();
		this.paymentAmount = paymentTotals.getPaymentAmount();
		this.paymentDate = paymentTotals.getPaymentDate();
		this.paymentType = paymentTotals.getPaymentType();
		this.paymentNote = paymentTotals.getPaymentNote();
		this.checkNbr = paymentTotals.getCheckNbr();
		this.checkDate = paymentTotals.getCheckDate();
		this.appliedAmount = paymentTotals.getAppliedAmount();
		this.appliedTaxAmt = paymentTotals.getAppliedTaxAmt();
		this.appliedTotal = paymentTotals.getAppliedTotal();
		this.available = paymentTotals.getAvailable();
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
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	@DBColumn(APPLIED_AMOUNT)
	public BigDecimal getAppliedAmount() {
		return appliedAmount;
	}
	@DBColumn(APPLIED_AMOUNT)
	public void setAppliedAmount(BigDecimal appliedAmount) {
		this.appliedAmount = appliedAmount;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	@DBColumn(APPLIED_TAX_AMT)
	public BigDecimal getAppliedTaxAmt() {
		return appliedTaxAmt;
	}
	@DBColumn(APPLIED_TAX_AMT)
	public void setAppliedTaxAmt(BigDecimal appliedTaxAmt) {
		this.appliedTaxAmt = appliedTaxAmt;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	@DBColumn(APPLIED_TOTAL)
	public BigDecimal getAppliedTotal() {
		return appliedTotal;
	}
	@DBColumn(APPLIED_TOTAL)
	public void setAppliedTotal(BigDecimal appliedTotal) {
		this.appliedTotal = appliedTotal;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	@DBColumn(AVAILABLE)
	public BigDecimal getAvailable() {
		return available;
	}
	@DBColumn(AVAILABLE)
	public void setAvailable(BigDecimal available) {
		this.available = available;
	}

	
	
}