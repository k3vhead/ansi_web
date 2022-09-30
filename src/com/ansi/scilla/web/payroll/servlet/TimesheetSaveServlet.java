package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payroll.request.TimesheetRequest;
import com.ansi.scilla.web.payroll.response.TimesheetResponseEmployee;
import com.ansi.scilla.web.payroll.response.TimesheetResponse;
import com.ansi.scilla.web.payroll.response.TimesheetSaveResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TimesheetSaveServlet extends TimesheetServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "timesheetSave";

	
	public TimesheetSaveServlet() {
		super();
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		logger.log(Level.DEBUG, "entering TimesheetSaveServlet.doPost() ");
		WebMessages webMessages = new WebMessages();
		TimesheetResponse data = new TimesheetResponse ();
		try {
			SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
			try {	
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);				
				String jsonString = super.makeJsonString(request);	
				processRequest(conn, response, sessionData, jsonString);				
			} catch (com.ansi.scilla.web.common.exception.InvalidFormatException | com.fasterxml.jackson.databind.exc.InvalidFormatException e) {
				String fieldName = super.findBadField(e.getMessage());
				webMessages.addMessage(fieldName, "Invalid Format");
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
//		} catch ( ServletException | IOException e ) {
//			throw e;
		} catch ( Exception e ) {
			throw new ServletException(e);
		} finally {
			if ( conn != null ) {
				AppUtils.closeQuiet(conn);
			}
		}
	}

	private void processRequest(Connection conn, HttpServletResponse response, SessionData sessionData, String jsonString) throws JsonProcessingException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, Exception {
		TimesheetSaveResponse data = new TimesheetSaveResponse();

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(jsonString);
		JsonNode employeeListNode = rootNode.get("employeeList");
		Iterator<JsonNode> employeeIterator = employeeListNode.elements();
		while ( employeeIterator.hasNext() ) {
			String employeeString = employeeIterator.next().toString();
//			System.out.println(node.toString());
			
			TimesheetRequest timesheetRequest = new TimesheetRequest();
			AppUtils.json2object(employeeString, timesheetRequest);
			timesheetRequest.setAction(TimesheetRequest.ACTION_IS_UPDATE);
			System.out.println(timesheetRequest);
			TimesheetResponseEmployee postResponse = null;
			try {
				postResponse = processUpdate(conn, response, timesheetRequest, sessionData);
			} catch ( RecordNotFoundException e ) {
				logger.log(Level.DEBUG, "Updating non-existing record, so we add it");
				logger.log(Level.DEBUG, "Employee: " + timesheetRequest.getEmployeeCode());
				logger.log(Level.DEBUG, "Division: " + timesheetRequest.getDivisionId());
				logger.log(Level.DEBUG, "Location: " + timesheetRequest.getCity() + ", " + timesheetRequest.getState());
				logger.log(Level.DEBUG, "Date: " + timesheetRequest.getWeekEnding().getTime());
				postResponse = processAdd(conn, response, timesheetRequest, sessionData);
			}
			postResponse.data.setCity(timesheetRequest.getCity());
//			postResponse.data.setDirectLabor(null);
			postResponse.data.setDivisionId(timesheetRequest.getDivisionId());
			postResponse.data.setEmployeeCode(timesheetRequest.getEmployeeCode());
			postResponse.data.setEmployeeName(timesheetRequest.getEmployeeName());
//			postResponse.data.setExpenses(null);
//			postResponse.data.setExpensesAllowed(null);
//			postResponse.data.setExpensesSubmitted(null);
//			postResponse.data.setGrossPay(null);;
//			postResponse.data.setHolidayHours(null);
//			postResponse.data.setHolidayPay(null);
//			postResponse.data.setOtHours(null);
//			postResponse.data.setOtPay(null);
//			postResponse.data.setRegularHours(null);
//			postResponse.data.setRegularPay(null);
			postResponse.data.setState(timesheetRequest.getState());
//			postResponse.data.setVacationHours(null);
//			postResponse.data.setVacationPay(null);
//			postResponse.data.setVolume(null);
			postResponse.data.setWeekEnding(timesheetRequest.getWeekEnding());

			data.addTimesheet( postResponse );

		}

		data.setWebMessages(new WebMessages());
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);		
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.sendNotAllowed(response);
	}


	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendNotAllowed(response);
	}

}
