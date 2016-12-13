package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.TaxRate;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.MessageKey;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.request.TaxRateRequest;
import com.ansi.scilla.web.response.taxRates.TaxRateListResponse;
import com.ansi.scilla.web.response.taxRates.TaxRateResponse;
import com.ansi.scilla.web.struts.SessionUser;
import com.thewebthing.commons.db2.RecordNotFoundException;

/**
 * The url for delete will be of the form /taxRate/<taxRateId>
 * 
 * The url for get will be one of:
 * 		/taxRate    			(retrieves everything)
 * 		/taxRate/<taxRateId>	(retrieves a single record)
 * 
 * The url for adding a new record will be a POST to:
 * 		/taxRate/add   with parameters in the JSON
 * 
 * The url for update will be a POST to:
 * 		/taxRate/<taxRateId> with parameters in the JSON
 * 
 * 
 * @author gagroce
 *
 */
public class TaxRateServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			String jsonString = super.makeJsonString(request);
			TaxRateRequest taxRateRequest = new TaxRateRequest(jsonString);
			System.out.println(taxRateRequest);
			TaxRate taxRate = new TaxRate();
			taxRate.setTableName(taxRateRequest.getTableName());
			taxRate.setFieldName(taxRateRequest.getFieldName());
			taxRate.setValue(taxRateRequest.getValue());
			taxRate.delete(conn);
			
			TaxRateResponse taxRateResponse = new TaxRateResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, taxRateResponse);
			
			conn.commit();
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		int idx = url.indexOf("/taxRate/");
		if ( idx > -1 ) {
			System.out.println("Url:" + url);
			String queryString = request.getQueryString();
			System.out.println("Query String: " + queryString);
			
			// we're in the right place
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				
				// Figure out what we've got:				
				String myString = url.substring(idx + "/taxRate/".length());
				
				String[] urlPieces = myString.split("/");
				String command = urlPieces[0];
				
				if ( StringUtils.isBlank(command)) {
					super.sendNotFound(response);
				} else {
					if ( command.equals("list")) {
						// we're getting all the taxRates in the database
						TaxRateListResponse taxRatesListResponse = makeTaxRateListResponse(conn);
						super.sendResponse(conn, response, ResponseCode.SUCCESS, taxRatesListResponse);
					} else {
						TaxRateListResponse taxRatesListResponse = makeFilteredListResponse(conn, urlPieces);
						super.sendResponse(conn, response, ResponseCode.SUCCESS, taxRatesListResponse);
					}
				}
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
			
		} else {
			super.sendNotFound(response);
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
			int idx = url.indexOf("/taxRate/");
			String myString = url.substring(idx + "/taxRate/".length());				
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];

			String jsonString = super.makeJsonString(request);
			System.out.println(jsonString);
			TaxRateRequest taxRateRequest = new TaxRateRequest(jsonString);
			
			TaxRate taxRate = null;
			ResponseCode responseCode = null;
			if ( command.equals(ACTION_IS_ADD) ) {
				WebMessages webMessages = validateAdd(conn, taxRateRequest);
				if (webMessages.isEmpty()) {
					try {
						taxRate = doAdd(conn, taxRateRequest, sessionUser);
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
				CodeResponse taxRateResponse = new CodeResponse(taxRate, webMessages);
				super.sendResponse(conn, response, responseCode, taxRateResponse);
				
			} else if ( urlPieces.length == 3 ) {   //  /<tableName>/<fieldName>/<value> = 3 pieces
				System.out.println("Doing Update Stuff");				
				WebMessages webMessages = validateAdd(conn, taxRateRequest);
				if (webMessages.isEmpty()) {
					System.out.println("passed validation");
					try {
						TaxRate key = new TaxRate();
						key.setTableName(urlPieces[0]);
						key.setFieldName(urlPieces[1]);
						key.setValue(urlPieces[2]);
						System.out.println("Trying to do update");
						taxRate = doUpdate(conn, key, taxRateRequest, sessionUser);
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
				TaxRateResponse taxRateResponse = new TaxRateResponse(taxRate, webMessages);
				super.sendResponse(conn, response, responseCode, taxRateResponse);
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


	protected TaxRate doAdd(Connection conn, TaxRateRequest taxRateRequest, SessionUser sessionUser) throws Exception {
		Date today = new Date();
		TaxRate taxRate = new TaxRate();
		taxRate.setAddedBy(sessionUser.getUserId());
		taxRate.setAddedDate(today);
		if ( ! StringUtils.isBlank(taxRateRequest.getDescription())) {
			taxRate.setDescription(taxRateRequest.getDescription());
		}
		if ( ! StringUtils.isBlank(taxRateRequest.getDisplayValue())) {
			taxRate.setDisplayValue(taxRateRequest.getDisplayValue());
		}
		taxRate.setFieldName(taxRateRequest.getFieldName());
		taxRate.setSeq(taxRateRequest.getSeq());
		taxRate.setStatus(taxRateRequest.getStatus());
		taxRate.setTableName(taxRateRequest.getTableName());
		taxRate.setUpdatedBy(sessionUser.getUserId());
		taxRate.setUpdatedDate(today);
		taxRate.setValue(taxRateRequest.getValue());
		try {
			taxRate.insertWithNoKey(conn);
		} catch ( SQLException e) {
			if ( e.getMessage().contains("duplicate key")) {
				throw new DuplicateEntryException();
			} else {
				AppUtils.logException(e);
				throw e;
			}
		} 
		return taxRate;
	}


	protected TaxRate doUpdate(Connection conn, TaxRate key, TaxRateRequest taxRateRequest, SessionUser sessionUser) throws Exception {
		System.out.println("This is the key:");
		System.out.println(key);
		System.out.println("************");
		Date today = new Date();
		TaxRate taxRate = new TaxRate();
		if ( ! StringUtils.isBlank(taxRateRequest.getDescription())) {
			taxRate.setDescription(taxRateRequest.getDescription());
		}
		if ( ! StringUtils.isBlank(taxRateRequest.getDisplayValue())) {
			taxRate.setDisplayValue(taxRateRequest.getDisplayValue());
		}
		taxRate.setFieldName(taxRateRequest.getFieldName());
		taxRate.setSeq(taxRateRequest.getSeq());
		taxRate.setStatus(taxRateRequest.getStatus());
		taxRate.setTableName(taxRateRequest.getTableName());
		taxRate.setUpdatedBy(sessionUser.getUserId());
		taxRate.setUpdatedDate(today);
		taxRate.setValue(taxRateRequest.getValue());
		// if we update something that isn't there, a RecordNotFoundException gets thrown
		// that exception get propagated and turned into a 404
		taxRate.update(conn, key);		
		return taxRate;
	}

	private TaxRateListResponse makeTaxRateListResponse(Connection conn) throws Exception {
		TaxRateListResponse taxRatesListResponse = new TaxRateListResponse(conn);
		return taxRatesListResponse;
	}

	private TaxRateListResponse makeFilteredListResponse(Connection conn, String[] urlPieces) throws Exception {
		String tableName = null;
		String fieldName = null;
		String value = null;
		try {
			tableName = urlPieces[0];
			fieldName = urlPieces[1];
			value = urlPieces[2];
		} catch (ArrayIndexOutOfBoundsException e) {
			// this is OK, just means we ran out of filters
		}
		TaxRate taxRate = new TaxRate();
		if ( ! StringUtils.isBlank(tableName)) {
			taxRate.setTableName(tableName);
		}
		if ( ! StringUtils.isBlank(fieldName)) {
			taxRate.setFieldName(fieldName);
		}
		if ( ! StringUtils.isBlank(value)) {
			taxRate.setValue(value);
		}
		List<TaxRate> taxRateList = TaxRate.cast(taxRate.selectSome(conn));
		Collections.sort(taxRateList,
				new Comparator<TaxRate>() {
			public int compare(TaxRate o1, TaxRate o2) {
				int ret = o1.getTableName().compareTo(o2.getTableName());
				if ( ret == 0 ) {
					ret = o1.getFieldName().compareTo(o2.getFieldName());
				}
				if ( ret == 0 ) {
					ret = o1.getValue().compareTo(o2.getValue());
				}
				return ret;
			}
		});
		TaxRateListResponse taxRateListResponse = new TaxRateListResponse();
		taxRateListResponse.setTaxRateList(taxRateList);
		return taxRateListResponse;
	}

	
	protected WebMessages validateAdd(Connection conn, TaxRateRequest taxRateRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredAddFields(taxRateRequest);
		if ( ! missingFields.isEmpty() ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		return webMessages;
	}

	protected WebMessages validateUpdate(Connection conn, TaxRate key, TaxRateRequest taxRateRequest) throws RecordNotFoundException, Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredUpdateFields(taxRateRequest);
		if ( ! missingFields.isEmpty() ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		// if we "select" the key, and it isn't found, a "RecordNotFoundException" is thrown.
		// That exception will propagate up the tree until it turns into a 404 message sent to the client
		TaxRate testKey = (TaxRate)key.clone(); 
		testKey.selectOne(conn);
		return webMessages;
	}

	
}
