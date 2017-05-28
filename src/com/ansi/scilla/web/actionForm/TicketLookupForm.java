package com.ansi.scilla.web.actionForm;

public class TicketLookupForm extends AbstractActionForm {

	private static final long serialVersionUID = 1L;
	
	public static final String KEY = "ticketLookupForm";
	
	public static final String JOB_ID = "jobId";
	public static final String DIVISION_ID = "divisionId";
	public static final String START_DATE = "startDate";
	
	private String jobId;
	private String divisionId;
	private String startDate;

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

	
	

}