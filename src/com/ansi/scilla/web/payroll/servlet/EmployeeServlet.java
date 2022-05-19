package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.EmployeeAlias;
import com.ansi.scilla.common.db.PayrollEmployee;
import com.ansi.scilla.common.payroll.common.EmployeeStatus;
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
//				Calendar today = Calendar.getInstance(new AnsiTime());
				
//				String jsonString = super.makeJsonString(request);
//				logger.log(Level.DEBUG, jsonString);
				AppUtils.validateSession(request, Permission.PAYROLL_WRITE);
//				EmployeeRequest employeeRequest = new EmployeeRequest();
//				AppUtils.json2object(jsonString, employeeRequest);
				
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
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		}
	}

	
	
	
	private void processAddRequest(Connection conn, HttpServletResponse response, EmployeeRequest employeeRequest, SessionData sessionData, Calendar today) throws Exception {
		WebMessages webMessages = employeeRequest.validateAdd(conn);
		ResponseCode responseCode = webMessages.isEmpty() ? ResponseCode.SUCCESS : ResponseCode.EDIT_FAILURE;

		if ( employeeRequest.getValidateOnly() != null && employeeRequest.getValidateOnly() ) {
			EmployeeValidateResponse data = new EmployeeValidateResponse(conn, employeeRequest.getEmployeeCode(), employeeRequest);
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, responseCode, data);
		} else {
			EmployeeResponse data = new EmployeeResponse();
			if ( responseCode.equals(ResponseCode.SUCCESS)) {
				doAdd(conn, employeeRequest, sessionData.getUser(), today);
				conn.commit();
				data = new EmployeeResponse(conn, employeeRequest.getEmployeeCode());
			} 
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, responseCode, data);
		}	
	}

	
	private void processUpdateRequest(Connection conn, HttpServletResponse response, Integer employeeCode, EmployeeRequest employeeRequest, Integer userId, Calendar today) throws RecordNotFoundException, Exception {
		WebMessages webMessages = employeeRequest.validateUpdate(conn);
		ResponseCode responseCode = webMessages.isEmpty() ? ResponseCode.SUCCESS : ResponseCode.EDIT_FAILURE;

		if ( employeeRequest.getValidateOnly() != null && employeeRequest.getValidateOnly() ) {
			EmployeeValidateResponse data = new EmployeeValidateResponse(conn, employeeRequest.getEmployeeCode(), employeeRequest);
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, responseCode, data);
		} else {
			EmployeeResponse data = new EmployeeResponse();
			if ( responseCode.equals(ResponseCode.SUCCESS)) {
				doUpdate(conn, employeeCode, employeeRequest, userId, today);
				conn.commit();
				data = new EmployeeResponse(conn, employeeRequest.getEmployeeCode());
			} 
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, responseCode, data);
		}		
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


	private void doUpdate(Connection conn, Integer employeeCode, EmployeeRequest employeeRequest, Integer userId, Calendar today) throws RecordNotFoundException, Exception {
		Division division = new Division();
		division.setDivisionId(employeeRequest.getDivisionId());
		division.selectOne(conn);
		
		java.sql.Date updateTime = new java.sql.Date(today.getTime().getTime());
		java.sql.Date terminationDate = employeeRequest.getTerminationDate() == null ? null : new java.sql.Date(employeeRequest.getTerminationDate().getTime().getTime());
		
		if ( employeeRequest.getSelectedEmployeeCode() != null && employeeRequest.getSelectedEmployeeCode().equals(employeeRequest.getEmployeeCode())) {
			processStandardUpdate(conn, employeeCode, employeeRequest, userId, division, today, updateTime, terminationDate);
		} else {
			processChangedEmployeeCode(conn, employeeCode, employeeRequest, userId, division, today, updateTime, terminationDate);
		}
	
		
	}

	/**
	 * We need to handle this special case because employee code is a foreign key from alias to employee. If the employee changes
	 * code, then the alias also needs to change employee code. So, here's the process:
	 * 
	 * Insert a employee record with the new employee code
	 * Update alias records to point to the new employee record
	 * Delete the old employee record
	 * 
	 * @param conn
	 * @param employeeCode
	 * @param employeeRequest
	 * @param userId
	 * @param today
	 * @param updateTime
	 * @param terminationDate
	 * @throws SQLException 
	 */
	private void processChangedEmployeeCode(Connection conn, Integer employeeCode, EmployeeRequest employeeRequest,
			Integer userId, Division division, Calendar today, Date updateTime, Date terminationDate) throws SQLException {
		// Insert employee record with new employee code:
		String insertSql = "insert into payroll_employee(\n" + 
				"	employee_code,\n" + 
				"	company_code,\n" + 
				"	division,\n" + 
				"	division_id,\n" + 
				"	employee_first_name,\n" + 
				"	employee_last_name,\n" + 
				"	employee_mi,\n" + 
				"	dept_description,\n" + 
				"	employee_status,\n" + 
				"	employee_termination_date,\n" + 
				"	notes,\n" + 
				"	union_member,\n" + 
				"	union_code,\n" + 
				"	union_rate,\n" + 
				"	process_date,\n" + 
				"	added_by,\n" + 
				"	added_date,\n" + 
				"	updated_by,\n" + 
				"	updated_date)\n" + 
				"select ? as employee_code,\n" + 
				"	company_code,\n" + 
				"	division,\n" + 
				"	division_id,\n" + 
				"	employee_first_name,\n" + 
				"	employee_last_name,\n" + 
				"	employee_mi,\n" + 
				"	dept_description,\n" + 
				"	employee_status,\n" + 
				"	employee_termination_date,\n" + 
				"	notes,\n" + 
				"	union_member,\n" + 
				"	union_code,\n" + 
				"	union_rate,\n" + 
				"	process_date,\n" + 
				"	added_by,\n" + 
				"	added_date,\n" + 
				"	updated_by,\n" + 
				"	updated_date from payroll_employee where payroll_employee.employee_code=?";
		PreparedStatement insertPs = conn.prepareStatement(insertSql);
		insertPs.setInt(1, employeeRequest.getEmployeeCode());
		insertPs.setInt(2, employeeRequest.getSelectedEmployeeCode());
		insertPs.executeUpdate();
		
		// update alias records
		PreparedStatement updatePs = conn.prepareStatement("update employee_alias set employee_code=? where employee_code=?");
		updatePs.setInt(1, employeeRequest.getEmployeeCode());
		updatePs.setInt(2, employeeRequest.getSelectedEmployeeCode());
		updatePs.executeUpdate();
		
		// delete the obsolete payroll employee
		PreparedStatement deletePs = conn.prepareStatement("delete payroll_employee where employee_code=?");
		deletePs.setInt(1, employeeRequest.getSelectedEmployeeCode());
		deletePs.executeUpdate();
		
		// update the rest of the fields
		processStandardUpdate(conn, employeeRequest.getEmployeeCode(), employeeRequest, userId, division, today, updateTime, terminationDate);
		
		
	}

	private void processStandardUpdate(Connection conn, Integer employeeCode, EmployeeRequest employeeRequest,
			Integer userId, Division division, Calendar today, Date updateTime, Date terminationDate) throws SQLException {
		String sql =
				  "UPDATE payroll_employee "
				+ "SET employee_code=?, \n"
				+ "    company_code=?, \n"
				+ "    division=?, \n"
				+ "    division_id=?, \n"
				+ "    employee_first_name=?, \n"
				+ "    employee_last_name=?, \n "
				+ "    employee_mi=?, \n"
				+ "    dept_description=?, \n"
				+ "    employee_status=?, \n"
				+ "    employee_termination_date=?, \n"
				+ "    notes=?, \n"
				+ "    updated_by=?, \n"
				+ "    updated_date=? \n"
				+ "WHERE employee_code=?";
		
		

		PreparedStatement ps = conn.prepareStatement(sql);
		int n = 1;
		ps.setInt(n, employeeRequest.getEmployeeCode());
		n++;
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
		
		boolean isUnionMember =  employeeRequest.getUnionMember() != null && employeeRequest.getUnionMember().intValue() == 1;
		
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
		employee.setUnionMember( isUnionMember ? 1 : 0 );
		if ( isUnionMember ) {
			employee.setUnionCode(employeeRequest.getUnionCode());
			employee.setUnionRate(new BigDecimal(employeeRequest.getUnionRate()).round(MathContext.DECIMAL32));
		}
		employee.setProcessDate(employeeRequest.getProcessDate().getTime());
		employee.setNotes(StringUtils.trimToNull(employeeRequest.getNotes()));
		employee.setUpdatedBy(sessionUser.getUserId());
		employee.setUpdatedDate(today.getTime());
		
	}

	

}
