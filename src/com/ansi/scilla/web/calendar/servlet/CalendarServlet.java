package com.ansi.scilla.web.calendar.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.AnsiCalendar;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.calendar.request.CalendarRequest;
import com.ansi.scilla.web.calendar.response.CalendarResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;



public class CalendarServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "ansiCalendar";
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		AnsiURL url = null;
		WebMessages messages = new WebMessages();
		logger.log(Level.DEBUG, "We're HERE !!!");
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.CALENDAR_READ);
			url = new AnsiURL(request, REALM, (String[])null, false);	
			if (url.getId() == null ) {
				Calendar now = Calendar.getInstance();
				url.setId(now.get(Calendar.YEAR));
			}
			CalendarResponse data = new CalendarResponse(conn, url.getId());
			if ( data.getDates().size() == 0 ) {
				messages.addMessage(WebMessages.GLOBAL_MESSAGE, "No Special Dates for " + url.getId());
			} else {
				messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
			}
			data.setWebMessages(messages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}


	


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		AnsiURL ansiURL = null; 
		Connection conn = null;
		CalendarResponse data = new CalendarResponse();
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);

			SessionData sessionData = AppUtils.validateSession(request, Permission.CALENDAR_WRITE);
			try{
				CalendarRequest calendarRequest = new CalendarRequest();
				AppUtils.json2object(jsonString, calendarRequest);
//				ansiURL = new AnsiURL(request, REALM, (String[])null); 
				
				/** ***************************** **/
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				JsonNode jsonNode = mapper.readTree(jsonString);
				String calendarString = jsonNode.get(CalendarRequest.DATE).textValue();
				SimpleDateFormat jsonFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date ansiDate = jsonFormat.parse(calendarString);				
				/** ****************************** **/
				calendarRequest.setDate(DateUtils.toCalendar(AppUtils.convertToChicagoTime(DateUtils.toCalendar(ansiDate))));

				SessionUser sessionUser = sessionData.getUser(); 
				WebMessages webMessages = calendarRequest.validateAdd(conn);
				if ( webMessages.isEmpty()) {
					doDateAdd(conn, calendarRequest, sessionUser);
					SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
					data = new CalendarResponse(conn, calendarRequest.getDate().get(Calendar.YEAR));
					data.setUpdated(monthFormat.format(calendarRequest.getDate().getTime()));
					super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
				} else {
					data.setWebMessages(webMessages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
				}

			}  catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);			
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.rollbackQuiet(conn);
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		Connection conn = null;
		CalendarResponse data = new CalendarResponse();
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);

			AppUtils.validateSession(request, Permission.CALENDAR_WRITE);
			try{
				CalendarRequest calendarRequest = new CalendarRequest();
				
				/** ***************************** **/
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				JsonNode jsonNode = mapper.readTree(jsonString);
				String calendarString = jsonNode.get(CalendarRequest.DATE).textValue();
				SimpleDateFormat jsonFormat = new SimpleDateFormat("MM/dd/yyyy");
				Date ansiDate = jsonFormat.parse(calendarString);				
				/** ****************************** **/
				calendarRequest.setDateType(jsonNode.get(CalendarRequest.DATE_TYPE).textValue());
				calendarRequest.setDate(DateUtils.toCalendar(AppUtils.convertToChicagoTime(DateUtils.toCalendar(ansiDate))));

				WebMessages webMessages = calendarRequest.validateDelete();
				if ( webMessages.isEmpty()) {
					doDateDelete(conn, calendarRequest);
					SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
					data = new CalendarResponse(conn, calendarRequest.getDate().get(Calendar.YEAR));
					data.setUpdated(monthFormat.format(calendarRequest.getDate().getTime()));
					super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
				} else {
					data.setWebMessages(webMessages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
				}

			}  catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);			
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.rollbackQuiet(conn);
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}





	private void doDateAdd(Connection conn, CalendarRequest calendarRequest, SessionUser sessionUser) throws Exception {
		AnsiCalendar ansiCalendar = new AnsiCalendar();
		Date now = new Date();
		ansiCalendar.setAddedBy(sessionUser.getUserId());
		ansiCalendar.setAddedDate(now);
		ansiCalendar.setAnsiDate(calendarRequest.getDate().getTime());
		ansiCalendar.setDateType(calendarRequest.getDateType());
		ansiCalendar.setUpdatedBy(sessionUser.getUserId());
		ansiCalendar.setUpdatedDate(now);
		ansiCalendar.insertWithNoKey(conn);	
		conn.commit();
	}
	
	private void doDateDelete(Connection conn, CalendarRequest calendarRequest) throws Exception {
		// we do it this way because date and calendar objects have a time built in, and the date object we
		// get from the db may be inconsistent with the date object we get from the request object.
		// Weirdness ensues.
		PreparedStatement ps = conn.prepareStatement("delete from ansi_calendar\n" + 
				"where year(ansi_date)=? and month(ansi_date)=? and day(ansi_date)=? and date_type=?");
		int mmm = calendarRequest.getDate().get(Calendar.MONTH)+1;
		System.out.println("Deleting: " + calendarRequest.getDate().get(Calendar.YEAR) + "|" + 
				mmm + "|" +  calendarRequest.getDate().get(Calendar.DAY_OF_MONTH) + "|" +
				calendarRequest.getDateType());
		ps.setInt(1, calendarRequest.getDate().get(Calendar.YEAR));
		ps.setInt(2, calendarRequest.getDate().get(Calendar.MONTH)+1);   // because it's zero-based
		ps.setInt(3, calendarRequest.getDate().get(Calendar.DAY_OF_MONTH));
		ps.setString(4, calendarRequest.getDateType());
		ps.executeUpdate();
		conn.commit();
	}


	



	
}
