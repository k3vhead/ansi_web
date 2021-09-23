package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.EmployeeAlias;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.common.request.RequestValidator;
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
import com.ansi.scilla.web.payroll.response.AliasResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class AliasServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "payroll/alias";
	
	public static final String EMPLOYEE_NAME = "employeeName";
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Alias get");
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
			try {
				String uri = request.getRequestURI();
				String[] uriPath = uri.split("/");
				Integer employeeCode = Integer.valueOf(uriPath[uriPath.length - 2]);
				String aliasName = URLDecoder.decode(uriPath[uriPath.length - 1],"UTF-8");
				logger.log(Level.DEBUG, employeeCode + "\t" + aliasName);
				EmployeeAlias alias = new EmployeeAlias();
				alias.setEmployeeCode(employeeCode);
				alias.setEmployeeName(aliasName);
				alias.delete(conn);
				conn.commit();
				AliasResponse data = new AliasResponse();
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
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Alias get");
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			AppUtils.validateSession(request, Permission.CLAIMS_READ);
			try {
				String uri = request.getRequestURI();
				String[] uriPath = uri.split("/");
				Integer employeeCode = Integer.valueOf(uriPath[uriPath.length - 2]);
				String aliasName = URLDecoder.decode(uriPath[uriPath.length - 1],"UTF-8");
				logger.log(Level.DEBUG, employeeCode + "\t" + aliasName);
				AliasResponse data = new AliasResponse(conn, employeeCode, aliasName);
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
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		}
	}
	

	/*
	 * Add a new Alias -- no updates to existing aliases
	 * 
	 * @see com.ansi.scilla.web.common.servlet.AbstractServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.log(Level.DEBUG, "Alias post");
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		ResponseCode responseCode = null;
		AliasResponse data = new AliasResponse();
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			try {
				String uri = request.getRequestURI();
				String[] uriPath = uri.split("/");
				Integer employeeCode = Integer.valueOf(uriPath[uriPath.length - 1]);
				Calendar today = Calendar.getInstance(new AnsiTime());
				
//				String jsonString = super.makeJsonString(request);
//				logger.log(Level.DEBUG, jsonString);
				SessionData sessionData = AppUtils.validateSession(request, Permission.PAYROLL_WRITE);
				String employeeName = request.getParameter(EMPLOYEE_NAME);
				RequestValidator.validateString(webMessages, EMPLOYEE_NAME, employeeName, true);
//				EmployeeRequest employeeRequest = new EmployeeRequest();
//				AppUtils.json2object(jsonString, employeeRequest);
//				webMessages = employeeRequest.validate(conn);
				if ( webMessages.isEmpty() ) {
					try {
						doUpdate(conn, employeeCode, employeeName, sessionData.getUser(), today);
						conn.commit();
						responseCode = ResponseCode.SUCCESS;
						data = new AliasResponse(conn, employeeCode, employeeName);
					} catch ( DuplicateEntryException e ) {
						webMessages.addMessage(EMPLOYEE_NAME, "Duplicate Alias");
						responseCode = ResponseCode.EDIT_FAILURE;
					}
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

	private void doUpdate(Connection conn, Integer employeeCode, String employeeName, SessionUser sessionUser, Calendar today) throws DuplicateEntryException, RecordNotFoundException, Exception {
		EmployeeAlias employee = new EmployeeAlias();
		employee.setEmployeeCode(employeeCode);
		employee.setEmployeeName(employeeName);
		
		try {
			employee.selectOne(conn);
			throw new DuplicateEntryException();
		} catch ( RecordNotFoundException e) {
			employee.setAddedBy(sessionUser.getUserId());
			employee.setAddedDate(today.getTime());
			employee.setUpdatedBy(sessionUser.getUserId());
			employee.setUpdatedDate(today.getTime());
			employee.insertWithNoKey(conn);
		}
		
	}

	

}
