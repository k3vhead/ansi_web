package com.ansi.scilla.web.response.payment;
import java.math.BigDecimal;
import java.util.Date;

import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.ansi.scilla.common.queries.ReportQuery;
import com.ansi.scilla.common.queries.TicketPaymentTotals;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class PaymentTicketDetailResponseItem extends ReportQuery {

	private static final long serialVersionUID = 1L;

	private Integer ticketId;
	private Integer invoiceId;
	private String status;
	private Integer divisionId;
	private String divisionCode;
	private Date processDate;
	private String processNotes;
	private BigDecimal actDl;
	private BigDecimal actDlPct;
	private BigDecimal actPricePerCleaning;
	private BigDecimal actTax;
	private BigDecimal totalVolPaid; // - sum(ticket_payment.amount);
	private BigDecimal totalTaxPaid; // - sum(ticket_payment.tax_amt);
	private BigDecimal balance; // (actPpc + actTax - (sumTcktPpcPaid + sumTktTaxPaid));
	private BigDecimal payVolume;
	private BigDecimal payTax;
	
	
	public PaymentTicketDetailResponseItem() {
		super();
	}

	public PaymentTicketDetailResponseItem(TicketPaymentTotals ticketPaymentTotals) throws RecordNotFoundException, Exception {
		this.ticketId = ticketPaymentTotals.getTicket().getTicketId();
		this.invoiceId = ticketPaymentTotals.getTicket().getInvoiceId();
		this.status = ticketPaymentTotals.getTicket().getStatus();
		this.divisionId = ticketPaymentTotals.getDivisionId();
		this.divisionCode = ticketPaymentTotals.getDivisionCode();
		this.processDate = ticketPaymentTotals.getTicket().getProcessDate();
		this.processNotes = ticketPaymentTotals.getTicket().getProcessNotes();
		this.actDl = ticketPaymentTotals.getTicket().getActDlAmt();
		this.actDlPct = ticketPaymentTotals.getTicket().getActDlPct();
		this.actPricePerCleaning = ticketPaymentTotals.getTicket().getActPricePerCleaning();
		this.actTax = ticketPaymentTotals.getTicket().getActTaxAmt();
		this.totalVolPaid = ticketPaymentTotals.getTotalVolPaid();
		this.totalTaxPaid = ticketPaymentTotals.getTotalTaxPaid();
		this.balance = actPricePerCleaning.add(actTax).subtract(totalVolPaid.add(totalTaxPaid));
	}

	public Integer getTicketId() {
		return ticketId;
	}
	
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	
	public Integer getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Integer getDivisionId() {
		return divisionId;
	}
	
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	
	public String getDivisionCode() {
		return divisionCode;
	}

	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	@JsonSerialize(using=AnsiDateFormatter.class)
	public Date getProcessDate() {
		return processDate;
	}
	
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}
	
	public String getProcessNotes() {
		return processNotes;
	}
	
	public void setProcessNotes(String processNotes) {
		this.processNotes = processNotes;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getActDl() {
		return actDl;
	}
	
	public void setActDl(BigDecimal actDl) {
		this.actDl = actDl;
	}
	
	public BigDecimal getActDlPct() {
		return actDlPct;
	}
	
	public void setActDlPct(BigDecimal actDlPct) {
		this.actDlPct = actDlPct;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getActPricePerCleaning() {
		return actPricePerCleaning;
	}
	
	public void setActPricePerCleaning(BigDecimal actPricePerCleaning) {
		this.actPricePerCleaning = actPricePerCleaning;
	}
	
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getActTax() {
		return actTax;
	}
	
	public void setActTax(BigDecimal actTax) {
		this.actTax = actTax;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getTotalVolPaid() {
		return totalVolPaid;
	}
	
	public void setTotalVolPaid(BigDecimal totalVolPaid) {
		this.totalVolPaid = totalVolPaid;
	}
	
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getTotalTaxPaid() {
		return totalTaxPaid;
	}
	
	public void setTotalTaxPaid(BigDecimal totalTaxPaid) {
		this.totalTaxPaid = totalTaxPaid;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getBalance() {
		return balance;
	}
	
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getPayVolume() {
		return payVolume;
	}
	
	public void setPayVolume(BigDecimal payVolume) {
		this.payVolume = payVolume;
	}
	
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getPayTax() {
		return payTax;
	}
	
	public void setPayTax(BigDecimal payTax) {
		this.payTax = payTax;
	}

	
	
}
