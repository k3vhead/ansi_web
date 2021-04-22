package com.ansi.scilla.web.tax.servlet;

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

import com.ansi.scilla.common.db.TaxRate;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.tax.request.TaxRateRequest;
import com.ansi.scilla.web.tax.response.TaxRateListResponse;
import com.ansi.scilla.web.tax.response.TaxRateResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;

/**
 * The url for delete will be of the form /taxRate/<taxRateId>
 * 
 * The url for get will be one of:
 * 		/taxRate/list    			(retrieves everything)
 * 		/taxRate/<taxRateId>		(retrieves a single record)
 * 	For 2.0 probably adding state, county and city fields to taxRate table
 * 		/taxRate/<state>				(Retrieves state contains <state>)
 * 		/taxRate/<state>/<county>		(Retrieves and county contains <county>)
 * 		/taxRate/<state>/<county>/<city>(Retrieves and city contains <city>)
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
			AppUtils.validateSession(request, Permission.SYSADMIN_WRITE);
			conn.setAutoCommit(false);
			
			String url = request.getRequestURI();
			logger.log(Level.DEBUG, "TaxRateServlet: doDelete() Url:" + url);
			int idx = url.indexOf("/taxRate/");
			String myString = url.substring(idx + "/taxRate/".length());				
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];

			logger.log(Level.DEBUG, "TaxRateServlet: doDelete() command:" + command);
			
//			ResponseCode responseCode = null;
			if ( urlPieces.length == 1 ) {   //  /<taxRateId> = 1 pieces
				logger.log(Level.DEBUG, "TaxRateServlet: doDelete() urlPieces == 1");
				TaxRate key = new TaxRate();
				if ( StringUtils.isNumeric(urlPieces[0])) { //Looks like a taxRateId
					logger.log(Level.DEBUG, "TaxRateServlet: doDelete() Trying to delete:" + command);
					key.setTaxRateId(Integer.valueOf(urlPieces[0]));
					key.delete(conn);
					
					TaxRateResponse taxRateResponse = new TaxRateResponse();
					super.sendResponse(conn, response, ResponseCode.SUCCESS, taxRateResponse);
					
					conn.commit();
				} else {
					logger.log(Level.DEBUG, "TaxRateServlet: doDelete() urlPieces[0] not numeric");
					throw new RecordNotFoundException();
				}
			} else {
				logger.log(Level.DEBUG, "TaxRateServlet: doDelete() urlPieces <> 1" + urlPieces.length);
				throw new RecordNotFoundException();
			}


/*			String jsonString = super.makeJsonString(request); //get request, change to Json
			TaxRateRequest taxRateRequest = new TaxRateRequest(jsonString); //put Json into taxRateReques
			logger.log(Level.DEBUG, taxRateRequest);//print request
			TaxRate taxRate = new TaxRate();
			taxRate.setTaxRateId(taxRateRequest.getTaxRateId());
			taxRate.delete(conn);
			
			TaxRateResponse taxRateResponse = new TaxRateResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, taxRateResponse);
			
			conn.commit();
*/
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( RecordNotFoundException e ) {
			logger.log(Level.DEBUG, "TaxRateServlet: doDelete() RecordNotFoundException 404");
			super.sendNotFound(response);						
		} catch ( Exception e) {
			logger.log(Level.DEBUG, "TaxRateServlet: doDelete() unexpected exception"+e);
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
			logger.log(Level.DEBUG, "Url:" + url);
			String queryString = request.getQueryString();
			logger.log(Level.DEBUG, "Query String: " + queryString);
			
			// we're in the right place
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				AppUtils.validateSession(request, Permission.SYSADMIN_READ);
				
				// Figure out what we've got:				
				String myString = url.substring(idx + "/taxRate/".length());
				
				String[] urlPieces = myString.split("/");
				String command = urlPieces[0];
				
				if ( StringUtils.isBlank(command)) {
//					super.sendNotFound(response);
					throw new RecordNotFoundException();
				} else {
					if ( command.equals("list")) {
						// we're getting all the taxRates in the database
						TaxRateListResponse taxRateListResponse = makeTaxRateListResponse(conn);
						super.sendResponse(conn, response, ResponseCode.SUCCESS, taxRateListResponse);
					} else {
						// we're getting a single taxRate by taxRateId returned in a single entry list
						TaxRateListResponse taxRateListResponse = makeSingleListResponse(conn, urlPieces);
						super.sendResponse(conn, response, ResponseCode.SUCCESS, taxRateListResponse);
					}
				}
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
				super.sendForbidden(response);
			} catch ( RecordNotFoundException e ) {
				logger.log(Level.DEBUG, "TaxRateServlet: doGet() RecordNotFoundException 404");
				super.sendNotFound(response);						
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
		logger.log(Level.DEBUG, "TaxRateServlet: doPost() Url:" + url);
		
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.SYSADMIN_WRITE);
			conn.setAutoCommit(false);

			// figure out if this is an "add" or an "update"
			int idx = url.indexOf("/taxRate/");
			String myString = url.substring(idx + "/taxRate/".length());				
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];
			logger.log(Level.DEBUG, "TaxRateServlet: doPost() command:"+command);

			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "TaxRateServlet: doPost() jsonString:"+jsonString);
			
			try {
				TaxRateRequest taxRateRequest = new TaxRateRequest(jsonString);
				processValidPostRequest(conn, response, request, command, urlPieces, taxRateRequest, sessionUser);
			} catch ( InvalidFormatException e) {
				String badField = super.findBadField(e.toString());
				TaxRateResponse data = new TaxRateResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
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


	private void processValidPostRequest(Connection conn, HttpServletResponse response, HttpServletRequest request, String command, String[] urlPieces, TaxRateRequest taxRateRequest, SessionUser sessionUser) throws Exception {
		TaxRate taxRate = null;
		ResponseCode responseCode = null;
		if ( command.equals(ACTION_IS_ADD) ) {
			logger.log(Level.DEBUG, "TaxRateServlet: doPost() action is add");
			WebMessages webMessages = taxRateRequest.validateAdd();
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
				logger.log(Level.DEBUG, "TaxRateServlet: doPost() add failed");
				responseCode = ResponseCode.EDIT_FAILURE;
			}
			logger.log(Level.DEBUG, "TaxRateServlet: doPost() prepare response");
			TaxRateResponse taxRateResponse = new TaxRateResponse(taxRate, webMessages);
			logger.log(Level.DEBUG, "TaxRateServlet: doPost() send response");
			super.sendResponse(conn, response, responseCode, taxRateResponse);
			logger.log(Level.DEBUG, "TaxRateServlet: doPost() response sent");
			
		} else if ( urlPieces.length == 1 ) {   //  /<taxRateId> = 1 pieces
			logger.log(Level.DEBUG, "TaxRateServlet: doPost() action is update");
			if ( StringUtils.isNumeric(urlPieces[0]) ) {
				WebMessages webMessages = taxRateRequest.validateAdd();
				Integer taxRateId = Integer.valueOf(urlPieces[0]);
				RequestValidator.validateId(conn, webMessages, "tax_rate", TaxRate.TAX_RATE_ID, TaxRateRequest.TAX_RATE_ID, taxRateId, true);
				if ( webMessages.isEmpty() ) {					
					TaxRate key = new TaxRate();
					key.setTaxRateId(taxRateId);
					taxRate = doUpdate(conn, key, taxRateRequest, sessionUser);
					String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
					responseCode = ResponseCode.SUCCESS;
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
				} else {
					responseCode = ResponseCode.EDIT_FAILURE;
				}
				TaxRateResponse taxRateResponse = new TaxRateResponse(taxRate, webMessages);
				super.sendResponse(conn, response, responseCode, taxRateResponse);
			} else {
				super.sendNotFound(response);
			}
			
			
		} else {
			super.sendNotFound(response);
		}
		
		conn.commit();		
	}

	protected TaxRate doAdd(Connection conn, TaxRateRequest taxRateRequest, SessionUser sessionUser) throws Exception {
		Date today = new Date();
		TaxRate taxRate = new TaxRate();
		taxRate.setAddedBy(sessionUser.getUserId());
		taxRate.setAddedDate(today);
		taxRate.setAmount(taxRateRequest.getAmount());
		taxRate.setEffectiveDate(taxRateRequest.getEffectiveDate());
		if ( ! StringUtils.isBlank(taxRateRequest.getLocation())) {
			taxRate.setLocation(taxRateRequest.getLocation());
		}
		taxRate.setRate(taxRateRequest.getRate());
		taxRate.setUpdatedBy(sessionUser.getUserId());
		taxRate.setUpdatedDate(today);
		try {
			Integer taxRateId = taxRate.insertWithKey(conn);
			taxRate.setTaxRateId(taxRateId);
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
		logger.log(Level.DEBUG, "This is the key:");
		logger.log(Level.DEBUG, key);
		logger.log(Level.DEBUG, "************");
		Date today = new Date();
		TaxRate taxRate = new TaxRate();
		taxRate.setTaxRateId(key.getTaxRateId());
		taxRate.selectOne(conn);
		
		taxRate.setAmount(taxRateRequest.getAmount());
		taxRate.setEffectiveDate(taxRateRequest.getEffectiveDate());
		if ( ! StringUtils.isBlank(taxRateRequest.getLocation())) {
			taxRate.setLocation(taxRateRequest.getLocation());
		}
		taxRate.setRate(taxRateRequest.getRate());
		//taxRate.setTaxRateId(taxRateRequest.getTaxRateId()); //key is passed in from the url
		taxRate.setUpdatedBy(sessionUser.getUserId());
		taxRate.setUpdatedDate(today);
		// if we update something that isn't there, a RecordNotFoundException gets thrown
		// that exception get propagated and turned into a 404
		taxRate.update(conn, key);		
		return taxRate;
	}

	private TaxRateListResponse makeTaxRateListResponse(Connection conn) throws Exception {
		TaxRateListResponse taxRatesListResponse = new TaxRateListResponse(conn);
		return taxRatesListResponse;
	}

	private TaxRateListResponse makeSingleListResponse(Connection conn, String[] urlPieces) throws Exception {
		//		TaxRateListResponse taxRateListResponse = new TaxRateListResponse(conn);

		if (StringUtils.isNumeric(urlPieces[0])){
			/*			TaxRate taxRate = new TaxRate();
			taxRate.setTaxRateId(Integer.valueOf(urlPieces[0]));
			logger.log(Level.DEBUG, "Getting TaxRate for taxRateId: " + urlPieces[0]);
			taxRate.selectOne(conn);
			taxRateListResponse.setTaxRateList(Arrays.asList(new TaxRate[] {taxRate} ));
			 */
			Integer taxRateId = Integer.valueOf(urlPieces[0]);
			TaxRateListResponse taxRateListResponse = new TaxRateListResponse(conn, taxRateId);
			return taxRateListResponse;
		} else {
			throw new RecordNotFoundException();
		}
	}

/*	private TaxRateListResponse makeFilteredListResponse(Connection conn, String[] urlPieces) throws Exception {
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
*/

	
	

	protected WebMessages validateUpdate(Connection conn, TaxRate key, TaxRateRequest taxRateRequest) throws RecordNotFoundException, Exception {
		WebMessages webMessages = new WebMessages();
		logger.log(Level.DEBUG, "TaxRateServlet: validateUpdate() before");
		List<String> missingFields = super.validateRequiredUpdateFields(taxRateRequest);
		logger.log(Level.DEBUG, "TaxRateServlet: validateUpdate() after");
		if ( ! missingFields.isEmpty() ) {
			logger.log(Level.DEBUG, "TaxRateServlet: validateUpdate() missing fields");
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		// if we "select" the key, and it isn't found, a "RecordNotFoundException" is thrown.
		// That exception will propagate up the tree until it turns into a 404 message sent to the client
		TaxRate testKey = (TaxRate)key.clone(); 
		logger.log(Level.DEBUG, "TaxRateServlet: validateUpdate() testKey:" + testKey.getTaxRateId());
		testKey.selectOne(conn);
		return webMessages;
	}

	
}
