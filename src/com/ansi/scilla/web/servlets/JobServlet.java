package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.jobticket.JobUtils;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.request.JobDetailRequest;
import com.ansi.scilla.web.request.JobDetailRequest.JobDetailRequestAction;
import com.ansi.scilla.web.response.job.JobDetailResponse;
import com.ansi.scilla.web.struts.SessionUser;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class JobServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "job";
	
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doDelete(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;

		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		
		try {
			conn = AppUtils.getDBCPConn();
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);	
			
			if( url.getId() == null || ! StringUtils.isBlank(url.getCommand())) {	
				throw new ResourceNotFoundException();
			} else if (url.getId() != null) {
				jobDetailResponse = new JobDetailResponse(conn, url.getId());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, jobDetailResponse);
			} else {
				// according to the URI parsing, this shouldn't happen, but it gives me warm fuzzies
				throw new ResourceNotFoundException();
			}
		} catch(RecordNotFoundException e) {
			super.sendNotFound(response);
		} catch(ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		AnsiURL url = null;
		SessionUser sessionUser = AppUtils.getSessionUser(request);
		WebMessages messages = new WebMessages();
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			System.out.println(jsonString);
			url = new AnsiURL(request, "job", (String[])null);
			try {
				JobDetailRequest jobDetailRequest = (JobDetailRequest) AppUtils.json2object(jsonString, JobDetailRequest.class);
				System.out.println(jobDetailRequest);
				JobDetailRequest.JobDetailRequestAction action = JobDetailRequest.JobDetailRequestAction.valueOf(jobDetailRequest.getAction());
				if (action.equals(JobDetailRequestAction.CANCEL_JOB)) {
					doCancelJob(conn, url.getId(), jobDetailRequest, sessionUser, response);					
				} else if ( action.equals(JobDetailRequestAction.ACTIVATE_JOB)) {
					doActivateJob(conn, url.getId(), jobDetailRequest, sessionUser, response);				
				} else if ( action.equals(JobDetailRequestAction.SCHEDULE_JOB)) {
					doScheduleJob(conn, url.getId(), jobDetailRequest, sessionUser, response);
				} else if ( action.equals(JobDetailRequestAction.REPEAT_JOB)) {
					doRepeatJob(conn, url.getId(), jobDetailRequest, sessionUser, response);
				}
			} catch ( IllegalArgumentException e) {
				conn.rollback();
				messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Missing Required Data: action");
				jobDetailResponse.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, jobDetailResponse);
			}
		} catch (ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}

	
	private void doCancelJob(Connection conn, Integer jobId, JobDetailRequest jobDetailRequest, SessionUser sessionUser, HttpServletResponse response) throws Exception {
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		WebMessages messages = new WebMessages();
		ResponseCode responseCode = null;
		if ( StringUtils.isBlank(jobDetailRequest.getCancelReason())) {
			messages.addMessage("cancelReason", "Required Field");
		}
		if ( jobDetailRequest.getCancelDate() == null ) {
			messages.addMessage("cancelDate", "Required Field");
		}
		if ( messages.isEmpty() ) {
			try {
				JobUtils.cancelJob(conn, jobId, jobDetailRequest.getCancelDate(), jobDetailRequest.getCancelReason(), sessionUser.getUserId());
				conn.commit();
				responseCode = ResponseCode.SUCCESS;
				messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
				jobDetailResponse = new JobDetailResponse(conn,jobId);
			} catch ( RecordNotFoundException e) {
				responseCode = ResponseCode.EDIT_FAILURE;
				messages.addMessage("cancelDate", "Invalid Job ID");
			}
		} else { 
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		jobDetailResponse.setWebMessages(messages);
		super.sendResponse(conn, response, responseCode, jobDetailResponse);
	}

	
	private void doActivateJob(Connection conn, Integer jobId, JobDetailRequest jobDetailRequest, SessionUser sessionUser, HttpServletResponse response) throws Exception {
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		WebMessages messages = new WebMessages();
		ResponseCode responseCode = null;
		try {
			if ( jobDetailRequest.getStartDate() == null ) {
				messages.addMessage("startDate", "Required Field");
			}
			if ( jobDetailRequest.getActivationDate() == null ) {
				messages.addMessage("activationDate", "Required Field");
			}
			if ( messages.isEmpty() ) {
				try {
					System.out.println("JobServlet 156");
					JobUtils.activateJob(conn, jobId, jobDetailRequest.getStartDate(), jobDetailRequest.getActivationDate(), sessionUser.getUserId());
					conn.commit();
					responseCode = ResponseCode.SUCCESS;
					messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
				} catch ( RecordNotFoundException e) {
					conn.rollback();
					responseCode = ResponseCode.EDIT_FAILURE;
					messages.addMessage("activationDate", "Invalid Job ID");
				}
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}
			jobDetailResponse = new JobDetailResponse(conn,jobId);
		} catch ( RecordNotFoundException e) {
			responseCode = ResponseCode.EDIT_FAILURE;
			messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Job ID");
		}
		jobDetailResponse.setWebMessages(messages);
		super.sendResponse(conn, response, responseCode, jobDetailResponse);
		
	}


	private void doScheduleJob(Connection conn, Integer jobId, JobDetailRequest jobDetailRequest, SessionUser sessionUser, HttpServletResponse response) throws Exception {
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		WebMessages messages = new WebMessages();
		ResponseCode responseCode = null;
		try {
			if ( jobDetailRequest.getStartDate() == null ) {
				messages.addMessage("scheduleStartDate", "Required Field");
			}
			if ( messages.isEmpty() ) {
				try {
					System.out.println("JobServlet 193");
					JobUtils.rescheduleJob(conn, jobId, jobDetailRequest.getStartDate(), sessionUser.getUserId());
					conn.commit();
					responseCode = ResponseCode.SUCCESS;
					messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
				} catch ( RecordNotFoundException e) {
					conn.rollback();
					responseCode = ResponseCode.EDIT_FAILURE;
					messages.addMessage("scheduleStartDate", "Invalid Job ID");
				}
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}
			jobDetailResponse = new JobDetailResponse(conn,jobId);
		} catch ( RecordNotFoundException e) {
			responseCode = ResponseCode.EDIT_FAILURE;
			messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Job ID");
		}
		jobDetailResponse.setWebMessages(messages);
		super.sendResponse(conn, response, responseCode, jobDetailResponse);
		
	}

	private void doRepeatJob(Connection conn, Integer jobId, JobDetailRequest jobDetailRequest, SessionUser sessionUser,
			HttpServletResponse response) throws Exception {
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		WebMessages messages = new WebMessages();
		ResponseCode responseCode = null;
		try {
			if ( jobDetailRequest.getAnnualRepeat() == null ) {
				messages.addMessage("annualRepeat", "Required Field");
			}
			if ( messages.isEmpty() ) {
				try {
					JobUtils.updateAnnualRepeat(conn, jobId, jobDetailRequest.getAnnualRepeat(), sessionUser.getUserId());
					conn.commit();
					responseCode = ResponseCode.SUCCESS;
					messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
				} catch ( RecordNotFoundException e) {
					conn.rollback();
					responseCode = ResponseCode.EDIT_FAILURE;
					messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Job ID");
				}
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}
			jobDetailResponse = new JobDetailResponse(conn,jobId);
		} catch ( RecordNotFoundException e) {
			responseCode = ResponseCode.EDIT_FAILURE;
			messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Job ID");
		}
		jobDetailResponse.setWebMessages(messages);
		super.sendResponse(conn, response, responseCode, jobDetailResponse);		
	}

	
}
