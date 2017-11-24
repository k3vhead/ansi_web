package com.ansi.scilla.web.ticket.actionForm;

import com.ansi.scilla.web.common.actionForm.AbstractActionForm;

public class TicketLookupForm extends AbstractActionForm {

	private static final long serialVersionUID = 1L;
	
	public static final String KEY = "ticketLookupForm";
	
	public static final String JOB_ID = "jobId";
	public static final String DIVISION_ID = "divisionId";
	public static final String START_DATE = "startDate";
	public static final String STATUS = "status"; 
	
	private String jobId;
	private String divisionId;
	private String startDate;
	private String status;

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

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	

}
