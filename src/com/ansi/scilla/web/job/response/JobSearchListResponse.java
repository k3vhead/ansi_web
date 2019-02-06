package com.ansi.scilla.web.job.response;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ansi.scilla.common.queries.JobSearch;
import com.ansi.scilla.web.common.response.MessageResponse;

/** 
 * Used to return a list of "code" objects to the client
 * 
 * @author ggroce
 *
 */
public class JobSearchListResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<JobSearchRecord> jobSearchList;

	public JobSearchListResponse() {
		super();
	}
	/**
	 * create a list of all code table records in the database, sorted by
	 * table, field, display value
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public JobSearchListResponse(Connection conn) throws Exception {
		List<JobSearch> jobSearchList = JobSearch.select(conn);
		this.jobSearchList = new ArrayList<JobSearchRecord>();
		for ( JobSearch record : jobSearchList ) {
			this.jobSearchList.add(new JobSearchRecord(record));
		}
	}
	
	public JobSearchListResponse(Connection conn, String queryTerm) throws Exception {
		List<JobSearch> jobSearchList = JobSearch.select(conn, queryTerm);
		this.jobSearchList = new ArrayList<JobSearchRecord>();
		for ( JobSearch record : jobSearchList ) {
			this.jobSearchList.add(new JobSearchRecord(record));
		}
	}
	
	public JobSearchListResponse(Connection conn, String queryTerm, String[] sortField) throws Exception {
		List<JobSearch> jobSearchList = JobSearch.select(conn, queryTerm, sortField);
		this.jobSearchList = new ArrayList<JobSearchRecord>();
		for ( JobSearch record : jobSearchList ) {
			this.jobSearchList.add(new JobSearchRecord(record));
		}
	}
	
	
	
	/**
	 * This method is not correct. It is passing a quoteId to the jobsearch when the jobsearch
	 * requires a job id. The servlet (JobSearchServlet) is not called anywhere that I can find
	 * and is likely a candidate for deletion
	 * @param conn
	 * @param quoteId
	 * @throws Exception
	 */
	@Deprecated
	public JobSearchListResponse(Connection conn, Integer quoteId) throws Exception {
//		JobSearch jobSearch = JobSearch.select(conn, quoteId);		
		JobSearch jobSearch = null;
		JobSearchRecord record = new JobSearchRecord(jobSearch);
		this.jobSearchList = Arrays.asList(new JobSearchRecord[] { record });
	}
	

	public List<JobSearchRecord> getJobSearchList() {
		return jobSearchList;
	}

	public void setJobSearchList(List<JobSearchRecord> jobSearchList) {
		this.jobSearchList = jobSearchList;
	}
	
	
	
}
