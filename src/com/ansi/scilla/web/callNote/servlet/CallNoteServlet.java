package com.ansi.scilla.web.callNote.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.callNote.CallNoteReference;
import com.ansi.scilla.common.db.CallLog;
import com.ansi.scilla.common.db.CallLogXref;
import com.ansi.scilla.web.callNote.request.CallNoteRequest;
import com.ansi.scilla.web.callNote.response.CallNoteResponse;
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

public class CallNoteServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "callNote";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			AppUtils.validateSession(request, Permission.CALL_NOTE_READ);
			
			String[] uri = request.getRequestURI().split("/");
			if ( uri.length < 2 ) {
				throw new ResourceNotFoundException();
			}
			String xrefId = uri[uri.length-1];
			String xrefType = uri[uri.length-2];
			
			if ( ! StringUtils.isNumeric(xrefId) ) {
				throw new ResourceNotFoundException();
			}
			try {
				CallNoteReference.valueOf(xrefType);
			} catch (IllegalArgumentException e) {
				throw new ResourceNotFoundException();
			}
			
			CallNoteResponse data = new CallNoteResponse(conn, xrefType, Integer.valueOf(xrefId));
			WebMessages webMessages = new WebMessages();
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch (ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}


	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			
			AppUtils.validateSession(request, Permission.CALL_NOTE_OVERRIDE);
						
			
			try {
				CallLogXref xref = new CallLogXref();
				xref.setCallLogId(url.getId());
				xref.delete(conn);
				
				CallLog callLog = new CallLog();
				callLog.setCallLogId(url.getId());
				callLog.delete(conn);
				conn.commit();
			} catch (Exception e) {
				conn.rollback();
				throw e;
			}
			
			CallNoteResponse data = new CallNoteResponse();
			WebMessages webMessages = new WebMessages();
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch (ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}	}


	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			SessionData sessionData = AppUtils.validateSession(request, Permission.CALL_NOTE_WRITE);
			SessionUser user = sessionData.getUser();
			
			String[] uri = request.getRequestURI().split("/");
			if ( uri.length < 2 ) {
				throw new ResourceNotFoundException();
			}
			String xrefId = uri[uri.length-1];
			String xrefType = uri[uri.length-2];
			
			if ( ! StringUtils.isNumeric(xrefId) ) {
				throw new ResourceNotFoundException();
			}
			try {
				CallNoteReference.valueOf(xrefType);
			} catch (IllegalArgumentException e) {
				throw new ResourceNotFoundException();
			}
			
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, jsonString);
			CallNoteRequest callNoteRequest = new CallNoteRequest();
			AppUtils.json2object(jsonString, callNoteRequest);
			WebMessages webMessages = callNoteRequest.getCallNoteId() == null ? callNoteRequest.validateAdd(conn) : callNoteRequest.validateUpdate(conn);
			if ( webMessages.isEmpty() ) {
				try {
					makeCallNote(conn, xrefType, xrefId, callNoteRequest, user);
					conn.commit();
				} catch ( Exception e) {
					conn.rollback();
					throw e;
				}
				CallNoteResponse data = new CallNoteResponse(conn, xrefType, Integer.valueOf(xrefId));
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
			} else {
				CallNoteResponse data = new CallNoteResponse();
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			}
			
			
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch (ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}


	private void makeCallNote(Connection conn, String xrefType, String xrefId, CallNoteRequest callNoteRequest, SessionUser user) throws Exception {
		Date today = new Date();
		GregorianCalendar startTime = new GregorianCalendar(
				callNoteRequest.getStartDate().get(Calendar.YEAR), 
				callNoteRequest.getStartDate().get(Calendar.MONTH),
				callNoteRequest.getStartDate().get(Calendar.DAY_OF_MONTH),
				callNoteRequest.getStartTime().get(Calendar.HOUR_OF_DAY),
				callNoteRequest.getStartTime().get(Calendar.MINUTE),
				0);

		CallLog callLog = new CallLog();
		callLog.setAddressId(callNoteRequest.getAddressId());
		callLog.setContent(callNoteRequest.getNotes());
		callLog.setContactId(callNoteRequest.getContactId());
		callLog.setSummary(callNoteRequest.getSummary());
		callLog.setTitle(callNoteRequest.getSummary());
		callLog.setUserId(callNoteRequest.getUserId());
		callLog.setStartTime(startTime.getTime());
		callLog.setContactType(callNoteRequest.getContactType());
		callLog.setUpdatedBy(user.getUserId());
		callLog.setUpdatedDate(today);

		if ( callNoteRequest.getCallNoteId() == null ) {
			// this is an add
			callLog.setAddedBy(user.getUserId());
			callLog.setAddedDate(today);
			Integer callNoteId = callLog.insertWithKey(conn);
			
			CallLogXref xref = new CallLogXref();
			xref.setXrefId(callNoteRequest.getXrefId());
			xref.setCallLogId(callNoteId);
			xref.setXrefType(callNoteRequest.getXrefType());
			xref.setAddedBy(user.getUserId());
			xref.setAddedDate(today);
			xref.setUpdatedBy(user.getUserId());
			xref.setUpdatedDate(today);
			xref.insertWithNoKey(conn);
		} else {
			// this is an update
			callLog.setCallLogId(callNoteRequest.getCallNoteId());
			CallLog key = new CallLog();
			key.setCallLogId(callNoteRequest.getCallNoteId());
			callLog.update(conn, key);
		}
		
	}

}
