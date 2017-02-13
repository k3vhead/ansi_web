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

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.MessageKey;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.request.QuoteRequest;
import com.ansi.scilla.web.response.quote.QuoteListResponse;
import com.ansi.scilla.web.response.quote.QuoteResponse;
import com.ansi.scilla.web.struts.SessionUser;
import com.thewebthing.commons.db2.RecordNotFoundException;

/**
 * The url for delete will be of the form /quote/&lt;quoteId&gt;/<quoteNumber>/<revisionNumber>
 * 
 * The url for get will be one of:
 * 		/quote    (retrieves everything)
 * 		/quote/&lt;quoteNumber&gt;	(filters quote table quoteId and quoteNumber
 * 		/quote/&lt;quoteNumber&gt;/&lt;revisionNumber&gt;	(retrieves a single record)
 * 
 * The url for adding a new record will be a POST to:
 * 		/quote/add   with parameters in the JSON
 * 
 * The url for update will be a POST to:
 * 		/quote/&lt;quoteNumber&gt;/&lt;revisionNumber&gt; with parameters in the JSON
 * 
 * 
 * 
 *
 */
public class QuoteServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			String jsonString = super.makeJsonString(request);
			QuoteRequest quoteRequest = new QuoteRequest(jsonString);
			System.out.println(quoteRequest);
			Quote quote = new Quote();
			quote.setQuoteId(quoteRequest.getQuoteId());

			quote.delete(conn);
			
			QuoteResponse quoteResponse = new QuoteResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, quoteResponse);
			
			conn.commit();
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	protected void doNewDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("QuoteServlet 54");
		String url = request.getRequestURI();
		
		Connection conn = null;
		try {
			ParsedUrl parsedUrl = new ParsedUrl(url);
			System.out.println("QuoteServlet 60");
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			Quote quote = new Quote();
			if(parsedUrl.quoteNumber != null){
				quote.setQuoteId(Integer.parseInt(parsedUrl.quoteNumber));
			} 
			
			quote.delete(conn);
			System.out.println("QuoteServlet 69");
			QuoteResponse quoteResponse = new QuoteResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, quoteResponse);
			System.out.println("QuoteServlet 72");
			conn.commit();
		} catch ( Exception e) {
			System.out.println("QuoteServlet 75");
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			System.out.println("QuoteServlet 79");
			AppUtils.closeQuiet(conn);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		Connection conn = null;
		try {			
			ParsedUrl parsedUrl = new ParsedUrl(url);
			conn = AppUtils.getDBCPConn();
			
			if ( parsedUrl.quoteNumber.equals("list")) {
				// we're getting all the codes in the database
				QuoteListResponse quotesListResponse = makeQuotesListResponse(conn);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, quotesListResponse);
			} else if(parsedUrl.quoteNumber.equals("delete")){
				doNewDelete(request,response);			
			} else {
				QuoteListResponse quotesListResponse = makeFilteredListResponse(conn, parsedUrl);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, quotesListResponse);
			}
		} catch ( RecordNotFoundException e) {
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
			
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		SessionUser sessionUser = AppUtils.getSessionUser(request);
		String url = request.getRequestURI();
//		String queryString = request.getQueryString();
		
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);

			// figure out if this is an "add" or an "update"
			int idx = url.indexOf("/quote/");
			String myString = url.substring(idx + "/quote/".length());				
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];

			String jsonString = super.makeJsonString(request);
			System.out.println(jsonString);
			QuoteRequest quoteRequest = new QuoteRequest(jsonString);
			
			Quote quote = null;
			ResponseCode responseCode = null;
			if ( command.equals(ACTION_IS_ADD) ) {
				WebMessages webMessages = validateAdd(conn, quoteRequest);
				if (webMessages.isEmpty()) {
					try {
//						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, sessionUser.toString());
						quote = doAdd(conn, quoteRequest, sessionUser);
						String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
						responseCode = ResponseCode.SUCCESS;
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
					} catch ( DuplicateEntryException e ) {
						String messageText = AppUtils.getMessageText(conn, MessageKey.DUPLICATE_ENTRY, "Record already Exists");
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
						responseCode = ResponseCode.EDIT_FAILURE;
					} catch ( Exception e ) {
						responseCode = ResponseCode.SYSTEM_FAILURE;
						AppUtils.logException(e);
						String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
					}
				} else {
					responseCode = ResponseCode.EDIT_FAILURE;
				}
				QuoteResponse quoteResponse = new QuoteResponse(quote, webMessages);
				super.sendResponse(conn, response, responseCode, quoteResponse);
				
			}  else if ( urlPieces.length == 3 ) {   //  /<tableName>/<fieldName>/<value> = 3 pieces
				System.out.println("Doing Update Stuff");				
				WebMessages webMessages = validateAdd(conn, quoteRequest);
				if (webMessages.isEmpty()) {
					System.out.println("passed validation");
					try {
						Quote key = new Quote();
						key.setQuoteId(Integer.parseInt(urlPieces[0]));
						key.setQuoteNumber(Integer.parseInt(urlPieces[1]));
						key.setRevision((urlPieces[2]));
						System.out.println("Trying to do update");
						quote = doUpdate(conn, key, quoteRequest, sessionUser);
						String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
						responseCode = ResponseCode.SUCCESS;
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
					} catch ( RecordNotFoundException e ) {
						System.out.println("Doing 404");
						super.sendNotFound(response);						
					} catch ( Exception e) {
						System.out.println("Doing SysFailure");
						responseCode = ResponseCode.SYSTEM_FAILURE;
						AppUtils.logException(e);
						String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
					}
				} else {
					System.out.println("Doing Edit Fail");
					responseCode = ResponseCode.EDIT_FAILURE;
				}
				QuoteResponse codeResponse = new QuoteResponse(quote, webMessages);
				super.sendResponse(conn, response, responseCode, codeResponse);
			} else {
				super.sendNotFound(response);
			}
			
			conn.commit();
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}


	protected Quote doAdd(Connection conn, QuoteRequest quoteRequest, SessionUser sessionUser) throws Exception {
		Date today = new Date();
		Quote quote = new Quote();
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! FIX SESSION ISSUE  *ASK DAVE
		quote.setAddedBy(5);
//		quote.setAddedBy(sessionUser.getUserId());
		
		quote.setAddedDate(today);
		
		
//		quote.setQuoteId(quoteRequest.getQuoteId());

//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! FIX SESSION ISSUE  *ASK DAVE
//		quote.setUpdatedBy(sessionUser.getUserId());
		quote.setUpdatedBy(5);
		quote.setUpdatedDate(today);
		
//		quote.setAddress(quoteRequest.getAddress());
		quote.setBillToAddressId(quoteRequest.getBillToAddressId());
		if ( quoteRequest.getCopiedFromQuoteId() != null) {
			quote.setCopiedFromQuoteId(quoteRequest.getCopiedFromQuoteId());
		}
		quote.setJobSiteAddressId(quoteRequest.getJobSiteAddressId());
		quote.setLeadType(quoteRequest.getLeadType());
		quote.setManagerId(quoteRequest.getManagerId());
//		quote.setName(quoteRequest.getName());
//		quote.setPaymentTerms(quoteRequest.getPaymentTerms());
		
		if ( quoteRequest.getProposalDate() != null) {
			quote.setProposalDate(quoteRequest.getProposalDate());
		}
		
		if ( ! StringUtils.isBlank(quoteRequest.getAccountType())) {
			quote.setAccountType(quoteRequest.getAccountType());
		}
//		quote.setQuoteGroupId(quoteRequest.getQuoteGroupId());
	
		quote.setQuoteNumber(quoteRequest.getQuoteNumber());
		quote.setRevision(quoteRequest.getRevisionNumber());
		if ( quoteRequest.getSignedByContactId() != null) {
			quote.setSignedByContactId(quoteRequest.getSignedByContactId());
		}
//		quote.setStatus(quoteRequest.getStatus());
		quote.setTemplateId(quoteRequest.getTemplateId());
	
		try {
			quote.insertWithKey(conn);
		} catch ( SQLException e) {
			if ( e.getMessage().contains("duplicate key")) {
				throw new DuplicateEntryException();
			} else {
				AppUtils.logException(e);
				throw e;
			}
		} 
			return quote;
	}


	protected Quote doUpdate(Connection conn, Quote key, QuoteRequest quoteRequest, SessionUser sessionUser) throws Exception {
		System.out.println("This is the key:");
		System.out.println(key);
		System.out.println("************");
		Date today = new Date();
		Quote quote = new Quote();
	
//		quote.setQuoteId(quoteRequest.getQuoteId());
	
		quote.setUpdatedBy(sessionUser.getUserId());
		quote.setUpdatedDate(today);
		
//		quote.setAddress(quoteRequest.getAddress());
		quote.setBillToAddressId(quoteRequest.getBillToAddressId());
		quote.setCopiedFromQuoteId(quoteRequest.getCopiedFromQuoteId());
		quote.setJobSiteAddressId(quoteRequest.getJobSiteAddressId());
		quote.setLeadType(quoteRequest.getLeadType());
		quote.setManagerId(quoteRequest.getManagerId());
//		quote.setName(quoteRequest.getName());
//		quote.setPaymentTerms(quoteRequest.getPaymentTerms());
		quote.setProposalDate(quoteRequest.getProposalDate());
//		quote.setQuoteGroupId(quoteRequest.getQuoteGroupId());
	
		quote.setQuoteNumber(quoteRequest.getQuoteNumber());
		quote.setRevision(quoteRequest.getRevisionNumber());
		quote.setSignedByContactId(quoteRequest.getSignedByContactId());
//		quote.setStatus(quoteRequest.getStatus());
		quote.setTemplateId(quoteRequest.getTemplateId());
		
//		 if we update something that isn't there, a RecordNotFoundException gets thrown
//		 that exception get propagated and turned into a 404
		quote.update(conn, key);		
		return quote;
	}

	private QuoteListResponse makeQuotesListResponse(Connection conn) throws Exception {
		QuoteListResponse quotesListResponse = new QuoteListResponse(conn);
		return quotesListResponse;
	}

	private QuoteListResponse makeFilteredListResponse(Connection conn, ParsedUrl parsedUrl) throws Exception {
		QuoteListResponse quoteListResponse = new QuoteListResponse(conn, parsedUrl.quoteNumber, parsedUrl.revisionNumber);
		return quoteListResponse;
	}

	
	protected WebMessages validateAdd(Connection conn, QuoteRequest quoteRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredAddFields(quoteRequest);
		if ( ! missingFields.isEmpty() ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		return webMessages;
	}

	protected WebMessages validateUpdate(Connection conn, Quote key, QuoteRequest quoteRequest) throws RecordNotFoundException, Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredUpdateFields(quoteRequest);
		if ( ! missingFields.isEmpty() ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		// if we "select" the key, and it isn't found, a "RecordNotFoundException" is thrown.
		// That exception will propagate up the tree until it turns into a 404 message sent to the client
		Quote testKey = (Quote)key.clone(); 
		testKey.selectOne(conn);
		return webMessages;
	}

	
	public class ParsedUrl extends ApplicationObject {
		private static final long serialVersionUID = 1L;

		public String quoteNumber;
		public String revisionNumber;
		public ParsedUrl(String url) throws RecordNotFoundException {
			int idx = url.indexOf("/quote/");	
			if ( idx < 0 ) {
				throw new RecordNotFoundException();
			}
			String myString = url.substring(idx + "/quote/".length());	
//			System.out.println(myString);
//			AppUtils.logException(new Exception(myString));
			String[] urlPieces = myString.split("/");
			if ( urlPieces.length >= 1 ) {
				this.quoteNumber = (urlPieces[0]);
			}
			if ( urlPieces.length >= 2 ) {
				this.revisionNumber = (urlPieces[1]);
			}
		
		}
	}
}
