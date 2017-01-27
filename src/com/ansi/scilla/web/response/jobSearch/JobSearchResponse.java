package com.ansi.scilla.web.response.jobSearch;

import java.io.Serializable;

import com.ansi.scilla.web.response.MessageResponse;

/**
 * Used to return a single code to the client
 * 
 * @author ggroce
 *
 */
public class JobSearchResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private JobSearchRecord jobSearch;

	public JobSearchResponse() {
		super();
	}

	public JobSearchResponse(JobSearchRecord jobSearch) {
		super();
		this.jobSearch = jobSearch;
	}

	public JobSearchRecord getJobSearch() {
		return jobSearch;
	}
	
	
}
