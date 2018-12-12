package com.ansi.scilla.web.permission.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.PermissionGroup;
import com.ansi.scilla.common.db.PermissionGroupLevel;
import com.ansi.scilla.web.common.request.RequestValidator;
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
import com.ansi.scilla.web.permission.request.PermissionRequest;
import com.ansi.scilla.web.permission.response.PermGroupCountRecord;
import com.ansi.scilla.web.permission.response.PermissionGroupResponse;
import com.ansi.scilla.web.permission.response.PermissionListResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;
/**
  The url for GET will be one of:
  
 		GET permission/<permissionGroupId> returns:
 		{
  			"webMessages": null,
  			"permissionList": [
  				"QUOTE",
  				"QUOTE_READ",
  				"QUOTE_WRITE",
  				"INVOICE",
  				"INVOICE_READ",
  				]
  		}
 ***********************************************************************************
 		GET permission/list returns:
 		{
  			"webMessages": null,
 			"permmissionList": [{
 				"permissionName": "INVOICE",
 				"level": 1 }, {
 				"permissionName": "INVOICE",
 				"level": 0 }, {
				"permissionName": "JOB",
				"level": 1 }, {
				"permissionName": "JOB",
				"level": 0 }]
		}
 
 ***********************************************************************************
  
  The url for update will be a POST to:
  		/permission/<permissionGroupId#>,	(returns a list of permissions having the same groupId)
  
  		data to pass in will look like : 
 			{	"permissionGroupId": 123,
 				permissionName:	"QUOTE",
 				permissionLevel: -1
 			}
 */

public class PermissionServlet extends AbstractServlet {
	/**
	 * @author jwlewis
	 * @author kwagner;
	 */
	protected final Logger logger = LogManager.getLogger(PermissionServlet.class);
	protected final Boolean LogDebugMsgs = true;
	
	private static final long serialVersionUID = 1L;

/*
	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		AnsiURL url = null;
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			AppUtils.validateSession(request, Permission.SYSADMIN, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);

			try {
				url = new AnsiURL(request,"permissionGroup", new String[] {""});

				if (url.getId() != null) {			// if true.. this is a delete and we have am id to delete
					doDeleteWork(conn, url.getId());
					conn.commit();
					PermissionGroupResponse permissionGroupResponse = new PermissionGroupResponse();
					super.sendResponse(conn, response, ResponseCode.SUCCESS, permissionGroupResponse);
				} else { // this is a call to DELETE with no id.. send an error.. 
					super.sendForbidden(response);
				}
			} catch (InvalidDeleteException e) {	// doDeleteWork Exceptions
				// an exception was thrown when we tried to delete it.. 
				// let the user know.. 
				String message;
				message = AppUtils.getMessageText(conn, MessageKey.DELETE_FAILED, "Invalid Delete");
				WebMessages webMessages = new WebMessages();
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
				PermissionGroupResponse permissionGroupResponse = new PermissionGroupResponse();
				permissionGroupResponse.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, permissionGroupResponse);
			} catch(RecordNotFoundException e_doDeleteWork) {
				super.sendNotFound(response);
			} catch (ResourceNotFoundException e_AnsiURL) {
				super.sendNotFound(response);
			}	
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e_validateSession) {
			super.sendForbidden(response);  	// permission related or network error exceptions.. 
		} catch ( Exception e_getMessage_sendResponse) {
			AppUtils.logException(e_getMessage_sendResponse);			// unaccounted for exceptions. 
			throw new ServletException(e_getMessage_sendResponse);
		} finally {								// do this no matter what.. 
			AppUtils.closeQuiet(conn);
		}
	}
*/
	
/*
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
*/

	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { //  Note : modeled after recommended uri parsing pattern 2018-04-19 kjw
		AnsiURL url = null;
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.PERMISSIONS_READ);

			url = new AnsiURL(request, "permission", new String[] { ACTION_IS_LIST });	
			if ( StringUtils.isBlank(url.getCommand() )) {
				validateGroupId(conn, url.getId());
			}			
			PermissionListResponse permissionListResponse = makePermissionListResponse(conn, url);
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");										// add messages to the response
			
