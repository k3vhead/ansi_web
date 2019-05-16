package com.ansi.scilla.web.ticket.actionForm;

import com.ansi.scilla.web.common.actionForm.AbstractActionForm;

public class TicketAssignmentLookupForm extends AbstractActionForm {

	private static final long serialVersionUID = 1L;
	
	public static final String KEY = "ticketLookupForm";
	
	public static final String JOB_ID = "jobId";
	public static final String TICKET_ID = "ticketId";
	public static final String WASHER_ID = "washerId";
	public static final String DIVISION_ID = "divisionId";
	
	private String jobId;
	private String ticketId;
	private String washerId;
	private String divisionId;
	

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(String divisionId) {
		this.divisionId = divisionId;
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getWasherId() {
		return washerId;
	}

	public void setWasherId(String washerId) {
		this.washerId = washerId;
	}

	

	
	

}
