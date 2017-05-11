package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.jobticket.JobUtils;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.MessageKey;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.request.JobDetailRequest;
import com.ansi.scilla.web.request.JobRequest;
import com.ansi.scilla.web.request.QuoteRequest;
import com.ansi.scilla.web.request.JobDetailRequest.JobDetailRequestAction;
import com.ansi.scilla.web.response.job.JobDetailResponse;
import com.ansi.scilla.web.response.job.JobResponse;
import com.ansi.scilla.web.response.quote.QuoteResponse;
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
			AppUtils.validateSession(request, Permission.JOB, PermissionLevel.PERMISSION_LEVEL_IS_READ);
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
		String url2 = request.getRequestURI();
		SessionUser sessionUser = AppUtils.getSessionUser(request);
		System.out.println("Session user: "+sessionUser);
		WebMessages messages = new WebMessages();
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		ResponseCode responseCode = null;
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			System.out.println(jsonString);
			
			int idx = url2.indexOf("/job/");
			String myString = url2.substring(idx + "/job/".length());				
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];
			
			System.out.println("Servlet Output: [0]"+urlPieces[0]);
			
			JobRequest jobRequest = new JobRequest(jsonString);
			
			if ( command.equalsIgnoreCase("add") ) {
				
				doAdd(conn, jobRequest, sessionUser, response);
			} /*else if ( action.equals(JobDetailRequestAction.UPDATE_JOB) ){
								
				try {
					Job key = new Job();
					key.setJobId(Integer.parseInt(urlPieces[1]));
					//key.selectOne(conn);
					
					System.out.println("This is the key:");
					System.out.println(key);
					System.out.println("************");
					
					//key.setRevision((urlPieces[2]));
					System.out.println("Trying to do update");
					Job job = doUpdate(conn, key, jobRequest, sessionUser, response);
					String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
					responseCode = ResponseCode.SUCCESS;
					messages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
				} catch ( RecordNotFoundException e ) {
					System.out.println("Doing 404");
					System.out.println(e);
					super.sendNotFound(response);						
				} catch ( Exception e) {
					System.out.println("Doing SysFailure");
					responseCode = ResponseCode.SYSTEM_FAILURE;
					AppUtils.logException(e);
					String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
					messages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
				}
				
				
			} */else {
			
				url = new AnsiURL(request, "job", (String[])null);
				
				System.out.println("URL:"+url);
				
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
					} else if ( action.equals(JobDetailRequestAction.UPDATE_JOB)) {
						Job key = new Job();
						key.setJobId(url.getId());
						//key.selectOne(conn);
						
						System.out.println("This is the key:");
						System.out.println(key);
						System.out.println("************");
						
						//key.setRevision((urlPieces[2]));
						System.out.println("Trying to do update");
						Job job = doUpdate(conn, key, jobRequest, sessionUser, response);
						String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
						responseCode = ResponseCode.SUCCESS;
						messages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
						JobResponse jobResponse = new JobResponse(job, messages);
						super.sendResponse(conn, response, responseCode, jobResponse);		

					}
				} catch ( RecordNotFoundException e ) {
					System.out.println("Doing 404");
					System.out.println(e);
					super.sendNotFound(response);						
				} catch ( IllegalArgumentException e) {
					conn.rollback();
					messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Missing Required Data: action");
					jobDetailResponse.setWebMessages(messages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, jobDetailResponse);
				}
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
	
	protected Job doAdd(Connection conn, JobRequest jobRequest, SessionUser sessionUser, HttpServletResponse response) throws Exception {
		ResponseCode responseCode = null;
		Date today = new Date();
		Job job = new Job();

		WebMessages messages = validateAdd(conn, jobRequest);
		if (messages.isEmpty()) {
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
			
			if(jobRequest.getActivationDate() == null) {
				job.setActivationDate(today);
			} else {
				job.setActivationDate(jobRequest.getActivationDate());
			}
			
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
			
			if(job.getJobTypeId() == 0){
				job.setJobTypeId(null);
			}
			
			job.setRequestSpecialScheduling(jobRequest.getRequestSpecialScheduling());
			job.setRepeatScheduleAnnually(jobRequest.getRepeatScheduleAnnually());
			
			if(jobRequest.getPaymentTerms() != null){
				job.setPaymentTerms(jobRequest.getPaymentTerms());
			}
			
			job.setUpdatedBy(sessionUser.getUserId());

			job.setUpdatedDate(today);
			

			System.out.println("Job servlet Add Data:");
			System.out.println(job.toString());
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
		} else {
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		JobResponse jobResponse = new JobResponse(job, messages);
		super.sendResponse(conn, response, responseCode, jobResponse);		
		return job;
	}
	
	protected Job doUpdate(Connection conn, Job key,JobRequest jobRequest, SessionUser sessionUser, HttpServletResponse response) throws Exception {
		WebMessages messages = new WebMessages();
		ResponseCode responseCode = null;
		Date today = new Date();
		

		Job job = new Job();
		job.setJobId(key.getJobId());
		job.selectOne(conn);

		WebMessages webMessages = validateUpdate(conn, key, jobRequest);
		if (webMessages.isEmpty()) {
			if(jobRequest.getJobFrequency() != null) {
				job.setJobFrequency(jobRequest.getJobFrequency());
			}
			
			if(jobRequest.getStatus() != null) {
				job.setStatus(jobRequest.getStatus());
			}
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
				job.setContractContactId(key.getContractContactId());
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
			
//			if(jobRequest.getBillingContactId() != null){
//				job.setBillingContactId(jobRequest.getBillingContactId());
//			}
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
			
//			if(jobRequest.getSiteContact() != null) {
//				job.setSiteContact(jobRequest.getSiteContact());
//			}
//			
//			if(jobRequest.getJobContactId() != null) {
//				job.setJobContactId(jobRequest.getJobContactId());
//			}
//			
			if(jobRequest.getJobTypeId() != null) {
				job.setJobTypeId(jobRequest.getJobTypeId());
			}
			
			if(job.getJobTypeId() == 0){
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
			

			System.out.println("Job servlet Add Data:");
			System.out.println(job.toString());
			job.update(conn, key);
			responseCode = ResponseCode.SUCCESS;
		} else {
			System.out.println("Doing Edit Fail");
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		JobResponse codeResponse = new JobResponse(job, webMessages);
		System.out.println("Response:");
		System.out.println("responseCode: " + responseCode);
		System.out.println("codeResponse: " + codeResponse);
		System.out.println("response: " + response);

		super.sendResponse(conn, response, responseCode, codeResponse);

		return job;
	}
	
	protected WebMessages validateAdd(Connection conn, JobRequest jobRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredAddFields(jobRequest);
		System.out.println("validateAdd");
		String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
		if ( ! missingFields.isEmpty() ) {
//			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
				System.out.println("field:"+field+":"+messageText);
			}
		}

		return webMessages;
	}

	protected WebMessages validateUpdate(Connection conn, Job key, JobRequest jobRequest) throws RecordNotFoundException, Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredUpdateFields(jobRequest);
		if ( ! missingFields.isEmpty() ) {
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


	
}
