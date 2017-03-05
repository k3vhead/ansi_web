package com.ansi.scilla.web.request;

import java.math.BigDecimal;
//import java.util.Date;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 
 * @author ggorce
 *
 *
 *
 */

public class TicketReturnRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	public static final String POST_ACTION_IS_COMPLETE = "complete";
	public static final String POST_ACTION_IS_SKIP = "skip";
	public static final String POST_ACTION_IS_VOID = "void";
	public static final String POST_ACTION_IS_REJECT = "reject";
	public static final String POST_ACTION_IS_REQUEUE = "requeue";
	
	private String action;
	private BigDecimal actDlAmt;
	private BigDecimal actDlPct;
	private BigDecimal actPricePerCleaning;
	private Integer billSheet;
	private Integer customerSignature;
	private Integer mgrApproval;
	private String newStatus;
	private Date processDate;
	private String processNotes;
	private String status;
	private Integer ticketId;
	private String panel;
	
	public TicketReturnRequest() {
		super();
	}
		
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setActDlAmt(BigDecimal actDlAmt) {
		this.actDlAmt = actDlAmt;
	}
	
	public BigDecimal getActDlAmt() {
		return this.actDlAmt;
	}

	public void setActDlPct(BigDecimal actDlPct) {
		this.actDlPct = actDlPct;
	}
	
	public BigDecimal getActDlPct() {
		return this.actDlPct;
	}

	public void setActPricePerCleaning(BigDecimal actPricePerCleaning) {
		this.actPricePerCleaning = actPricePerCleaning;
	}
	
	public BigDecimal getActPricePerCleaning() {
		return this.actPricePerCleaning;
	}

	public void setBillSheet(Integer billSheet) {
		this.billSheet = billSheet;
	}
	
	public Integer getBillSheet() {
		return this.billSheet;
	}

	public void setCustomerSignature(Integer customerSignature) {
		this.customerSignature = customerSignature;
	}
	
	public Integer getCustomerSignature() {
		return this.customerSignature;
	}

	public void setMgrApproval(Integer mgrApproval) {
		this.mgrApproval = mgrApproval;
	}
	
	public Integer getMgrApproval() {
		return this.mgrApproval;
	}

	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}
	
	public String getNewStatus() {
		return this.newStatus;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}
	
	public Date getProcessDate() {
		return this.processDate;
	}

	public void setProcessNotes(String processNotes) {
		this.processNotes = processNotes;
	}
	
	public String getProcessNotes() {
		return this.processNotes;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return this.status;
	}

	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	
	public Integer getTicketId() {
		return this.ticketId;
	}

	public String getPanel() {
		return panel;
	}

	public void setPanel(String panel) {
		this.panel = panel;
	}


}
