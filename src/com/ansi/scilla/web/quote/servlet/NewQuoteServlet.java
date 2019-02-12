package com.ansi.scilla.web.quote.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.quote.request.QuoteRequest;
import com.ansi.scilla.web.quote.response.NewQuoteAddressResponse;
import com.ansi.scilla.web.quote.response.NewQuoteContactResponse;
import com.ansi.scilla.web.quote.response.QuoteResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class NewQuoteServlet extends AbstractQuoteServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String url = request.getRequestURI();
		logger.log(Level.DEBUG, "URL: " + url);
		Quote quote = new Quote();
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		QuoteResponse quoteResponse = new QuoteResponse();
		ResponseCode responseCode = null;
		
		try {
			conn = AppUtils.getDBCPConn();
			SessionData sessionData = AppUtils.validateSession(request, Permission.QUOTE_CREATE);
			conn.setAutoCommit(false);
			
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "Quote Json: " + jsonString);
			QuoteRequest quoteRequest = StringUtils.isBlank(jsonString) ? new QuoteRequest() : new QuoteRequest(jsonString);
			
			trafficCop(conn, response, sessionData, quoteRequest);
			
			
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

	private void trafficCop(Connection conn, HttpServletResponse response, SessionData sessionData, QuoteRequest quoteRequest) throws Exception {
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
		} else {
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Quote Request Type");
			QuoteResponse quoteResponse = new QuoteResponse();
			quoteResponse.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.SYSTEM_FAILURE, quoteResponse);

		}

	}

	

	

	
}
