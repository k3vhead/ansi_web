package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.Division;
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

		try {
			conn = AppUtils.getDBCPConn();
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
					processUpdateRequest(conn, response, employeeCode, employeeRequest, sessionData.getUser().getUserId(), today);
				}					
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

	private void processAddRequest(Connection conn, HttpServletResponse response, EmployeeRequest employeeRequest, SessionData sessionData, Calendar today) throws Exception {
		ResponseCode responseCode = null;
		EmployeeResponse data = new EmployeeResponse();
		WebMessages webMessages = employeeRequest.validateAdd(conn);
		if ( webMessages.isEmpty() ) {
			doAdd(conn, employeeRequest, sessionData.getUser(), today);
			conn.commit();
			responseCode = ResponseCode.SUCCESS;
			data = new EmployeeResponse(conn, employeeRequest.getEmployeeCode());
		} else {
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, data);		
	}

	
	private void processUpdateRequest(Connection conn, HttpServletResponse response, Integer employeeCode, EmployeeRequest employeeRequest, Integer userId, Calendar today) throws RecordNotFoundException, Exception {
		ResponseCode responseCode = null;
		EmployeeResponse data = new EmployeeResponse();
		WebMessages webMessages = employeeRequest.validateUpdate(conn);

		if ( webMessages.isEmpty() ) {
			doUpdate(conn, employeeCode, employeeRequest, userId, today);
			conn.rollback();
			responseCode = ResponseCode.SUCCESS;
			data = new EmployeeResponse(conn, employeeRequest.getEmployeeCode());
		} else {
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, data);	
	}

	
	private void doAdd(Connection conn, EmployeeRequest employeeRequest, SessionUser sessionUser, Calendar today) throws Exception {
		PayrollEmployee employee = new PayrollEmployee();
		
		populateEmployee(conn, employee, employeeRequest, sessionUser, today);
		
		employee.setEmployeeCode(employeeRequest.getEmployeeCode());
		employee.setAddedBy(sessionUser.getUserId());
		employee.setAddedDate(today.getTime());
		
		employee.insertWithNoKey(conn);		
	}


	private void doUpdate(Connection conn, Integer employeeCode, EmployeeRequest employeeRequest, Integer userId, Calendar today) throws RecordNotFoundException, Exception {
		logger.log(Level.DEBUG, "Employee Servlet doUpdate");
		logger.log(Level.DEBUG, "employeeCode: " + employeeCode);
		logger.log(Level.DEBUG, employeeRequest);
		Division division = new Division();
		division.setDivisionId(employeeRequest.getDivisionId());
		division.selectOne(conn);
		
		java.sql.Date updateTime = new java.sql.Date(today.getTime().getTime());
		java.sql.Date terminationDate = employeeRequest.getTerminationDate() == null ? null : new java.sql.Date(employeeRequest.getTerminationDate().getTime().getTime());
		
		String sql =
				  "UPDATE payroll_employee "
				+ "SET company_code=?, "
				+ "    division=?, "
				+ "    division_id=?, "
				+ "    employee_first_name=?, "
				+ "    employee_last_name=?, "
				+ "    employee_mi=?, "
				+ "    dept_description=?, "
				+ "    employee_status=?, "
				+ "    employee_termination_date=?, "
				+ "    notes=?, "
				+ "    updated_by=?, "
				+ "    updated_date=? "
				+ "WHERE employee_code=?";
		
		logger.log(Level.DEBUG, sql);
		logger.log(Level.DEBUG, StringUtils.trimToNull(employeeRequest.getLastName()));
		logger.log(Level.DEBUG, StringUtils.trimToNull(employeeRequest.getMiddleInitial()));
		logger.log(Level.DEBUG, StringUtils.trimToNull(employeeRequest.getDepartmentDescription()));
		logger.log(Level.DEBUG, StringUtils.trimToNull(employeeRequest.getStatus()));
		logger.log(Level.DEBUG, terminationDate);
		logger.log(Level.DEBUG, employeeCode);
		logger.log(Level.DEBUG, StringUtils.trimToNull(employeeRequest.getFirstName()));
		logger.log(Level.DEBUG, StringUtils.trimToNull(employeeRequest.getCompanyCode()));
		logger.log(Level.DEBUG, employeeRequest.getDivisionId());
		logger.log(Level.DEBUG, String.valueOf(division.getDivisionNbr()));
		logger.log(Level.DEBUG, StringUtils.trimToNull(employeeRequest.getNotes()));
		logger.log(Level.DEBUG, userId);
		logger.log(Level.DEBUG, updateTime);
		logger.log(Level.DEBUG, employeeCode);

		PreparedStatement ps = conn.prepareStatement(sql);
		int n = 1;
		ps.setString(n, StringUtils.trimToNull(employeeRequest.getCompanyCode()));
		n++;
		ps.setString(n, String.valueOf(division.getDivisionNbr()));
		n++;
		ps.setInt(n, employeeRequest.getDivisionId());
		n++;
		ps.setString(n, StringUtils.trimToNull(employeeRequest.getFirstName()));
		n++;
		ps.setString(n, StringUtils.trimToNull(employeeRequest.getLastName()));
		n++;
		ps.setString(n, StringUtils.trimToNull(employeeRequest.getMiddleInitial()));
		n++;
		ps.setString(n, StringUtils.trimToNull(employeeRequest.getDepartmentDescription()));
		n++;
		ps.setString(n, StringUtils.trimToNull(employeeRequest.getStatus()));
		n++;
		ps.setDate(n, terminationDate);
		n++;
		ps.setString(n, StringUtils.trimToNull(employeeRequest.getNotes()));
		n++;
		ps.setInt(n, userId);
		n++;
		ps.setDate(n, updateTime);
		n++;
		ps.setInt(n, employeeCode);
		
		
//		n++;
//		ps.setInt(n, employeeCode);

		ps.executeUpdate();
	}

	private void populateEmployee(Connection conn, PayrollEmployee employee, EmployeeRequest employeeRequest, SessionUser sessionUser, Calendar today) throws Exception {
		Division division = new Division();
		division.setDivisionId(employeeRequest.getDivisionId());
		division.selectOne(conn);
		
		employee.setCompanyCode(StringUtils.trimToNull(employeeRequest.getCompanyCode()));
		employee.setDeptDescription(StringUtils.trimToNull(employeeRequest.getDepartmentDescription()));
		employee.setDivision(String.valueOf(division.getDivisionNbr()));
		employee.setDivisionId(employeeRequest.getDivisionId());
		employee.setEmployeeFirstName(StringUtils.trimToNull(employeeRequest.getFirstName()));
		employee.setEmployeeLastName(StringUtils.trimToNull(employeeRequest.getLastName()));
		employee.setEmployeeMi(StringUtils.trimToNull(employeeRequest.getMiddleInitial()));
		employee.setEmployeeStatus(StringUtils.trimToNull(employeeRequest.getStatus()));
		EmployeeStatus employeeStatus = EmployeeStatus.valueOf(employeeRequest.getStatus());
		if ( employeeStatus.equals(EmployeeStatus.ACTIVE)) {
			employee.setEmployeeTerminationDate(null);
		} else {
			employee.setEmployeeTerminationDate(employeeRequest.getTerminationDate().getTime());
		}
		
		employee.setNotes(StringUtils.trimToNull(employeeRequest.getNotes()));
		employee.setUpdatedBy(sessionUser.getUserId());
		employee.setUpdatedDate(today.getTime());
		
	}

	

}
