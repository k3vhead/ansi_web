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

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.common.db.PayrollWorksheet;
import com.ansi.scilla.common.payroll.common.PayrollUtils;
import com.ansi.scilla.common.payroll.parser.worksheet.PayrollWorksheetEmployee;
import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheetEmployee;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.locale.common.LocaleUtils;
import com.ansi.scilla.web.payroll.common.PayrollValidation;
import com.ansi.scilla.web.payroll.request.TimesheetRequest;
import com.ansi.scilla.web.payroll.response.TimesheetResponseEmployee;
import com.ansi.scilla.web.payroll.response.TimesheetEmployee;
import com.ansi.scilla.web.payroll.response.TimesheetResponse;
import com.ansi.scilla.web.payroll.response.TimesheetValidationResponse;
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
//				logger.log(Level.DEBUG, timesheetRequest);
				
				Locale locale = LocaleUtils.makeLocale(conn, timesheetRequest.getState(), timesheetRequest.getCity());
				data = new TimesheetResponse(conn, 
						timesheetRequest.getDivisionId(), 
						timesheetRequest.getWeekEnding(), 
						timesheetRequest.getEmployeeCode(), 
						locale);
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
		logger.log(Level.DEBUG, "entering timesheetServlet.doPost() ");
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
								
				logger.log(Level.DEBUG, "Action sent was -> " + timesheetRequest.getAction());				
				switch ( timesheetRequest.getAction() ) {
				case TimesheetRequest.ACTION_IS_ADD:
					logger.log(Level.DEBUG, "msg from the TimesheetServlet - TimesheetRequest.ACTION_IS_ADD");
					TimesheetResponseEmployee postResponse = processAdd(conn, response, timesheetRequest, sessionData);
					super.sendResponse(conn, response, postResponse.responseCode, postResponse.data);
					break;
				case TimesheetRequest.ACTION_IS_VALIDATE:
					logger.log(Level.DEBUG, "msg from the TimesheetServlet - TimesheetRequest.ACTION_IS_VALIDATE");
					processValidate(conn, response, timesheetRequest, sessionData);
					break;
				case TimesheetRequest.ACTION_IS_UPDATE:
					try {
						TimesheetResponseEmployee updateResponse = processUpdate(conn, response, timesheetRequest, sessionData);
						super.sendResponse(conn, response, updateResponse.responseCode, updateResponse.data);
					} catch ( RecordNotFoundException e ) {
						logger.log(Level.DEBUG, "Updating non-existing record, so we add it");
						logger.log(Level.DEBUG, "Employee: " + timesheetRequest.getEmployeeCode());
						logger.log(Level.DEBUG, "Division: " + timesheetRequest.getDivisionId());
						logger.log(Level.DEBUG, "Location: " + timesheetRequest.getCity() + ", " + timesheetRequest.getState());
						logger.log(Level.DEBUG, "Date: " + timesheetRequest.getWeekEnding().getTime());
						TimesheetResponseEmployee addResponse = processAdd(conn, response, timesheetRequest, sessionData);
						super.sendResponse(conn, response, addResponse.responseCode, addResponse.data);
					}
					break;
				default:
					throw new com.ansi.scilla.web.common.exception.InvalidFormatException(TimesheetRequest.EMPLOYEE_CODE);
				}
			} catch ( RecordNotFoundException e) {
				logger.log(Level.DEBUG, "RecordNotFoundException");
				e.printStackTrace();
				super.sendNotFound(response);
			} catch (com.ansi.scilla.web.common.exception.InvalidFormatException e) {
				String fieldName = super.findBadField(e.getMessage());
				logger.log(Level.DEBUG, "Invalid Format");
				webMessages.addMessage(fieldName, "Invalid Format");
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (com.fasterxml.jackson.databind.exc.InvalidFormatException e) {
				String fieldName = super.findBadField(e.getMessage());
				logger.log(Level.DEBUG, "Invalid Format 2");
				webMessages.addMessage(fieldName, "Invalid Format");
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
				logger.log(Level.DEBUG, "Forbidden");
				super.sendForbidden(response);
			} finally {
				logger.log(Level.DEBUG, "CloseQuiet");
				AppUtils.closeQuiet(conn);
			}
		} catch ( Exception e) {
			logger.log(Level.DEBUG, "ServerException");
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

	/**
	 * We're going to validate the employee the same way that the timesheet import validates each row of the 
	 * worksheet employee list, and then return the same object that the Worksheet Import servlet returns.
	 * 
	 * @param conn
	 * @param response
	 * @param timesheetRequest
	 * @param sessionData
	 * @throws Exception
	 */
	private void processValidate(Connection conn, HttpServletResponse response, TimesheetRequest timesheetRequest, SessionData sessionData) throws Exception {
		TimesheetValidationResponse data = new TimesheetValidationResponse();
		
		PayrollWorksheetEmployee payrollWorksheetEmployee = makePayrollWorksheetEmployee(timesheetRequest);
		Division division = new Division();
		division.setDivisionId( timesheetRequest.getDivisionId() );
		division.selectOne(conn);
		
		Double maxExpenseRate = PayrollUtils.makeMaxExpenseRate(conn);
		
		ValidatedWorksheetEmployee validatedEmployee = new ValidatedWorksheetEmployee(conn, payrollWorksheetEmployee, division, maxExpenseRate);
		TimesheetEmployee timesheetEmployee = new TimesheetEmployee(validatedEmployee);
		data.setEmployee( timesheetEmployee );
		logger.log(Level.DEBUG, timesheetEmployee);
		ResponseCode responseCode = null;
		switch (timesheetEmployee.getErrorLevel()) {
		case ERROR:
			responseCode = ResponseCode.EDIT_FAILURE;
			break;
		case OK:
			responseCode = ResponseCode.SUCCESS;
			break;
		case WARNING:
			responseCode = ResponseCode.EDIT_WARNING;
			break;
		default:
			throw new Exception("Unexpected errorLevel: " + timesheetEmployee.getErrorLevel().name() );
		}
		
//		data.setWebMessages(validationResponse.getWebMessages());
		super.sendResponse(conn, response, responseCode, data);
	}
	
	
	
	protected TimesheetResponseEmployee processAdd(Connection conn, HttpServletResponse response, TimesheetRequest timesheetRequest, SessionData sessionData) throws Exception {
		TimesheetResponse data = new TimesheetResponse();
		PayrollValidation validationResponse = timesheetRequest.validateAdd(conn);
		
		// do the update for success and warning, but not for failure.
		if ( ! validationResponse.getResponseCode().equals(ResponseCode.EDIT_FAILURE) ) {
			
			Calendar today = AppUtils.getToday();
			PayrollWorksheet timesheet = new PayrollWorksheet();		
			populateTimesheetKeys(conn, timesheet, timesheetRequest);
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
		return new TimesheetResponseEmployee(validationResponse.getResponseCode(), data);

	}

	protected TimesheetResponseEmployee processUpdate(Connection conn, HttpServletResponse response, TimesheetRequest timesheetRequest, SessionData sessionData) throws RecordNotFoundException, Exception {
		TimesheetResponse data = new TimesheetResponse();
		PayrollValidation validationResponse = timesheetRequest.validateUpdate(conn);
		
		// do the update for success and warning, but not for failure.
		if ( ! validationResponse.getResponseCode().equals(ResponseCode.EDIT_FAILURE) ) {
			Calendar today = AppUtils.getToday();
			
			PayrollWorksheet timesheet = new PayrollWorksheet();
			populateTimesheetKeys(conn, timesheet, timesheetRequest);
			PayrollWorksheet key = (PayrollWorksheet)timesheet.clone();
			timesheet.selectOne(conn);
			populateTimesheetValues(timesheet, timesheetRequest);
	
			timesheet.setUpdatedBy(sessionData.getUser().getUserId());
			timesheet.setUpdatedDate(today.getTime());
			timesheet.update(conn, key);
			
			conn.commit();
		} 
		
		data.setWebMessages(validationResponse.getWebMessages());
		return new TimesheetResponseEmployee(validationResponse.getResponseCode(), data);
	}

	
	
	private void processDelete(Connection conn, TimesheetRequest timesheetRequest) throws Exception {
		PayrollWorksheet timesheet = new PayrollWorksheet();		
		populateTimesheetKeys(conn, timesheet, timesheetRequest);
		timesheet.delete(conn);		
	}



	private void populateTimesheetKeys(Connection conn, PayrollWorksheet timesheet, TimesheetRequest timesheetRequest) throws RecordNotFoundException, Exception {
		timesheet.setDivisionId(timesheetRequest.getDivisionId());
		timesheet.setWeekEnding(timesheetRequest.getWeekEnding().getTime());
//		timesheet.setState(timesheetRequest.getState());
		timesheet.setEmployeeCode(timesheetRequest.getEmployeeCode());
//		timesheet.setCity(timesheetRequest.getCity());
		timesheet.setLocaleId(LocaleUtils.makeLocale(conn, timesheetRequest.getState(), timesheetRequest.getCity()).getLocaleId());		
		
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
//		Double productivity = timesheetRequest.getProductivity();
//		timesheet.setProductivity(new BigDecimal(productivity == null ? 0.0D : productivity));
	}



	private PayrollWorksheetEmployee makePayrollWorksheetEmployee(TimesheetRequest timesheetRequest) {
		PayrollWorksheetEmployee employee = new PayrollWorksheetEmployee();
		
		Integer row = timesheetRequest.getRow();
		employee.setRow(row == null ? null : String.valueOf(row));
		employee.setEmployeeName(timesheetRequest.getEmployeeName());
		employee.setRegularHours(makeRequestValue(timesheetRequest.getRegularHours()));
		employee.setRegularPay(makeRequestValue(timesheetRequest.getRegularPay()));
		employee.setExpenses(makeRequestValue(timesheetRequest.getExpenses()));
		employee.setOtHours(makeRequestValue(timesheetRequest.getOtHours()));
		employee.setOtPay(makeRequestValue(timesheetRequest.getOtPay()));
		employee.setVacationHours(makeRequestValue(timesheetRequest.getVacationHours()));
		employee.setVacationPay(makeRequestValue(timesheetRequest.getVacationPay()));
		employee.setHolidayHours(makeRequestValue(timesheetRequest.getHolidayHours()));
		employee.setHolidayPay(makeRequestValue(timesheetRequest.getHolidayPay()));
		employee.setGrossPay(makeRequestValue(timesheetRequest.getGrossPay()));
		employee.setExpensesSubmitted(makeRequestValue(timesheetRequest.getExpensesSubmitted()));
		employee.setExpensesAllowed(makeRequestValue(timesheetRequest.getExpensesAllowed()));
		employee.setVolume(makeRequestValue(timesheetRequest.getVolume()));
		employee.setDirectLabor(makeRequestValue(timesheetRequest.getDirectLabor()));
//		employee.setProductivity(makeRequestValue(timesheetRequest.getProductivity()));
		
		
		return employee;
	}



	private String makeRequestValue(Double value) {
		String requestValue = null;
		if ( value != null ) {
			requestValue = String.valueOf(value);
		}
		return requestValue;
	}
	

	
}
