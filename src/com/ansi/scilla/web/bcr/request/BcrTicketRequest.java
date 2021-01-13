package com.ansi.scilla.web.bcr.request;

import java.sql.Connection;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.struts.SessionUser;

public class BcrTicketRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	public static final String TICKET_ID = "ticketId";
	public static final String DL_AMT  = "dlAmt";
	public static final String TOTAL_VOLUME  = "totalVolume";
	public static final String VOLUME_CLAIMED  = "volumeClaimed";
	public static final String NOTES  = "notes";
	public static final String BILLED_AMOUNT  = "billedAmount";
	public static final String EMPLOYEE  = "employee";
	public static final String CLAIM_WEEK  = "claimWeek";
	
	
	private Integer ticketId;
	private Double dlAmt;
	private Double totalVolume;
	private Double volumeClaimed;
	private String notes;
	private Double billedAmount;
	private String employee;
	private String claimWeek;
	
	public Integer getTicketId() {
		return ticketId;
	}
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	public Double getDlAmt() {
		return dlAmt;
	}
	public void setDlAmt(Double dlAmt) {
		this.dlAmt = dlAmt;
	}
	public Double getTotalVolume() {
		return totalVolume;
	}
	public void setTotalVolume(Double totalVolume) {
		this.totalVolume = totalVolume;
	}
	public Double getVolumeClaimed() {
		return volumeClaimed;
	}
	public void setVolumeClaimed(Double volumeClaimed) {
		this.volumeClaimed = volumeClaimed;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public Double getBilledAmount() {
		return billedAmount;
	}
	public void setBilledAmount(Double billedAmount) {
		this.billedAmount = billedAmount;
	}
	public String getEmployee() {
		return employee;
	}
	public void setEmployee(String employee) {
		this.employee = employee;
	}
	public String getClaimWeek() {
		return claimWeek;
	}
	public void setClaimWeek(String claimWeek) {
		this.claimWeek = claimWeek;
	}

	
	public WebMessages validate(Connection conn, SessionUser sessionUser) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateTicketId(conn, webMessages, TICKET_ID, this.ticketId, true);
		RequestValidator.validateNumber(webMessages, DL_AMT, this.dlAmt, 0.0D, null, true);
		RequestValidator.validateNumber(webMessages, TOTAL_VOLUME, this.totalVolume, 0.0D, null, true);
		RequestValidator.validateNumber(webMessages, VOLUME_CLAIMED, this.volumeClaimed, 0.0D, null, true);
		RequestValidator.validateNumber(webMessages, BILLED_AMOUNT, this.billedAmount, 0.0D, null, true);

//		private String claimWeek;
		
		return webMessages;
	}
}
