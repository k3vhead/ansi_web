package com.ansi.scilla.web.request.job;

import java.util.Calendar;

import com.ansi.scilla.web.request.AbstractRequest;
import com.ansi.scilla.web.request.RequiredForAdd;
import com.ansi.scilla.web.request.RequiredForUpdate;
import com.fasterxml.jackson.annotation.JsonFormat;

public class JobScheduleRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Calendar jobDate;
	
	public JobScheduleRequest() {
		super();
	}

	@RequiredForAdd
	@RequiredForUpdate
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getJobDate() {
		return jobDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setJobDate(Calendar jobDate) {
		this.jobDate = jobDate;
	}


	
	
}
