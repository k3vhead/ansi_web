package com.ansi.scilla.web.response.ticket;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.ansi.scilla.common.queries.TicketPaymentTotals;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TicketDetail extends ApplicationObject { //TicketPaymentTotal populate from
	
	/**
	 * 
	 */
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
	private Boolean billSheet;
	private Boolean customerSignature;
	private Boolean mgrApproval;
	private List<TicketStatus> nextAllowedStatusList;
	private Integer jobId; // - passed to job panels;
	private BigDecimal actTax;
	private BigDecimal totalVolPaid; // - sum(ticket_payment.amount);
	private BigDecimal totalTaxPaid; // - sum(ticket_payment.tax_amt);
	private BigDecimal balance; // (actPpc + actTax - (sumTcktPpcPaid + sumTktTaxPaid));
	private Integer daysToPay; //(today, invoiceDate, balance);
//	  					if balance == 0, daysToPay = max(paymentDate)-invoiceDate;
//	  					if balance <> 0, daysToPay = today - invoiceDate;
//	  			**ticket write off amount - stub for v 2.0;
	private String divisionDisplay;
	
	
	public TicketDetail(){
		super();
	}
	
	public TicketDetail(Connection conn, Integer ticketId) throws RecordNotFoundException, Exception {
		TicketPaymentTotals ticketPaymentTotals = TicketPaymentTotals.select(conn, ticketId);
		this.ticketId = ticketId;
		this.invoiceId = ticketPaymentTotals.getTicket().getInvoiceId();
		this.status = ticketPaymentTotals.getTicket().getStatus();
		this.divisionId = ticketPaymentTotals.getDivisionId();
		this.divisionCode = ticketPaymentTotals.getDivisionCode();
		this.processDate = ticketPaymentTotals.getTicket().getProcessDate();
		this.processNotes = ticketPaymentTotals.getTicket().getProcessNotes();
		this.actDl = ticketPaymentTotals.getTicket().getActDlAmt();
		this.actDlPct = ticketPaymentTotals.getTicket().getActDlPct();
		this.actPricePerCleaning = ticketPaymentTotals.getTicket().getActPricePerCleaning();
		this.billSheet = ticketPaymentTotals.getTicket().getBillSheet() == 1;
		this.customerSignature = ticketPaymentTotals.getTicket().getCustomerSignature() == 1;
		this.mgrApproval = ticketPaymentTotals.getTicket().getMgrApproval() == 1;
		this.nextAllowedStatusList = TicketStatus.lookup(ticketPaymentTotals.getTicket().getStatus()).nextValues();
		this.jobId = ticketPaymentTotals.getTicket().getJobId();
		this.actTax = ticketPaymentTotals.getTicket().getActTaxAmt();
		this.totalVolPaid = ticketPaymentTotals.getTotalVolPaid();
		this.totalTaxPaid = ticketPaymentTotals.getTotalTaxPaid();
		this.balance = actPricePerCleaning.add(actTax).subtract(totalVolPaid.add(totalTaxPaid));
		//daysToPay insert HERE:***
		this.divisionDisplay = ticketPaymentTotals.getDivisionDisplay();
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
	
	public Boolean getBillSheet() {
		return billSheet;
	}
	
	public void setBillSheet(Boolean billSheet) {
		this.billSheet = billSheet;
	}
	
	public Boolean getCustomerSignature() {
		return customerSignature;
	}
	
	public void setCustomerSignature(Boolean customerSignature) {
		this.customerSignature = customerSignature;
	}
	
	public Boolean getMgrApproval() {
		return mgrApproval;
	}
	
	public void setMgrApproval(Boolean mgrApproval) {
		this.mgrApproval = mgrApproval;
	}
	
	public List<TicketStatus> getNextAllowedStatusList() {
		return nextAllowedStatusList;
	}
	
	public void setNextAllowedStatusList(List<TicketStatus> nextAllowedStatusList) {
		this.nextAllowedStatusList = nextAllowedStatusList;
	}
	
	public Integer getJobId() {
		return jobId;
	}
	
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
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
	
	public Integer getDaysToPay() {
		return daysToPay;
	}
	
	public void setDaysToPay(Integer daysToPay) {
		this.daysToPay = daysToPay;
	}

	public String getDivisionDisplay() {
		return divisionDisplay;
	}

	public void setDivisionDisplay(String divisionDisplay) {
		this.divisionDisplay = divisionDisplay;
	}
	
}
