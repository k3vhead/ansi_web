package com.ansi.scilla.web.response.job;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.response.MessageResponse;

/**
 * Used to return a single quote to the client
 * 
 * 
 *
 */
public class JobResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Job job;

	
	public JobResponse() {
		super();
	}

	public JobResponse(Job job, WebMessages webMessages) throws IllegalAccessException, InvocationTargetException {
		super(webMessages);
		this.job = job;
		
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

		
}
