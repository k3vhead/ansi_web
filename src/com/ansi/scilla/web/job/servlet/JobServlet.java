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
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.exceptions.ActionNotPermittedException;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.common.exceptions.InvalidJobStatusException;
import com.ansi.scilla.common.jobticket.JobStatus;
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
import com.ansi.scilla.web.common.utils.UserPermission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.job.request.JobRequest;
import com.ansi.scilla.web.job.request.JobRequestAction;
import com.ansi.scilla.web.job.response.JobDetailResponse;
import com.ansi.scilla.web.quote.response.QuoteResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class JobServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "job";
	
	// these update types are at the bottom of quoteMaintenance.jsp
	public static final String UPDATE_TYPE_IS_PROPOSAL_PANEL = "proposal";
	public static final String UPDATE_TYPE_IS_ACTIVATION_PANEL = "activation";
	public static final String UPDATE_TYPE_IS_INVOICE_PANEL = "invoice";
	public static final String UPDATE_TYPE_IS_SCHEDULE_PANEL = "schedule";
	public static final String UPDATE_TYPE_IS_NEW_JOB = "add";
	
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
			SessionData sessionData = AppUtils.validateSession(request, Permission.QUOTE_READ);
			List<UserPermission> permissionList = sessionData.getUserPermissionList();
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);	
			
			if( url.getId() == null || ! StringUtils.isBlank(url.getCommand())) {	
				throw new ResourceNotFoundException();
			} else if (url.getId() != null) {
				jobDetailResponse = new JobDetailResponse(conn, url.getId(), permissionList);
				QuoteResponse quoteResponse = new QuoteResponse(conn, url.getId(), permissionList);
				quoteResponse.getQuote().setJobDetail(jobDetailResponse);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, quoteResponse);
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
//		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		QuoteResponse quoteResponse = new QuoteResponse();
		WebMessages webMessages = new WebMessages();
		ResponseCode responseCode = null;
		
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			// this is the minimum necessary permission. More granular checks will be made later
			SessionData sessionData = AppUtils.validateSession(request, Permission.QUOTE_CREATE);
			List<UserPermission> permissionList = sessionData.getUserPermissionList();
			String jsonString = super.makeJsonString(request);
			url = new AnsiURL(request, REALM, new String[] {ACTION_IS_ADD});
			if ( url.getId() == null && StringUtils.isBlank(url.getCommand())) {
				throw new ResourceNotFoundException();
			} 
			
			Job job = new Job();
			if ( url.getId() != null ) {
				job = selectJob(conn, url.getId());
			}
			logger.log(Level.DEBUG, jsonString);
			JobRequest jobRequest = new JobRequest(jsonString);
			
			try {
				Quote quote = selectQuote(conn, job, jobRequest);
				JobRequestAction action = trafficCop(conn, response, sessionData, job, quote, jobRequest);					
				
				
				// After we delete, there are no job details to return, but we still need the job headers
				// so the page can redisplay them
				if ( action == JobRequestAction.DELETE_JOB ) {
					quoteResponse = new QuoteResponse(conn, quote, webMessages, permissionList);
//					jobDetailResponse = new JobDetailResponse();
//					List<JobHeader> jobHeaderList = JobHeader.getJobHeaderList(conn, quote, permissionList);	
//					if ( jobHeaderList.size() == 1 ) {
//						jobHeaderList.get(0).setCanDelete(false); // don't delete the only job you've got
//					}
//					jobDetailResponse.setJobHeaderList(jobHeaderList);
				} else {
					JobDetailResponse jobDetail = new JobDetailResponse(conn, url.getId(), permissionList);
					quoteResponse = new QuoteResponse(conn, url.getId(), permissionList);
					quoteResponse.getQuote().setJobDetail(jobDetail);
				}
			} catch ( JobProcessException e ) {
				conn.rollback();
				responseCode = ResponseCode.SYSTEM_FAILURE;
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, e.getMessage());
				quoteResponse.setWebMessages(webMessages);
				super.sendResponse(conn, response, responseCode, quoteResponse);
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

	
	
	private JobRequestAction trafficCop(Connection conn, HttpServletResponse response, SessionData sessionData, Job job, Quote quote, JobRequest jobRequest) throws NotAllowedException, Exception {
		SessionUser user = sessionData.getUser();
		JobRequestAction action = null;
		validateStateTransition(quote, sessionData);
		if ( StringUtils.isBlank(jobRequest.getAction()) ) {
			// THis is a panel edit update
			if ( jobRequest.getUpdateType().equalsIgnoreCase(UPDATE_TYPE_IS_PROPOSAL_PANEL)) {
				makeProposalUpdate(conn, response, user, job, jobRequest, sessionData.getUserPermissionList());				
			} else if ( jobRequest.getUpdateType().equalsIgnoreCase(UPDATE_TYPE_IS_ACTIVATION_PANEL)) {
				makeActivationUpdate(conn, response, user, job, jobRequest, sessionData.getUserPermissionList());
			} else if ( jobRequest.getUpdateType().equalsIgnoreCase(UPDATE_TYPE_IS_INVOICE_PANEL)) {
				makeInvoiceUpdate(conn, response, user, job, jobRequest, sessionData.getUserPermissionList());
			} else if ( jobRequest.getUpdateType().equalsIgnoreCase(UPDATE_TYPE_IS_SCHEDULE_PANEL)) {
				makeScheduleUpdate(conn, response, user, job, jobRequest, sessionData.getUserPermissionList());
			} else if ( jobRequest.getUpdateType().equalsIgnoreCase(UPDATE_TYPE_IS_NEW_JOB)) {
				makeNewJob(conn, response, user, job, jobRequest, sessionData.getUserPermissionList());
			} else {
				WebMessages webMessages = new WebMessages();
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Update Type");
				throw new JobProcessException("Invalid Update Type");
			}
		} else {
			// this is an action update
			action = JobRequestAction.valueOf(jobRequest.getAction());
			if (action.equals(JobRequestAction.CANCEL_JOB)) {				
				doCancelJob(conn, job, jobRequest, user, response, sessionData.getUserPermissionList());					
			} else if ( action.equals(JobRequestAction.ACTIVATE_JOB)) {
				doActivateJob(conn, job, jobRequest, user, response, sessionData.getUserPermissionList());				
			} else if ( action.equals(JobRequestAction.DELETE_JOB)) {
				doDeleteJob(conn, job, jobRequest, user, response, sessionData.getUserPermissionList());				
			} else if ( action.equals(JobRequestAction.SCHEDULE_JOB)) {
				doScheduleJob(conn, job, jobRequest, user, response, sessionData.getUserPermissionList());
//			} else if ( action.equals(JobRequestAction.REPEAT_JOB)) {
//				doRepeatJob(conn, job, jobRequest, user, response);
			} else {
				WebMessages webMessages = new WebMessages();
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Job Action");
				throw new JobProcessException("Invalid Job Action");
			}
		}
		
		return action;
	}

	
	
	/**
	 * Throws a "you can't do this" if your permission set doesn't match the current status of the quuote
	 * @param quote
	 * @param sessionData
	 * @throws NotAllowedException
	 */
	private void validateStateTransition(Quote quote, SessionData sessionData) throws NotAllowedException {
		if ( quote.getProposalDate() == null ) {
			AppUtils.checkPermission(Permission.QUOTE_CREATE, sessionData.getUserPermissionList());
		} else {
			AppUtils.checkPermission(Permission.QUOTE_UPDATE, sessionData.getUserPermissionList());
		}
	}

	private void makeProposalUpdate(Connection conn, HttpServletResponse response, SessionUser user, Job job, JobRequest jobRequest, List<UserPermission> permissionList) throws Exception {
		WebMessages webMessages = new WebMessages();
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		ResponseCode responseCode = null;
		
		try {
			webMessages = jobRequest.validateProposalUpdate();
			if ( webMessages.isEmpty() ) {
				populateJobProposal(job, jobRequest);
				updateJob(conn, user, job);
				conn.commit();
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
				responseCode = ResponseCode.SUCCESS;
				jobDetailResponse = new JobDetailResponse(conn, job.getJobId(), permissionList);
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}
		} catch (Exception e) {
			AppUtils.logException(e);
			conn.rollback();
			responseCode = ResponseCode.SYSTEM_FAILURE;
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "System Failure");
		}
		
		QuoteResponse quoteResponse = new QuoteResponse(conn, job.getJobId(), permissionList);
		quoteResponse.getQuote().setJobDetail(jobDetailResponse);
		quoteResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, quoteResponse);
	}

	
	private void populateJobProposal(Job job, JobRequest jobRequest) {
		job.setJobFrequency(jobRequest.getJobFrequency());
		job.setJobNbr(jobRequest.getJobNbr());
		job.setPricePerCleaning(jobRequest.getPricePerCleaning());
		job.setServiceDescription(jobRequest.getServiceDescription());		
	}

	

	
	
	
	

	
	private void makeActivationUpdate(Connection conn, HttpServletResponse response, SessionUser user, Job job, JobRequest jobRequest, List<UserPermission> permissionList) throws Exception {
		WebMessages webMessages = new WebMessages();
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		ResponseCode responseCode = null;
		
		try {
			webMessages = jobRequest.validateActivationUpdate();
			if ( webMessages.isEmpty() ) {
				populateJobActivation(job, jobRequest);
				updateJob(conn, user, job);
				conn.commit();
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
				responseCode = ResponseCode.SUCCESS;
				jobDetailResponse = new JobDetailResponse(conn, job.getJobId(), permissionList);
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}
		} catch (Exception e) {
			AppUtils.logException(e);
			conn.rollback();			
			responseCode = ResponseCode.SYSTEM_FAILURE;
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "System Failure");
		}
		
		QuoteResponse quoteResponse = new QuoteResponse(conn, job.getJobId(), permissionList);
		quoteResponse.getQuote().setJobDetail(jobDetailResponse);
		quoteResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, quoteResponse);
	}

	
	private void populateJobActivation(Job job, JobRequest jobRequest) {
		job.setRequestSpecialScheduling(jobRequest.getRequestSpecialScheduling());
		job.setDirectLaborPct(jobRequest.getDirectLaborPct());
		job.setBudget(jobRequest.getBudget());
		job.setFloors(jobRequest.getFloors());
		job.setEquipment(jobRequest.getEquipment());
		job.setWasherNotes(jobRequest.getWasherNotes());
		job.setOmNotes(jobRequest.getOmNotes());
		job.setBillingNotes(jobRequest.getBillingNotes());		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	private void makeInvoiceUpdate(Connection conn, HttpServletResponse response, SessionUser user, Job job, JobRequest jobRequest, List<UserPermission> permissionList) throws Exception {
		WebMessages webMessages = new WebMessages();
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		ResponseCode responseCode = null;
		
		try {
			webMessages = jobRequest.validateInvoiceUpdate();
			if ( webMessages.isEmpty() ) {
				populateInvoiceUpdate(job, jobRequest);
				updateJob(conn, user, job);
				conn.commit();
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
				responseCode = ResponseCode.SUCCESS;
				jobDetailResponse = new JobDetailResponse(conn, job.getJobId(), permissionList);
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}
		} catch (Exception e) {
			AppUtils.logException(e);
			conn.rollback();
			responseCode = ResponseCode.SYSTEM_FAILURE;
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "System Failure");
		}
		
		QuoteResponse quoteResponse = new QuoteResponse(conn, job.getJobId(), permissionList);
		quoteResponse.getQuote().setJobDetail(jobDetailResponse);
		quoteResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, quoteResponse);
	}

	
	private void populateInvoiceUpdate(Job job, JobRequest jobRequest) {
		job.setPoNumber(jobRequest.getPoNumber());
		job.setOurVendorNbr(jobRequest.getOurVendorNbr());
		job.setExpirationDate(jobRequest.getExpirationDate());
		job.setExpirationReason(jobRequest.getExpirationReason());		
	}

	
	
	
	

	
	private void makeScheduleUpdate(Connection conn, HttpServletResponse response, SessionUser user, Job job, JobRequest jobRequest, List<UserPermission> permissionList) throws Exception {
		WebMessages webMessages = new WebMessages();
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		ResponseCode responseCode = null;
		
		try {
			webMessages = jobRequest.validateScheduleUpdate();
			if ( webMessages.isEmpty() ) {
				populateScheduleUpdate(job, jobRequest);
				updateJob(conn, user, job);
				conn.commit();
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
				responseCode = ResponseCode.SUCCESS;
				jobDetailResponse = new JobDetailResponse(conn, job.getJobId(), permissionList);
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}
		} catch (Exception e) {
			AppUtils.logException(e);
			conn.rollback();
			responseCode = ResponseCode.SYSTEM_FAILURE;
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "System Failure");
		}
		
		QuoteResponse quoteResponse = new QuoteResponse(conn, job.getJobId(), permissionList);
		quoteResponse.getQuote().setJobDetail(jobDetailResponse);
		quoteResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, quoteResponse);
	}

	
	private void populateScheduleUpdate(Job job, JobRequest jobRequest) {
		job.setRepeatScheduleAnnually(jobRequest.getRepeatScheduleAnnually());			
	}

	
	
	
	

	

	
	private void makeNewJob(Connection conn, HttpServletResponse response, SessionUser user, Job job, JobRequest jobRequest, List<UserPermission> permissionList) throws Exception {
		WebMessages webMessages = new WebMessages();
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		ResponseCode responseCode = null;
		
		try {
			webMessages = jobRequest.validateNewJob(conn);
			if ( webMessages.isEmpty() ) {
				populateNewJob(job, jobRequest);
				Integer newJobId = insertJob(conn, user, job);
				conn.commit();
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
				responseCode = ResponseCode.SUCCESS;
				jobDetailResponse = new JobDetailResponse(conn, newJobId, permissionList);
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}
		} catch (Exception e) {
			AppUtils.logException(e);
			conn.rollback();
			responseCode = ResponseCode.SYSTEM_FAILURE;
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "System Failure");
		}
		
		QuoteResponse quoteResponse = new QuoteResponse(conn, job.getJobId(), permissionList);
		quoteResponse.getQuote().setJobDetail(jobDetailResponse);
		quoteResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, quoteResponse);
	}	
	
	private void populateNewJob(Job job, JobRequest jobRequest) throws InvalidJobStatusException {
		populateJobProposal(job, jobRequest);
		populateJobActivation(job, jobRequest);
		populateInvoiceUpdate(job, jobRequest);
		populateScheduleUpdate(job, jobRequest);

		job.setBillingContactId(jobRequest.getBillingContactId());
		job.setBuildingType(jobRequest.getBuildingType());
		job.setContractContactId(jobRequest.getContractContactId());
		job.setDivisionId(jobRequest.getDivisionId());
		job.setInvoiceBatch(jobRequest.getInvoiceBatch());
		job.setInvoiceGrouping(jobRequest.getInvoiceGrouping());
		job.setInvoiceStyle(jobRequest.getInvoiceStyle());
		job.setInvoiceTerms(jobRequest.getInvoiceTerms());
		job.setJobContactId(jobRequest.getJobContactId());		
		job.setJobTypeId(jobRequest.getJobTypeId());
		job.setPaymentTerms(jobRequest.getPaymentTerms());
		job.setQuoteId(jobRequest.getQuoteId());
		job.setSiteContact(jobRequest.getSiteContact());
		job.setStatus(JobStatus.NEW.code());
		job.setTaxExempt(jobRequest.getTaxExempt());
		job.setTaxExemptReason(jobRequest.getTaxExemptReason());		
	}

	
	

	
	private Job selectJob(Connection conn, Integer jobId) throws RecordNotFoundException, Exception {
		Job job = new Job();
		job.setJobId(jobId);
		job.selectOne(conn);
		return job;
	}

	
	
	private Quote selectQuote(Connection conn, Job job, JobRequest jobRequest) throws RecordNotFoundException, Exception {
		Integer quoteId = job == null ? jobRequest.getQuoteId() : job.getQuoteId();
		Quote quote =new Quote();
		quote.setQuoteId(quoteId);
		quote.selectOne(conn);
		return quote;
	}

	
	private void updateJob(Connection conn, SessionUser user, Job job) throws Exception {
		Job key = new Job();
		key.setJobId(job.getJobId());
		job.setUpdatedBy(user.getUserId());
		job.update(conn, key);
	}
	
	
	
	private Integer insertJob(Connection conn, SessionUser user, Job job) throws Exception {
		job.setAddedBy(user.getUserId());
		job.setUpdatedBy(user.getUserId());
		Integer jobId = job.insertWithKey(conn);
		return jobId;
	}

	

	
	private void doCancelJob(Connection conn, Job job, JobRequest jobDetailRequest, SessionUser sessionUser, HttpServletResponse response, List<UserPermission> permissionList) throws Exception  {
		Integer jobId = job.getJobId();
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		WebMessages webMessages = new WebMessages();
		ResponseCode responseCode = null;
		
		if ( StringUtils.isBlank(jobDetailRequest.getCancelReason())) {
			webMessages.addMessage("cancelReason", "Required Field");
		}
		if ( jobDetailRequest.getCancelDate() == null ) {
			webMessages.addMessage("cancelDate", "Required Field");
		}
		if ( webMessages.isEmpty() ) {
			try {
				JobUtils.cancelJob(conn, jobId, jobDetailRequest.getCancelDate(), jobDetailRequest.getCancelReason(), sessionUser.getUserId());
				conn.commit();
				responseCode = ResponseCode.SUCCESS;
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
				jobDetailResponse = new JobDetailResponse(conn,jobId, permissionList);
			} catch ( RecordNotFoundException e) {
				responseCode = ResponseCode.EDIT_FAILURE;
				webMessages.addMessage("cancelDate", "Invalid Job ID");
			} catch (Exception e) {
				AppUtils.logException(e);
				conn.rollback();
				responseCode = ResponseCode.SYSTEM_FAILURE;
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "System Error");
			} 			
		} else { 
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		QuoteResponse quoteResponse = new QuoteResponse(conn, job.getJobId(), permissionList);
		quoteResponse.getQuote().setJobDetail(jobDetailResponse);
		quoteResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, quoteResponse);
	}

	
	private void doDeleteJob(Connection conn, Job job, JobRequest jobDetailRequest, SessionUser sessionUser, HttpServletResponse response, List<UserPermission> permissionList) throws Exception {
		Integer jobId = job.getJobId();
//		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		WebMessages webMessages = jobDetailRequest.validateDeleteJob(job);
		ResponseCode responseCode = null;
		
		if ( webMessages.isEmpty() ) {
			try {
				JobUtils.deleteJob(conn, jobId, sessionUser.getUserId());
				JobUtils.renumberJobs(conn, job.getQuoteId(), sessionUser.getUserId());
				conn.commit();
				responseCode = ResponseCode.SUCCESS;
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
//				jobDetailResponse = new JobDetailResponse(conn,jobId, permissionList);
			} catch ( RecordNotFoundException e) {
				responseCode = ResponseCode.EDIT_FAILURE;
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Job ID");
			} catch (Exception e) {
				AppUtils.logException(e);
				conn.rollback();
				responseCode = ResponseCode.SYSTEM_FAILURE;
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "System Error");
			} 			
		} else { 
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		QuoteResponse quoteResponse = new QuoteResponse(conn, job.getJobId(), permissionList);
//		All the other actions populate a jobDetail here. Since we're deleting, there is no job to detail
//		quoteResponse.getQuote().setJobDetail(jobDetailResponse);
		quoteResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, quoteResponse);
	}

	

	private void doActivateJob(Connection conn, Job job, JobRequest jobDetailRequest, SessionUser sessionUser, HttpServletResponse response, List<UserPermission> permissionList) throws Exception {
		Integer jobId = job.getJobId();
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		WebMessages webMessages = new WebMessages();
		ResponseCode responseCode = null;
		
		try {
			if ( jobDetailRequest.getStartDate() == null ) {
				webMessages.addMessage("startDate", "Required Field");
			}
			if ( jobDetailRequest.getActivationDate() == null ) {
				webMessages.addMessage("activationDate", "Required Field");
			}
			if ( webMessages.isEmpty() ) {
				try {
					logger.log(Level.DEBUG, "JobServlet 156");
					JobUtils.activateJob(conn, jobId, jobDetailRequest.getStartDate(), jobDetailRequest.getActivationDate(), sessionUser.getUserId());
					conn.commit();
					responseCode = ResponseCode.SUCCESS;
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
				} catch ( RecordNotFoundException e) {
					conn.rollback();
					responseCode = ResponseCode.EDIT_FAILURE;
					webMessages.addMessage("activationDate", "Invalid Job ID");
				} catch (Exception e) {
					AppUtils.logException(e);
					conn.rollback();
					responseCode = ResponseCode.SYSTEM_FAILURE;
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "System Error");
				}
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}
			jobDetailResponse = new JobDetailResponse(conn,jobId, permissionList);
		} catch ( RecordNotFoundException e) {
			responseCode = ResponseCode.EDIT_FAILURE;
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Job ID");
		}
		QuoteResponse quoteResponse = new QuoteResponse(conn, job.getJobId(), permissionList);
		quoteResponse.getQuote().setJobDetail(jobDetailResponse);
		quoteResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, quoteResponse);
		
	}


	private void doScheduleJob(Connection conn, Job job, JobRequest jobDetailRequest, SessionUser sessionUser, HttpServletResponse response, List<UserPermission> permissionList) throws Exception {
		Integer jobId = job.getJobId();
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		WebMessages webMessages = new WebMessages();
		ResponseCode responseCode = null;
		
		try {
			if ( jobDetailRequest.getStartDate() == null ) {
				webMessages.addMessage("startDate", "Required Field");
			}
			if ( webMessages.isEmpty() ) {
				try {
					logger.log(Level.DEBUG, "JobServlet 193");
					JobUtils.rescheduleJob(conn, jobId, jobDetailRequest.getStartDate(), sessionUser.getUserId());
					conn.commit();
					responseCode = ResponseCode.SUCCESS;
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
				} catch ( RecordNotFoundException e) {
					conn.rollback();
					responseCode = ResponseCode.EDIT_FAILURE;
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Job ID");
				} catch ( ActionNotPermittedException e) {
					conn.rollback();
					responseCode = ResponseCode.EDIT_FAILURE;
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Action Not Permitted for this job");
				} catch (Exception e) {
					AppUtils.logException(e);
					conn.rollback();
					responseCode = ResponseCode.SYSTEM_FAILURE;
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "System Error");
				}
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}
			jobDetailResponse = new JobDetailResponse(conn,jobId, permissionList);
		} catch ( RecordNotFoundException e) {
			responseCode = ResponseCode.EDIT_FAILURE;
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Job ID");
		}
		QuoteResponse quoteResponse = new QuoteResponse(conn, job.getJobId(), permissionList);
		quoteResponse.getQuote().setJobDetail(jobDetailResponse);
		quoteResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, quoteResponse);
		
	}

	/*
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
	*/
	
	
	protected Job doAdd(Connection conn, JobRequest jobRequest, SessionUser sessionUser, List<UserPermission> permissionList, HttpServletResponse response) throws Exception {
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		ResponseCode responseCode = null;
		Date today = new Date();
		Job job = new Job();

		WebMessages webMessages = validateAdd(conn, jobRequest);
		if(jobRequest.getQuoteId() != null && jobRequest.getQuoteId() ==0){
			responseCode = ResponseCode.EDIT_FAILURE;
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "No Quote ID, Try saving quote first");
		} else if (webMessages.isEmpty()) {
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
			jobDetailResponse = new JobDetailResponse(conn, job.getJobId(), permissionList);
		} else {
			responseCode = ResponseCode.EDIT_FAILURE;
		}
