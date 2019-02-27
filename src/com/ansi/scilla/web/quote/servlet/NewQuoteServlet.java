package com.ansi.scilla.web.quote.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.common.exceptions.InvalidJobStatusException;
import com.ansi.scilla.common.jobticket.JobStatus;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.job.query.ContactItem;
import com.ansi.scilla.web.job.request.JobRequest;
import com.ansi.scilla.web.job.response.JobDetailResponse;
import com.ansi.scilla.web.quote.request.NewQuoteRequest;
import com.ansi.scilla.web.quote.request.QuoteRequest;
import com.ansi.scilla.web.quote.response.NewQuoteAddressResponse;
import com.ansi.scilla.web.quote.response.NewQuoteContactResponse;
import com.ansi.scilla.web.quote.response.NewQuoteDisplayResponse;
import com.ansi.scilla.web.quote.response.QuoteResponse;
import com.ansi.scilla.web.quote.response.QuoteResponseItem;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class NewQuoteServlet extends AbstractQuoteServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String url = request.getRequestURI();
		logger.log(Level.DEBUG, "URL: " + url);
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		
		try {
			conn = AppUtils.getDBCPConn();
			SessionData sessionData = AppUtils.validateSession(request, Permission.QUOTE_CREATE);
			conn.setAutoCommit(false);
			
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "Quote Json: " + jsonString);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode jsonNode1 = mapper.readTree(jsonString);
			String action = jsonNode1.get("action").asText();
			
			if ( action.equalsIgnoreCase(NewQuoteRequest.ACTION_IS_VALIDATE)) {
				NewQuoteRequest quoteRequest = StringUtils.isBlank(jsonString) ? new NewQuoteRequest() : new NewQuoteRequest(jsonString);
				logger.log(Level.DEBUG, quoteRequest);
				doValidateQuote(conn, response, sessionData, quoteRequest);
			} else if ( action.equalsIgnoreCase(NewQuoteRequest.ACTION_IS_SAVE) ) {
				NewQuoteRequest quoteRequest = StringUtils.isBlank(jsonString) ? new NewQuoteRequest() : new NewQuoteRequest(jsonString);
				logger.log(Level.DEBUG, quoteRequest);
				doSave(conn, response, sessionData, quoteRequest);
			} else if ( action.equalsIgnoreCase("job")) {
				JobRequest jobRequest = new JobRequest(jsonString);
				logger.log(Level.DEBUG, jobRequest);
				doValidateJob(conn, response, sessionData, jobRequest);
			} else {
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid New Quote Action");
				QuoteResponse quoteResponse = new QuoteResponse();
				quoteResponse.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.SYSTEM_FAILURE, quoteResponse);
			}
			
			
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

	private void doValidateQuote(Connection conn, HttpServletResponse response, SessionData sessionData, NewQuoteRequest quoteRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		if ( quoteRequest.getJobSiteAddressId() != null ) {
			try {
				NewQuoteAddressResponse jobSiteAddressResponse = new NewQuoteAddressResponse();
				jobSiteAddressResponse.makeJobSiteAddressResponse(conn, quoteRequest.getJobSiteAddressId());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, jobSiteAddressResponse);
			} catch (RecordNotFoundException e) {
				webMessages.addMessage(QuoteRequest.JOB_SITE_ADDRESS_ID, "Invalid address");
				NewQuoteAddressResponse jobSiteAddressResponse = new NewQuoteAddressResponse();
				jobSiteAddressResponse.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, jobSiteAddressResponse);
			}
		} else if ( quoteRequest.getBillToAddressId() != null ) {
			try {
				NewQuoteAddressResponse jobSiteAddressResponse = new NewQuoteAddressResponse();
				jobSiteAddressResponse.makeBillToAddressResponse(conn, quoteRequest.getBillToAddressId());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, jobSiteAddressResponse);
			} catch (RecordNotFoundException e) {
				webMessages.addMessage(QuoteRequest.BILL_TO_ADDRESS_ID, "Invalid address");
				NewQuoteAddressResponse jobSiteAddressResponse = new NewQuoteAddressResponse();
				jobSiteAddressResponse.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, jobSiteAddressResponse);
			}
		} else if ( quoteRequest.getJobContactId() != null ) {
			try {
				NewQuoteContactResponse contactResponse = new NewQuoteContactResponse(conn, quoteRequest.getJobContactId());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, contactResponse);
			} catch (RecordNotFoundException e) {
				webMessages.addMessage(QuoteRequest.JOB_CONTACT_ID, "Invalid contact");
				NewQuoteAddressResponse jobSiteAddressResponse = new NewQuoteAddressResponse();
				jobSiteAddressResponse.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, jobSiteAddressResponse);
			}
		} else if ( quoteRequest.getSiteContact() != null ) {
			try {
				NewQuoteContactResponse contactResponse = new NewQuoteContactResponse(conn, quoteRequest.getSiteContact());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, contactResponse);
			} catch (RecordNotFoundException e) {
				webMessages.addMessage(QuoteRequest.SITE_CONTACT, "Invalid contact");
				NewQuoteAddressResponse jobSiteAddressResponse = new NewQuoteAddressResponse();
				jobSiteAddressResponse.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, jobSiteAddressResponse);
			}
		} else if ( quoteRequest.getContractContactId() != null ) {
			try {
				NewQuoteContactResponse contactResponse = new NewQuoteContactResponse(conn, quoteRequest.getContractContactId());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, contactResponse);
			} catch (RecordNotFoundException e) {
				webMessages.addMessage(QuoteRequest.CONTRACT_CONTACT_ID, "Invalid contact");
				NewQuoteAddressResponse jobSiteAddressResponse = new NewQuoteAddressResponse();
				jobSiteAddressResponse.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, jobSiteAddressResponse);
			}
		} else if ( quoteRequest.getBillingContactId() != null ) {
			try {
				NewQuoteContactResponse contactResponse = new NewQuoteContactResponse(conn, quoteRequest.getBillingContactId());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, contactResponse);
			} catch (RecordNotFoundException e) {
				webMessages.addMessage(QuoteRequest.BILLING_CONTACT_ID, "Invalid contact");
				NewQuoteAddressResponse jobSiteAddressResponse = new NewQuoteAddressResponse();
				jobSiteAddressResponse.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, jobSiteAddressResponse);
			}
		} else if ( quoteRequest.hasJobUpdates() || quoteRequest.hasQuoteHeaderUpdates() ) {
			validateQuoteHeader(conn, sessionData, response, quoteRequest);
		} else {
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Quote Request Type");
			QuoteResponse quoteResponse = new QuoteResponse();
			quoteResponse.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.SYSTEM_FAILURE, quoteResponse);
	
		}
	
	}

	private void validateQuoteHeader(Connection conn, SessionData sessionData, HttpServletResponse response, NewQuoteRequest quoteRequest) throws Exception {
		WebMessages webMessages = quoteRequest.validateQuoteHeader(conn);

		QuoteResponse quoteResponse = new QuoteResponse();
		ResponseCode responseCode = webMessages.isEmpty() ? ResponseCode.SUCCESS : ResponseCode.EDIT_FAILURE;
		quoteResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, quoteResponse);
	}

	
	private void doValidateJob(Connection conn, HttpServletResponse response, SessionData sessionData, JobRequest jobRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		ResponseCode responseCode = null;
		Integer newJobId = null;
		Job job = new Job();
		
		try {
			webMessages = jobRequest.validateNewJob(conn);
			if ( webMessages.isEmpty() ) {
				populateNewJob(job, jobRequest);
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
				responseCode = ResponseCode.SUCCESS;
				jobDetailResponse = new JobDetailResponse(conn, newJobId, sessionData.getUserPermissionList());
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}
		} catch (Exception e) {
			AppUtils.logException(e);
			conn.rollback();
			responseCode = ResponseCode.SYSTEM_FAILURE;
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "System Failure");
		}
		QuoteResponse quoteResponse = new QuoteResponse(conn, null, webMessages, sessionData.getUserPermissionList());
		QuoteResponseItem quoteResponseItem = new QuoteResponseItem();
		quoteResponseItem.setJobDetail(jobDetailResponse);
		quoteResponse.setQuote(quoteResponseItem);
		quoteResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, quoteResponse);
	}
	
	

	private void doSave(Connection conn, HttpServletResponse response, SessionData sessionData, NewQuoteRequest quoteRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		try {
			Date today = new Date();
			Quote quote = new Quote();
			SessionUser sessionUser = sessionData.getUser();

			quote.setAddedBy(sessionUser.getUserId());		
			quote.setAddedDate(today);
			quote.setUpdatedBy(sessionUser.getUserId());
			quote.setUpdatedDate(today);

			quote.setBillToAddressId(quoteRequest.getBillToAddressId());
			quote.setJobSiteAddressId(quoteRequest.getJobSiteAddressId());
			quote.setLeadType(quoteRequest.getLeadType());
			quote.setDivisionId(quoteRequest.getDivisionId());
			quote.setAccountType(quoteRequest.getAccountType());
			quote.setManagerId(quoteRequest.getManagerId());
			quote.setTemplateId(0);

			if ( ! StringUtils.isBlank(quoteRequest.getAccountType())) {
				quote.setAccountType(quoteRequest.getAccountType());
			}

			quote.setQuoteNumber(AppUtils.getNextQuoteNumber(conn));
			quote.setRevision("A");
			quote.setSignedByContactId(null);

			logger.log(Level.DEBUG, "new Quote servlet Add Data:");
			logger.log(Level.DEBUG, quote.toString());
			int quoteId = 0;
			try {
				quoteId = quote.insertWithKey(conn);
			} catch ( SQLException e) {
				if ( e.getMessage().contains("duplicate key")) {
					throw new DuplicateEntryException();
				} else {
					AppUtils.logException(e);
					throw e;
				}
			} 
			quote.setQuoteId(quoteId);
			
			
			conn.commit();
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Quote Inserted");
			NewQuoteDisplayResponse quoteResponse = new NewQuoteDisplayResponse();
			quoteResponse.setQuoteId(quoteId);
			quoteResponse.setInvoiceGrouping(quoteRequest.getInvoiceGrouping());
			quoteResponse.setInvoiceStyle(quoteRequest.getInvoiceStyle());
			quoteResponse.setBuildingType(quoteRequest.getBuildingType());
			quoteResponse.setInvoiceBatch(quoteRequest.getInvoiceBatch());
			quoteResponse.setInvoiceTerms(quoteRequest.getInvoiceTerms());
			if ( quoteRequest.getTaxExempt() ) {
				quoteResponse.setTaxExempt(true);
				quoteResponse.setTaxExemptReason(quoteRequest.getTaxExemptReason());
			} else {
				quoteResponse.setTaxExempt(false);
				quoteResponse.setTaxExemptReason(null);
			}			
			quoteResponse.setJobContact(new ContactItem(conn, quoteRequest.getJobContactId()));
			quoteResponse.setSiteContact(new ContactItem(conn, quoteRequest.getSiteContact()));
			quoteResponse.setContractContact(new ContactItem(conn, quoteRequest.getContractContactId()));
			quoteResponse.setBillingContact(new ContactItem(conn, quoteRequest.getBillingContactId()));
			quoteResponse.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, quoteResponse);
		} catch ( Exception e ) {
			conn.rollback();
			throw e;
		}
	}

	
	private void populateNewJob(Job job, JobRequest jobRequest) throws InvalidJobStatusException {
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
	

	

	
}
