package com.ansi.scilla.web.response.ticket;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.jobticket.TicketStatus;

public class TicketDetail extends ApplicationObject { //TicketPaymentTotal populate from
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer ticketId;
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
	public TicketDetail(){
		super();
	}
	
	public Integer getTicketId() {
		return ticketId;
	}
	
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
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