//		JobDetailResponse jobDetailResponse = new JobDetailResponse(conn, job.getJobId());
//		JobResponse jobResponse = new JobResponse(job, messages);

		QuoteResponse quoteResponse = new QuoteResponse(conn, job.getJobId(), permissionList);
		quoteResponse.getQuote().setJobDetail(jobDetailResponse);
		quoteResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, quoteResponse);		
		return job;
	}
	
	protected Job doUpdate(Connection conn, Job key,JobRequest jobRequest, SessionUser sessionUser, List<UserPermission> permissionList, HttpServletResponse response) throws Exception {
		WebMessages webMessages = new WebMessages();
		ResponseCode responseCode = null;
		

		Job job = new Job();
		job.setJobId(key.getJobId());
		job.selectOne(conn);

		webMessages = validateUpdate(conn, key, jobRequest);
		if (webMessages.isEmpty()) {
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
		JobDetailResponse jobDetailResponse = new JobDetailResponse(conn, job.getJobId(), permissionList);
//		JobResponse codeResponse = new JobResponse(job, webMessages);
		logger.log(Level.DEBUG, "Response:");
		logger.log(Level.DEBUG, "responseCode: " + responseCode);
		logger.log(Level.DEBUG, "jobDetailResponse: " + jobDetailResponse);
		logger.log(Level.DEBUG, "response: " + response);


		QuoteResponse quoteResponse = new QuoteResponse(conn, job.getJobId(), permissionList);
		quoteResponse.getQuote().setJobDetail(jobDetailResponse);
		quoteResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, quoteResponse);

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
			super(string);
		}

		private static final long serialVersionUID = 1L;		
	}
}
