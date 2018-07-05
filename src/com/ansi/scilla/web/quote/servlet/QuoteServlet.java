package com.ansi.scilla.web.quote.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.common.quote.QuoteUtils;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.common.utils.UserPermission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.quote.common.QuoteValidator;
import com.ansi.scilla.web.quote.request.QuoteRequest;
import com.ansi.scilla.web.quote.response.QuoteListResponse;
import com.ansi.scilla.web.quote.response.QuoteResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	public static final String ACTION_IS_REVISE = "revise";
	
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
			logger.log(Level.DEBUG, quoteRequest);
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
		logger.log(Level.TRACE, "QuoteServlet 54");
		String url = request.getRequestURI();
		
		Connection conn = null;
		try {
			ParsedUrl parsedUrl = new ParsedUrl(url);
			logger.log(Level.TRACE, "QuoteServlet 60");
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.QUOTE, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			conn.setAutoCommit(false);
			
			Quote quote = new Quote();
			if(parsedUrl.quoteId != null){
				quote.setQuoteId(Integer.parseInt(parsedUrl.quoteId));
				QuoteUtils.updateQuoteHistory(conn, Integer.parseInt(parsedUrl.quoteId));
			} 
			
			quote.delete(conn);
			logger.log(Level.TRACE, "QuoteServlet 69");
			QuoteResponse quoteResponse = new QuoteResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, quoteResponse);
			logger.log(Level.TRACE, "QuoteServlet 72");
			conn.commit();
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			logger.log(Level.TRACE, "QuoteServlet 75");
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			logger.log(Level.TRACE, "QuoteServlet 79");
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
			SessionData sessionData = AppUtils.validateSession(request, Permission.QUOTE_READ);
			
			if ( parsedUrl.quoteId.equals("list")) {
				QuoteListResponse quotesListResponse = makeQuotesListResponse(conn, sessionData.getUserPermissionList());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, quotesListResponse);
			} else if(parsedUrl.quoteId.equals("delete")){
//				doNewDelete(request,response);	
				super.sendForbidden(response);
			} else {
				QuoteListResponse quotesListResponse = makeFilteredListResponse(conn, parsedUrl.quoteId, sessionData.getUserPermissionList());
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


	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String url = request.getRequestURI();
		logger.log(Level.DEBUG, "URL: " + url);
//		String queryString = request.getQueryString();
		
		PostedUrl postedUrl = null;
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			SessionData sessionData = AppUtils.validateSession(request, Permission.QUOTE_CREATE);
			conn.setAutoCommit(false);

			// figure out if this is an "add" or an "update"
//			int idx = url.indexOf("/quote/");
//			String myString = url.substring(idx + "/quote/".length());				
//			String[] urlPieces = myString.split("/");
//			String command = urlPieces[0];
			postedUrl = new PostedUrl(url);

			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "Quote Json: " + jsonString);
			QuoteRequest quoteRequest = StringUtils.isBlank(jsonString) ? new QuoteRequest() : new QuoteRequest(jsonString);
			logger.log(Level.DEBUG, "Quote Request: " + quoteRequest);
			Quote quote = null;
			ResponseCode responseCode = null;
			if ( postedUrl.command.equals(ACTION_IS_ADD) ) {
				processAddRequest(conn, quoteRequest, quote, sessionData, responseCode, response);
			}  else if ( postedUrl.command.equals(ACTION_IS_COPY) ) {  				
				processCopyRequest(conn, postedUrl.quoteId, quoteRequest, quote, sessionData, responseCode, response);
			}  else if ( postedUrl.command.equals(ACTION_IS_REVISE) ) { 				
				processReviseRequest(conn, postedUrl.quoteId, quoteRequest, quote, sessionData, responseCode, response);
			}  else if ( postedUrl.command.equals(ACTION_IS_UPDATE) ) {   
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> fieldMap = new HashMap<String, Object>();
				fieldMap = mapper.readValue(jsonString, fieldMap.getClass());
				// get a list of fields that we're actually going to try to update
				Set<String> keyset = fieldMap.keySet();
				processUpdateRequest(conn, postedUrl.quoteId, quoteRequest, keyset, quote, sessionData, responseCode, response);				
			} else {
				logger.log(Level.DEBUG, "Sending not found ");
				super.sendNotFound(response);
			}
			
			conn.commit();
		} catch ( ArrayIndexOutOfBoundsException | NumberFormatException e ) {
			// the url didn't have enough pieces, or they were in the wrong order, or had mismatch format
			super.sendNotFound(response);
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

	
	
	private void processAddRequest(Connection conn, QuoteRequest quoteRequest, Quote quote, SessionData sessionData, ResponseCode responseCode, HttpServletResponse response) throws Exception {
		logger.log(Level.DEBUG, "Processing Add: ");
		logger.log(Level.DEBUG, "Call validateAdd"+quoteRequest);
		WebMessages webMessages = validateAdd(conn, quoteRequest);
		if (webMessages.isEmpty()) {
			try {
				//				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, sessionUser.toString());
				logger.log(Level.DEBUG, "Doing Add");
				quote = doAdd(conn, quoteRequest, sessionData);

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
				logger.log(Level.DEBUG, "Fail: System Failure");
				AppUtils.logException(e);
				String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
			}
		} else {
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		QuoteResponse quoteResponse = new QuoteResponse(conn, quote, webMessages, sessionData.getUserPermissionList());
		super.sendResponse(conn, response, responseCode, quoteResponse);		
	}

	private void processCopyRequest(Connection conn, Integer quoteId, QuoteRequest quoteRequest, Quote quote, SessionData sessionData,
			ResponseCode responseCode, HttpServletResponse response) throws Exception {
		logger.log(Level.DEBUG, "Processing Copy: ");
		WebMessages webMessages = new WebMessages();
		SessionUser sessionUser = sessionData.getUser();
		
		try {
			logger.log(Level.DEBUG, "Doing Copy");
			quote = doCopy(conn, quoteId, sessionUser);
			
			String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
			responseCode = ResponseCode.SUCCESS;
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
		} catch ( DuplicateEntryException e ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.DUPLICATE_ENTRY, "Record already Exists");
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
			responseCode = ResponseCode.EDIT_FAILURE;
		} catch ( Exception e ) {
			responseCode = ResponseCode.SYSTEM_FAILURE;
			logger.log(Level.DEBUG, "Fail: System Failure");
			AppUtils.logException(e);
			String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
		}
		QuoteResponse quoteResponse = new QuoteResponse(conn, quote, webMessages, sessionData.getUserPermissionList());
		super.sendResponse(conn, response, responseCode, quoteResponse);		
	}

	
	
	private void processReviseRequest(Connection conn, Integer quoteId, QuoteRequest quoteRequest, Quote quote,
			SessionData sessionData, ResponseCode responseCode, HttpServletResponse response) throws Exception {
		logger.log(Level.DEBUG, "Processing Revise: ");
		SessionUser sessionUser = sessionData.getUser();
		WebMessages webMessages = new WebMessages();
		try {
			logger.log(Level.DEBUG, "Doing Revise");
			quote = doRevise(conn, quoteId, sessionUser);
			
			String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
			responseCode = ResponseCode.SUCCESS;
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
		} catch ( DuplicateEntryException e ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.DUPLICATE_ENTRY, "Record already Exists");
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
			responseCode = ResponseCode.EDIT_FAILURE;
		} catch ( Exception e ) {
			responseCode = ResponseCode.SYSTEM_FAILURE;
			logger.log(Level.DEBUG, "Fail: System Failure");
			AppUtils.logException(e);
			String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
		}
		QuoteResponse quoteResponse = new QuoteResponse(conn, quote, webMessages, sessionData.getUserPermissionList());
		super.sendResponse(conn, response, responseCode, quoteResponse);		
	}

	
	
	private void processUpdateRequest(Connection conn, Integer quoteId, QuoteRequest quoteRequest, Set<String> keyset, Quote quote, SessionData sessionData, ResponseCode responseCode, HttpServletResponse response) throws RecordNotFoundException, Exception {
		logger.log(Level.DEBUG, "Doing Update Stuff");			
		logger.log(Level.DEBUG, "Call validateUpdate:"+quoteRequest);
		SessionUser sessionUser = sessionData.getUser();
		Quote key = new Quote();
		key.setQuoteId(quoteId);
		logger.log(Level.DEBUG, "Key:"+key);
		WebMessages webMessages = validateUpdate(conn, key, quoteRequest, keyset);
		if (webMessages.isEmpty()) {
			logger.log(Level.DEBUG, "passed validation");
			try {
				logger.log(Level.DEBUG, "Trying to do update for quote "+key.getQuoteId());
				quote = doUpdate(conn, key, quoteRequest, sessionUser, keyset);
				String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
				responseCode = ResponseCode.SUCCESS;
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
			} catch ( RecordNotFoundException e ) {
				logger.log(Level.DEBUG, "Doing 404");
				super.sendNotFound(response);						
			} catch ( Exception e) {
				logger.log(Level.DEBUG, "Doing SysFailure");
				responseCode = ResponseCode.SYSTEM_FAILURE;
				AppUtils.logException(e);
				String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
			}
		} else {			
			logger.log(Level.DEBUG, "Doing Edit Fail");
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		logger.log(Level.DEBUG, "Making a response with this quote:");
		logger.log(Level.DEBUG, quote);
		QuoteResponse codeResponse = new QuoteResponse(conn, quote, webMessages, sessionData.getUserPermissionList());
		logger.log(Level.DEBUG, "Response:");
		logger.log(Level.DEBUG, "responseCode: " + responseCode);
		logger.log(Level.DEBUG, "codeResponse: " + codeResponse);
		logger.log(Level.DEBUG, "response: " + response);

		super.sendResponse(conn, response, responseCode, codeResponse);		
	}

	
	
	protected Quote doAdd(Connection conn, QuoteRequest quoteRequest, SessionData sessionData) throws Exception {
		Date today = new Date();
		Quote quote = new Quote();
		SessionUser sessionUser = sessionData.getUser();
		
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
		logger.log(Level.DEBUG, "Quote servlet Add Data:");
		logger.log(Level.DEBUG, quote.toString());
		int q = 0;
		try {
			q = quote.insertWithKey(conn);
			//logger.log(Level.DEBUG, "QuoteID After Insert? "+ q);
			
			
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

		logger.log(Level.DEBUG, "Quote servlet call copyQuote:" + sourceQuoteId);

		Quote quote = new Quote();
		try {
			quote = QuoteUtils.copyQuote(conn, sourceQuoteId, sessionUser.getUserId());
		} catch ( SQLException e) {
			AppUtils.logException(e);
			throw e;
		} 
		return quote;
	}

	
	/**
	 * Make a copy of the quote; increment the revision, but leave the quote number the same
	 * @param conn
	 * @param sourceQuoteId
	 * @param sessionUser
	 * @return
	 * @throws Exception
	 */
	protected Quote doRevise(Connection conn, Integer sourceQuoteId, SessionUser sessionUser) throws Exception {

		logger.log(Level.DEBUG, "Quote servlet call reviseQuote:" + sourceQuoteId);

		Quote quote = new Quote();
		try {
			quote = QuoteUtils.reviseQuote(conn, sourceQuoteId, sessionUser.getUserId());
		} catch ( SQLException e) {
			AppUtils.logException(e);
			throw e;
		} 
		return quote;
	}
	
	

	protected Quote doUpdate(Connection conn, Quote keyQuote, QuoteRequest quoteRequest, SessionUser sessionUser, Set<String> keyset) throws Exception {
		logger.log(Level.DEBUG, "This is the key:");
		logger.log(Level.DEBUG, keyQuote);
		logger.log(Level.DEBUG, "************");
		
		for ( String x : keyset ) {
			logger.log(Level.DEBUG, x);
		}
		Date today = new Date();
		Quote quote = new Quote();
		quote.setQuoteId(keyQuote.getQuoteId());
		quote.selectOne(conn);
		
//		quote.setQuoteId(quoteRequest.getQuoteId());
	
		quote.setUpdatedBy(sessionUser.getUserId());
		quote.setUpdatedDate(today);
		
		
//		String value = key.getRevision();
//		int charValue = value.charAt(0);
//		String next = String.valueOf( (char) (charValue + 1));
		
//		quote.setAddress(quoteRequest.getAddress());
		if ( keyset.contains(QuoteRequest.BILL_TO_ADDRESS_ID)) { quote.setBillToAddressId(quoteRequest.getBillToAddressId()); }
//		quote.setQuoteNumber(quoteRequest.getQuoteNumber());
//		quote.setRevision(quoteRequest.getRevisionNumber());
		if ( keyset.contains(QuoteRequest.COPIED_FROM_QUOTE_ID)) { quote.setCopiedFromQuoteId(quoteRequest.getCopiedFromQuoteId()); }
		if ( keyset.contains(QuoteRequest.JOB_SITE_ADDRESS_ID)) { quote.setJobSiteAddressId(quoteRequest.getJobSiteAddressId()); }
		if ( keyset.contains(QuoteRequest.LEAD_TYPE)) { quote.setLeadType(quoteRequest.getLeadType()); }
		if ( keyset.contains(QuoteRequest.MANAGER_ID)) { quote.setManagerId(quoteRequest.getManagerId()); }
		if ( keyset.contains(QuoteRequest.DIVISION_ID)) { quote.setDivisionId(quoteRequest.getDivisionId()); }
//		quote.setName(quoteRequest.getName());

		if ( keyset.contains(QuoteRequest.PROPOSAL_DATE) && quoteRequest.getProposalDate() != null) {
			quote.setProposalDate(quoteRequest.getProposalDate());
		}
		if ( keyset.contains(QuoteRequest.ACCOUNT_TYPE) && ! StringUtils.isBlank(quoteRequest.getAccountType())) {
			quote.setAccountType(quoteRequest.getAccountType());
		}

	
//		quote.setQuoteNumber(AppUtils.getNextQuoteNumber(conn));
//		quote.setRevision(App);
		if ( keyset.contains(QuoteRequest.SIGNED_BY_CONTACT_ID) && (quoteRequest.getSignedByContactId() != null) && (quoteRequest.getSignedByContactId() != 0) ) {
			logger.log(Level.DEBUG, "signed by: " + quoteRequest.getSignedByContactId());
			quote.setSignedByContactId(quoteRequest.getSignedByContactId());
		}
		if ( keyset.contains(QuoteRequest.TEMPLATE_ID) ) { quote.setTemplateId(quoteRequest.getTemplateId()); }
		
//		 if we update something that isn't there, a RecordNotFoundException gets thrown
//		 that exception get propagated and turned into a 404
		
		logger.log(Level.DEBUG, "This is the update quote:");
		logger.log(Level.DEBUG, quote);

		QuoteUtils.updateQuoteHistory(conn, keyQuote.getQuoteId());
		
		quote.update(conn, keyQuote);
		
		if ( keyset.contains(QuoteRequest.BILLING_CONTACT_ID)) {
			updateJobContacts(conn, Job.BILLING_CONTACT_ID, keyQuote.getQuoteId(), quoteRequest.getBillingContactId(), sessionUser, today);
		}
		if ( keyset.contains(QuoteRequest.CONTRACT_CONTACT_ID)) {
			updateJobContacts(conn, Job.CONTRACT_CONTACT_ID, keyQuote.getQuoteId(), quoteRequest.getContractContactId(), sessionUser, today);
		}
		if ( keyset.contains(QuoteRequest.JOB_CONTACT_ID)) {
			updateJobContacts(conn, Job.JOB_CONTACT_ID, keyQuote.getQuoteId(), quoteRequest.getJobContactId(), sessionUser, today);
		}
		if ( keyset.contains(QuoteRequest.SITE_CONTACT)) {
			updateJobContacts(conn, Job.SITE_CONTACT, keyQuote.getQuoteId(), quoteRequest.getSiteContact(), sessionUser, today);
		}
		return quote;
	}

	
	private void updateJobContacts(Connection conn, String fieldName, Integer quoteId, Integer contactId, SessionUser sessionUser, Date today) throws SQLException {
		String sql = "update job set " + fieldName + "=?, updated_by=?, updated_date=? where " + Job.QUOTE_ID + "=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, contactId);
		ps.setInt(2, sessionUser.getUserId());
		ps.setDate(3, new java.sql.Date(today.getTime()));
		ps.setInt(4, quoteId);
		ps.executeUpdate();
	}

	
	
	private QuoteListResponse makeQuotesListResponse(Connection conn, List<UserPermission> permissionList) throws Exception {
		QuoteListResponse quotesListResponse = new QuoteListResponse(conn, permissionList);
		return quotesListResponse;
	}

	

	private QuoteListResponse makeFilteredListResponse(Connection conn, String quoteId, List<UserPermission> permissionList) throws Exception {
		QuoteListResponse quoteListResponse = new QuoteListResponse(conn, quoteId, permissionList);
		return quoteListResponse;
	}

	
	protected WebMessages validateAdd(Connection conn, QuoteRequest quoteRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredAddFields(quoteRequest);
		logger.log(Level.DEBUG, "validateAdd");
		String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
		if ( ! missingFields.isEmpty() ) {
//			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
				logger.log(Level.DEBUG, "field:"+field+":"+messageText);
			}
		}

		return webMessages;
	}

	protected WebMessages validateUpdate(Connection conn, Quote key, QuoteRequest quoteRequest, Set<String> keyset) throws RecordNotFoundException, Exception {
		WebMessages webMessages = new WebMessages();
		if ( keyset.isEmpty() ) {
			// no fields were passed to be updated
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Nothing to Update");
		}
		for ( String fieldName : keyset ) {
			QuoteValidator.validate(conn, quoteRequest, fieldName, webMessages);			
		}
		
//		List<String> missingFields = super.validateRequiredUpdateFields(quoteRequest);
//		if ( ! missingFields.isEmpty() ) {
//			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
//			for ( String field : missingFields ) {
//				webMessages.addMessage(field, messageText);
//			}
//		}
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
//			logger.log(Level.DEBUG, myString);
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
	
	/**
	 * Figures out which of the URLs is being posted:
	 *		/quote/copy/nnnn	- Copy existing quote nnnn to a new quote
	 *		/quote/revise/nnnn	- Revise existing quote nnnn (keep the quote number, increment the revision)
	 *		/quote/new			- Add a new quote
	 *		/quote/nnnn			- Update an existing quote
	 * @author dclewis
	 *
	 */
	public class PostedUrl extends ApplicationObject {

		private static final long serialVersionUID = 1L;
		public Integer quoteId;
		public String command;
		
		public PostedUrl(String url) {
			int idx = url.indexOf("/quote/");				
			String myString = url.substring(idx + "/quote/".length());	
			String[] urlPieces = myString.split("/");
			
			if ( urlPieces[0].equalsIgnoreCase(ACTION_IS_COPY)) {
				command = ACTION_IS_COPY;
				quoteId = Integer.parseInt(urlPieces[1]);
			} else if ( urlPieces[0].equalsIgnoreCase(ACTION_IS_REVISE) ) {
				command = ACTION_IS_REVISE;
				quoteId = Integer.parseInt(urlPieces[1]);
			} else if ( urlPieces[0].equalsIgnoreCase(ACTION_IS_ADD) ) {
				command = ACTION_IS_ADD;
				quoteId = null;
			} else {
				command = ACTION_IS_UPDATE;
				quoteId = Integer.parseInt(urlPieces[0]);
			}
			
		}
	}
}
