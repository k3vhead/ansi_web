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

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.quote.request.NewQuoteRequest;
import com.ansi.scilla.web.quote.request.QuoteRequest;
import com.ansi.scilla.web.quote.response.NewQuoteAddressResponse;
import com.ansi.scilla.web.quote.response.NewQuoteContactResponse;
import com.ansi.scilla.web.quote.response.NewQuoteDisplayResponse;
import com.ansi.scilla.web.quote.response.QuoteResponse;
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
			NewQuoteRequest quoteRequest = StringUtils.isBlank(jsonString) ? new NewQuoteRequest() : new NewQuoteRequest(jsonString);
			logger.log(Level.DEBUG, quoteRequest);
			if ( quoteRequest.getAction().equalsIgnoreCase(NewQuoteRequest.ACTION_IS_VALIDATE)) {
				doValidate(conn, response, sessionData, quoteRequest);
			} else if ( quoteRequest.getAction().equalsIgnoreCase(NewQuoteRequest.ACTION_IS_SAVE) ) {
				doSave(conn, response, sessionData, quoteRequest);
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

	private void doValidate(Connection conn, HttpServletResponse response, SessionData sessionData, NewQuoteRequest quoteRequest) throws Exception {
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
		WebMessages webMessages = new WebMessages();

		RequestValidator.validateLeadType(conn, webMessages, NewQuoteRequest.LEAD_TYPE, quoteRequest.getLeadType(), true);
		RequestValidator.validateId(conn, webMessages, "ansi_user", "user_id", NewQuoteRequest.MANAGER_ID, quoteRequest.getManagerId(), true);
		RequestValidator.validateInvoiceTerms(webMessages, NewQuoteRequest.INVOICE_TERMS, quoteRequest.getInvoiceTerms(), true);
		RequestValidator.validateAccountType(conn, webMessages, NewQuoteRequest.ACCOUNT_TYPE, quoteRequest.getAccountType(), true);
		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, NewQuoteRequest.DIVISION_ID, quoteRequest.getDivisionId(), true);
		RequestValidator.validateBoolean(webMessages, NewQuoteRequest.TAX_EXEMPT, quoteRequest.getTaxExempt(), false);
		if ( quoteRequest.getTaxExempt() != null && quoteRequest.getTaxExempt() ) {
			RequestValidator.validateString(webMessages, NewQuoteRequest.TAX_EXEMPT_REASON, quoteRequest.getTaxExemptReason(), true);
		}
		RequestValidator.validateBoolean(webMessages, NewQuoteRequest.INVOICE_BATCH, quoteRequest.getInvoiceBatch(), false);
		RequestValidator.validateInvoiceStyle(webMessages, NewQuoteRequest.INVOICE_STYLE, quoteRequest.getInvoiceStyle(), true);
		RequestValidator.validateBuildingType(conn, webMessages, NewQuoteRequest.BUILDING_TYPE, quoteRequest.getBuildingType(), true);
		RequestValidator.validateInvoiceGrouping(webMessages, NewQuoteRequest.INVOICE_GROUPING, quoteRequest.getInvoiceGrouping(), true);

		QuoteResponse quoteResponse = new QuoteResponse();
		ResponseCode responseCode = webMessages.isEmpty() ? ResponseCode.SUCCESS : ResponseCode.EDIT_FAILURE;
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
				quoteRequest.setTaxExempt(true);
				quoteRequest.setTaxExemptReason(quoteRequest.getTaxExemptReason());
			} else {
				quoteRequest.setTaxExempt(false);
				quoteRequest.setTaxExemptReason(null);
			}			
			quoteResponse.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, quoteResponse);
		} catch ( Exception e ) {
			conn.rollback();
			throw e;
		}
	}

	

	

	
}
