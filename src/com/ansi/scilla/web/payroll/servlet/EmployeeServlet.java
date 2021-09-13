package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.PayrollEmployee;
import com.ansi.scilla.common.payroll.EmployeeStatus;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payroll.request.EmployeeRequest;
import com.ansi.scilla.web.payroll.response.EmployeeResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class EmployeeServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "payroll/employee";
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doDelete(request, response);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Employee get");
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
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
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.log(Level.DEBUG, "Employee post");
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		ResponseCode responseCode = null;
		EmployeeResponse data = new EmployeeResponse();
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			try {
				String uri = request.getRequestURI();
				String[] uriPath = uri.split("/");
				Integer employeeCode = Integer.valueOf(uriPath[uriPath.length - 1]);
				Calendar today = Calendar.getInstance(new AnsiTime());
				
				String jsonString = super.makeJsonString(request);
				logger.log(Level.DEBUG, jsonString);
				SessionData sessionData = AppUtils.validateSession(request, Permission.PAYROLL_WRITE);
				EmployeeRequest employeeRequest = new EmployeeRequest();
				AppUtils.json2object(jsonString, employeeRequest);
				webMessages = employeeRequest.validate(conn);
				if ( webMessages.isEmpty() ) {
					doUpdate(conn, employeeCode, employeeRequest, sessionData.getUser(), today);
					conn.commit();
					responseCode = ResponseCode.SUCCESS;
					data = new EmployeeResponse(conn, employeeCode);
				} else {
					responseCode = ResponseCode.EDIT_FAILURE;
				}
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, responseCode, data);
			} catch ( RecordNotFoundException e) {
				super.sendNotFound(response);
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

	private void doUpdate(Connection conn, Integer employeeCode, EmployeeRequest employeeRequest, SessionUser sessionUser, Calendar today) throws RecordNotFoundException, Exception {
		PayrollEmployee employee = new PayrollEmployee();
		employee.setEmployeeCode(employeeCode);
		employee.selectOne(conn);
		
		employee.setCompanyCode(employeeRequest.getCompanyCode());
		employee.setDeptDescription(employeeRequest.getDepartmentDescription());
		employee.setDivision(null);
		employee.setDivisionId(employeeRequest.getDivisionId());
		employee.setEmployeeFirstName(employeeRequest.getFirstName());
		employee.setEmployeeLastName(employeeRequest.getLastName());
		employee.setEmployeeMi(employeeRequest.getMiddleInitial());
		employee.setEmployeeStatus(employeeRequest.getStatus());
		EmployeeStatus employeeStatus = EmployeeStatus.valueOf(employeeRequest.getStatus());
		if ( employeeStatus.equals(EmployeeStatus.ACTIVE)) {
			employee.setEmployeeTerminationDate(null);
		} else {
			employee.setEmployeeTerminationDate(employeeRequest.getTerminationDate().getTime());
		}
		
		employee.setNotes(employeeRequest.getNotes());
		employee.setUpdatedBy(sessionUser.getUserId());
		employee.setUpdatedDate(today.getTime());
		
		PayrollEmployee key = new PayrollEmployee();
		key.setEmployeeCode(employeeCode);
		employee.update(conn, key);
	}

	

}
