package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payroll.request.TimesheetRequest;
import com.ansi.scilla.web.payroll.response.TimesheetResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TimesheetServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doDelete(request, response);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		TimesheetResponse data = new TimesheetResponse ();
		try {
			try {
				AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
	
				conn = AppUtils.getDBCPConn();
				TimesheetRequest timesheetRequest = new TimesheetRequest(request);
				data = new TimesheetResponse(conn, 
						timesheetRequest.getDivisionId(), 
						timesheetRequest.getWeekEnding(), 
						timesheetRequest.getState(), 
						timesheetRequest.getEmployeeCode(), 
						timesheetRequest.getCity());
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	
			} catch (com.ansi.scilla.web.common.exception.InvalidFormatException e) {
				String fieldName = super.findBadField(e.getMessage());
				webMessages.addMessage(fieldName, "Invalid Format");
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (com.fasterxml.jackson.databind.exc.InvalidFormatException e) {
				String fieldName = super.findBadField(e.getMessage());
				webMessages.addMessage(fieldName, "Invalid Format");
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
				super.sendForbidden(response);
			} finally {
				AppUtils.closeQuiet(conn);
			}
		} catch ( Exception e) {
			throw new ServletException(e);
		} 		
	}

	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		TimesheetResponse data = new TimesheetResponse ();
		ResponseCode responseCode = null;
		try {
			try {
				SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
	
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				TimesheetRequest timesheetRequest = new TimesheetRequest();
				String jsonString = super.makeJsonString(request);
				AppUtils.json2object(jsonString, timesheetRequest);
				
				webMessages = timesheetRequest.validate(conn);
				if ( webMessages.isEmpty() ) {
					processUpdate(conn, timesheetRequest, sessionData);
					conn.commit();
					responseCode = ResponseCode.SUCCESS;
				} else {
					conn.rollback();
					responseCode = ResponseCode.EDIT_FAILURE;
				}
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, responseCode, data);
			} catch ( RecordNotFoundException e) {
				super.sendNotFound(response);
			} catch (com.ansi.scilla.web.common.exception.InvalidFormatException e) {
				String fieldName = super.findBadField(e.getMessage());
				webMessages.addMessage(fieldName, "Invalid Format");
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (com.fasterxml.jackson.databind.exc.InvalidFormatException e) {
				String fieldName = super.findBadField(e.getMessage());
				webMessages.addMessage(fieldName, "Invalid Format");
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
				super.sendForbidden(response);
			} finally {
				AppUtils.closeQuiet(conn);
			}
		} catch ( Exception e) {
			throw new ServletException(e);
		} 
		
	}

	private void processUpdate(Connection conn, TimesheetRequest timesheetRequest, SessionData sessionData) throws RecordNotFoundException {
		// TODO Auto-generated method stub
		
	}
	

}
