package com.ansi.scilla.web.bcr.request;

import java.sql.Connection;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.JobTag;
import com.ansi.scilla.common.db.TicketClaim;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class BcrExpenseRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String TICKET_ID = "ticketId";
	public static final String SERVICE_TAG_ID = "serviceTagId";
	public static final String CLAIM_WEEK = "claimWeek";
	public static final String VOLUME = "volume";
	public static final String EXPENSE_TYPE = "expenseType";
	public static final String NOTES = "notes";
	public static final String DIVISION_ID = "divisionId";
	public static final String WORK_YEAR = "workYear";
	public static final String WORK_WEEKS = "workWeeks";
	public static final String CLAIM_ID = "claimId";
	
	private Integer divisionId;
	private Integer ticketId;
	private Integer serviceTagId;
	private String claimWeek;
	private Double volume;
	private String expenseType;
	private String notes;
	private Integer workYear;
	private String workWeeks;
	
	
	
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	public Integer getTicketId() {
		return ticketId;
	}
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	
	public Integer getServiceTagId() {
		return serviceTagId;
	}
	public void setServiceTagId(Integer serviceTagId) {
		this.serviceTagId = serviceTagId;
	}
	public String getClaimWeek() {
		return claimWeek;
	}
	public void setClaimWeek(String claimWeek) {
		this.claimWeek = claimWeek;
	}
	public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	public String getExpenseType() {
		return expenseType;
	}
	public void setExpenseType(String expenseType) {
		this.expenseType = expenseType;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}	
	public Integer getWorkYear() {
		return workYear;
	}
	public void setWorkYear(Integer workYear) {
		this.workYear = workYear;
	}
	
	
	public String getWorkWeeks() {
		return workWeeks;
	}
	public void setWorkWeeks(String workWeeks) {
		this.workWeeks = workWeeks;
	}
	public WebMessages validateAdd(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		/*
		WorkYear workYear = new WorkYear(selectedDate.get(Calendar.YEAR));
		
		Calendar firstOfMonth = workYear.getFirstOfMonth(selectedDate);
		Calendar lastOfMonth = workYear.getLastOfMonth(selectedDate);
		List<WorkWeek> workCalendar = BcrUtils.makeWorkCalendar(firstOfMonth, lastOfMonth);
		
		boolean isValidClaimWeek = false;
		for ( WorkWeek workWeek : workCalendar ) {
			String weekOfYear = workWeek.getWeekOfYear() < 10 ? "0" + String.valueOf(workWeek.getWeekOfYear()) : String.valueOf(workWeek.getWeekOfYear());
			String testWeek = workYear.get(Calendar.YEAR)+ "-" + weekOfYear;
			if ( this.claimWeek.equals(testWeek) ) {
				isValidClaimWeek = true;
			}
		}
		if ( ! isValidClaimWeek ) {
			webMessages.addMessage(CLAIM_WEEK, "Invalid Claim Week");
		}
		*/
		
		
		
		RequestValidator.validateNumber(webMessages, WORK_YEAR, this.workYear, null, null, true, "Year");
		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, DIVISION_ID, this.divisionId, true, "Division");
		RequestValidator.validateClaimWeek(webMessages, CLAIM_WEEK, this.claimWeek, true);
		RequestValidator.validatePassthruExpenseType(conn, webMessages, EXPENSE_TYPE, this.expenseType, true, "Expense Type");		
		RequestValidator.validateDouble(webMessages, VOLUME, this.volume, null, null, true, "Volume");		
		RequestValidator.validateId(conn, webMessages, JobTag.TABLE, JobTag.TAG_ID, SERVICE_TAG_ID, this.serviceTagId, true, "Service Tag");
		RequestValidator.validateTicketId(conn, webMessages, TICKET_ID, this.ticketId, true, "Ticket");
		RequestValidator.validateString(webMessages, NOTES, this.notes, 1000, false, "Note");

		return webMessages;
	}
	
	public WebMessages validateUpdate(Connection conn, Integer claimId) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateId(conn, webMessages, TicketClaim.TABLE, TicketClaim.CLAIM_ID, CLAIM_ID, claimId, true, "Claim ID");
		RequestValidator.validateNumber(webMessages, WORK_YEAR, this.workYear, null, null, true, "Year");
		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, DIVISION_ID, this.divisionId, true, "Division");
		RequestValidator.validateClaimWeek(webMessages, CLAIM_WEEK, this.claimWeek, true);
		RequestValidator.validatePassthruExpenseType(conn, webMessages, EXPENSE_TYPE, this.expenseType, true, "Expense Type");		
		RequestValidator.validateDouble(webMessages, VOLUME, this.volume, null, null, true, "Volume");		
		RequestValidator.validateId(conn, webMessages, JobTag.TABLE, JobTag.TAG_ID, SERVICE_TAG_ID, this.serviceTagId, true, "Service Tag");
		RequestValidator.validateTicketId(conn, webMessages, TICKET_ID, this.ticketId, true, "Ticket");
		RequestValidator.validateString(webMessages, NOTES, this.notes, 1000, false, "Note");
		return webMessages;
	}
	
	
	public static WebMessages validateDelete(Connection conn, Integer claimId) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateId(conn, webMessages, TicketClaim.TABLE, TicketClaim.CLAIM_ID, CLAIM_ID, claimId, true, "Claim ID");		
		return webMessages;
	}
}
