package com.ansi.scilla.web.division.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractCrudServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.division.request.DivisionUncloseRequest;
import com.ansi.scilla.web.division.response.DivisionCloseResponse;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class DivisionUncloseServlet extends AbstractCrudServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "divisionUnclose";
	

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SessionUser sessionUser = AppUtils.getSessionUser(request);
		try {
			String jsonString = super.makeJsonString(request);
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			SessionData sessionData = AppUtils.validateSession(request, Permission.DIVISION_CLOSE_WRITE);
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
	
				// figure out if this is an "add" or an "update"
				try {
					DivisionUncloseRequest divisionRequest = new DivisionUncloseRequest();
					AppUtils.json2object(jsonString, divisionRequest);
					WebMessages webMessages = divisionRequest.validate(conn);
					if ( webMessages.isEmpty() ) {
						Division division = uncloseDivision(conn, divisionRequest, sessionUser);
						conn.commit();
						sendItWorked(conn, response, division);
					} else {
						sendItFailed(conn, response, webMessages);
					}
				} catch (InvalidFormatException formatException) {
					processBadPostRequest(conn, response, formatException);
				}
			} catch (Exception e) {
				AppUtils.logException(e);
				AppUtils.rollbackQuiet(conn);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
	
		} catch (ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		}		
	}


	private Division uncloseDivision(Connection conn, DivisionUncloseRequest divisionRequest, SessionUser sessionUser) throws RecordNotFoundException, Exception {
		Division division = new Division();
		division.setDivisionId(divisionRequest.getDivisionId());
		division.selectOne(conn);
		Date now = new Date();
		Date closeDate = divisionRequest.getActCloseDate() == null ? (Date)null : divisionRequest.getActCloseDate().getTime();
		division.setActCloseDate(closeDate);
		division.setUpdatedBy(sessionUser.getUserId());
		division.setUpdatedDate(now);
		Division key = new Division();
		key.setDivisionId(divisionRequest.getDivisionId());
		division.update(conn, key);
		return division;
	}





	private void processBadPostRequest(Connection conn, HttpServletResponse response,
			InvalidFormatException formatException) throws Exception {
		WebMessages webMessages = new WebMessages();
		String field = findBadField(formatException.toString());
		String messageText = AppUtils.getMessageText(conn, MessageKey.INVALID_DATA, "Invalid Format");
		webMessages.addMessage(field, messageText);
		sendItFailed(conn, response, webMessages);
	
	}





	private void sendItWorked(Connection conn, HttpServletResponse response, Division division) throws Exception {
			WebMessages webMessages = new WebMessages();
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
			
			DivisionCloseResponse data = new DivisionCloseResponse();
			Calendar actCloseDate = division.getActCloseDate() == null ? (Calendar)null : DateUtils.toCalendar(division.getActCloseDate());
			data.setActCloseDate(actCloseDate);
	//		data.setClosedThruDate(closedThruDate);
			data.setDivisionDisplay(division.getDivisionDisplay());
			data.setDivisionId(division.getDivisionId());
	//		data.setNextCloseDate(nextCloseDate);
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		}





	private void sendItFailed(Connection conn, HttpServletResponse response, WebMessages webMessages) throws Exception {
		DivisionCloseResponse data = new DivisionCloseResponse();		
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);		
	}

	@Override
	protected WebMessages validateAdd(Connection conn, HashMap<String, Object> addRequest) throws Exception {
		throw new Exception("Not used");
//		return null;
	}


	@Override
	protected WebMessages validateUpdate(Connection conn, HashMap<String, Object> updateRequest) throws Exception {
		throw new Exception("Not used");
//		return null;
	}
	
		
}
