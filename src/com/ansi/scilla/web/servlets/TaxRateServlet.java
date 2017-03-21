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
import com.ansi.scilla.common.db.TaxRate;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.MessageKey;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.request.TaxRateRequest;
import com.ansi.scilla.web.response.taxRate.TaxRateListResponse;
import com.ansi.scilla.web.response.taxRate.TaxRateResponse;
import com.ansi.scilla.web.struts.SessionUser;
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
			AppUtils.validateSession(request, Permission.SYSADMIN, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			conn.setAutoCommit(false);
			
			String url = request.getRequestURI();
			System.out.println("TaxRateServlet: doDelete() Url:" + url);
			int idx = url.indexOf("/taxRate/");
			String myString = url.substring(idx + "/taxRate/".length());				
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];

			System.out.println("TaxRateServlet: doDelete() command:" + command);
			
//			ResponseCode responseCode = null;
			if ( urlPieces.length == 1 ) {   //  /<taxRateId> = 1 pieces
				System.out.println("TaxRateServlet: doDelete() urlPieces == 1");
				TaxRate key = new TaxRate();
				if ( StringUtils.isNumeric(urlPieces[0])) { //Looks like a taxRateId
					System.out.println("TaxRateServlet: doDelete() Trying to delete:" + command);
					key.setTaxRateId(Integer.valueOf(urlPieces[0]));
					key.delete(conn);
					
					TaxRateResponse taxRateResponse = new TaxRateResponse();
					super.sendResponse(conn, response, ResponseCode.SUCCESS, taxRateResponse);
					
					conn.commit();
				} else {
					System.out.println("TaxRateServlet: doDelete() urlPieces[0] not numeric");
					throw new RecordNotFoundException();
				}
			} else {
				System.out.println("TaxRateServlet: doDelete() urlPieces <> 1" + urlPieces.length);
				throw new RecordNotFoundException();
			}


/*			String jsonString = super.makeJsonString(request); //get request, change to Json
			TaxRateRequest taxRateRequest = new TaxRateRequest(jsonString); //put Json into taxRateReques
			System.out.println(taxRateRequest);//print request
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
			System.out.println("TaxRateServlet: doDelete() RecordNotFoundException 404");
			super.sendNotFound(response);						
		} catch ( Exception e) {
			System.out.println("TaxRateServlet: doDelete() unexpected exception"+e);
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
				AppUtils.validateSession(request, Permission.SYSADMIN, PermissionLevel.PERMISSION_LEVEL_IS_READ);
				
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
				System.out.println("TaxRateServlet: doGet() RecordNotFoundException 404");
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
		System.out.println("TaxRateServlet: doPost() Url:" + url);
		
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.SYSADMIN, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			conn.setAutoCommit(false);

			// figure out if this is an "add" or an "update"
			int idx = url.indexOf("/taxRate/");
			String myString = url.substring(idx + "/taxRate/".length());				
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];
			System.out.println("TaxRateServlet: doPost() command:"+command);

			String jsonString = super.makeJsonString(request);
			System.out.println("TaxRateServlet: doPost() jsonString:"+jsonString);
			TaxRateRequest taxRateRequest = new TaxRateRequest(jsonString);
			
			TaxRate taxRate = null;
			ResponseCode responseCode = null;
			if ( command.equals(ACTION_IS_ADD) ) {
				System.out.println("TaxRateServlet: doPost() action is add");
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
					System.out.println("TaxRateServlet: doPost() add failed");
					responseCode = ResponseCode.EDIT_FAILURE;
				}
				System.out.println("TaxRateServlet: doPost() prepare response");
				TaxRateResponse taxRateResponse = new TaxRateResponse(taxRate, webMessages);
				System.out.println("TaxRateServlet: doPost() send response");
				super.sendResponse(conn, response, responseCode, taxRateResponse);
				System.out.println("TaxRateServlet: doPost() response sent");
				
			} else if ( urlPieces.length == 1 ) {   //  /<taxRateId> = 1 pieces
				System.out.println("TaxRateServlet: doPost() action is update");
				WebMessages webMessages = validateAdd(conn, taxRateRequest);
				if (webMessages.isEmpty()) {
					System.out.println("passed validation");
					try {
						TaxRate key = new TaxRate();
						if ( StringUtils.isNumeric(urlPieces[0]) ) {//looks like a taxRateId
							System.out.println("TaxRateServlet: doPost() trying to update:"+urlPieces[0]);
							key.setTaxRateId(Integer.valueOf(urlPieces[0]));
							taxRate = doUpdate(conn, key, taxRateRequest, sessionUser);
							String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
							responseCode = ResponseCode.SUCCESS;
							webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
						} else { //non-integer taxRateId, probably a bad thing
							throw new RecordNotFoundException();
						}
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
				System.out.println("TaxRateServlet: doPost() prepare response");
				TaxRateResponse taxRateResponse = new TaxRateResponse(taxRate, webMessages);
				System.out.println("TaxRateServlet: doPost() send response");
				super.sendResponse(conn, response, responseCode, taxRateResponse);
				System.out.println("TaxRateServlet: doPost() response sent");
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
		System.out.println("This is the key:");
		System.out.println(key);
		System.out.println("************");
		Date today = new Date();
		TaxRate taxRate = new TaxRate();
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
			System.out.println("Getting TaxRate for taxRateId: " + urlPieces[0]);
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

	
	protected WebMessages validateAdd(Connection conn, TaxRateRequest taxRateRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		System.out.println("TaxRateServlet: validateAdd() before");
		List<String> missingFields = super.validateRequiredAddFields(taxRateRequest);
		System.out.println("TaxRateServlet: validateAdd() after");
		if ( ! missingFields.isEmpty() ) {
			System.out.println("TaxRateServlet: validateAdd() missing fields");
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		return webMessages;
	}

	protected WebMessages validateUpdate(Connection conn, TaxRate key, TaxRateRequest taxRateRequest) throws RecordNotFoundException, Exception {
		WebMessages webMessages = new WebMessages();
		System.out.println("TaxRateServlet: validateUpdate() before");
		List<String> missingFields = super.validateRequiredUpdateFields(taxRateRequest);
		System.out.println("TaxRateServlet: validateUpdate() after");
		if ( ! missingFields.isEmpty() ) {
			System.out.println("TaxRateServlet: validateUpdate() missing fields");
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		// if we "select" the key, and it isn't found, a "RecordNotFoundException" is thrown.
		// That exception will propagate up the tree until it turns into a 404 message sent to the client
		TaxRate testKey = (TaxRate)key.clone(); 
		System.out.println("TaxRateServlet: validateUpdate() testKey:" + testKey.getTaxRateId());
		testKey.selectOne(conn);
		return webMessages;
	}

	
}
