package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Connection;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.EmployeeAlias;
import com.ansi.scilla.common.db.PayrollEmployee;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payroll.request.EmployeeRequest;
import com.ansi.scilla.web.payroll.response.EmployeeResponse;
import com.ansi.scilla.web.payroll.response.EmployeeValidateResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class EmployeeServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "payroll/employee";
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;

		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			try {
				String uri = request.getRequestURI();
				String[] uriPath = uri.split("/");
				Integer employeeCode = StringUtils.isNumeric(uriPath[uriPath.length - 1]) ? Integer.valueOf(uriPath[uriPath.length - 1]) : null;
				AppUtils.validateSession(request, Permission.PAYROLL_WRITE);
				
				processDeleteRequest(conn, response, employeeCode);
			} finally {
				conn.close();
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		}		
	}
	
	
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			AppUtils.validateSession(request, Permission.PAYROLL_READ);
			try {				
				String uri = request.getRequestURI();
				String[] uriPath = uri.split("/");
				Integer employeeCode = Integer.valueOf(uriPath[uriPath.length - 1]);
				EmployeeResponse data = new EmployeeResponse(conn, employeeCode);
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
			} catch ( RecordNotFoundException e) {
				super.sendNotFound(response);
			} finally {
				conn.close();
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);	
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;

		try {
			// this servlet will get called multiple times at (very) short intervals from the employee import
			// so we're going to to handle the connection pool running dry:
			int connCount = 0;
			while ( conn == null && connCount < 3 ) {
				conn = AppUtils.getDBCPConn();
				if ( conn == null ) {
					TimeUnit.SECONDS.sleep(7);
				}
				connCount++;
			}
			if ( conn == null ) {
				throw new ServletException("Connection pool ran dry; we DDoS'd ourselves");
			}
			conn.setAutoCommit(false);
			try {
				String uri = request.getRequestURI();
				String[] uriPath = uri.split("/");
				Integer employeeCode = StringUtils.isNumeric(uriPath[uriPath.length - 1]) ? Integer.valueOf(uriPath[uriPath.length - 1]) : null;
				Calendar today = Calendar.getInstance(new AnsiTime());
				
				String jsonString = super.makeJsonString(request);
				logger.log(Level.DEBUG, jsonString);
				SessionData sessionData = AppUtils.validateSession(request, Permission.PAYROLL_WRITE);
				EmployeeRequest employeeRequest = new EmployeeRequest();
				AppUtils.json2object(jsonString, employeeRequest);
				
				if ( employeeCode == null ) {
					processAddRequest(conn, response, employeeRequest, sessionData, today);
				} else {
					if ( employeeRequest.getValidateOnly() != null && employeeRequest.getValidateOnly() ) {
						processValidateRequest(conn, response, employeeCode, employeeRequest);
					} else {
						processUpdateRequest(conn, response, employeeCode, employeeRequest, sessionData.getUser(), today);
					}
					
				}		
			} catch ( InvalidFormatException e) {
				String badField = super.findBadField(e.toString());
				EmployeeResponse data = new EmployeeResponse();
				WebMessages webMessages = new WebMessages();
				webMessages.addMessage(badField, "Invalid Format");
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch ( RecordNotFoundException e) {
				super.sendNotFound(response);
			} finally {
				conn.close();
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( IOException | ServletException e ) {
			AppUtils.logException(e);
			throw e;
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			if ( conn != null ) {
				AppUtils.rollbackQuiet(conn);
				AppUtils.closeQuiet(conn);
			}
		}
		
	}

	
	
	
	private void processValidateRequest(Connection conn, HttpServletResponse response, Integer employeeCode, EmployeeRequest employeeRequest) throws Exception {
		WebMessages webMessages = employeeRequest.validateUpdate(conn);
		EmployeeValidateResponse data = new EmployeeValidateResponse(conn, employeeCode, employeeRequest, webMessages);		
		ResponseCode responseCode = webMessages.isEmpty() ? ResponseCode.SUCCESS : ResponseCode.EDIT_FAILURE;
		super.sendResponse(conn, response, responseCode, data);		
	}




	private void processAddRequest(Connection conn, HttpServletResponse response, EmployeeRequest employeeRequest, SessionData sessionData, Calendar today) throws Exception {
		WebMessages webMessages = employeeRequest.validateAdd(conn);
		ResponseCode responseCode = webMessages.isEmpty() ? ResponseCode.SUCCESS : ResponseCode.EDIT_FAILURE;

		EmployeeResponse data = new EmployeeResponse();
		if ( responseCode.equals(ResponseCode.SUCCESS)) {
			doAdd(conn, employeeRequest, sessionData.getUser(), today);
			conn.commit();
			data = new EmployeeResponse(conn, employeeRequest.getEmployeeCode());
		} 
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, data);
	}

	
	private void processUpdateRequest(Connection conn, HttpServletResponse response, Integer employeeCode, EmployeeRequest employeeRequest, SessionUser sessionUser, Calendar today) throws RecordNotFoundException, Exception {
		WebMessages webMessages = employeeRequest.validateUpdate(conn);
		EmployeeResponse data = new EmployeeResponse();
		ResponseCode responseCode = webMessages.isEmpty() ? ResponseCode.SUCCESS : ResponseCode.EDIT_FAILURE;

		if ( responseCode.equals(ResponseCode.SUCCESS)) {
			doUpdate(conn, employeeCode, employeeRequest, sessionUser, today);
			conn.commit();
			data = new EmployeeResponse(conn, employeeRequest.getEmployeeCode());
		} 
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, data);
	}

	
	
	private void processDeleteRequest(Connection conn, HttpServletResponse response, Integer employeeCode) throws Exception {
		try {
			EmployeeAlias alias = new EmployeeAlias();
			alias.setEmployeeCode(employeeCode);
			alias.delete(conn);
		} catch ( RecordNotFoundException e ) {
			// we don't care. It's a delete of something that doesn't exist
		}
		
		try {
			PayrollEmployee employee = new PayrollEmployee();
			employee.setEmployeeCode(employeeCode);
			employee.delete(conn);
		} catch ( RecordNotFoundException e ) {
			// we don't care. It's a delete of something that doesn't exist
		}
		
		conn.commit();
		
		EmployeeResponse data = new EmployeeResponse();
		data.setWebMessages(new WebMessages());
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);	
	}




	private void doAdd(Connection conn, EmployeeRequest employeeRequest, SessionUser sessionUser, Calendar today) throws Exception {
		PayrollEmployee employee = new PayrollEmployee();
		
		populateEmployee(conn, employee, employeeRequest, sessionUser, today);
		
		employee.setEmployeeCode(employeeRequest.getEmployeeCode());
		employee.setAddedBy(sessionUser.getUserId());
		employee.setAddedDate(today.getTime());
		
		employee.insertWithNoKey(conn);		
	}


	private void doUpdate(Connection conn, Integer employeeCode, EmployeeRequest employeeRequest, SessionUser sessionUser, Calendar today) throws RecordNotFoundException, Exception {
		Division division = new Division();
		division.setDivisionId(employeeRequest.getDivisionId());
		division.selectOne(conn);
		
		PayrollEmployee employee = new PayrollEmployee();
		employee.setEmployeeCode(employeeCode);
		employee.selectOne(conn);
		populateEmployee(conn, employee, employeeRequest, sessionUser, today);
		
		PayrollEmployee key = new PayrollEmployee();
		key.setEmployeeCode(employeeCode);
		
		employee.update(conn, key);
	}

	

	

	private void populateEmployee(Connection conn, PayrollEmployee employee, EmployeeRequest employeeRequest, SessionUser sessionUser, Calendar today) throws Exception {
		Division division = new Division();
		division.setDivisionId(employeeRequest.getDivisionId());
		division.selectOne(conn);
		
		boolean isUnionMember =  employeeRequest.getUnionMember() != null && employeeRequest.getUnionMember().intValue() == 1;
		
		employee.setCompanyCode(StringUtils.trimToNull(employeeRequest.getCompanyCode()));
		employee.setDeptDescription(StringUtils.trimToNull(employeeRequest.getDepartmentDescription()));
		employee.setDivision(String.valueOf(division.getDivisionNbr()));
		employee.setDivisionId(employeeRequest.getDivisionId());
		employee.setEmployeeFirstName(StringUtils.trimToNull(employeeRequest.getFirstName()));
		employee.setEmployeeLastName(StringUtils.trimToNull(employeeRequest.getLastName()));
		employee.setEmployeeMi(StringUtils.trimToNull(employeeRequest.getMiddleInitial()));
		employee.setEmployeeStatus(StringUtils.trimToNull(employeeRequest.getStatus()));
//		EmployeeStatus employeeStatus = EmployeeStatus.valueOf(employeeRequest.getStatus());
		if ( employeeRequest.getTerminationDate() == null ) {
			employee.setEmployeeTerminationDate(null);
		} else {
			employee.setEmployeeTerminationDate(employeeRequest.getTerminationDate().getTime());
		}
		employee.setUnionMember( isUnionMember ? 1 : 0 );
		if ( isUnionMember ) {
			employee.setUnionCode(employeeRequest.getUnionCode());
			employee.setUnionRate(new BigDecimal(employeeRequest.getUnionRate()).round(MathContext.DECIMAL32));
		} else {
			employee.setUnionCode(null);
			employee.setUnionRate(null);
		}
//		employee.setProcessDate(employeeRequest.getProcessDate().getTime());
		employee.setNotes(StringUtils.trimToNull(employeeRequest.getNotes()));
		employee.setUpdatedBy(sessionUser.getUserId());
		employee.setUpdatedDate(today.getTime());
		
	}

	

}
