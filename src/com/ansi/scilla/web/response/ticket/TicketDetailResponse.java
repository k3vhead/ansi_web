package com.ansi.scilla.web.response.ticket;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.web.response.ticket.TicketReturnRecord.NextAllowedStatusItem;

public class TicketDetailResponse extends ApplicationObject {
	
	private Ticket ticket;
	private Integer status;
	private Division division;
	private Date processDate;
	private String processNotes;
	private BigDecimal actDl;
	private BigDecimal actDlPct;
	private BigDecimal actPricePerCleaning;
	private Integer billSheet;
	private Integer customerSignature;
	private Integer mgrApproval;
	private List<NextAllowedStatusItem> nextAllowedStatusList;
	private Integer jobId; // - passed to job panels;
	private BigDecimal actPpc;
	private BigDecimal actTax;
	private BigDecimal sumTktPpcPaid; // - sum(ticket_payment.amount);
	private BigDecimal sumTktTaxPaid; // - sum(ticket_payment.tax_amt);
	private BigDecimal balance; // (actPpc + actTax - (sumTcktPpcPaid + sumTktTaxPaid));
	private Integer daysToPay; //(today, invoiceDate, balance);
//	  					if balance == 0, daysToPay = max(paymentDate)-invoiceDate;
//	  					if balance <> 0, daysToPay = today - invoiceDate;
//	  			**ticket write off amount - stub for v 2.0;
//	
	public TicketDetailResponse(){
		super();
	}
	
	public Ticket getTicket() {
		return ticket;
	}
	
	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Division getDivision() {
		return division;
	}
	
	public void setDivision(Division division) {
		this.division = division;
	}
	
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
	
	public BigDecimal getActPricePerCleaning() {
		return actPricePerCleaning;
	}
	
	public void setActPricePerCleaning(BigDecimal actPricePerCleaning) {
		this.actPricePerCleaning = actPricePerCleaning;
	}
	
	public Integer getBillSheet() {
		return billSheet;
	}
	
	public void setBillSheet(Integer billSheet) {
		this.billSheet = billSheet;
	}
	
	public Integer getCustomerSignature() {
		return customerSignature;
	}
	
	public void setCustomerSignature(Integer customerSignature) {
		this.customerSignature = customerSignature;
	}
	
	public Integer getMgrApproval() {
		return mgrApproval;
	}
	
	public void setMgrApproval(Integer mgrApproval) {
		this.mgrApproval = mgrApproval;
	}
	
	public List<NextAllowedStatusItem> getNextAllowedStatusList() {
		return nextAllowedStatusList;
	}
	
	public void setNextAllowedStatusList(List<NextAllowedStatusItem> nextAllowedStatusList) {
		this.nextAllowedStatusList = nextAllowedStatusList;
	}
	
	public Integer getJobId() {
		return jobId;
	}
	
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	
	public BigDecimal getActPpc() {
		return actPpc;
	}
	
	public void setActPpc(BigDecimal actPpc) {
		this.actPpc = actPpc;
	}
	
	public BigDecimal getActTax() {
		return actTax;
	}
	
	public void setActTax(BigDecimal actTax) {
		this.actTax = actTax;
	}
	
	public BigDecimal getSumTktPpcPaid() {
		return sumTktPpcPaid;
	}
	
	public void setSumTktPpcPaid(BigDecimal sumTktPpcPaid) {
		this.sumTktPpcPaid = sumTktPpcPaid;
	}
	
	public BigDecimal getSumTktTaxPaid() {
		return sumTktTaxPaid;
	}
	
	public void setSumTktTaxPaid(BigDecimal sumTktTaxPaid) {
		this.sumTktTaxPaid = sumTktTaxPaid;
	}
	
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
	
}
