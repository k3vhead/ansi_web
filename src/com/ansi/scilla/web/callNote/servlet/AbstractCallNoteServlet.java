package com.ansi.scilla.web.callNote.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.callNote.CallNoteReference;
import com.ansi.scilla.common.db.CallLog;
import com.ansi.scilla.common.db.CallLogXref;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.callNote.request.CallNoteRequest;
import com.ansi.scilla.web.callNote.response.CallNoteResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;

public abstract class AbstractCallNoteServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			SessionData sessionData = AppUtils.validateSession(request, Permission.CALL_NOTE_READ);
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
			
			CallNoteResponse data = makeResponse(conn, xrefType, Integer.valueOf(xrefId), user);
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
			
			AnsiURL url = new AnsiURL(request, makeRealm(), (String[])null);
			
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
			WebMessages webMessages = callNoteRequest.getCallNoteId() == null ? callNoteRequest.validateAdd(conn, sessionData) : callNoteRequest.validateUpdate(conn);
			if ( webMessages.isEmpty() ) {
				try {
					makeCallNote(conn, xrefType, xrefId, callNoteRequest, sessionData);
					conn.commit();
				} catch ( Exception e) {
					conn.rollback();
					throw e;
				}
				CallNoteResponse data = makeResponse(conn, xrefType, Integer.valueOf(xrefId), user);
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


	private void makeCallNote(Connection conn, String xrefType, String xrefId, CallNoteRequest callNoteRequest, SessionData sessionData) throws Exception {
		SessionUser user = sessionData.getUser();
		Date today = new Date();

		// we go through this conversion/transition/shuffle because the html5 date and time tags are
		// being translated as UTC, and we're expecting the dates in the DB to be US/Central (essentially chicago time)
		// This should have been handled by the JSON serializer, but that doesn't appear to be working as expected.
		// Even if the serializer returns a chicago time, this code should just change chicago to chicago and all is good.
		TimeZone callTimeZone = callNoteRequest.getStartDate().getTimeZone();
		ZoneId zid = callTimeZone.toZoneId();
		ZonedDateTime callTime = ZonedDateTime.of(
				callNoteRequest.getStartDate().get(Calendar.YEAR), 
				callNoteRequest.getStartDate().get(Calendar.MONTH) + 1,   // different between java.util.Calendar and java.time.ZonedDateTime
				callNoteRequest.getStartDate().get(Calendar.DAY_OF_MONTH),
				callNoteRequest.getStartTime().get(Calendar.HOUR_OF_DAY),
				callNoteRequest.getStartTime().get(Calendar.MINUTE),
				0,  // second
				0,	// nano
				zid);
		
		ZoneId chicagoZone = ZoneId.of("US/Central");
		ZonedDateTime adjustedCallTime = callTime.withZoneSameInstant(chicagoZone);
		
		

		CallLog callLog = new CallLog();
		callLog.setAddressId(callNoteRequest.getAddressId());
		callLog.setContent(callNoteRequest.getNotes());
		callLog.setContactId(callNoteRequest.getContactId());
		callLog.setSummary(callNoteRequest.getSummary());
		callLog.setTitle(callNoteRequest.getSummary());
		
		boolean canDoUser = false;
		if ( sessionData.getUser().getSuperUser().equals(User.SUPER_USER_IS_YES) ) {
			canDoUser = true;
		}
		if ( sessionData.hasPermission(Permission.CALL_NOTE_OVERRIDE)  ) {
			canDoUser = true;
		}
		if ( canDoUser ) {
			callLog.setUserId(callNoteRequest.getUserId());
		} else {
			callLog.setUserId(user.getUserId());
		}		
		
		callLog.setStartTime(Date.from(adjustedCallTime.toInstant()));
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
	
	protected abstract CallNoteResponse makeResponse(Connection conn, String xrefType, Integer xrefId, SessionUser user) throws Exception;
	protected abstract String makeRealm();
}
