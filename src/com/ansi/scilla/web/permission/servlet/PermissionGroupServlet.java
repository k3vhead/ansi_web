package com.ansi.scilla.web.permission.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
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
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.permission.request.PermGroupRequest;
import com.ansi.scilla.web.permission.response.PermGroupCountRecord;
import com.ansi.scilla.web.permission.response.PermissionGroupListResponse;
import com.ansi.scilla.web.permission.response.PermissionGroupResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;
/**
 * The url for delete will be of the form :
 * 		 /permissionGroup/id# 	(deletes the record with this id)
 * 
 * The url for get will be one of:
 * 		/permissionGroup/list  	(retrieves everything)
 * 		/permissionGroup/id#,	(returns one permission group)
 * 
 * The url for adding a new record will be a POST to:
 * 		/permissionGroup/add   	with parameters in the JSON
 * 
 * The url for update will be a POST to:
 * 		/permissionGroup/id# 		with parameters in the JSON
 *
 */
public class PermissionGroupServlet extends AbstractServlet {
	/**
	 * @author jwlewis
	 * @author kwagner;
	 */
	protected final Logger logger = LogManager.getLogger(PermissionGroupServlet.class);
	protected final Boolean LogDebugMsgs = true;
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		AnsiURL url = null;
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);

			SessionData sessionData = AppUtils.validateSession(request, Permission.SYSADMIN, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			SessionUser sessionUser = sessionData.getUser();

			try {
				PermGroupRequest permGroupRequest = new PermGroupRequest();

				url = new AnsiURL(request,"permissionGroup", new String[] {""});

				if (url.getId() != null) {			// if true.. this is a delete and we have am id to delete
					doDeleteWork(conn, url.getId());
					conn.commit();
					PermissionGroupResponse permissionGroupResponse = new PermissionGroupResponse();
					super.sendResponse(conn, response, ResponseCode.SUCCESS, permissionGroupResponse);
				} else { // this is a call to DELETE with no id.. send an error.. 
					super.sendForbidden(response);
				}
			} catch (JsonMappingException e_json2object) {
				// this probably won't ever happen..
			} catch (InvalidDeleteException e) {	// doDeleteWork Exceptions
				// an exception was thrown when we tried to delete it.. 
				// let the user know.. 
				String message;
				try {
					message = AppUtils.getMessageText(conn, MessageKey.DELETE_FAILED, "Invalid Delete");
				} catch (Exception e_getMessageText) {
					throw new ServletException(e);
				}
				WebMessages webMessages = new WebMessages();
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
				PermissionGroupResponse permissionGroupResponse = new PermissionGroupResponse();
				permissionGroupResponse.setWebMessages(webMessages);
				try {
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, permissionGroupResponse);
				} catch (Exception e_sendResponse) {
					throw new ServletException(e);
				}
			} catch(RecordNotFoundException e_doDeleteWork) {
				super.sendNotFound(response);
			} catch (ResourceNotFoundException e_AnsiURL) {
				super.sendNotFound(response);
			} catch ( Exception e) {
				AppUtils.logException(e);			// unaccounted for exceptions. 
				throw new ServletException(e);
			}	
		} catch (SQLException | NamingException conn_exceptions) 	{
			// getDBCPConn Exceptions 
			//   ...?
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e_validateSession) {
			super.sendForbidden(response);  	// permission related or network error exceptions.. 
		} finally {								// do this no matter what.. 
			AppUtils.closeQuiet(conn);
		}
	}
	
	
	
	public void doDeleteWork(Connection conn, Integer permGroupId) throws RecordNotFoundException, InvalidDeleteException, Exception {
		
		PermissionGroup perm = new PermissionGroup();
		perm.setPermissionGroupId(permGroupId);

		User user = new User();						//	Create a user object
		user.setPermissionGroupId(permGroupId);		//	Set the user object's group ID 
													//		to the ID of the group being deleted
		try {
			user.selectOne(conn);					//	Query to see if anybody at all is using this group ID
			throw new InvalidDeleteException();		//		throw an error that it cannot yet be deleted.
		} catch (RecordNotFoundException e) {		//  Nobody is using this ID
			perm.delete(conn);						//		Delete the permission group
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
			AppUtils.validateSession(request, Permission.SYSADMIN, PermissionLevel.PERMISSION_LEVEL_IS_READ);  		// make sure we're allowed to be here
			url = new AnsiURL(request, "permissionGroup",new String[] { ACTION_IS_LIST });							// parse the URL and look for "contact"
			PermissionGroupListResponse permissionGroupListResponse = makeSingleListResponse(conn, url);			// get the data we're looking for
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");											// add messages to the response
			permissionGroupListResponse.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, permissionGroupListResponse);					// send the response
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {								// these are thrown by session validation
			super.sendForbidden(response);
		} catch ( RecordNotFoundException e ) {			// if they're asking for an id that doesn't exist
			super.sendNotFound(response);						
		} catch ( ResourceNotFoundException e) {		
			super.sendNotFound(response);
		} catch ( Exception e) {						// something bad happened
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);					// return the connection to the pool
		}
	}
	
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
								
				PermGroupRequest permGroupRequest = new PermGroupRequest();

				if (!StringUtils.isBlank(jsonString))
					AppUtils.json2object(jsonString, permGroupRequest);
				
				url = new AnsiURL(request,"permissionGroup", new String[] {ACTION_IS_ADD});

				if ( url.getId() != null ) {
					// THis is an update
					this.logger.log(Level.DEBUG, "ID Found is " + Integer.toString(url.getId()));
					processUpdate(conn, response, url.getId(), permGroupRequest, sessionUser);

				} else if (url.getCommand().equalsIgnoreCase(ACTION_IS_ADD)) {
					// this is an add
					processAdd(conn, response, permGroupRequest, sessionUser);
				} else {
					// this is messed up
					this.logger.log(Level.DEBUG, "...this is messed up");
					super.sendNotFound(response);
				}
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				PermissionGroupResponse data = new PermissionGroupResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch ( RecordNotFoundException e ) {
				super.sendNotFound(response);
			} catch (ResourceNotFoundException e_AnsiURL) {
				super.sendNotFound(response);
			}
		} catch (NotAllowedException | TimeoutException | ExpiredLoginException e_validateSession) {
			super.sendNotAllowed(response);
		} catch ( NamingException | SQLException e_getDBCPConn) {			
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}			
	
	protected void processAdd(Connection conn, HttpServletResponse response, PermGroupRequest permissionGroupRequest, SessionUser sessionUser) throws Exception {   // copied from contactServlet
		this.logger.log(Level.DEBUG, "Hi! Let's go BOOM!");
		ResponseCode responseCode = null;
		PermissionGroup permissionGroup = new PermissionGroup();
	
		WebMessages webMessages = validateAdd(conn, permissionGroupRequest);
		if (webMessages.isEmpty()) {  // if validateAdd returned no messages/errors.. 
			// do the add.. 
			permissionGroup = doAdd(conn, permissionGroupRequest, sessionUser);
			String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");		
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
			responseCode = ResponseCode.SUCCESS;
		} else {
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		
		PermGroupCountRecord permGroupCountItem = new PermGroupCountRecord();
		PropertyUtils.copyProperties(permGroupCountItem, permissionGroup);
		permGroupCountItem.setUserCount(0);
		PermissionGroupResponse permissionGroupResponse = new PermissionGroupResponse(permGroupCountItem);
		permissionGroupResponse.setWebMessages(webMessages);		
		
		super.sendResponse(conn, response, responseCode, permissionGroupResponse);
	}

	protected WebMessages validateAdd(Connection conn, PermGroupRequest permGroupRequest) throws Exception {
			WebMessages webMessages = new WebMessages();
			// Use @RequiredForAdd annotations in the <realm>Request Class to check for missing fields.
			List<String> missingFields = super.validateRequiredAddFields(permGroupRequest);
		
			if ( missingFields.isEmpty() ) {	// if there aren't any missing fields
				// check that all special format fields are correct.. 
				List<String> badFormatFieldList = super.validateFormat(permGroupRequest);
				if ( badFormatFieldList.isEmpty() ) {	// if all required formats are valud.. 
					Integer status = permGroupRequest.getStatus();
					// make sure that status us one of the enumerated values.. 
					if ( ! Arrays.asList(new Integer[] { PermissionGroup.STATUS_IS_ACTIVE, PermissionGroup.STATUS_IS_INACTIVE}).contains(status)) {
						// add an error message to pass back to be displayed to the user.. 
						webMessages.addMessage("status", "Invalid Status");
					}
				} else {
					// we have required fields that are not populated
					String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
					// add a an error message for each missing field.. 
					for ( String field : missingFields ) {
						webMessages.addMessage(field, messageText);
					}
				}
				
				
			}
			// so, if no validation problems were found, this will be an empty list..
			return webMessages;   
		}

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

	protected WebMessages validateUpdate(Connection conn, PermissionGroup permissionGroup, PermGroupRequest permGroupRequest) throws RecordNotFoundException, Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredUpdateFields(permGroupRequest);
		if ( missingFields.isEmpty() ) {
			List<String> badFormatFieldList = super.validateFormat(permGroupRequest);
			if ( badFormatFieldList.isEmpty() ) {
				Integer status = permGroupRequest.getStatus();
				
				// make sure that status us one of the enumerated values.. 
				if ( ! Arrays.asList(new Integer[] { PermissionGroup.STATUS_IS_ACTIVE, PermissionGroup.STATUS_IS_INACTIVE}).contains(status)) {
					// add an error message to pass back to be displayed to the user.. 
					webMessages.addMessage("status", "Invalid Status");
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
		return webMessages;
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
}
