package com.ansi.scilla.web.quote.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.common.exceptions.InvalidJobStatusException;
import com.ansi.scilla.common.jobticket.JobStatus;
import com.ansi.scilla.common.jobticket.JobTagDisplay;
import com.ansi.scilla.common.jobticket.JobUtils;
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
import com.ansi.scilla.web.job.response.JobDetail;
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
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
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
				
				// this is where we figure out whether we're creating a new quote, or filling in the blanks from an orphan
				int idx = url.indexOf("/quote/");				
				String myString = url.substring(idx + "/quote/".length());	
				String[] urlPieces = myString.split("/");   // this should be a 1- or 2-item array. 
															// [0] has either 'new' or 'orphan', [1] should be the orphaned quote id
				for ( String x : urlPieces ) {
					logger.log(Level.DEBUG, "urlPieces:" + x);
				}
				logger.log(Level.DEBUG, quoteRequest);
				if ( urlPieces[0].equals("new") ) {
					doSave(conn, response, sessionData, quoteRequest);
				} else {
					doOrphanSave(conn, response, sessionData, quoteRequest, urlPieces[1]);
				}
				
			} else if ( action.equalsIgnoreCase(NewQuoteRequest.ACTION_IS_JOB)) {
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
		} catch (InvalidFormatException e) {
			String badField = super.findBadField(e.toString());
			QuoteResponse data = new QuoteResponse();
			WebMessages messages = new WebMessages();
			messages.addMessage(badField, "Invalid Format");
			data.setWebMessages(messages);
			try {
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (Exception e2) {
				AppUtils.logException(e2);
				AppUtils.rollbackQuiet(conn);
				throw new ServletException(e2);
			}
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
		
		try {
			webMessages = jobRequest.validateNewQuote(conn);
			if ( webMessages.isEmpty() ) {
				if ( ! JobUtils.isValidDLPct(jobRequest.getDirectLaborPct())) {
					webMessages.addMessage("directLaborPct", "Invalid DL Pct");
					responseCode = ResponseCode.EDIT_FAILURE;
				} else {
					Job newJob = populateNewJob(jobRequest);
					List<JobTagDisplay> jobTagDisplayList = new ArrayList<JobTagDisplay>();
					if ( jobRequest.getJobtags() != null && jobRequest.getJobtags().length > 0 ) {
						jobTagDisplayList = JobTagDisplay.makeDisplayList(conn, jobRequest.getJobtags());
					}
					logger.log(Level.DEBUG, newJob);
					JobDetail jobDetail = new JobDetail(conn, newJob, jobTagDisplayList, new User(), new User());
					logger.log(Level.DEBUG, jobDetail);
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
					responseCode = ResponseCode.SUCCESS;
					jobDetailResponse = new JobDetailResponse();
					jobDetailResponse.setJob(jobDetail);
				}
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
			
			
			setAddressDefaults(conn, quoteRequest, sessionUser);
			
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

	
	private void doOrphanSave(Connection conn, HttpServletResponse response, SessionData sessionData,
			NewQuoteRequest quoteRequest, String quoteId) throws RecordNotFoundException, Exception {
		WebMessages webMessages = new WebMessages();

		try {
			Date today = new Date();
			Quote quote = new Quote();
			quote.setQuoteId(Integer.valueOf(quoteId));
			quote.selectOne(conn);
			
			SessionUser sessionUser = sessionData.getUser();

//			quote.setAddedBy(sessionUser.getUserId());		
//			quote.setAddedDate(today);
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

			Quote key = new Quote();
			key.setQuoteId(Integer.valueOf(quoteId));
			quote.update(conn, key);
			
			setAddressDefaults(conn, quoteRequest, sessionUser);
			
			conn.commit();
			
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Quote Updated");
			NewQuoteDisplayResponse quoteResponse = new NewQuoteDisplayResponse();
			quoteResponse.setQuoteId(Integer.valueOf(quoteId));
			quoteResponse.setInvoiceGrouping(quoteRequest.getInvoiceGrouping());
			quoteResponse.setInvoiceStyle(quoteRequest.getInvoiceStyle());
			quoteResponse.setBuildingType(quoteRequest.getBuildingType());
			quoteResponse.setInvoiceBatch(quoteRequest.getInvoiceBatch());
			quoteResponse.setInvoiceTerms(quoteRequest.getInvoiceTerms());
			if ( quoteRequest.getTaxExempt() != null && quoteRequest.getTaxExempt() == true ) {
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

	
	
	
	private void setAddressDefaults(Connection conn, NewQuoteRequest quoteRequest, SessionUser sessionUser) throws RecordNotFoundException, Exception {
		Address address = new Address();
		address.setAddressId(quoteRequest.getJobSiteAddressId());
		address.selectOne(conn);
		
		address.setBilltoBillingContactDefault(quoteRequest.getBillingContactId());
		address.setBilltoAccountTypeDefault(quoteRequest.getAccountType());
		address.setBilltoContractContactDefault(quoteRequest.getContractContactId());
		Integer taxExempt = quoteRequest.getTaxExempt() != null && quoteRequest.getTaxExempt() == true ? 1 : 0;
		address.setBilltoTaxExempt(taxExempt);
		address.setBilltoTaxExemptReason(quoteRequest.getTaxExemptReason());
		Integer invoiceBatchDefault = quoteRequest.getInvoiceBatch() != null && quoteRequest.getInvoiceBatch() == true ? 1 : 0;
		address.setInvoiceBatchDefault(invoiceBatchDefault);
		address.setInvoiceGroupingDefault(quoteRequest.getInvoiceGrouping());
		address.setInvoiceStyleDefault(quoteRequest.getInvoiceStyle());
		address.setInvoiceTermsDefault(quoteRequest.getInvoiceTerms());
		address.setJobsiteBilltoAddressDefault(quoteRequest.getBillToAddressId());
		address.setJobsiteBuildingTypeDefault(quoteRequest.getBuildingType());
		//address.setJobsiteFloorsDefault(quoteRequest.get());
		address.setJobsiteJobContactDefault(quoteRequest.getJobContactId());
		address.setJobsiteSiteContactDefault(quoteRequest.getSiteContact());
		//address.setOurVendorNbrDefault(quoteRequest.get);
		address.setUpdatedBy(sessionUser.getUserId());
		
		Address key = new Address();
		key.setAddressId(quoteRequest.getJobSiteAddressId());
		address.update(conn, key);
		
	}

	private Job populateNewJob(JobRequest jobRequest) throws InvalidJobStatusException {
		logger.log(Level.DEBUG, jobRequest);
		Job job = new Job();
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
		
		job.setBillingNotes(jobRequest.getBillingNotes());
		job.setBudget(jobRequest.getBudget());
		job.setCancelDate(jobRequest.getCancelDate());
		job.setCancelReason(jobRequest.getCancelReason());
		job.setDirectLaborPct(jobRequest.getDirectLaborPct());
		job.setEquipment(jobRequest.getEquipment());
		job.setExpirationDate(jobRequest.getExpirationDate());
		job.setExpirationReason(jobRequest.getExpirationReason());
		job.setFloors(jobRequest.getFloors());
		job.setJobFrequency(jobRequest.getJobFrequency());
		job.setJobNbr(jobRequest.getJobNbr());
		job.setOmNotes(jobRequest.getOmNotes());
		job.setOurVendorNbr(jobRequest.getOurVendorNbr());
		job.setPoNumber(jobRequest.getPoNumber());
		job.setPricePerCleaning(jobRequest.getPricePerCleaning());
		job.setRepeatScheduleAnnually(jobRequest.getRepeatScheduleAnnually());
		job.setRequestSpecialScheduling(jobRequest.getRequestSpecialScheduling());
		job.setServiceDescription(jobRequest.getServiceDescription());
		job.setWasherNotes(jobRequest.getWasherNotes());
		
		logger.log(Level.DEBUG, job);
		return job;
	}
	

	

	
}
