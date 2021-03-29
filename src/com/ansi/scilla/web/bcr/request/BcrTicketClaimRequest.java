package com.ansi.scilla.web.bcr.request;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.JobTag;
import com.ansi.scilla.common.db.TicketClaim;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;

public class BcrTicketClaimRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	public static final String TICKET_ID = "ticketId";
	public static final String DL_AMT  = "dlAmt";
	public static final String TOTAL_VOLUME  = "totalVolume";
	public static final String VOLUME_CLAIMED  = "volumeClaimed";
	public static final String NOTES  = "notes";
	public static final String BILLED_AMOUNT  = "billedAmount";
	public static final String EMPLOYEE  = "employee";
	public static final String CLAIM_WEEK  = "claimWeek";
	public static final String DIVISION_ID = "divisionId";
	public static final String CLAIM_ID = "claimId";
	public static final String WORK_YEAR = "workYear";
	public static final String WORK_WEEKS = "workWeeks";
	public static final String SERVICE_TAG_ID = "serviceTagId";
	
	
	private Integer ticketId;
	private Double dlAmt;
	private Double totalVolume;
	private Double volumeClaimed;
	private String notes;
	private Double billedAmount;
	private String employee;
	private String claimWeek;
	private Integer claimId;
	private Integer divisionId;
	private Integer workYear;
	private String workWeeks;
	private Integer serviceTagId;
	
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
	public Integer getClaimId() {
		return claimId;
	}
	public void setClaimId(Integer claimId) {
		this.claimId = claimId;
	}
	
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	public Integer getWorkYear() {
		return workYear;
	}
	/**
	 * The ANSI calendar year for which we are retrieving data, filtered by the weeks defined in work weeks.
	 * @param workYear
	 */
	public void setWorkYear(Integer workYear) {
		this.workYear = workYear;
	}
	public String getWorkWeeks() {
		return workWeeks;
	}
	/**
	 * comma-delimited list of ANSI calendar weeks for which we are retrieving data. Typically all the weeks in a month (eg "44,45,46,47"). 
	 * @param workWeeks
	 */
	public void setWorkWeeks(String workWeeks) {
		this.workWeeks = workWeeks;
	}
	public Integer getServiceTagId() {
		return serviceTagId;
	}
	public void setServiceTagId(Integer serviceTagId) {
		this.serviceTagId = serviceTagId;
	}
	public WebMessages validateGet(Connection conn, SessionUser sessionUser, List<SessionDivision> divisionList, Integer divisionId, Integer claimYear, String claimWeeks) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateTicketId(conn, webMessages, TICKET_ID, this.ticketId, true, null);
		RequestValidator.validateNumber(webMessages, DL_AMT, this.dlAmt, null, null, true, null);
//		RequestValidator.validateNumber(webMessages, TOTAL_VOLUME, this.totalVolume, 0.0D, null, true, null);
		RequestValidator.validateNumber(webMessages, VOLUME_CLAIMED, this.volumeClaimed, null, null, true, null);
//		RequestValidator.validateNumber(webMessages, BILLED_AMOUNT, this.billedAmount, 0.0D, null, true, null);
		// claimId is not required because it won't be there for add transactions
		RequestValidator.validateId(conn, webMessages, TicketClaim.TABLE, TicketClaim.CLAIM_ID, WebMessages.GLOBAL_MESSAGE, claimId, false, null);

		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, WebMessages.GLOBAL_MESSAGE, divisionId, true, null);
		if ( ! webMessages.containsKey(WebMessages.GLOBAL_MESSAGE)) {
			RequestValidator.validateDivisionUser(conn, webMessages, divisionId, WebMessages.GLOBAL_MESSAGE, sessionUser.getUserId(), DIVISION_ID, true);
		}
		
		if ( StringUtils.isBlank(claimWeeks) ) {
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid System State. Reload page and try again");
		} else {
			// claimWeek contains a string like '2020-46'
			// claimWeeks contains a string like '45,46,47,48'
			// make sure the "week" from claimWeek is in claimWeeks
			String[] validClaimWeeks = StringUtils.split(claimWeeks, ",");
			String[] selectedClaimWeek = StringUtils.split(claimWeek, "-");
			Integer claimWeek = Integer.valueOf(selectedClaimWeek[1]);
			boolean isValidClaimWeek = false;
			for ( String validClaimWeek : validClaimWeeks ) {
				if ( claimWeek == Integer.valueOf(validClaimWeek).intValue() ) {
					isValidClaimWeek = true;
				}
			}
			if ( ! isValidClaimWeek ) {
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Claim Week. Reload page and try again");
			}
		}
		
		return webMessages;
	}
	
	
	
	public WebMessages validateAdd(Connection conn, SessionUser sessionUser) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateTicketId(conn, webMessages, TICKET_ID, this.ticketId, true, "Ticket ID");
		RequestValidator.validateNumber(webMessages, DL_AMT, this.dlAmt, null, null, true, "D/L Amount");
//		RequestValidator.validateNumber(webMessages, TOTAL_VOLUME, this.totalVolume, 0.0D, null, true, null);
		RequestValidator.validateNumber(webMessages, VOLUME_CLAIMED, this.volumeClaimed, null, null, true, "Volume Claimed");
//		RequestValidator.validateNumber(webMessages, BILLED_AMOUNT, this.billedAmount, 0.0D, null, true, null);
		RequestValidator.validateId(conn, webMessages, JobTag.TABLE, JobTag.TAG_ID, SERVICE_TAG_ID, this.serviceTagId, true);
		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, WebMessages.GLOBAL_MESSAGE, divisionId, true, "Division ID");
		if ( ! webMessages.containsKey(WebMessages.GLOBAL_MESSAGE)) {
			RequestValidator.validateDivisionUser(conn, webMessages, divisionId, WebMessages.GLOBAL_MESSAGE, sessionUser.getUserId(), DIVISION_ID, true);
		}
		
		if ( StringUtils.isBlank(workWeeks) ) {
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid System State. Reload page and try again");
		} else {
			// claimWeek contains a string like '2020-46'
			// claimWeeks contains a string like '45,46,47,48'
			// make sure the "week" from claimWeek is in claimWeeks
			String[] validClaimWeeks = StringUtils.split(workWeeks, ",");
			String[] selectedClaimWeek = StringUtils.split(claimWeek, "-");
			Integer claimWeek = Integer.valueOf(selectedClaimWeek[1]);
			boolean isValidClaimWeek = false;
			for ( String validClaimWeek : validClaimWeeks ) {
				if ( claimWeek == Integer.valueOf(validClaimWeek).intValue() ) {
					isValidClaimWeek = true;
				}
			}
			if ( ! isValidClaimWeek ) {
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Claim Week. Reload page and try again");
			}
		}
		
		return webMessages;
	}
	public WebMessages validateUpdate(Connection conn, Integer claimId2) {
		// TODO Auto-generated method stub
		return null;
	}
}
