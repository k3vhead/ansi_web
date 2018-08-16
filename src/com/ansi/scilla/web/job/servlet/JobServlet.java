package com.ansi.scilla.web.job.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.common.jobticket.JobUtils;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.job.request.JobDetailRequest;
import com.ansi.scilla.web.job.request.JobDetailRequest.JobDetailRequestAction;
import com.ansi.scilla.web.job.request.JobRequest;
import com.ansi.scilla.web.job.request.JobRequestAction;
import com.ansi.scilla.web.job.response.JobDetailResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class JobServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "job";
	
	// these update types are at the bottom of quoteMaintenance.jsp
	public static final String UPDATE_TYPE_IS_PROPOSAL_PANEL = "proposal";
	public static final String UPDATE_TYPE_IS_ACTIVATION_PANEL = "activation";
	public static final String UPDATE_TYPE_IS_INVOICE_PANEL = "invoice";
	public static final String UPDATE_TYPE_IS_SCHEDULE_PANEL = "schedule";
	
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
			AppUtils.validateSession(request, Permission.JOB_READ);
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
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch(RecordNotFoundException | ResourceNotFoundException e) {
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
		Connection conn = null;
		WebMessages messages = new WebMessages();
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		ResponseCode responseCode = null;
		
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			SessionData sessionData = AppUtils.validateSession(request, Permission.JOB_WRITE);
			SessionUser user = sessionData.getUser();
			String jsonString = super.makeJsonString(request);
			url = new AnsiURL(request, "job", (String[])null);
			if ( url.getId() == null ) {
				super.sendNotFound(response);
			} else {
				Job job = getJob(conn, url.getId());
				logger.log(Level.DEBUG, jsonString);
				JobRequest jobRequest = new JobRequest(jsonString);
				WebMessages webMessages = new WebMessages();
				
				try {
					trafficCop(conn, response, user, job, jobRequest, webMessages);					
					conn.commit();
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
					responseCode = ResponseCode.SUCCESS;
					jobDetailResponse = new JobDetailResponse(conn, url.getId());
				} catch ( JobProcessException e ) {
					conn.rollback();
					responseCode = ResponseCode.EDIT_FAILURE;
				}

				jobDetailResponse.setWebMessages(webMessages);
				super.sendResponse(conn, response, responseCode, jobDetailResponse);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch (ResourceNotFoundException e) {
			super.sendNotAllowed(response);
		} catch ( RecordNotFoundException e ) {
			super.sendNotFound(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	private void trafficCop(Connection conn, HttpServletResponse response, SessionUser user, Job job, JobRequest jobRequest, WebMessages webMessages) throws Exception {
		if ( StringUtils.isBlank(jobRequest.getAction()) ) {
			// THis is a panel edit update
			if ( jobRequest.getUpdateType().equalsIgnoreCase(UPDATE_TYPE_IS_PROPOSAL_PANEL)) {
				makeProposalUpdate(conn, user, job, jobRequest, webMessages);
			} else if ( jobRequest.getUpdateType().equalsIgnoreCase(UPDATE_TYPE_IS_ACTIVATION_PANEL)) {
				makeActivationUpdate(conn, user, job, jobRequest, webMessages);
			} else if ( jobRequest.getUpdateType().equalsIgnoreCase(UPDATE_TYPE_IS_INVOICE_PANEL)) {
				makeInvoiceUpdate(conn, user, job, jobRequest, webMessages);
			} else if ( jobRequest.getUpdateType().equalsIgnoreCase(UPDATE_TYPE_IS_SCHEDULE_PANEL)) {
				makeScheduleUpdate(conn, user, job, jobRequest, webMessages);
			} else {
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Update Type");
				throw new JobProcessException("Invalid Update Type");
			}
		} else {
			// this is an action update
			JobRequestAction action = JobRequestAction.valueOf(jobRequest.getAction());
			if (action.equals(JobRequestAction.CANCEL_JOB)) {
				doCancelJob(conn, job, jobRequest, user, response);					
			} else if ( action.equals(JobRequestAction.ACTIVATE_JOB)) {
				doActivateJob(conn, job, jobRequest, user, response);				
			} else if ( action.equals(JobRequestAction.DELETE_JOB)) {
				doDeleteJob(conn, job, jobRequest, user, response);				
			} else if ( action.equals(JobRequestAction.SCHEDULE_JOB)) {
				doScheduleJob(conn, job, jobRequest, user, response);
			} else if ( action.equals(JobRequestAction.REPEAT_JOB)) {
				doRepeatJob(conn, job, jobRequest, user, response);
			} else {
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Job Action");
				throw new JobProcessException("Invalid Job Action");
			}
		}
		
	}

	private void makeProposalUpdate(Connection conn, SessionUser user, Job job, JobRequest jobRequest, WebMessages webMessages) throws Exception {
		job.setJobFrequency(jobRequest.getJobFrequency());
		job.setJobNbr(jobRequest.getJobNbr());
		job.setPricePerCleaning(jobRequest.getPricePerCleaning());
		job.setServiceDescription(jobRequest.getServiceDescription());
		updateJob(conn, user, job);
	}

	private void makeActivationUpdate(Connection conn, SessionUser user, Job job, JobRequest jobRequest, WebMessages webMessages) throws Exception {
		job.setRequestSpecialScheduling(jobRequest.getRequestSpecialScheduling());
		job.setDirectLaborPct(jobRequest.getDirectLaborPct());
		job.setBudget(jobRequest.getBudget());
		job.setFloors(jobRequest.getFloors());
		job.setEquipment(jobRequest.getEquipment());
		job.setWasherNotes(jobRequest.getWasherNotes());
		job.setOmNotes(jobRequest.getOmNotes());
		job.setBillingNotes(jobRequest.getBillingNotes());		
		updateJob(conn, user, job);
	}

	private void makeInvoiceUpdate(Connection conn, SessionUser user, Job job, JobRequest jobRequest, WebMessages webMessages) throws Exception {
		job.setPoNumber(jobRequest.getPoNumber());
		job.setOurVendorNbr(jobRequest.getOurVendorNbr());
		job.setExpirationDate(jobRequest.getExpirationDate());
		job.setExpirationReason(jobRequest.getExpirationReason());
		updateJob(conn, user, job);
		
	}

	private void makeScheduleUpdate(Connection conn, SessionUser user, Job job, JobRequest jobRequest, WebMessages webMessages) throws Exception {
		job.setRepeatScheduleAnnually(jobRequest.getRepeatScheduleAnnually());
		updateJob(conn, user, job);
		
	}

	private Job getJob(Connection conn, Integer jobId) throws RecordNotFoundException, Exception {
		Job job = new Job();
		job.setJobId(jobId);
		job.selectOne(conn);
		return job;
	}

	private void updateJob(Connection conn, SessionUser user, Job job) throws Exception {
		Job key = new Job();
		key.setJobId(job.getJobId());
		job.setUpdatedBy(user.getUserId());
		job.update(conn, key);
	}
	
	protected void doPost_xx(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		AnsiURL url = null;
		String url2 = request.getRequestURI();
		SessionUser sessionUser = AppUtils.getSessionUser(request);
		logger.log(Level.DEBUG, "Session user: "+sessionUser);
		WebMessages messages = new WebMessages();
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		ResponseCode responseCode = null;
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, jsonString);
			
			int idx = url2.indexOf("/job/");
			String myString = url2.substring(idx + "/job/".length());				
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];
			
			logger.log(Level.DEBUG, "Servlet Output: [0]"+urlPieces[0]);
			
			JobRequest jobRequest = new JobRequest(jsonString);
			
			if ( command.equalsIgnoreCase("add") ) {
				
				doAdd(conn, jobRequest, sessionUser, response);
			} /*else if ( action.equals(JobDetailRequestAction.UPDATE_JOB) ){
								
				try {
					Job key = new Job();
					key.setJobId(Integer.parseInt(urlPieces[1]));
					//key.selectOne(conn);
					
					
					//key.setRevision((urlPieces[2]));
					Job job = doUpdate(conn, key, jobRequest, sessionUser, response);
					String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
					responseCode = ResponseCode.SUCCESS;
					messages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
				} catch ( RecordNotFoundException e ) {
					logger.log(Level.DEBUG, "Doing 404");
					logger.log(Level.DEBUG, e);
					super.sendNotFound(response);						
				} catch ( Exception e) {
					logger.log(Level.DEBUG, "Doing SysFailure");
					responseCode = ResponseCode.SYSTEM_FAILURE;
					AppUtils.logException(e);
					String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
					messages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
				}
				
				
			} */else {
			
				url = new AnsiURL(request, "job", (String[])null);
				
				logger.log(Level.DEBUG, "URL:"+url);
				/* DCL
				try {

					JobDetailRequest jobDetailRequest = (JobDetailRequest) AppUtils.json2object(jsonString, JobDetailRequest.class);
					logger.log(Level.DEBUG, jobDetailRequest);
					JobDetailRequest.JobDetailRequestAction action = JobDetailRequest.JobDetailRequestAction.valueOf(jobDetailRequest.getAction());
					if (action.equals(JobDetailRequestAction.CANCEL_JOB)) {
						doCancelJob(conn, url.getId(), jobDetailRequest, sessionUser, response);					
					} else if ( action.equals(JobDetailRequestAction.ACTIVATE_JOB)) {
						doActivateJob(conn, url.getId(), jobDetailRequest, sessionUser, response);				
					} else if ( action.equals(JobDetailRequestAction.DELETE_JOB)) {
						doDeleteJob(conn, url.getId(), jobDetailRequest, sessionUser, response);				
					} else if ( action.equals(JobDetailRequestAction.SCHEDULE_JOB)) {
						doScheduleJob(conn, url.getId(), jobDetailRequest, sessionUser, response);
					} else if ( action.equals(JobDetailRequestAction.REPEAT_JOB)) {
						doRepeatJob(conn, url.getId(), jobDetailRequest, sessionUser, response);
					} else if ( action.equals(JobDetailRequestAction.UPDATE_JOB)) {
						Job key = new Job();
						key.setJobId(url.getId());
						//key.selectOne(conn);
						
						logger.log(Level.DEBUG, "This is the key:");
						logger.log(Level.DEBUG, key);
						logger.log(Level.DEBUG, "************");
						
						//key.setRevision((urlPieces[2]));
						logger.log(Level.DEBUG, "Trying to do update");
						Job job = doUpdate(conn, key, jobRequest, sessionUser, response);
						String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
						responseCode = ResponseCode.SUCCESS;
						messages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
						jobDetailResponse = new JobDetailResponse(conn, job.getJobId());
//						JobResponse jobResponse = new JobResponse(job, messages);
						super.sendResponse(conn, response, responseCode, jobDetailResponse);		

					}
				} catch ( RecordNotFoundException e ) {
					logger.log(Level.DEBUG, "Doing 404");
					logger.log(Level.DEBUG, e);
					super.sendNotFound(response);						
				} catch ( IllegalArgumentException e) {
					conn.rollback();
					messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Missing Required Data: action");
					jobDetailResponse.setWebMessages(messages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, jobDetailResponse);
				}
				DCL */
			}
			conn.commit();
		} catch (ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}

	
	private void doCancelJob(Connection conn, Job job, JobRequest jobDetailRequest, SessionUser sessionUser, HttpServletResponse response) throws Exception {
		Integer jobId = job.getJobId();
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

	
	private void doDeleteJob(Connection conn, Job job, JobRequest jobDetailRequest, SessionUser sessionUser, HttpServletResponse response) throws Exception {
		Integer jobId = job.getJobId();
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		WebMessages messages = new WebMessages();
		ResponseCode responseCode = null;
		if ( messages.isEmpty() ) {
			try {
				JobUtils.deleteJob(conn, jobId, sessionUser.getUserId());
				conn.commit();
				responseCode = ResponseCode.SUCCESS;
				messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Delete Successful");
//				jobDetailResponse = new JobDetailResponse(conn,jobId);
			} catch ( RecordNotFoundException e) {
				responseCode = ResponseCode.EDIT_FAILURE;
				messages.addMessage("jobId", "Invalid Job ID");
			}
		} else { 
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		jobDetailResponse.setWebMessages(messages);
		super.sendResponse(conn, response, responseCode, jobDetailResponse);
	}

	
	private void doActivateJob(Connection conn, Job job, JobRequest jobDetailRequest, SessionUser sessionUser, HttpServletResponse response) throws Exception {
		Integer jobId = job.getJobId();
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
					logger.log(Level.DEBUG, "JobServlet 156");
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


	private void doScheduleJob(Connection conn, Job job, JobRequest jobDetailRequest, SessionUser sessionUser, HttpServletResponse response) throws Exception {
		Integer jobId = job.getJobId();
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		WebMessages messages = new WebMessages();
		ResponseCode responseCode = null;
		try {
			if ( jobDetailRequest.getStartDate() == null ) {
				messages.addMessage("scheduleStartDate", "Required Field");
			}
			if ( messages.isEmpty() ) {
				try {
					logger.log(Level.DEBUG, "JobServlet 193");
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

	private void doRepeatJob(Connection conn, Job job, JobRequest jobDetailRequest, SessionUser sessionUser,
			HttpServletResponse response) throws Exception {
		Integer jobId = job.getJobId();
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
	
	protected Job doAdd(Connection conn, JobRequest jobRequest, SessionUser sessionUser, HttpServletResponse response) throws Exception {
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		ResponseCode responseCode = null;
		Date today = new Date();
		Job job = new Job();

		WebMessages messages = validateAdd(conn, jobRequest);
		if(jobRequest.getQuoteId() != null && jobRequest.getQuoteId() ==0){
			responseCode = ResponseCode.EDIT_FAILURE;
			messages.addMessage(WebMessages.GLOBAL_MESSAGE, "No Quote ID, Try saving quote first");
		} else if (messages.isEmpty()) {
			job.setAddedBy(sessionUser.getUserId());
			
			job.setAddedDate(today);
			
//			job.setJobId(jobRequest.getJobId());
			
			if(jobRequest.getQuoteId() != null){
				job.setQuoteId(jobRequest.getQuoteId());
			}
			
			job.setJobNbr(jobRequest.getJobNbr());

			job.setJobFrequency(jobRequest.getJobFrequency());
			job.setStatus("N");
			job.setPricePerCleaning(jobRequest.getPricePerCleaning());
			job.setDivisionId(jobRequest.getDivisionId());
			job.setDirectLaborPct(jobRequest.getDirectLaborPct());
			job.setBudget(jobRequest.getBudget());
			job.setFloors(jobRequest.getFloors());
			job.setInvoiceTerms(jobRequest.getInvoiceTerms());
			job.setInvoiceStyle(jobRequest.getInvoiceStyle());
			job.setInvoiceBatch(jobRequest.getInvoiceBatch());
			
			if(jobRequest.getInvoiceGrouping() != null) {
				job.setInvoiceGrouping(jobRequest.getInvoiceGrouping());
			}
			
			job.setContractContactId(jobRequest.getContractContactId());
			job.setTaxExempt(jobRequest.getTaxExempt());
			
			if(jobRequest.getPoNumber() != null){
				job.setPoNumber(jobRequest.getPoNumber());
			}
			
			if(jobRequest.getExpirationDate() != null){
				job.setExpirationDate(jobRequest.getExpirationDate());
			}
			
			if(jobRequest.getExpirationReason() != null){
				job.setExpirationReason(jobRequest.getExpirationReason());
			}
			
			job.setBuildingType(jobRequest.getBuildingType());
			
			if(jobRequest.getOurVendorNbr() != null){
				job.setOurVendorNbr(jobRequest.getOurVendorNbr());
			}
			
			job.setServiceDescription(jobRequest.getServiceDescription());
			
			if(jobRequest.getEquipment() != null){
				job.setEquipment(jobRequest.getEquipment());
			}
			
			if(jobRequest.getWasherNotes() != null){
				job.setWasherNotes(jobRequest.getWasherNotes());
			}
			
			job.setBillingContactId(jobRequest.getBillingContactId());
			
			if(jobRequest.getBillingNotes() != null) {
				job.setBillingNotes(jobRequest.getBillingNotes());
			}
			
			if(jobRequest.getOmNotes() != null) {
				job.setOmNotes(jobRequest.getOmNotes());
			}
			
//			if(jobRequest.getActivationDate() == null) {
//				job.setActivationDate(today);
//			} else {
//				job.setActivationDate(jobRequest.getActivationDate());
//			}
			
			if(jobRequest.getStartDate() != null){
				job.setStartDate(jobRequest.getStartDate());
			}
			
			if(jobRequest.getCancelDate() != null){
				job.setCancelDate(jobRequest.getCancelDate());
			}
			
			if(jobRequest.getCancelReason() != null){
				job.setCancelReason(jobRequest.getCancelReason());
			}
			
			if(jobRequest.getSiteContact() != null) {
				job.setSiteContact(jobRequest.getSiteContact());
			}
			
			if(jobRequest.getJobContactId() != null) {
				job.setJobContactId(jobRequest.getJobContactId());
			}
			
			if(jobRequest.getJobTypeId() != null) {
				job.setJobTypeId(jobRequest.getJobTypeId());
			}
			
			if(job.getJobTypeId() != null && job.getJobTypeId() == 0){
				job.setJobTypeId(null);
			}
			
			job.setRequestSpecialScheduling(jobRequest.getRequestSpecialScheduling());
			job.setRepeatScheduleAnnually(jobRequest.getRepeatScheduleAnnually());
			
			if(jobRequest.getPaymentTerms() != null){
				job.setPaymentTerms(jobRequest.getPaymentTerms());
			}
			
			job.setUpdatedBy(sessionUser.getUserId());

			job.setUpdatedDate(today);
			

			logger.log(Level.DEBUG, "Job servlet Add Data:");
			logger.log(Level.DEBUG, job.toString());
			int j = 0;
			try {
				j = job.insertWithKey(conn);
				responseCode = ResponseCode.SUCCESS;
			} catch ( SQLException e) {
				responseCode = ResponseCode.EDIT_FAILURE;
				if ( e.getMessage().contains("duplicate key")) {
					throw new DuplicateEntryException();
				} else {
					AppUtils.logException(e);
					throw e;
				}
			} 
			job.setJobId(j);
			jobDetailResponse = new JobDetailResponse(conn, job.getJobId());
		} else {
			responseCode = ResponseCode.EDIT_FAILURE;
		}
//		JobDetailResponse jobDetailResponse = new JobDetailResponse(conn, job.getJobId());
//		JobResponse jobResponse = new JobResponse(job, messages);
		jobDetailResponse.setWebMessages(messages);
		super.sendResponse(conn, response, responseCode, jobDetailResponse);		
		return job;
	}
	
	protected Job doUpdate(Connection conn, Job key,JobRequest jobRequest, SessionUser sessionUser, HttpServletResponse response) throws Exception {
		WebMessages messages = new WebMessages();
		ResponseCode responseCode = null;
		Date today = new Date();
		

		Job job = new Job();
		job.setJobId(key.getJobId());
		job.selectOne(conn);

		messages = validateUpdate(conn, key, jobRequest);
		if (messages.isEmpty()) {
//			job.insertHistory(conn);
			if(jobRequest.getJobFrequency() != null) {
				job.setJobFrequency(jobRequest.getJobFrequency());
			}
			
//			if(jobRequest.getStatus() != null) {
//				job.setStatus(jobRequest.getStatus());
//			}
			if(jobRequest.getPricePerCleaning() != null) {
				job.setPricePerCleaning(jobRequest.getPricePerCleaning());
			}
//			if(jobRequest.getDivisionId() != null) {
//				job.setDivisionId(jobRequest.getDivisionId());
//			}
			if(jobRequest.getDirectLaborPct() != null) {
				job.setDirectLaborPct(jobRequest.getDirectLaborPct());
			}
			if(jobRequest.getBudget() != null) {
				job.setBudget(jobRequest.getBudget());
			}
			if(jobRequest.getFloors() != null) {
				job.setFloors(jobRequest.getFloors());
			}
			if(jobRequest.getInvoiceTerms() != null) {
				job.setInvoiceTerms(jobRequest.getInvoiceTerms());
			}
			if(jobRequest.getInvoiceStyle() != null) {
				job.setInvoiceStyle(jobRequest.getInvoiceStyle());
			}
			if(jobRequest.getInvoiceBatch() != null) {
				job.setInvoiceBatch(jobRequest.getInvoiceBatch());
			}
			if(jobRequest.getInvoiceGrouping() != null) {
				job.setInvoiceGrouping(jobRequest.getInvoiceGrouping());
			}
			
//			if(jobRequest.getContractContactId() != null) {
				job.setContractContactId(jobRequest.getContractContactId());
//			}
			if(jobRequest.getTaxExempt() != null) {
				job.setTaxExempt(jobRequest.getTaxExempt());
			}
			if(jobRequest.getPoNumber() != null){
				job.setPoNumber(jobRequest.getPoNumber());
			}
			
			if(jobRequest.getExpirationDate() != null){
				job.setExpirationDate(jobRequest.getExpirationDate());
			}
			
			if(jobRequest.getExpirationReason() != null){
				if(jobRequest.getExpirationReason().equals("")){
					job.setExpirationReason("");
				} else {
					job.setExpirationReason(jobRequest.getExpirationReason());
				}
			}
			
		
			if(jobRequest.getBuildingType() != null){
				job.setBuildingType(jobRequest.getBuildingType());
			}
			if(jobRequest.getOurVendorNbr() != null){
				job.setOurVendorNbr(jobRequest.getOurVendorNbr());
			}
			if(jobRequest.getServiceDescription() != null){
				job.setServiceDescription(jobRequest.getServiceDescription());
			}
			if(jobRequest.getEquipment() != null){
				job.setEquipment(jobRequest.getEquipment());
			}
			
			if(jobRequest.getWasherNotes() != null){
				if(jobRequest.getWasherNotes().equals("")){
					job.setWasherNotes(null);
				} else {
					job.setWasherNotes(jobRequest.getWasherNotes());
				}
			} 
			
			if(jobRequest.getBillingContactId() != null){
				job.setBillingContactId(jobRequest.getBillingContactId());
			}
			if(jobRequest.getBillingNotes() != null) {
				if(jobRequest.getBillingNotes().equals("")) {
					job.setBillingNotes(null);
				} else {
					job.setBillingNotes(jobRequest.getBillingNotes());
				}
			}
			

			
			if(jobRequest.getOmNotes() != null) {
				if(jobRequest.getOmNotes().equals("")) {
					job.setOmNotes(null);
				} else {
					job.setOmNotes(jobRequest.getOmNotes());
				}
			}
			
//			if(jobRequest.getActivationDate() == null) {
//				job.setActivationDate(today);
//			} else {
//				job.setActivationDate(jobRequest.getActivationDate());
//			}
			
			if(jobRequest.getStartDate() != null){
				job.setStartDate(jobRequest.getStartDate());
			}
			
			if(jobRequest.getCancelDate() != null){
				job.setCancelDate(jobRequest.getCancelDate());
			}
			
			if(jobRequest.getCancelReason() != null){
				job.setCancelReason(jobRequest.getCancelReason());
			}
			
			if(jobRequest.getSiteContact() != null) {
				job.setSiteContact(jobRequest.getSiteContact());
			}
			
			if(jobRequest.getJobContactId() != null) {
				job.setJobContactId(jobRequest.getJobContactId());
			}
			
			if(jobRequest.getJobTypeId() != null) {
				job.setJobTypeId(jobRequest.getJobTypeId());
			}
			
			if(job.getJobTypeId()!= null && job.getJobTypeId() == 0){
				job.setJobTypeId(null);
			}
			if(jobRequest.getRequestSpecialScheduling() != null) {
				job.setRequestSpecialScheduling(jobRequest.getRequestSpecialScheduling());
			}
//			if(jobRequest.getRepeatScheduleAnnually() != null) {
				job.setRepeatScheduleAnnually(1);
//			}
			if(jobRequest.getPaymentTerms() != null){
				if(jobRequest.getPaymentTerms().equals("")){
					job.setPaymentTerms(null);
				} else {
					job.setPaymentTerms(jobRequest.getPaymentTerms());
				}
			}
			
		
			
			job.setUpdatedBy(sessionUser.getUserId());

//			job.setUpdatedDate(today);
			

			logger.log(Level.DEBUG, "Job servlet Add Data:");
			logger.log(Level.DEBUG, job.toString());
			JobUtils.updateJobHistory(conn, job.getJobId());
			job.update(conn, key);
			responseCode = ResponseCode.SUCCESS;
		} else {
			logger.log(Level.DEBUG, "Doing Edit Fail");
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		JobDetailResponse jobDetailResponse = new JobDetailResponse(conn, job.getJobId());
//		JobResponse codeResponse = new JobResponse(job, webMessages);
		logger.log(Level.DEBUG, "Response:");
		logger.log(Level.DEBUG, "responseCode: " + responseCode);
		logger.log(Level.DEBUG, "jobDetailResponse: " + jobDetailResponse);
		logger.log(Level.DEBUG, "response: " + response);

		jobDetailResponse.setWebMessages(messages);
		super.sendResponse(conn, response, responseCode, jobDetailResponse);

		return job;
	}
	
	protected WebMessages validateAdd(Connection conn, JobRequest jobRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredAddFields(jobRequest);
		logger.log(Level.DEBUG, "validateAdd");
		String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
		if ( missingFields.isEmpty() ) {
			if ( ! JobUtils.isValidDLPct(jobRequest.getDirectLaborPct())) {
				webMessages.addMessage("directLabotPct", "Invalid DL Pct");
			}
		} else {
//			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
				logger.log(Level.DEBUG, "field:"+field+":"+messageText);
			}
		}

		return webMessages;
	}


	protected WebMessages validateUpdate(Connection conn, Job key, JobRequest jobRequest) throws RecordNotFoundException, Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredUpdateFields(jobRequest);
		if ( missingFields.isEmpty() ) {
			if ( ! JobUtils.isValidDLPct(jobRequest.getDirectLaborPct())) {
				webMessages.addMessage("actDLPct", "Invalid DL Pct");
			}
		} else {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		// if we "select" the key, and it isn't found, a "RecordNotFoundException" is thrown.
		// That exception will propagate up the tree until it turns into a 404 message sent to the client
		Job testKey = (Job)key.clone(); 
		testKey.selectOne(conn);
		return webMessages;
	}

	
	public class JobProcessException extends Exception {
		public JobProcessException(String string) {
			// TODO Auto-generated constructor stub
		}

		private static final long serialVersionUID = 1L;		
	}
}
