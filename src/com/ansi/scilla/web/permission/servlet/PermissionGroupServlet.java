package com.ansi.scilla.web.permission.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
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
import com.ansi.scilla.common.exceptions.InvalidDeleteException;
import com.ansi.scilla.common.queries.PermissionGroupUserCount;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.permission.request.PermGroupRequest;
import com.ansi.scilla.web.permission.response.PermGroupCountRecord;
import com.ansi.scilla.web.permission.response.PermissionGroupListResponse;
import com.ansi.scilla.web.permission.response.PermissionGroupResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class PermissionGroupServlet extends AbstractServlet {
	
	/**
	 * 
	 * @author jwlewis
	 */
	protected final Logger logger = LogManager.getLogger(PermissionGroupServlet.class);
	protected final Boolean LogDebugMsgs = true;
	
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
	
	 @Override
	 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { //  Note : modeled after recommended uri parsing pattern 2018-04-19 kjw
		//if(LogDebugMsgs == true) this.logger.log(Level.DEBUG, "Begin");
		this.logger.log(Level.DEBUG, "Begin");
		AnsiURL url = null;
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.SYSADMIN, PermissionLevel.PERMISSION_LEVEL_IS_READ);  // make sure we're allowed to be here
			url = new AnsiURL(request, "permissionGroup",new String[] { ACTION_IS_LIST });												// parse the URL and look for "contact"
			PermissionGroupListResponse permissionGroupListResponse = makeSingleListResponse(conn, url);				// get the data we're looking for
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");										// add messages to the response
			permissionGroupListResponse.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, permissionGroupListResponse);						// send the response
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {  // note : from contactServlet..
		this.logger.log(Level.DEBUG, "Begin");
		
		AnsiURL url = null;
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			SessionData sessionData = AppUtils.validateSession(request, Permission.SYSADMIN, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			SessionUser sessionUser = sessionData.getUser();

			try {
				String jsonString = super.makeJsonString(request);
				PermGroupRequest permGroupRequest = (PermGroupRequest)AppUtils.json2object(jsonString, PermGroupRequest.class);
				url = new AnsiURL(request,"permissionGroup", new String[] {ACTION_IS_ADD});

				if ( url.getId() != null ) {
					// THis is an update
					processUpdate(conn, response, url.getId(), permGroupRequest, sessionUser);
				} else if ( url.getCommand().equalsIgnoreCase(ACTION_IS_ADD)) {
					// this is an add
					processAdd(conn, response, permGroupRequest, sessionUser);
				} else {
					// this is messed up
					super.sendNotFound(response);
				}
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				PermissionGroupResponse data = new PermissionGroupResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
				super.sendForbidden(response);
			} catch ( RecordNotFoundException e ) {
				super.sendNotFound(response);
			}
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}			
				
	// copied from contactServlet
	protected PermissionGroup doAdd(Connection conn, PermGroupRequest permGroupRequest, SessionUser sessionUser) throws Exception {
		this.logger.log(Level.DEBUG, "trying to add item.. ");
		
		PermissionGroup permissionGroup = new PermissionGroup();
		permissionGroup.setDescription(permGroupRequest.getDescription());
		permissionGroup.setName(permGroupRequest.getName());
		permissionGroup.setStatus(permGroupRequest.getStatus());

		permissionGroup.setAddedBy(sessionUser.getUserId());		
		permissionGroup.setUpdatedBy(sessionUser.getUserId());

		Integer permGroupId = permissionGroup.insertWithKey(conn);
		permissionGroup.setPermissionGroupId(permGroupId);
		conn.commit();
		return permissionGroup;
	}
	
	protected PermissionGroup doUpdate (Connection conn, Integer permGroupId, PermissionGroup permissionGroup, PermGroupRequest permGroupRequest, SessionUser sessionUser) throws Exception {

		permissionGroup.setDescription(permGroupRequest.getDescription());
		permissionGroup.setName(permGroupRequest.getName());
		permissionGroup.setStatus(permGroupRequest.getStatus());
		permissionGroup.setUpdatedBy(sessionUser.getUserId());
		permissionGroup.setPermissionGroupId(permGroupId);

		PermissionGroup key = new PermissionGroup ();
		key.setPermissionGroupId(permGroupId);  
		permissionGroup.update(conn, key);	
		return permissionGroup;

		//Integer permGroupId = permissionGroup.insertWithKey(conn);
	}
	
	
	protected void processUpdate(Connection conn, HttpServletResponse response, Integer permGroupId, PermGroupRequest permGroupReequestRequest, SessionUser sessionUser) throws RecordNotFoundException, Exception {
		PermissionGroup permissionGroup = new PermissionGroup();
		PermissionGroupResponse data = new PermissionGroupResponse();
		
		permissionGroup.setPermissionGroupId(permGroupId);
		permissionGroup.selectOne(conn);		// this throws RecordNotFound, which is propagated up the line into a 404 return

		WebMessages webMessages = validateUpdate(conn, permissionGroup, permGroupReequestRequest);
		 
		if (webMessages.isEmpty()) {
			permissionGroup = doUpdate(conn, permGroupId, permissionGroup, permGroupReequestRequest, sessionUser);
			conn.commit();
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update successful!");
			//data.setPermGroupCountRecord(permGroupCountRecord);
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		} else {
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
		}
	}


	protected WebMessages validateUpdate(Connection conn, PermissionGroup PermissionGroup, PermGroupRequest permGroupRequest) throws RecordNotFoundException, Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredUpdateFields(permGroupRequest);
		if ( missingFields.isEmpty() ) {
			List<String> badFormatFieldList = super.validateFormat(permGroupRequest);
			/* need to complete this.. 
			if ( badFormatFieldList.isEmpty() ) {
				if (permGroupRequest.    .isValidPreferredContact(conn)) {
					validatePreferredContact(permGroupRequest, webMessages);
				} else {
					webMessages.addMessage(permGroupRequest.PREFERRED_CONTACT, "Invalid value");
				}
			} else {
				for ( String field : badFormatFieldList ) {
					webMessages.addMessage(field, "Invalid Format");
				}
			}
			*/
		} else {
			// we have required fields that are not populated
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		return webMessages;
	}
	
	
	protected  PermissionGroupListResponse makeSingleListResponse(Connection conn, AnsiURL url) throws Exception {
		PermissionGroupListResponse permissionGroupListResponse = new PermissionGroupListResponse();		

		List<PermissionGroupUserCount> permissionGroupUserCount = new ArrayList<PermissionGroupUserCount>();
		
		this.logger.log(Level.DEBUG, url);

		if(StringUtils.isBlank(url.getCommand())){			
			permissionGroupUserCount.add(PermissionGroupUserCount.select(conn, url.getId()));
		}else {
			permissionGroupUserCount = PermissionGroupUserCount.select(conn, new String[] {PermissionGroup.NAME});			
		}
		List<PermGroupCountRecord> recordList = new ArrayList<PermGroupCountRecord>();
		
		for(PermissionGroupUserCount rec : permissionGroupUserCount) {
			recordList.add(new PermGroupCountRecord(rec));
		}
		permissionGroupListResponse.setPermGroupItemList(recordList);
		return permissionGroupListResponse;
	}

	
	protected void processAdd(Connection conn, HttpServletResponse response, PermGroupRequest permissionGroupRequest, SessionUser sessionUser) throws Exception {
	ResponseCode responseCode = null;
	PermissionGroup permissionGroup = new PermissionGroup();
	
	WebMessages webMessages = validateAdd(conn, permissionGroupRequest);
	if (webMessages.isEmpty()) {			
		permissionGroup = doAdd(conn, permissionGroupRequest, sessionUser);
		String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");		
		webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
		responseCode = ResponseCode.SUCCESS;
	} else {
		responseCode = ResponseCode.EDIT_FAILURE;
	}
	// need to correct this... just setting this so it will compile..
	PermGroupCountRecord tmpPermGroupRecord = new PermGroupCountRecord();
	PermissionGroupResponse permissionGroupResponse = new PermissionGroupResponse(tmpPermGroupRecord); 
	super.sendResponse(conn, response, responseCode, permissionGroupResponse);
	}


	protected WebMessages validateAdd(Connection conn, PermGroupRequest permGroupRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredAddFields(permGroupRequest);
		/* need to complete this
		if ( missingFields.isEmpty() ) {
			List<String> badFormatFieldList = super.validateFormat(permGroupRequest);
			if ( badFormatFieldList.isEmpty() ) {
				if ( permGroupRequest.isValidPreferredContact(conn)) {
					validatePreferredContact(contactRequest, webMessages);
				} else {
					webMessages.addMessage(ContactRequest.PREFERRED_CONTACT, "Invalid value");
				}
				boolean isDupe = isDuplicateContact(conn, contactRequest);
				if ( isDupe ) {
					webMessages.addMessage(ContactRequest.LAST_NAME, "Duplicate Contact");
				}
			} else {
				for ( String field : badFormatFieldList ) {
					webMessages.addMessage(field, "Invalid Format");
				}
			}
		} else {
			// we have required fields that are not populated
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		*/
		return webMessages;
	}

	
	
	/*
	//replacing this..  maybe not needed?
	protected WebMessages old_validateUpdate(Connection conn, PermGroupRequest permGroupRequest) throws Exception {
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
	*/
	
	/*
	//..maybe not needed?
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
	*/
	
	/*
	// don't think this is needed anymore.. 
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
	*/

	
	/*
	
	//replacing this.
	protected WebMessages old_validateAdd(Connection conn, PermGroupRequest permGroupRequest) throws Exception {
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
	*/
	
	
	/*
	//replacing this
	protected PermissionGroup old_doUpdate(Connection conn, PermissionGroup key,
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
	*/
	
	/*
	//replacing this.. method not found in the contactServlet.. maybe not needed anymore?
	private void old_processPostRequest(Connection conn, HttpServletResponse response, String command,
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
		/
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
			} 
			
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
	*/
	
	/*
	//only used by old_doPost.. 
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
	*/
	
	/*
	//replacing this..  method not found in the contactServlet.. maybe not needed anymore?
	protected PermissionGroup old_doAdd(Connection conn, PermGroupRequest permissionGroupRequest, SessionUser sessionUser) throws Exception {
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
	*/

	/*
	// replacing this.. 
	@Override
	protected void old_doPost(HttpServletRequest request,
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
	*/
	
	
}