			permissionListResponse.setWebMessages(webMessages);
			
			super.sendResponse(conn, response, ResponseCode.SUCCESS, permissionListResponse);					// send the response
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {							// these are thrown by session validation
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
		AnsiURL url = null;
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			//validateSession()
			//SessionData sessionData = AppUtils.validateSession(request, Permission.SYSADMIN, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			SessionData sessionData = AppUtils.validateSession(request, Permission.PERMISSIONS_WRITE);
			SessionUser sessionUser = sessionData.getUser();

			try {
				String jsonString = super.makeJsonString(request);								
				PermissionRequest permRequest = new PermissionRequest();

				if (!StringUtils.isBlank(jsonString))
					AppUtils.json2object(jsonString, permRequest);
				
				url = new AnsiURL(request,"permission", (String []) null);

				if ( url.getId() == null ) {									// this is an update
					super.sendNotFound(response);
				} else {
					processUpdate(conn, response, url.getId(), permRequest, sessionUser);
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
			super.sendForbidden(response);
		} catch ( Exception e ) {   // SQLException and NamingException are subclasses of Exception 
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}			
	
	protected void validateGroupId(Connection conn, Integer permissionGroupId) throws RecordNotFoundException, Exception{
		logger.log(Level.DEBUG, "validating group id: " + permissionGroupId);
		PermissionGroup permissionGroup = new PermissionGroup();
		permissionGroup.setPermissionGroupId(permissionGroupId);
		permissionGroup.selectOne(conn);		// this throws RecordNotFound, which is propagated up the line into a 404 return
	}

	protected void processAdd(Connection conn, HttpServletResponse response, PermissionRequest permRequest, SessionUser sessionUser) throws Exception {   // copied from contactServlet
		ResponseCode responseCode = null;
		PermissionGroup permissionGroup = new PermissionGroup();
	
		WebMessages webMessages = validateAdd(conn, permRequest);
		
		if (webMessages.isEmpty()) {  														// if validateAdd returned no messages/errors..  
			permissionGroup = doAdd(conn, permRequest, sessionUser);				// do the add..
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

	protected WebMessages validateAdd(Connection conn, PermissionRequest permRequest) throws Exception {
			WebMessages webMessages = new WebMessages();
			List<String> missingFields = super.validateRequiredAddFields(permRequest);		// Use @RequiredForAdd annotations in the <realm>Request Class to check for missing fields.
			
			List<String> badFormatFieldList;												// if any fields with invalid formats are found
			
			if ( missingFields.isEmpty() ) {	// if there aren't any missing fields
				badFormatFieldList = super.validateFormat(permRequest);					//      they will be added to this list.
				if ( badFormatFieldList.isEmpty() ) {											// if all required formats are valid.. 
					boolean status = permRequest.isPermissionIsActive();
					Integer[] permittedValues = new Integer[2];									// build list of allowable values
					permittedValues[0] = PermissionGroup.STATUS_IS_ACTIVE;
					permittedValues[1] = PermissionGroup.STATUS_IS_INACTIVE;
					
					if ( ! Arrays.asList(permittedValues).contains(status)) {					// make sure that status us one of the enumerated values..											 
						webMessages.addMessage("status", "Invalid Status");						// add an error message to pass back to be displayed to the user..				
					}
				} else {																		// we have required fields that are not populated
					String messageText = AppUtils.getMessageText(conn, MessageKey.INVALID_DATA, "Invalid Entry");	 
					for ( String field : badFormatFieldList ) {									// add a an error message for each missing field..
						webMessages.addMessage(field, messageText);
					}
				}
			} else {																		// we have required fields that are not populated
				String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");	 
				for ( String field : missingFields ) {										// add a an error message for each missing field..
					webMessages.addMessage(field, messageText);
				}
			}
			return webMessages;	// so, if no validation problems were found, this will be an empty list..   
		}

	protected PermissionGroup doAdd(Connection conn, PermissionRequest permRequest, SessionUser sessionUser) throws Exception {
		PermissionGroup permissionGroup = new PermissionGroup();
		//permissionGroup.setDescription(permRequest.getDescription());
		permissionGroup.setName(permRequest.getPermissionName());
		//permissionGroup.setStatus(permRequest.isPermissionIsActive());
	
		permissionGroup.setAddedBy(sessionUser.getUserId());		
		permissionGroup.setUpdatedBy(sessionUser.getUserId());
	
		Integer permGroupId = permissionGroup.insertWithKey(conn);
		permissionGroup.setPermissionGroupId(permGroupId);
		conn.commit();
		return permissionGroup;
	}

	protected void processUpdate(Connection conn, HttpServletResponse response, Integer permGroupId, PermissionRequest permRequest, SessionUser sessionUser) throws RecordNotFoundException, Exception {
		
		
		//WebMessages webMessages = validateUpdate(conn, permissionGroup, permRequest);
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateId(conn, webMessages, "permission_group", PermissionGroup.PERMISSION_GROUP_ID, WebMessages.GLOBAL_MESSAGE, permGroupId, true);
		RequestValidator.validatePermissionName(webMessages, PermissionRequest.PERMISSION_NAME, permRequest.getPermissionName(), true);
		if (webMessages.isEmpty()) {
			doUpdate(conn, permGroupId, permRequest, sessionUser);
			conn.commit();
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update successful!");
			
			PermissionGroupResponse permGroupResp = new PermissionGroupResponse();
			permGroupResp = new PermissionGroupResponse(conn, permGroupId);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, permGroupResp);
			
		} else {
			PermissionGroupResponse permGroupResp = new PermissionGroupResponse(conn, permGroupId);
			permGroupResp.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, permGroupResp);
		}
			
	}

	protected WebMessages validateUpdate(Connection conn, PermissionGroup permissionGroup, PermissionRequest permRequest) throws RecordNotFoundException, Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredUpdateFields(permRequest);
		if ( missingFields.isEmpty() ) {
			List<String> badFormatFieldList = super.validateFormat(permRequest);
			if ( badFormatFieldList.isEmpty() ) {
				boolean status = permRequest.isPermissionIsActive();
				
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

	protected void doUpdate (Connection conn, Integer permGroupId, PermissionRequest permRequest, SessionUser sessionUser) throws Exception {
		Permission reqPermission = Permission.valueOf(permRequest.getPermissionName());
		
		List<Permission> functionalAreaList = reqPermission.makeFunctionalAreaList();
		String bindVar = AppUtils.makeBindVariables(functionalAreaList);
		String sql = "delete from permission_group_level where permission_group_id = ? and permission_name in " + bindVar;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, permGroupId);
		for(int n = 0; n < functionalAreaList.size(); n++) {
			ps.setString(n + 2, functionalAreaList.get(n).name());
		}
		ps.executeUpdate();
		if(permRequest.isPermissionIsActive() == true) {
			PermissionGroupLevel pgl = new PermissionGroupLevel();
			pgl.setAddedBy(sessionUser.getUserId());
			//pgl.setAddedDate(Calendar.getInstance());
			pgl.setPermissionGroupId(permGroupId);
			pgl.setPermissionLevel(1);
			pgl.setPermissionName(permRequest.getPermissionName());
			pgl.setUpdatedBy(sessionUser.getUserId());
			//pgl.setUpdatedDate(Calendar.getInstance());
			//pgl.setUseSystemDate(true);
			pgl.insertWithNoKey(conn);
			
		}

	}
	
	protected PermissionListResponse makePermissionListResponse(Connection conn, AnsiURL url) throws Exception {
		
		PermissionListResponse permissionListResponse;
		
		if (url.getId() != null) {
			// if an ID was passed in, then get all permissions having that group ID.
			// note : this queries the permission_group_level table.
			permissionListResponse = new PermissionListResponse(conn, url.getId());			
		} else {
			// If no ID return LIST of all permissions
			// then return all permissions in all groups
			permissionListResponse = new PermissionListResponse();			
		}
		return permissionListResponse;
	}	
}
