package com.ansi.scilla.web.job.actionForm;

import com.ansi.scilla.web.common.actionForm.AbstractActionForm;

public class JobIdForm extends AbstractActionForm {

	private static final long serialVersionUID = 1L;
	
	public static final String JOB_ID = "jobId";
	
	private String jobId;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	
	

}
