package com.ansi.scilla.web.servlets.addresses;

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
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.MessageKey;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.request.AddressRequest;
import com.ansi.scilla.web.response.address.AddressListResponse;
import com.ansi.scilla.web.response.address.AddressResponse;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.ansi.scilla.web.struts.SessionUser;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class AddressServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String COMMAND_IS_ADD = "add";

	protected void doDelete_old(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.JOB, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
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
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
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

		String url = request.getRequestURI();
		
		Connection conn = null;
		try {
			ParsedUrl parsedUrl = new ParsedUrl(url);

			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.JOB, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			conn.setAutoCommit(false);
			
			Address address = new Address();
			if(parsedUrl.addressId != null){
				address.setAddressId(Integer.parseInt(parsedUrl.addressId));
			} 
			System.out.println("Address ID: "+ Integer.parseInt(parsedUrl.addressId));
			address.delete(conn);
	
			AddressResponse addressResponse = new AddressResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, addressResponse);

			conn.commit();
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
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
		Connection conn = null;
		try {			
			ParsedUrl parsedUrl = new ParsedUrl(url);
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.JOB, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			
			if ( parsedUrl.addressId.equals("list")) {
				// we're getting all the addresses in the database
				AddressListResponse addressListResponse = makeAddressListResponse(conn);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, addressListResponse);
			} else if ( parsedUrl.addressId.equals("add")) {
				doPost(request, response);	
			} else {
				AddressListResponse addressListResponse = makeFilteredListResponse(conn, parsedUrl);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, addressListResponse);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
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
		
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.JOB, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			conn.setAutoCommit(false);

			String jsonString = super.makeJsonString(request);
			AnsiURL url = new AnsiURL(request, "address", new String[] {COMMAND_IS_ADD});

			System.out.println(jsonString);
			AddressRequest addressRequest = new AddressRequest(jsonString);
			
			if ( (! StringUtils.isBlank(url.getCommand())) && url.getCommand().equals(COMMAND_IS_ADD) ) {
				processAddRequest(conn, response, addressRequest, sessionUser);		
			} else if ( url.getId() != null ) {
				processUpdateRequest(conn, response, url.getId(), addressRequest, sessionUser);
			} else {
				// this should never happen
				throw new ServletException("Invalid system state");
			}
			
			conn.commit();
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}


	private void processAddRequest(Connection conn, HttpServletResponse response, AddressRequest addressRequest, SessionUser sessionUser) throws Exception {
		Address address = new Address();
		ResponseCode responseCode = null;
		
		List<String> badFields = super.validateRequiredAddFields(addressRequest);
		WebMessages webMessages = makeWebMessages(conn, badFields);
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
	}

	protected Address doAdd(Connection conn, AddressRequest addressRequest, SessionUser sessionUser) throws Exception {
		Date today = new Date();
		Address address = new Address();
		

		address.setAddedBy(sessionUser.getUserId());
		address.setAddedBy(8);
		address.setAddedDate(today);

//		address.setAddressId(addressRequest.getAddressId());
		address.setName(addressRequest.getName());
		address.setCountryCode(addressRequest.getCountryCode());
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
		

		address.setUpdatedBy(sessionUser.getUserId());
		
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


	private void processUpdateRequest(Connection conn, HttpServletResponse response, Integer addressId, AddressRequest addressRequest, SessionUser sessionUser) throws Exception {
		System.out.println("Doing Update Stuff");	
		ResponseCode responseCode = null;
		Address address = new Address();
		
		List<String> badFields = super.validateRequiredUpdateFields(addressRequest);
		WebMessages webMessages = makeWebMessages(conn, badFields);
		if (webMessages.isEmpty()) {
			System.out.println("passed validation");
			try {
				Address key = new Address();
				key.setAddressId(addressId);
				System.out.println("Trying to do update");
				address = doUpdate(conn, key, addressRequest, sessionUser);
				String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
				responseCode = ResponseCode.SUCCESS;
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
				AddressResponse addressResponse = new AddressResponse(address, webMessages);
				super.sendResponse(conn, response, responseCode, addressResponse);
			} catch ( RecordNotFoundException e ) {
				System.out.println("Doing 404");
				super.sendNotFound(response);						
			} catch ( Exception e) {
				System.out.println("Doing SysFailure");
				responseCode = ResponseCode.SYSTEM_FAILURE;
				AppUtils.logException(e);
				String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
				AddressResponse addressResponse = new AddressResponse(address, webMessages);
				super.sendResponse(conn, response, responseCode, addressResponse);
			}
		} else {
			System.out.println("Doing Edit Fail");
			responseCode = ResponseCode.EDIT_FAILURE;
			AddressResponse addressResponse = new AddressResponse(address, webMessages);
			super.sendResponse(conn, response, responseCode, addressResponse);
		}
	}

	protected Address doUpdate(Connection conn, Address key, AddressRequest addressRequest, SessionUser sessionUser) throws Exception {
		System.out.println("This is the key:");
		System.out.println(key);
		System.out.println("************");
		Date today = new Date();
		Address address = new Address();
		
		address.setAddedBy(sessionUser.getUserId());
		address.setAddedDate(today);
		address.setCountryCode(addressRequest.getCountryCode());
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

	
	protected WebMessages makeWebMessages(Connection conn, List<String> missingFields) throws Exception {
		WebMessages webMessages = new WebMessages();
		if ( ! missingFields.isEmpty() ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		return webMessages;
	}


	
	public class ParsedUrl extends ApplicationObject {
		public String addressId;

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
			
			
		}
	}
}
