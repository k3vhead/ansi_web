package com.ansi.scilla.web.quote.actionForm;

import com.ansi.scilla.web.common.actionForm.AbstractActionForm;

public class QuoteMaintenanceForm extends AbstractActionForm {

	private static final long serialVersionUID = 1L;

	private String id;
	private String jobId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	
	
}
