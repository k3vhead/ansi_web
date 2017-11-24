package com.ansi.scilla.web.quote.servlet;

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
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.common.quote.QuoteUtils;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.quote.request.QuoteRequest;
import com.ansi.scilla.web.quote.response.QuoteListResponse;
import com.ansi.scilla.web.quote.response.QuoteResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;
/**
 * The url for delete will be of the form /quote/&lt;quoteId&gt;/<quoteNumber>/<revision>
 * 
 * The url for get will be one of:
 * 		/quote    (retrieves everything)
 * 		/quote/&lt;quoteId&gt;	(filters quote table quoteId and quoteNumber
 * 		/quote/&lt;quoteId&gt;/&lt;quoteNumber&gt;/&lt;revision&gt;	(retrieves a single record)
 * 
 * The url for adding a new record will be a POST to:
 * 		/quote/add   	with parameters in the JSON
 * 		/quote/copy/&lt;quoteId&gt; 
 * 
 * The url for update will be a POST to:
 * 		/quote/&lt;quoteId&gt;/&lt;quoteNumber&gt;/&lt;revision&gt; with parameters in the JSON
 * 
 * 
 * 
 *
 */
public class QuoteServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String ACTION_IS_UPDATE = "update";
	
	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.QUOTE, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			conn.setAutoCommit(false);
			
			String jsonString = super.makeJsonString(request);
			QuoteRequest quoteRequest = new QuoteRequest(jsonString);
			System.out.println(quoteRequest);
			Quote quote = new Quote();
			quote.setQuoteId(quoteRequest.getQuoteId());

			QuoteUtils.updateQuoteHistory(conn, quoteRequest.getQuoteId());

			quote.delete(conn);
			
			QuoteResponse quoteResponse = new QuoteResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, quoteResponse);
			
			conn.commit();
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
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
			AppUtils.validateSession(request, Permission.QUOTE, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			conn.setAutoCommit(false);
			
			Quote quote = new Quote();
			if(parsedUrl.quoteId != null){
				quote.setQuoteId(Integer.parseInt(parsedUrl.quoteId));
				QuoteUtils.updateQuoteHistory(conn, Integer.parseInt(parsedUrl.quoteId));
			} 
			
			quote.delete(conn);
			System.out.println("QuoteServlet 69");
			QuoteResponse quoteResponse = new QuoteResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, quoteResponse);
			System.out.println("QuoteServlet 72");
			conn.commit();
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
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
			AppUtils.validateSession(request, Permission.QUOTE, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			
			if ( parsedUrl.quoteId.equals("list")) {
				// we're getting all the codes in the database
				QuoteListResponse quotesListResponse = makeQuotesListResponse(conn);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, quotesListResponse);
			} else if(parsedUrl.quoteId.equals("delete")){
				doNewDelete(request,response);			
			} else {
				QuoteListResponse quotesListResponse = makeFilteredListResponse(conn, parsedUrl.quoteId);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, quotesListResponse);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
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
			AppUtils.validateSession(request, Permission.QUOTE, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			conn.setAutoCommit(false);

			// figure out if this is an "add" or an "update"
			int idx = url.indexOf("/quote/");
			String myString = url.substring(idx + "/quote/".length());				
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];

			String jsonString = super.makeJsonString(request);
			System.out.println("Quote Json: " + jsonString);
			QuoteRequest quoteRequest = StringUtils.isBlank(jsonString) ? new QuoteRequest() : new QuoteRequest(jsonString);
			
			Quote quote = null;
			ResponseCode responseCode = null;
			if ( command.equals(ACTION_IS_ADD) ) {
				System.out.println("Call validateAdd"+quoteRequest);
				WebMessages webMessages = validateAdd(conn, quoteRequest);
				if (webMessages.isEmpty()) {
					try {
//						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, sessionUser.toString());
						System.out.println("Doing Add");
						quote = doAdd(conn, quoteRequest, sessionUser);
						
						/* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%% CANT GET QUOTE NUMBER OR REVISION TO UPDATE %%%%%%%%%%%%%%%%%%%%%%%%%%%%%% */
						//conn.commit();
						//quote.update(conn, quote);
						String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
						responseCode = ResponseCode.SUCCESS;
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
						//quote.update(conn, quote);
					} catch ( DuplicateEntryException e ) {
						String messageText = AppUtils.getMessageText(conn, MessageKey.DUPLICATE_ENTRY, "Record already Exists");
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
						responseCode = ResponseCode.EDIT_FAILURE;
					} catch ( Exception e ) {
						responseCode = ResponseCode.SYSTEM_FAILURE;
						System.out.println("Fail: System Failure");
						AppUtils.logException(e);
						String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
					}
				} else {
					responseCode = ResponseCode.EDIT_FAILURE;
				}
				QuoteResponse quoteResponse = new QuoteResponse(quote, webMessages);
				super.sendResponse(conn, response, responseCode, quoteResponse);
				
			}  else if ( command.equals(ACTION_IS_COPY) ) {   
				
				WebMessages webMessages = new WebMessages();
				try {
					System.out.println("Doing Copy");
					quote = doCopy(conn, Integer.parseInt(urlPieces[1]), sessionUser);
					
					String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
					responseCode = ResponseCode.SUCCESS;
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
				} catch ( DuplicateEntryException e ) {
					String messageText = AppUtils.getMessageText(conn, MessageKey.DUPLICATE_ENTRY, "Record already Exists");
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
					responseCode = ResponseCode.EDIT_FAILURE;
				} catch ( Exception e ) {
					responseCode = ResponseCode.SYSTEM_FAILURE;
					System.out.println("Fail: System Failure");
					AppUtils.logException(e);
					String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
				}
				QuoteResponse quoteResponse = new QuoteResponse(quote, webMessages);
				super.sendResponse(conn, response, responseCode, quoteResponse);
				
			}  else if ( urlPieces.length >= 2 ) {   //  /<tableName>/<fieldName>/<value> = 3 pieces
				System.out.println("Doing Update Stuff");				
				System.out.println("Call validateUpdate:"+quoteRequest);
				Quote key = new Quote();
				key.setQuoteId(Integer.parseInt(urlPieces[0]));
				System.out.println("Key:"+key);
				WebMessages webMessages = validateUpdate(conn, key, quoteRequest);
				if (webMessages.isEmpty()) {
					System.out.println("passed validation");
					System.out.println(urlPieces);
					try {
						System.out.println("Trying to do update for quote "+key.getQuoteId());
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
				System.out.println("Response:");
				System.out.println("responseCode: " + responseCode);
				System.out.println("codeResponse: " + codeResponse);
				System.out.println("response: " + response);

				super.sendResponse(conn, response, responseCode, codeResponse);
			} else {
				super.sendNotFound(response);
			}
			
			conn.commit();
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

	protected Quote doAdd(Connection conn, QuoteRequest quoteRequest, SessionUser sessionUser) throws Exception {
		Date today = new Date();
		Quote quote = new Quote();

		quote.setAddedBy(sessionUser.getUserId());
		
		quote.setAddedDate(today);

		quote.setUpdatedBy(sessionUser.getUserId());

		quote.setUpdatedDate(today);
		
//		quote.setAddress(quoteRequest.getAddress());
		quote.setBillToAddressId(quoteRequest.getBillToAddressId());
		if ( quoteRequest.getCopiedFromQuoteId() != null) {
			quote.setCopiedFromQuoteId(quoteRequest.getCopiedFromQuoteId());
		}
		quote.setQuoteNumber(quoteRequest.getQuoteNumber());
		quote.setRevision(quoteRequest.getRevision());
		quote.setJobSiteAddressId(quoteRequest.getJobSiteAddressId());
		quote.setLeadType(quoteRequest.getLeadType());
		quote.setDivisionId(quoteRequest.getDivisionId());
		quote.setAccountType(quoteRequest.getAccountType());
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
	
		quote.setQuoteNumber(AppUtils.getNextQuoteNumber(conn));
//		quote.setQuoteNumber(5000000);
		quote.setRevision("A");
		quote.setSignedByContactId(null);
		
//		quote.setStatus(quoteRequest.getStatus());
		quote.setTemplateId(quoteRequest.getTemplateId());
		System.out.println("Quote servlet Add Data:");
		System.out.println(quote.toString());
		int q = 0;
		try {
			q = quote.insertWithKey(conn);
			//System.out.println("QuoteID After Insert? "+ q);
			
			
		} catch ( SQLException e) {
			if ( e.getMessage().contains("duplicate key")) {
				throw new DuplicateEntryException();
			} else {
				AppUtils.logException(e);
				throw e;
			}
		} 
			quote.setQuoteId(q);
//			quote.setQuoteNumber(q);
			
//			Quote key = new Quote();
//			key.setQuoteId(q);
//			key.setQuoteNumber(q);
			
			//quote.update(conn, null);
			return quote;
	}


	protected Quote doCopy(Connection conn, Integer sourceQuoteId, SessionUser sessionUser) throws Exception {

		System.out.println("Quote servlet call copyQuote:" + sourceQuoteId);

		Quote quote = new Quote();
		try {
			quote = QuoteUtils.copyQuote(conn, sourceQuoteId, sessionUser.getUserId());
		} catch ( SQLException e) {
			AppUtils.logException(e);
			throw e;
		} 
		return quote;
	}


	protected Quote doUpdate(Connection conn, Quote key, QuoteRequest quoteRequest, SessionUser sessionUser) throws Exception {
		System.out.println("This is the key:");
		System.out.println(key);
		System.out.println("************");
		Date today = new Date();
		Quote quote = new Quote();
		quote.setQuoteId(key.getQuoteId());
		quote.selectOne(conn);

		//		quote.setQuoteId(quoteRequest.getQuoteId());
	
		quote.setUpdatedBy(sessionUser.getUserId());
		quote.setUpdatedDate(today);
		
		
//		String value = key.getRevision();
//		int charValue = value.charAt(0);
//		String next = String.valueOf( (char) (charValue + 1));
		
//		quote.setAddress(quoteRequest.getAddress());
		quote.setBillToAddressId(quoteRequest.getBillToAddressId());
//		quote.setQuoteNumber(quoteRequest.getQuoteNumber());
//		quote.setRevision(quoteRequest.getRevisionNumber());
		quote.setCopiedFromQuoteId(quoteRequest.getCopiedFromQuoteId());
		quote.setJobSiteAddressId(quoteRequest.getJobSiteAddressId());
		quote.setLeadType(quoteRequest.getLeadType());
		quote.setManagerId(quoteRequest.getManagerId());
		quote.setDivisionId(quoteRequest.getDivisionId());
//		quote.setName(quoteRequest.getName());

		if ( quoteRequest.getProposalDate() != null) {
			quote.setProposalDate(quoteRequest.getProposalDate());
		}
		if ( ! StringUtils.isBlank(quoteRequest.getAccountType())) {
			quote.setAccountType(quoteRequest.getAccountType());
		}

	
//		quote.setQuoteNumber(AppUtils.getNextQuoteNumber(conn));
//		quote.setRevision(App);
		if ( (quoteRequest.getSignedByContactId() != null) && (quoteRequest.getSignedByContactId() != 0) ) {
			System.out.println("signed by: " + quoteRequest.getSignedByContactId());
			quote.setSignedByContactId(quoteRequest.getSignedByContactId());
		}
		quote.setTemplateId(quoteRequest.getTemplateId());
		
//		 if we update something that isn't there, a RecordNotFoundException gets thrown
//		 that exception get propagated and turned into a 404
		
		System.out.println("This is the update quote:");
		System.out.println(quote);

		QuoteUtils.updateQuoteHistory(conn, key.getQuoteId());
		
		quote.update(conn, key);		
		return quote;
	}

	private QuoteListResponse makeQuotesListResponse(Connection conn) throws Exception {
		QuoteListResponse quotesListResponse = new QuoteListResponse(conn);
		return quotesListResponse;
	}

	private QuoteListResponse makeFilteredListResponse(Connection conn, ParsedUrl parsedUrl) throws Exception {
		QuoteListResponse quoteListResponse = new QuoteListResponse(conn, parsedUrl.quoteNumber, parsedUrl.revision);
		return quoteListResponse;
	}

	private QuoteListResponse makeFilteredListResponse(Connection conn, String quoteId) throws Exception {
		QuoteListResponse quoteListResponse = new QuoteListResponse(conn, quoteId);
		return quoteListResponse;
	}

	
	protected WebMessages validateAdd(Connection conn, QuoteRequest quoteRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredAddFields(quoteRequest);
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

		public String quoteId;
		public String quoteNumber;
		public String revision;
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
				this.quoteId = (urlPieces[0]);
			}
			if ( urlPieces.length >= 2 ) {
				this.quoteNumber = (urlPieces[1]);
			}
			if ( urlPieces.length >= 3 ) {
				this.revision = (urlPieces[2]);
			}
		
		}
	}
}
