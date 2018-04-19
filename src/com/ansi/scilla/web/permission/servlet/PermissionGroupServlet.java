package com.ansi.scilla.web.permission.servlet;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.PermissionGroup;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.common.exceptions.InvalidDeleteException;
import com.ansi.scilla.web.address.response.AddressListResponse;
import com.ansi.scilla.web.address.servlet.AddressServlet.ParsedUrl;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.contact.response.ContactListResponse;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.permission.request.PermGroupRequest;
import com.ansi.scilla.web.permission.response.PermissionGroupListResponse;
import com.ansi.scilla.web.permission.response.PermissionGroupResponse;
import com.ansi.scilla.web.test.TestServlet;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class PermissionGroupServlet extends AbstractServlet {
	
	/**
	 * 
	 * @author jwlewis
	 */
	
	protected final Logger logger = LogManager.getLogger(PermissionGroupServlet.class);
		
	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
				
		String url = request.getRequestURI();
		int idx = url.indexOf("/permissionGroup/");
		// idx is the position of the first character of "/permissionGroup/"
		
		if ( idx > -1 ) {
			// we're in the right place
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
			
				SessionData sessionData = AppUtils.validateSession(request, Permission.USER_ADMIN, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
				
				// Figure out what we've got:
				// get everything following the "/permissionGroup/" tag.. 
				String myString = url.substring(idx + "/permissionGroup/".length());
				
				// split it all into a handy array.. 
				String[] urlPieces = myString.split("/");
				
				// first item should be a number... 
				String command = urlPieces[0];
				
				if ( StringUtils.isBlank(command) || ! StringUtils.isNumeric(command)) {
					super.sendNotFound(response);  // first item was either blank or not a number.. 
				} else {
					try {
						//  we've got a number to work with.. try to delete the record with that id.
						doDeleteWork(conn, Integer.valueOf(command));
						conn.commit();
						PermissionGroupResponse permissionGroupResponse = new PermissionGroupResponse();
						super.sendResponse(conn, response, ResponseCode.SUCCESS, permissionGroupResponse);
					} catch (InvalidDeleteException e) {
						// an exception was thrown when we tried to delete it.. 
						// let the user know.. 
						String message = AppUtils.getMessageText(conn, MessageKey.DELETE_FAILED, "Invalid Delete");
						WebMessages webMessages = new WebMessages();
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
						PermissionGroupResponse permissionGroupResponse = new PermissionGroupResponse();
						permissionGroupResponse.setWebMessages(webMessages);
						super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, permissionGroupResponse);
					} catch(RecordNotFoundException recordNotFoundEx) {
						// couldn't find the record..
						// let the parent class handle this alert..
						super.sendNotFound(response);
					}
				}
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
				super.sendForbidden(response);  	// permission related or network error exceptions.. 
			} catch ( Exception e) {
				AppUtils.logException(e);			// unaccounted for exceptions. 
				throw new ServletException(e);
			} finally {								// do this no matter what.. 
				AppUtils.closeQuiet(conn);
			}
			
		} else {
			super.sendNotFound(response); 			// '/permissionGroup/' was not found in the url...
		}
		
	}
	
	public void doDeleteWork(Connection conn, Integer permGroupId) throws RecordNotFoundException, InvalidDeleteException, Exception {
		
		PermissionGroup perm = new PermissionGroup();
		perm.setPermissionGroupId(permGroupId);

		User user = new User();
		user.setPermissionGroupId(permGroupId);

		try {
			user.selectOne(conn);
			throw new InvalidDeleteException();
		} catch (RecordNotFoundException e) {
			perm.delete(conn);
		}
		
	}
	
	
	/**
	 * This is the "doGet" method copied from ContactServlet.   It is a good pattern to follow
	 * for parsing the URL and building the response. The "doGet" for PermissionGroup
	 * is version 0 code and should be replaced
	 
	 protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		AnsiURL url = null;
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.JOB, PermissionLevel.PERMISSION_LEVEL_IS_READ);  // make sure we're allowed to be here
			url = new AnsiURL(request, "contact",(String[])null);										// parse the URL and look for "contact"
			ContactListResponse contactListResponse = makeSingleListResponse(conn, url.getId());		// get the data we're looking for
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");								// add messages to the response
			contactListResponse.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, contactListResponse);				// send the response
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {					// these are thrown by session validation
			super.sendForbidden(response);
		} catch ( RecordNotFoundException e ) {															// if they're asking for an id that doesn't exist
			super.sendNotFound(response);						
		} catch ( Exception e) {																		// something bad happened
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);																	// return the connection to the pool
		}
	}
	  
	 
	 */
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		this.logger.log(Level.DEBUG, "Here we are.. ");

		System.out.println("kilroy : here we are in the doGet member.. ");

		String url = request.getRequestURI();
		int idx = url.indexOf("/permissionGroup/");
		// idx contains the position of the first character of '/permissionGroup/' within the uri
		// idx will be a negative value if that was not found.
		
		if ( idx > -1 ) {
			// found the correct tag.. get everything after the tag... 
			String myString = url.substring(idx + "/permissionGroup/".length());
			
			// split it all into a handy array.. 
			String[] urlPieces = myString.split("/");
			
			// first value should be the command...
			String command = urlPieces[0];

			Connection conn = null;
			try {
				if ( StringUtils.isBlank(command)) {
					throw new RecordNotFoundException();
				}
				conn = AppUtils.getDBCPConn();

				PermissionGroupListResponse permListResponse = doGetWork(conn, myString);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, permListResponse);
			} catch(RecordNotFoundException recordNotFoundEx) {
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
		
	public PermissionGroupListResponse doGetWork(Connection conn, String url) throws RecordNotFoundException, Exception {
		PermissionGroupListResponse permListResponse = new PermissionGroupListResponse();
		String[] x = url.split("/");

		System.out.println("kilroy : here we are in the doGetWork member.. ");
				
		if(x[0].equals("list")){
			permListResponse = new PermissionGroupListResponse(conn);
		} else if (StringUtils.isNumeric(x[0])) {
			Integer permGroupId = Integer.valueOf(x[0]);
			permListResponse = new PermissionGroupListResponse(conn, permGroupId);
		} else {
			throw new RecordNotFoundException();
		}
		return permListResponse;
	}
	
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.logger.log(Level.DEBUG, "Here we are.. ");
		
		SessionUser sessionUser = AppUtils.getSessionUser(request);
		String url = request.getRequestURI();
		System.out.println("kilroy : here we are in the doPost member.. ");
		
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);

			// figure out if this is an "add" or an "update"
			int idx = url.indexOf("/permissionGroup/");
			String myString = url.substring(idx + "/permissionGroup/".length());		
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];
			String jsonString = super.makeJsonString(request);
			try {
				PermGroupRequest permGroupRequest = (PermGroupRequest) AppUtils.json2object(jsonString, PermGroupRequest.class);
				processPostRequest(conn, response, command, sessionUser, permGroupRequest);
			} catch ( InvalidFormatException formatException) {
				processBadPostRequest(conn, response, formatException);
			}
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	private void processPostRequest(Connection conn, HttpServletResponse response, String command,
			SessionUser sessionUser, PermGroupRequest permGroupRequest) throws Exception {
		PermissionGroup permissionGroup = null;
		ResponseCode responseCode = null;
		if ( command.equals(ACTION_IS_ADD) ) {
			WebMessages webMessages = validateAdd(conn, permGroupRequest);
			if (webMessages.isEmpty()) {
				webMessages = validateFormat(conn, permGroupRequest);
			}
			if (webMessages.isEmpty()) {
				try {
					permissionGroup = doAdd(conn, permGroupRequest, sessionUser);
					String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
					responseCode = ResponseCode.SUCCESS;
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
					conn.commit();
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
			
			
			PermissionGroupResponse permissionGroupResponse = new PermissionGroupResponse();
			if ( permissionGroup != null ) {
				permissionGroupResponse = new PermissionGroupResponse(conn, permissionGroup);
			}
			if ( ! webMessages.isEmpty()) {
				permissionGroupResponse.setWebMessages(webMessages);
			}
			super.sendResponse(conn, response, responseCode, permissionGroupResponse);
		
		/*
		This is the Update portion of the doPost
		*/
		} else if ( StringUtils.isNumeric(command) ) {   
			WebMessages webMessages = validateUpdate(conn, permGroupRequest);
			if (webMessages.isEmpty()) {
				webMessages = validateFormat(conn, permGroupRequest);
			}
			if (webMessages.isEmpty()) {
				try {
					PermissionGroup key = new PermissionGroup();
					key.setPermissionGroupId(Integer.valueOf(command));
					permissionGroup = doUpdate(conn, key, permGroupRequest, sessionUser);
					String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
					responseCode = ResponseCode.SUCCESS;
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
					conn.commit();
				} catch ( RecordNotFoundException e ) {
					super.sendNotFound(response);
					conn.rollback();
				} catch ( Exception e) {
					responseCode = ResponseCode.SYSTEM_FAILURE;
					AppUtils.logException(e);
					String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
					conn.rollback();
				}
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}
			/* DivisionResponse divisionResponse = null;
			if ( division != null ) {
				divisionResponse = new DivisionResponse(conn, division);
			}
			if ( ! webMessages.isEmpty()) {
				divisionResponse.setWebMessages(webMessages);
			} */
			
			PermissionGroupResponse permissionGroupResponse = new PermissionGroupResponse();
			if ( permissionGroup != null ) {
				permissionGroupResponse = new PermissionGroupResponse(conn, permissionGroup);
			}
			if ( ! webMessages.isEmpty()) {
				permissionGroupResponse.setWebMessages(webMessages);
			}
			
			super.sendResponse(conn, response, responseCode, permissionGroupResponse);
		} else {
			super.sendNotFound(response);
		}
			
	}
	
	private void processBadPostRequest(Connection conn, HttpServletResponse response,
			InvalidFormatException formatException) throws Exception {
		
		WebMessages webMessages = new WebMessages();
		String field = findBadField(formatException.toString());
		String messageText = AppUtils.getMessageText(conn, MessageKey.INVALID_DATA, "Invalid Format");
		webMessages.addMessage(field, messageText);
		PermissionGroupResponse permissionGroupResponse = new PermissionGroupResponse();
		permissionGroupResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, permissionGroupResponse);
		
	}
	
	protected PermissionGroup doAdd(Connection conn, PermGroupRequest permissionGroupRequest, SessionUser sessionUser) throws Exception {
		Date today = new Date();
		PermissionGroup permissionGroup = new PermissionGroup();

		this.logger.log(Level.DEBUG, "Here we are.. ");
		
		makePermissionGroup(permissionGroup, permissionGroupRequest, sessionUser, today);
		permissionGroup.setAddedBy(sessionUser.getUserId());
		permissionGroup.setAddedDate(today);

		
		try {
			Integer permissionGroupId = permissionGroup.insertWithKey(conn);
			permissionGroup.setPermissionGroupId(permissionGroupId);
		} catch ( SQLException e) {
			if ( e.getMessage().contains("duplicate key")) {
				throw new DuplicateEntryException();
			} else {
				AppUtils.logException(e);
				throw e;
			}
		} 
		return permissionGroup;
	}
	
	protected WebMessages validateAdd(Connection conn, PermGroupRequest permGroupRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredAddFields(permGroupRequest);
		if ( ! missingFields.isEmpty() ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		return webMessages;
	}

	protected PermissionGroup doUpdate(Connection conn, PermissionGroup key,
			PermGroupRequest permGroupRequest, SessionUser sessionUser) throws Exception{
		
		Date today = new Date();
		PermissionGroup permissionGroup = new PermissionGroup();
		permissionGroup.setPermissionGroupId(key.getPermissionGroupId());
		permissionGroup.selectOne(conn);
		makePermissionGroup(permissionGroup, permGroupRequest, sessionUser, today);

		try {
			permissionGroup.update(conn, key);
			conn.commit();
		} catch ( SQLException e) {
			AppUtils.logException(e);
			throw e;
		} 
		return permissionGroup;
	}
	
	protected WebMessages validateUpdate(Connection conn, PermGroupRequest permGroupRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredUpdateFields(permGroupRequest);
		if ( ! missingFields.isEmpty() ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		return webMessages;
	}
	
	protected WebMessages validateFormat(Connection conn, PermGroupRequest permGroupRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateFormat(permGroupRequest);
		if ( ! missingFields.isEmpty() ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		return webMessages;
	}

	private PermissionGroup makePermissionGroup(PermissionGroup permissionGroup, PermGroupRequest permGroupRequest,
			SessionUser sessionUser, Date today) {
		if ( ! StringUtils.isBlank(permGroupRequest.getDescription())) {
			permissionGroup.setDescription(permGroupRequest.getDescription());
		}
		if ( permGroupRequest.getPermissionGroupId() != null) {
			permissionGroup.setPermissionGroupId(permGroupRequest.getPermissionGroupId());
		}
		permissionGroup.setStatus(permGroupRequest.getStatus());
		permissionGroup.setName(permGroupRequest.getName());
		permissionGroup.setUpdatedBy(sessionUser.getUserId());
		permissionGroup.setUpdatedDate(today);
		
		return permissionGroup;
	}
	
}
