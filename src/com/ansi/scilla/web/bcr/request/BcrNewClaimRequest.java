package com.ansi.scilla.web.bcr.request;

import java.sql.Connection;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.response.WebMessages;

public class BcrNewClaimRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String TICKET_ID = "ticketId";
	public static final String SERVICE_TYPE_ID = "serviceTypeId";
	public static final String CLAIM_WEEK = "claimWeek";
	public static final String DL_AMT = "dlAmt";
	public static final String EXPENSE_VOLUME = "expenseVolume";
	public static final String VOLUME_CLAIMED = "volumeClaimed";
	public static final String EXPENSE_TYPE = "expenseType";
	public static final String EMPLOYEE = "employee";
	public static final String NOTES = "notes";
	
	private Integer ticketId;
	private Integer serviceTypeId;
	private String claimWeek;	// format: yyyy-nn
	private Double dlAmt;
	private Double expenseVolume;
	private Double volumeClaimed;
	private String expenseType;
	private String employee;
	private String notes;
	
	
	public Integer getTicketId() {
		return ticketId;
	}
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	public Integer getServiceTypeId() {
		return serviceTypeId;
	}
	public void setServiceTypeId(Integer serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}
	public String getClaimWeek() {
		return claimWeek;
	}
	public void setClaimWeek(String claimWeek) {
		this.claimWeek = claimWeek;
	}
	public Double getDlAmt() {
		return dlAmt;
	}
	public void setDlAmt(Double dlAmt) {
		this.dlAmt = dlAmt;
	}
	public Double getExpenseVolume() {
		return expenseVolume;
	}
	public void setExpenseVolume(Double expenseVolume) {
		this.expenseVolume = expenseVolume;
	}
	public Double getVolumeClaimed() {
		return volumeClaimed;
	}
	public void setVolumeClaimed(Double volumeClaimed) {
		this.volumeClaimed = volumeClaimed;
	}
	public String getExpenseType() {
		return expenseType;
	}
	public void setExpenseType(String expenseType) {
		this.expenseType = expenseType;
	}
	public String getEmployee() {
		return employee;
	}
	public void setEmployee(String employee) {
		this.employee = employee;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
	public WebMessages validate(Connection conn) {
		WebMessages webMessages = new WebMessages();
		return webMessages;
	}
	
	
}
