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

import com.ansi.scilla.common.db.PermissionGroup;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.common.exceptions.InvalidDeleteException;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.MessageKey;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.request.PermGroupRequest;
import com.ansi.scilla.web.response.permissionGroup.PermissionGroupListResponse;
import com.ansi.scilla.web.response.permissionGroup.PermissionGroupResponse;
import com.ansi.scilla.web.struts.SessionUser;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class PermissionGroupServlet extends AbstractServlet {
	
	/**
	 * 
	 * @author jwlewis
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String url = request.getRequestURI();
		int idx = url.indexOf("/permissionGroup/");
		if ( idx > -1 ) {
			// we're in the right place
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				
				// Figure out what we've got:
				String myString = url.substring(idx + "/permissionGroup/".length());
				
				String[] urlPieces = myString.split("/");
				String command = urlPieces[0];
				
				if ( StringUtils.isBlank(command) || ! StringUtils.isNumeric(command)) {
					super.sendNotFound(response);
				} else {
					try {
						doDeleteWork(conn, Integer.valueOf(command));
						conn.commit();
						PermissionGroupResponse permissionGroupResponse = new PermissionGroupResponse();
						super.sendResponse(conn, response, ResponseCode.SUCCESS, permissionGroupResponse);
					} catch (InvalidDeleteException e) {
						String message = AppUtils.getMessageText(conn, MessageKey.DELETE_FAILED, "Invalid Delete");
						WebMessages webMessages = new WebMessages();
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
						PermissionGroupResponse permissionGroupResponse = new PermissionGroupResponse();
						permissionGroupResponse.setWebMessages(webMessages);
						super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, permissionGroupResponse);
					} catch(RecordNotFoundException recordNotFoundEx) {
						super.sendNotFound(response);
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
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String url = request.getRequestURI();
		int idx = url.indexOf("/permissionGroup/");
		if ( idx > -1 ) {
			//String queryString = request.getQueryString();
			
			// Figure out what we've got:
			// "myString" is the piece of the URL that we actually care about
			String myString = url.substring(idx + "/permissionGroup/".length());
			String[] urlPieces = myString.split("/");
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
		
		SessionUser sessionUser = AppUtils.getSessionUser(request);
		String url = request.getRequestURI();
		
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
			WebMessages webMessages = validateAdd(conn, permGroupRequest);
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
