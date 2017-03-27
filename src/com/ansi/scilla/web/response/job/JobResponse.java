package com.ansi.scilla.web.response.job;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.Date;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.response.MessageResponse;
import com.ansi.scilla.web.response.job.JobDetailResponse.QuoteDetail;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thewebthing.commons.lang.BeanUtils;

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
