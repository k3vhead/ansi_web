package com.ansi.scilla.web.response.options;

import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.jobticket.JobStatus;

public class JobStatusOption extends WebOption  {
	private static final long serialVersionUID = 1L;
	private String code;
	private List<String> nextValues;
	public JobStatusOption(JobStatus jobStatus) {
		super();
		this.code = jobStatus.code();
		this.display = jobStatus.display();
		this.nextValues = new ArrayList<String>();
		for ( JobStatus js : jobStatus.nextValues() ) {
			this.nextValues.add(js.code());
		}
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<String> getNextValues() {
		return nextValues;
	}
	public void setNextValues(List<String> nextValues) {
		this.nextValues = nextValues;
	}
	
}
