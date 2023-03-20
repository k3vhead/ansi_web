package com.ansi.scilla.web.division.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.division.query.DivisionCloseQuery;
import com.ansi.scilla.web.division.request.DivisionCloseRequest;
import com.ansi.scilla.web.division.response.DivisionCloseResponse;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class DivisionCloseServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "divisionClose";
	
	
	public static final String ACT_CLOSE_DATE = "act_close_date";
	public static final String NEXT_CLOSE_DATE = "next_close_date";
	public static final String LAST_CLOSE_DATE = "last_close_date";
	
	public static final String ACT_CLOSE_DATE_DISPLAY = "act_close_date_display";
	public static final String NEXT_CLOSE_DATE_DISPLAY = "next_close_date_display";
	public static final String LAST_CLOSE_DATE_DISPLAY = "last_close_date_display";
	public static final String CAN_CLOSE = "can_close";

	public DivisionCloseServlet() {
		super(Permission.DIVISION_CLOSE_READ);
		cols = new String[] { 
				DivisionCloseQuery.DIVISION_ID,
				DivisionCloseQuery.DIV,
				DivisionCloseQuery.DIVISION_DESCRIPTION,
				DivisionCloseQuery.ACT_CLOSE_DATE,
				DivisionCloseQuery.LAST_CLOSE_DATE,
				DivisionCloseQuery.NEXT_CLOSE_DATE,
				CAN_CLOSE,
				};
		super.setAmount(50);
		super.itemTransformer = new ItemTransformer();
	}


	


	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		
		SessionUser user = sessionData.getUser();
		List<SessionDivision> divisionList = sessionData.getDivisionList();
		try {
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			String searchTerm = null;
			if(request.getParameter("search[value]") != null){
				searchTerm = request.getParameter("search[value]");
			}
//			Integer divisionIdFilterValue = null;			
			DivisionCloseQuery lookupQuery = new DivisionCloseQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
//			divisionIdFilterValue = url.getId();
//			if ( divisionIdFilterValue == null ) {
//				throw new ResourceNotFoundException();
//			}
//			lookupQuery.setDivisionId(divisionIdFilterValue);
			
			return lookupQuery;
			
		} catch (ResourceNotFoundException e) { 
			throw new RuntimeException(e);
		}
	}


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
					DivisionCloseRequest divisionRequest = new DivisionCloseRequest();
					AppUtils.json2object(jsonString, divisionRequest);
					WebMessages webMessages = divisionRequest.validate(conn);
					if ( webMessages.isEmpty() ) {
						Division division = closeDivision(conn, divisionRequest, sessionUser);
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


	private Division closeDivision(Connection conn, DivisionCloseRequest divisionRequest, SessionUser sessionUser) throws RecordNotFoundException, Exception {
		Division division = new Division();
		division.setDivisionId(divisionRequest.getDivisionId());
		division.selectOne(conn);
		Date now = new Date();
		division.setActCloseDate(now);
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
			data.setActCloseDate(DateUtils.toCalendar(division.getActCloseDate()));
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





	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {
		private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy"); 
	
		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			
			Timestamp actCloseDate = (Timestamp)arg0.get(ACT_CLOSE_DATE);
			if ( actCloseDate != null ) {
				String completedDateDisplay = sdf.format(actCloseDate);
				arg0.put(ACT_CLOSE_DATE_DISPLAY, completedDateDisplay);
			}
			
			java.sql.Date nextCloseDate = (java.sql.Date)arg0.get(NEXT_CLOSE_DATE);
			if ( nextCloseDate != null ) {
				String nextCloseDateDisplay = sdf.format(nextCloseDate);
				arg0.put(NEXT_CLOSE_DATE_DISPLAY, nextCloseDateDisplay);
			}
			
			java.sql.Date lastCloseDate = (java.sql.Date)arg0.get(LAST_CLOSE_DATE);
			if ( lastCloseDate != null ) {
				String lastDateDisplay = sdf.format(lastCloseDate);
				arg0.put(LAST_CLOSE_DATE_DISPLAY, lastDateDisplay);
			}
			
			if ( actCloseDate == null ) {
				arg0.put(CAN_CLOSE, true);
			} else {
				Date now = new Date();
				if ( now.after(nextCloseDate)) {
					arg0.put(CAN_CLOSE, true);
				} else {
					arg0.put(CAN_CLOSE, false);
				}
			}
			return arg0;
		}
		
	}
	
		
}
