package com.ansi.scilla.web.bcr.request;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.JobTag;
import com.ansi.scilla.web.bcr.response.BcrSplitClaimValidationResponse;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.struts.SessionUser;

public class BcrSplitClaimRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String TICKET_ID = "ticketId";
	public static final String SERVICE_TYPE_ID = "serviceTypeId";
	public static final String CLAIM_WEEK = "claimWeek";
	public static final String DL_AMT = "dlAmt";
	public static final String EXPENSE_VOLUME = "expenseVolume";
	public static final String VOLUME_CLAIMED = "volumeClaimed";
	public static final String EXPENSE_TYPE = "expenseType";
	public static final String EMPLOYEE = "employee";
	public static final String LABOR_NOTES = "laborNotes";
	public static final String EXPENSE_NOTES = "expenseNotes";
	public static final String DIVISION_ID = "divisionId";
	public static final String WORK_YEAR = "workYear";
	public static final String WORK_WEEKS = "workWeeks";
	
	private Integer divisionId;
	private Integer ticketId;
	private Integer serviceTypeId;
	private Integer workYear;
	private String workWeeks;
	private String claimWeek;	// format: yyyy-nn
	
	private String expenseType;
	private Double expenseVolume;
	private String expenseNotes;
	private BcrEmployeeClaim[] employeeClaims;
	
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
	public Integer getServiceTypeId() {
		return serviceTypeId;
	}
	public void setServiceTypeId(Integer serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
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
	public String getClaimWeek() {
		return claimWeek;
	}
	public void setClaimWeek(String claimWeek) {
		this.claimWeek = claimWeek;
	}
	public String getExpenseType() {
		return expenseType;
	}
	public void setExpenseType(String expenseType) {
		this.expenseType = expenseType;
	}
	public Double getExpenseVolume() {
		return expenseVolume;
	}
	public void setExpenseVolume(Double expenseVolume) {
		this.expenseVolume = expenseVolume;
	}
	public String getExpenseNotes() {
		return expenseNotes;
	}
	public void setExpenseNotes(String expenseNotes) {
		this.expenseNotes = expenseNotes;
	}
	public BcrEmployeeClaim[] getEmployeeClaims() {
		return employeeClaims;
	}
	public void setEmployeeClaims(BcrEmployeeClaim[] employeeClaims) {
		this.employeeClaims = employeeClaims;
	}



	public BcrSplitClaimValidationResponse validate(Connection conn, SessionUser sessionUser) throws Exception {
		boolean hasLaborClaim = hasLaborClaim();
		boolean hasExpenseClaim = hasExpenseClaim();
		
		WebMessages webMessages = new WebMessages();
		WebMessages expenseMessages = new WebMessages();
		HashMap<Integer, WebMessages> laborMessages = new HashMap<Integer, WebMessages>();
		
		if ( hasLaborClaim || hasExpenseClaim ) {
			webMessages = validateCommonFields(conn, sessionUser);
			expenseMessages = hasExpenseClaim ? validateExpenseClaim(conn) : new WebMessages();
			laborMessages = hasLaborClaim ? validateLaborClaim(conn) : new HashMap<Integer, WebMessages>();			
		} else {
			webMessages.addMessage(TICKET_ID, "No Claims Submitted");
		}
		
		return new BcrSplitClaimValidationResponse(webMessages, expenseMessages, laborMessages);
	}
	
	
	private WebMessages validateCommonFields(Connection conn, SessionUser sessionUser) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		if ( StringUtils.isBlank(workWeeks) ) {
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid System State. Reload page and try again");
		}
		RequestValidator.validateTicketId(conn, webMessages, TICKET_ID, this.ticketId, true, "Ticket");
		RequestValidator.validateClaimWeek(webMessages, CLAIM_WEEK, this.claimWeek, true);
		if ( ! webMessages.containsKey(CLAIM_WEEK) ) {
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
		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, WebMessages.GLOBAL_MESSAGE, this.divisionId, true, "Division");
		RequestValidator.validateNumber(webMessages, WORK_YEAR, this.workYear, null, null, true, "Year");
		RequestValidator.validateId(conn, webMessages, JobTag.TABLE, JobTag.TAG_ID, SERVICE_TYPE_ID, this.serviceTypeId, true, "Service Tag");
		if ( ! webMessages.containsKey(WebMessages.GLOBAL_MESSAGE)) {
			RequestValidator.validateDivisionUser(conn, webMessages, divisionId, WebMessages.GLOBAL_MESSAGE, sessionUser.getUserId(), DIVISION_ID, true);
		}
		
		return webMessages;
	}


	
	
	private WebMessages validateExpenseClaim(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validatePassthruExpenseType(conn, webMessages, EXPENSE_TYPE, this.expenseType, true, "Expense Type");		
		RequestValidator.validateDouble(webMessages, EXPENSE_VOLUME, this.expenseVolume, null, null, true, "Volume");		
		RequestValidator.validateString(webMessages, EXPENSE_NOTES, this.expenseNotes, 1000, false, "Expense Note");
		
		return webMessages;
	}
	
	
	private HashMap<Integer, WebMessages> validateLaborClaim(Connection conn) throws Exception {
		HashMap<Integer, WebMessages> webMessagesList = new HashMap<Integer, WebMessages>();
		for ( BcrEmployeeClaim employeeClaim : this.employeeClaims ) {
			webMessagesList.put( employeeClaim.getIndex(), employeeClaim.validate(conn) );
		}
		return webMessagesList;
	}
	
	
	public boolean hasExpenseClaim() {
		boolean hasExpenseClaim = false;
		if ( ! StringUtils.isBlank(this.expenseType)) {hasExpenseClaim = true; }
		if ( this.expenseVolume != null ) {hasExpenseClaim = true; }
		if ( ! StringUtils.isBlank(this.expenseNotes)) {hasExpenseClaim = true; }

		return hasExpenseClaim;
	}
	
	
	public boolean hasLaborClaim() {
		boolean hasLaborClaim = false;
		for ( BcrEmployeeClaim employeeClaim : this.employeeClaims ) {
			if ( employeeClaim.hasLaborClaim() ) { hasLaborClaim = true; }
		}
		return hasLaborClaim;
	}
	
	
	public BcrExpenseRequest makeBcrExpenseRequest() {
		BcrExpenseRequest expenseRequest = new BcrExpenseRequest();
		expenseRequest.setDivisionId(this.divisionId);
		expenseRequest.setTicketId(this.ticketId);
		expenseRequest.setServiceTagId(this.serviceTypeId);
		expenseRequest.setClaimWeek(this.claimWeek);
		expenseRequest.setVolume(this.expenseVolume);
		expenseRequest.setExpenseType(this.expenseType);
		expenseRequest.setNotes(this.expenseNotes);
		expenseRequest.setWorkYear(this.workYear);
		expenseRequest.setWorkWeeks(this.workWeeks);
		
		return expenseRequest;
	}
	
	
	
	public List<BcrTicketClaimRequest> makeBcrTicketClaimRequests() {
		List<BcrTicketClaimRequest> laborRequestList = new ArrayList<BcrTicketClaimRequest>();
		for ( BcrEmployeeClaim laborClaim : this.employeeClaims ) {
			if ( StringUtils.isNotBlank(laborClaim.getEmployee()) ) {
				BcrTicketClaimRequest claimRequest = new BcrTicketClaimRequest();
				claimRequest.setTicketId(this.ticketId);
				claimRequest.setDlAmt(laborClaim.getDlAmt());
	//			claimRequest.setTotalVolume(this.);
				claimRequest.setVolumeClaimed(laborClaim.getVolumeClaimed());
				claimRequest.setNotes(laborClaim.getLaborNotes());
	//			claimRequest.setBilledAmount(this.);
				claimRequest.setEmployee(laborClaim.getEmployee());
				claimRequest.setClaimWeek(this.claimWeek);
	//			claimRequest.setClaimId(this.);
				claimRequest.setDivisionId(this.divisionId);
				claimRequest.setWorkYear(this.workYear);
				claimRequest.setWorkWeeks(this.workWeeks);
				claimRequest.setServiceTagId(this.serviceTypeId);
	//			claimRequest.setClaimedEquipment(this.);
				laborRequestList.add(claimRequest);
			}
		}
		return laborRequestList;
	}
}
