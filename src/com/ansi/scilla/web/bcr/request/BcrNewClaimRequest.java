package com.ansi.scilla.web.bcr.request;

import java.sql.Connection;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.struts.SessionUser;

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
	public static final String LABOR_NOTES = "laborNotes";
	public static final String EXPENSE_NOTES = "expenseNotes";
	public static final String DIVISION_ID = "divisionId";
	public static final String WORK_YEAR = "workYear";
	public static final String WORK_WEEKS = "workWeeks";
	
	private Integer divisionId;
	private Integer ticketId;
	private Integer serviceTypeId;
	private String claimWeek;	// format: yyyy-nn
	private Double dlAmt;
	private Double expenseVolume;
	private Double volumeClaimed;
	private String expenseType;
	private String employee;
	private String laborNotes;
	private String expenseNotes;
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
	public String getLaborNotes() {
		return laborNotes;
	}
	public void setLaborNotes(String laborNotes) {
		this.laborNotes = laborNotes;
	}
	public String getExpenseNotes() {
		return expenseNotes;
	}
	public void setExpenseNotes(String expenseNotes) {
		this.expenseNotes = expenseNotes;
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
	
	
	public WebMessages validate(Connection conn, SessionUser sessionUser) throws Exception {
		WebMessages webMessages = new WebMessages();
		if ( isExpenseClaim() || isLaborClaim() ) {
			BcrTicketClaimRequest bcrTicketClaimRequest = makeBcrTicketClaimRequest();
			BcrExpenseRequest bcrExpenseRequest = makeBcrExpenseRequest();
			
			if ( isLaborClaim() ) {
				WebMessages laborMessages = bcrTicketClaimRequest.validateAdd(conn, sessionUser);
				if ( ! laborMessages.isEmpty() ) {
					webMessages.addAllMessages(laborMessages);
				}
			}
			
			if ( isExpenseClaim() ) {
				WebMessages expenseMessages = bcrExpenseRequest.validateAdd(conn);
				if ( ! expenseMessages.isEmpty() ) {
					webMessages.addAllMessages(expenseMessages);
				}
			}
		} else {
			webMessages.addMessage("newClaim", "No claim entered");
		}
		return webMessages;
	}
	
	
	
	public BcrExpenseRequest makeBcrExpenseRequest() {
		BcrExpenseRequest request = new BcrExpenseRequest();
		request.setDivisionId(divisionId);
		request.setTicketId(ticketId);
		request.setServiceTagId(serviceTypeId);
		request.setClaimWeek(claimWeek);
		request.setVolume(expenseVolume);
		request.setExpenseType(expenseType);
		request.setNotes(expenseNotes);
		request.setWorkYear(workYear);
		request.setWorkWeeks(workWeeks);
		return request;
	}
	
	
	public BcrTicketClaimRequest makeBcrTicketClaimRequest() {
		BcrTicketClaimRequest request = new BcrTicketClaimRequest();
		request.setTicketId(ticketId);
		request.setDlAmt(dlAmt);
		request.setVolumeClaimed(volumeClaimed);
		request.setNotes(laborNotes);
		request.setEmployee(employee);
		request.setClaimWeek(claimWeek);
		request.setDivisionId(divisionId);
		request.setWorkYear(workYear);
		request.setWorkWeeks(workWeeks);
		request.setServiceTagId(serviceTypeId);
		return request;
	}
	
	
	
	public boolean isExpenseClaim() {
		boolean isExpense = false;
		
		if ( expenseVolume != null ) { isExpense = true; }
		if ( ! StringUtils.isBlank(expenseType) ) { isExpense = true; }
		if ( ! StringUtils.isBlank(expenseNotes) ) { isExpense = true; }
		
		return isExpense;
	}
	
	
	
	public boolean isLaborClaim() {
		boolean isLabor = false;
		
		if ( dlAmt != null ) { isLabor = true; }
		if ( volumeClaimed != null ) { isLabor = true; }
		if ( ! StringUtils.isBlank(employee) ) { isLabor = true; }
		if ( ! StringUtils.isBlank(laborNotes) ) { isLabor = true; }
		
		return isLabor;
	}
	
}
