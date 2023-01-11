package com.ansi.scilla.web.test.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.payroll.request.EmployeeRequest;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TestEmployeeUpdate {

	private final Logger logger = LogManager.getLogger(TestEmployeeUpdate.class);
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			Integer employeeCode = 123;
			Integer userId = 5;
			Calendar today = Calendar.getInstance(new AnsiTime());
			
			EmployeeRequest er = new EmployeeRequest();
			er.setEmployeeCode(null);
			er.setCompanyCode("CHX");
			er.setDivisionId(101);
			er.setFirstName("D");
			er.setLastName("Lewis");
			er.setMiddleInitial("");
			er.setDepartmentDescription("Department stuff");
			er.setStatus("ACTIVE");
			er.setTerminationDate(null);
			er.setNotes("Notes");
			
			doUpdate(conn, employeeCode, er, userId, today);
			conn.commit();
			System.out.println("********  SUCCESS **********");
		} catch ( Exception e) {
			System.out.println("********  FAIL **********");
			conn.rollback();
			throw e;
		} finally {
			AppUtils.closeQuiet(conn);
		}
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
		
		String sql = "UPDATE payroll_employee \n"
				+ "SET employee_last_name=?,\n"
				+ "		employee_mi=?,\n"
				+ "		dept_description=?,\n"
				+ "		employee_status=?,\n"
				+ "		employee_termination_date=?,\n"
				+ "		employee_code=?,\n"
				+ "		employee_first_name=?,\n"
				+ "		company_code=?,\n"
				+ "		division_id=?,\n"
				+ "		division=?,\n"
				+ "		notes=?,\n"
				+ "		updated_by=?,\n"
				+ "		updated_date=? \n"
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
		ps.setInt(n, employeeCode);
		n++;
		ps.setString(n, StringUtils.trimToNull(employeeRequest.getFirstName()));
		n++;
		ps.setString(n, StringUtils.trimToNull(employeeRequest.getCompanyCode()));
		n++;
		ps.setInt(n, employeeRequest.getDivisionId());
		n++;
		ps.setString(n, String.valueOf(division.getDivisionNbr()));
		n++;
		ps.setString(n, StringUtils.trimToNull(employeeRequest.getNotes()));
		n++;
		ps.setInt(n, userId);
		n++;
		ps.setDate(n, updateTime);
		n++;
		ps.setInt(n, employeeCode);

		ps.executeUpdate();
	}
	
	
	public static void main(String[] args) {
		try {
			new TestEmployeeUpdate().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
