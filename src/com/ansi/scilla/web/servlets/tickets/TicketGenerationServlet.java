package com.ansi.scilla.web.servlets.tickets;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.common.db.PermissionLevel;
//import com.ansi.scilla.batch.scheduling.GenerateTickets;
import com.ansi.scilla.common.jobticket.JobUtils;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.request.ticket.TicketGenerationRequest;
import com.ansi.scilla.web.response.ticket.TicketGenerationResponse;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class TicketGenerationServlet extends AbstractServlet{
	
	private static final long serialVersionUID = 1L;
	
	
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.sendNotAllowed(response);
	}



	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.sendNotAllowed(response);
	}



	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			try {
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				String jsonString = super.makeJsonString(request);
				TicketGenerationRequest generateTicketRequest = new TicketGenerationRequest();
				AppUtils.json2object(jsonString, generateTicketRequest);
//				ansiURL = new AnsiURL(request, "invoiceGeneration", (String[])null); //  .../ticket/etc
				SessionData sessionData = AppUtils.validateSession(request, Permission.TICKET, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
				
				SessionUser sessionUser = sessionData.getUser(); 
				List<String> addErrors = super.validateRequiredAddFields(generateTicketRequest);
				HashMap<String, String> errors = new HashMap<String, String>();
				if ( addErrors.isEmpty() ) {
					errors = validateDates(generateTicketRequest);
				}
				if (addErrors.isEmpty() && errors.isEmpty()) {
					processUpdate(conn, request, response, generateTicketRequest, sessionUser);
//					fakeThePDF(conn, request, response, invoicePrintRequest);
				} else {
					processError(conn, response, addErrors, errors);
				}
				
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				TicketGenerationResponse data = new TicketGenerationResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
				super.sendForbidden(response);
			}
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	private HashMap<String, String> validateDates(TicketGenerationRequest generateTicketRequest) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.S");
		System.out.println("Start Date: " + sdf.format(generateTicketRequest.getStartDate()));
		System.out.println("End Date: " + sdf.format(generateTicketRequest.getEndDate()));
		HashMap<String, String> dateErrors = new HashMap<String, String>();
		if(!DateUtils.isSameDay(generateTicketRequest.getStartDate(), generateTicketRequest.getEndDate())){
			if ( ! generateTicketRequest.getEndDate().after(generateTicketRequest.getStartDate())) {
				dateErrors.put("endDate", "End Date Must be on or after Start Date");
			}
		}
		return dateErrors;
	}
	
	private void processError(Connection conn, HttpServletResponse response, List<String> addErrors,
			HashMap<String,String> errors) throws Exception {
		WebMessages webMessages = new WebMessages();
		for ( String error : addErrors ) {
			webMessages.addMessage(error, "Required field");
		}
		for ( String key : errors.keySet() ) {
			webMessages.addMessage(key, errors.get(key));
		}
		TicketGenerationResponse data = new TicketGenerationResponse();
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
	}

	private void processUpdate(Connection conn, HttpServletRequest request, HttpServletResponse response, 
			TicketGenerationRequest generateTicketRequest, SessionUser sessionUser) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.S");
		Calendar startDate = Calendar.getInstance(new Locale("America/Chicago"));
		Calendar endDate = Calendar.getInstance(new Locale("America/Chicago"));
		startDate.setTime(generateTicketRequest.getStartDate());
		endDate.setTime(generateTicketRequest.getEndDate());
		DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
		endDate = DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
		System.out.println(sdf.format(startDate.getTime()));
		System.out.println(sdf.format(endDate.getTime()));
		
		JobUtils.generateTicketsFromJobSchedule(conn, generateTicketRequest.getDivisionId(), startDate, endDate, sessionUser.getUserId());
		conn.commit();
		WebMessages webMessages = new WebMessages();
		TicketGenerationResponse data = new TicketGenerationResponse();
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}
	
	
}
