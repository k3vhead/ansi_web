package com.ansi.scilla.web.quote.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.quote.request.JobReorderRequest;
import com.ansi.scilla.web.quote.response.QuoteResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;
/**
 * The url for this servlet will be of the form /reorderJobList/&lt;quoteId&gt;
 * 
 *	The new order of Job Id's will be in the posted JSON data
 * 
 */
public class ReorderJobListServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	
	


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
//		String queryString = request.getQueryString();
		
		AnsiURL url = null;
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			SessionData sessionData = AppUtils.validateSession(request, Permission.QUOTE_CREATE);
			SessionUser user = sessionData.getUser();
			conn.setAutoCommit(false);
			
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "Job Reorder Json: " + jsonString);

			url = new AnsiURL(request, "reorderJobs", (String[])null);
			Integer quoteId = url.getId();

			JobReorderRequest jobReorderRequest = new JobReorderRequest();
			AppUtils.json2object(jsonString, jobReorderRequest);
			logger.log(Level.DEBUG, "Reorder Request: " + jobReorderRequest);
			ResponseCode responseCode = null;
			
			QuoteResponse quoteResponse = null;
			Quote quote = null;
			try {
				quote = doReorder(conn, quoteId, jobReorderRequest, user);
				responseCode = ResponseCode.SUCCESS;
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
				conn.commit();
			} catch ( JobReorderError e ) {
				responseCode = ResponseCode.EDIT_FAILURE;
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, e.getMessage());
				conn.rollback();
			}
			
			quoteResponse = new QuoteResponse(conn, quote, webMessages, sessionData.getUserPermissionList());
			super.sendResponse(conn, response, responseCode, quoteResponse);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}





	private Quote doReorder(Connection conn, Integer quoteId, JobReorderRequest jobOrderRequest, SessionUser user) throws JobReorderError, Exception {
		Integer jobNbr = 1;
		Job key = new Job();
		
		for ( Integer jobId : jobOrderRequest.getJobIdList() ) {
			Job job = new Job();
			job.setJobId(jobId);
			try {
				job.selectOne(conn);
				if ( ! job.getQuoteId().equals(quoteId)) {
					throw new JobReorderError("Job doesn't belong to quote");					
				}
				quoteId = job.getQuoteId();
				job.setJobNbr(jobNbr);
				job.setUpdatedBy(user.getUserId());
				key.setJobId(job.getJobId());
				job.update(conn, key);
				jobNbr++;
			} catch ( RecordNotFoundException e) {
				throw new JobReorderError("Invalid Job ID: " + jobId);
			}
		}
		
		Quote quote = new Quote();
		try {
			quote.setQuoteId(quoteId);
			quote.selectOne(conn);
		} catch ( RecordNotFoundException e) {
			throw new JobReorderError("Invalid quote ID: " + quoteId);
		}
		return quote;
		
	}

	
	
	

	
	
	
	
	

	

	public class JobReorderError extends Exception {
		private static final long serialVersionUID = 1L;

		public JobReorderError() {
			super();
		}

		public JobReorderError(String message) {
			super(message);
		}
		
	}
	
	

	
	
}
