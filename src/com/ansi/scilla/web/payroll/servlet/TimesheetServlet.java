package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Connection;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.PayrollWorksheet;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payroll.common.PayrollValidationResponse;
import com.ansi.scilla.web.payroll.request.TimesheetRequest;
import com.ansi.scilla.web.payroll.response.TimesheetResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TimesheetServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

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
		try {
			try {
				SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
	
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				TimesheetRequest timesheetRequest = new TimesheetRequest();
				String jsonString = super.makeJsonString(request);
				AppUtils.json2object(jsonString, timesheetRequest);
				
				
				switch ( timesheetRequest.getAction() ) {
				case TimesheetRequest.ACTION_IS_ADD:
					processAdd(conn, response, timesheetRequest, sessionData);
					break;
				case TimesheetRequest.ACTION_IS_UPDATE:
					processUpdate(conn, response, timesheetRequest, sessionData);
					break;
				default:
					throw new com.ansi.scilla.web.common.exception.InvalidFormatException(TimesheetRequest.EMPLOYEE_CODE);
				}
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

	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		TimesheetResponse data = new TimesheetResponse();
		try {
			try {
				AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
	
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				TimesheetRequest timesheetRequest = new TimesheetRequest();
				String jsonString = super.makeJsonString(request);
				AppUtils.json2object(jsonString, timesheetRequest);
				
				webMessages = timesheetRequest.validateDelete(conn);
				processDelete(conn, timesheetRequest);
				conn.commit();
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
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



	private void processAdd(Connection conn, HttpServletResponse response, TimesheetRequest timesheetRequest, SessionData sessionData) throws Exception {
		TimesheetResponse data = new TimesheetResponse();
		PayrollValidationResponse validationResponse = timesheetRequest.validateAdd(conn);
		
		// do the update for success and warning, but not for failure.
		if ( ! validationResponse.getResponseCode().equals(ResponseCode.EDIT_FAILURE) ) {
			Calendar today = AppUtils.getToday();
			PayrollWorksheet timesheet = new PayrollWorksheet();		
			populateTimesheetKeys(timesheet, timesheetRequest);
			populateTimesheetValues(timesheet, timesheetRequest);
			timesheet.setEmployeeName(timesheetRequest.getEmployeeName());
			timesheet.setAddedBy(sessionData.getUser().getUserId());
			timesheet.setAddedDate(today.getTime());
			timesheet.setUpdatedBy(sessionData.getUser().getUserId());
			timesheet.setUpdatedDate(today.getTime());
			timesheet.insertWithNoKey(conn);	
			
			conn.commit();
		} 

		data.setWebMessages(validationResponse.getWebMessages());
		super.sendResponse(conn, response, validationResponse.getResponseCode(), data);

	}
	
	
	

	private void processUpdate(Connection conn, HttpServletResponse response, TimesheetRequest timesheetRequest, SessionData sessionData) throws RecordNotFoundException, Exception {
		TimesheetResponse data = new TimesheetResponse();
		PayrollValidationResponse validationResponse = timesheetRequest.validateUpdate(conn);
		
		// do the update for success and warning, but not for failure.
		if ( ! validationResponse.getResponseCode().equals(ResponseCode.EDIT_FAILURE) ) {
			Calendar today = AppUtils.getToday();
			
			PayrollWorksheet timesheet = new PayrollWorksheet();
			populateTimesheetKeys(timesheet, timesheetRequest);
			PayrollWorksheet key = (PayrollWorksheet)timesheet.clone();
			timesheet.selectOne(conn);
			populateTimesheetValues(timesheet, timesheetRequest);
	
			timesheet.setUpdatedBy(sessionData.getUser().getUserId());
			timesheet.setUpdatedDate(today.getTime());
			timesheet.update(conn, key);
			
			conn.commit();
		} 
		
		data.setWebMessages(validationResponse.getWebMessages());
		super.sendResponse(conn, response, validationResponse.getResponseCode(), data);
	}

	
	
	private void processDelete(Connection conn, TimesheetRequest timesheetRequest) throws Exception {
		PayrollWorksheet timesheet = new PayrollWorksheet();		
		populateTimesheetKeys(timesheet, timesheetRequest);
		timesheet.delete(conn);		
	}



	private void populateTimesheetKeys(PayrollWorksheet timesheet, TimesheetRequest timesheetRequest) {
		timesheet.setDivisionId(timesheetRequest.getDivisionId());
		timesheet.setWeekEnding(timesheetRequest.getWeekEnding().getTime());
		timesheet.setState(timesheetRequest.getState());
		timesheet.setEmployeeCode(timesheetRequest.getEmployeeCode());
		timesheet.setCity(timesheetRequest.getCity());
		
	}

	private void populateTimesheetValues(PayrollWorksheet timesheet, TimesheetRequest timesheetRequest) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		for (TimesheetRequest.PayrollField key : TimesheetRequest.PayrollField.values() ) {
			String fieldName = key.fieldName();
			String getterName = "get" + StringUtils.capitalize(fieldName);
			Method getter = TimesheetRequest.class.getMethod(getterName, (Class<?>[])null);
			String setterName = "set" + StringUtils.capitalize(fieldName);
			Method setter = PayrollWorksheet.class.getMethod(setterName, new Class<?>[] {BigDecimal.class});
			Double getterValue = (Double)getter.invoke(timesheetRequest, (Object[])null);
			BigDecimal setterValue =  getterValue == null ? null : new BigDecimal(getterValue).round(MathContext.DECIMAL32);
			setter.invoke(timesheet, new Object[] {setterValue} );
		}
		Double productivity = timesheetRequest.getProductivity();
		timesheet.setProductivity(new BigDecimal(productivity == null ? 0.0D : productivity));
	}
	

}
