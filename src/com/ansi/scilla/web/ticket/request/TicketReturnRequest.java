package com.ansi.scilla.web.ticket.request;

import java.math.BigDecimal;
import java.util.Date;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 
 * @author ggroce
 *
 *
 *
 */

public class TicketReturnRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	public static final String ACT_DL_AMT = "actDlAmt";
	public static final String ACT_DL_PCT = "actDlPct";
	public static final String ACT_PRICE_PER_CLEANING = "actPricePerCleaning";
	public static final String BILL_SHEET = "billSheet";
	public static final String CUSTOMER_SIGNATURE = "customerSignature";
	public static final String MGR_APPROVAL = "mgrApproval";
	public static final String NEW_STATUS = "newStatus";
	public static final String PROCESS_DATE = "processDate";
	public static final String PROCESS_NOTES = "processNotes";
	public static final String STATUS = "status";
	public static final String TICKET_ID = "ticketId";

	
	
	private BigDecimal actDlAmt;
	private BigDecimal actDlPct;
	private BigDecimal actPricePerCleaning;
	private Boolean billSheet;
	private Boolean customerSignature;
	private Boolean mgrApproval;
	private String newStatus;
	private Date processDate;
	private String processNotes;
	private String status;
	private Integer ticketId;
	private String actPoNumber;
	
	public TicketReturnRequest() {
		super();
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

	public void setBillSheet(Boolean billSheet) {
		this.billSheet = billSheet;
	}
	
	public Boolean getBillSheet() {
		return this.billSheet;
	}

	public void setCustomerSignature(Boolean customerSignature) {
		this.customerSignature = customerSignature;
	}
	
	public Boolean getCustomerSignature() {
		return this.customerSignature;
	}

	public void setMgrApproval(Boolean mgrApproval) {
		this.mgrApproval = mgrApproval;
	}
	
	public Boolean getMgrApproval() {
		return this.mgrApproval;
	}

	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}
	
	public String getNewStatus() {
		return this.newStatus;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")	
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

	public String getActPoNumber() {
		return actPoNumber;
	}

	public void setActPoNumber(String actPoNumber) {
		this.actPoNumber = actPoNumber;
	}



}
