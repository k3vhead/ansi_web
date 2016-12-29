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
import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.MessageKey;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.request.AddressRequest;
import com.ansi.scilla.web.response.address.AddressListResponse;
import com.ansi.scilla.web.response.address.AddressResponse;
import com.ansi.scilla.web.struts.SessionUser;
import com.thewebthing.commons.db2.RecordNotFoundException;

/**
 * The url for delete will be of the form /address/<addressId>/<name>/<status>
 * 
 * The url for get will be one of:
 * 		/address    (retrieves everything)
<<<<<<< HEAD
 * 		/address/<addressId>      (retrieves a single record)


>>>>>>> a14eeab4d9e7aed809d44375e6d91dd837a59033
 * 
 * The url for adding a new record will be a POST to:
 * 		/address/add   with parameters in the JSON
 * 
 * The url for update will be a POST to:
 * 		/address/<addressId> with parameters in the JSON
 * 
 * 
 * 
 *
 */
public class AddressServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;


	protected void doDelete_old(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			String jsonString = super.makeJsonString(request);
			AddressRequest addressRequest = new AddressRequest(jsonString);
			System.out.println(addressRequest);
			Address address = new Address();
			//address.setAddressId(addressRequest.getDeleteId());

			address.delete(conn);
			
			AddressResponse addressResponse = new AddressResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, addressResponse);
			
			conn.commit();
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("AddressServlet 54");
		String url = request.getRequestURI();
		
		Connection conn = null;
		try {
			ParsedUrl parsedUrl = new ParsedUrl(url);
			System.out.println("AddressServlet 60");
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			Address address = new Address();
			if(parsedUrl.deleteId != null){
				address.setAddressId(Integer.parseInt(parsedUrl.deleteId));
			} 
			address.delete(conn);
			System.out.println("AddressServlet 69");
			AddressResponse addressResponse = new AddressResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, addressResponse);
			System.out.println("AddressServlet 72");
			conn.commit();
		} catch ( Exception e) {
			System.out.println("AddressServlet 75");
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			System.out.println("AddressServlet 79");
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
			
			if ( parsedUrl.addressId.equals("list")) {
				// we're getting all the addresses in the database
				AddressListResponse addressListResponse = makeAddressListResponse(conn);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, addressListResponse);
			} else if ( parsedUrl.addressId.equals("delete")) {
				doDelete(request,response);	
			} else {
				AddressListResponse addressListResponse = makeFilteredListResponse(conn, parsedUrl);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, addressListResponse);
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
			int idx = url.indexOf("/address/");
			String myString = url.substring(idx + "/address/".length());				
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];

			String jsonString = super.makeJsonString(request);
			System.out.println(jsonString);
			AddressRequest addressRequest = new AddressRequest(jsonString);
			
			Address address = null;
			ResponseCode responseCode = null;
			if ( command.equals(ACTION_IS_ADD) ) {
				WebMessages webMessages = validateAdd(conn, addressRequest);
				if (webMessages.isEmpty()) {
					try {
						address = doAdd(conn, addressRequest, sessionUser);
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
				AddressResponse addressResponse = new AddressResponse(address, webMessages);
				super.sendResponse(conn, response, responseCode, addressResponse);
				
			} else if ( urlPieces.length == 3 ) {   //  /<tableName>/<fieldName>/<value> = 3 pieces
				System.out.println("Doing Update Stuff");				
				WebMessages webMessages = validateAdd(conn, addressRequest);
				if (webMessages.isEmpty()) {
					System.out.println("passed validation");
					try {
						Address key = new Address();
						key.setAddressId(Integer.parseInt(urlPieces[0]));
						System.out.println("Trying to do update");
						address = doUpdate(conn, key, addressRequest, sessionUser);
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
				AddressResponse addressResponse = new AddressResponse(address, webMessages);
				super.sendResponse(conn, response, responseCode, addressResponse);
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


	protected Address doAdd(Connection conn, AddressRequest addressRequest, SessionUser sessionUser) throws Exception {
		Date today = new Date();
		Address address = new Address();
		
//		!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! FIX SESSION ISSUE *ASK DAVE
//		address.setAddedBy(sessionUser.getUserId());
		address.setAddedBy(8);
		address.setAddedDate(today);

//		address.setAddressId(addressRequest.getAddressId());
		address.setName(addressRequest.getName());
		
		if ( ! StringUtils.isBlank(addressRequest.getAddress1())) {
			address.setAddress1(addressRequest.getAddress1());
		} if ( ! StringUtils.isBlank(addressRequest.getAddress2())) {
			address.setAddress2(addressRequest.getAddress2());
		} if ( ! StringUtils.isBlank(addressRequest.getCity())) {
			address.setCity(addressRequest.getCity());
		} if ( ! StringUtils.isBlank(addressRequest.getCounty())) {
			address.setCounty(addressRequest.getCounty());
		} if ( ! StringUtils.isBlank(addressRequest.getState())) {
			address.setState(addressRequest.getState());
		} if ( ! StringUtils.isBlank(addressRequest.getStatus())) {
			address.setStatus(addressRequest.getStatus());
		} if ( ! StringUtils.isBlank(addressRequest.getZip())) {
			address.setZip(addressRequest.getZip());
		}
		
//		!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! FIX SESSION ISSUE *ASK DAVE
//		address.setUpdatedBy(sessionUser.getUserId());
		address.setUpdatedBy(8);
		address.setUpdatedDate(today);
		
		try {
			address.insertWithKey(conn);
		} catch ( SQLException e) {
			if ( e.getMessage().contains("duplicate key")) {
				throw new DuplicateEntryException();
			} else {
				AppUtils.logException(e);
				throw e;
			}
		} 
		return address;
	}


	protected Address doUpdate(Connection conn, Address key, AddressRequest addressRequest, SessionUser sessionUser) throws Exception {
		System.out.println("This is the key:");
		System.out.println(key);
		System.out.println("************");
		Date today = new Date();
		Address address = new Address();
		
		address.setAddedBy(sessionUser.getUserId());
		address.setAddedDate(today);

		address.setAddressId(addressRequest.getAddressId());
		address.setAddress1(addressRequest.getAddress1());
		address.setAddress2(addressRequest.getAddress2());
		address.setCity(addressRequest.getCity());
		address.setCounty(addressRequest.getCounty());
		address.setName(addressRequest.getName());
		address.setState(addressRequest.getState());
		address.setStatus(addressRequest.getStatus());
		address.setZip(addressRequest.getZip());
		
		address.setUpdatedBy(sessionUser.getUserId());
		address.setUpdatedDate(today);
		
		// if we update something that isn't there, a RecordNotFoundException gets thrown
		// that exception get propagated and turned into a 404
		address.update(conn, key);		
		return address;
	}

	private AddressListResponse makeAddressListResponse(Connection conn) throws Exception {
		AddressListResponse addressesListResponse = new AddressListResponse(conn);
		return addressesListResponse;
	}

	private AddressListResponse makeFilteredListResponse(Connection conn, ParsedUrl parsedUrl) throws Exception {
		AddressListResponse addressListResponse = new AddressListResponse(conn, parsedUrl.addressId);
		return addressListResponse;
	}

	
	protected WebMessages validateAdd(Connection conn, AddressRequest addressRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredAddFields(addressRequest);
		if ( ! missingFields.isEmpty() ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		return webMessages;
	}

	protected WebMessages validateUpdate(Connection conn, Address key, AddressRequest addressRequest) throws RecordNotFoundException, Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredUpdateFields(addressRequest);
		if ( ! missingFields.isEmpty() ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		// if we "select" the key, and it isn't found, a "RecordNotFoundException" is thrown.
		// That exception will propagate up the tree until it turns into a 404 message sent to the client
		Address testKey = (Address)key.clone(); 
		testKey.selectOne(conn);
		return webMessages;
	}

	
	public class ParsedUrl extends ApplicationObject {
		public String addressId;
		public String deleteId;
		private static final long serialVersionUID = 1L;


		public ParsedUrl(String url) throws RecordNotFoundException {
			int idx = url.indexOf("/address/");	
			if ( idx < 0 ) {
				throw new RecordNotFoundException();
			}
			String myString = url.substring(idx + "/address/".length());			
			String[] urlPieces = myString.split("/");
			if ( urlPieces.length >= 1 ) {
				this.addressId = (urlPieces[0]);
			}
			if ( urlPieces.length >= 2 ) {
				this.deleteId = (urlPieces[1]);
			}
			
		}
	}
}
