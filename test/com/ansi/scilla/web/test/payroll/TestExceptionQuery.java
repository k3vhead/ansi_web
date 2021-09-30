package com.ansi.scilla.web.test.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.payroll.common.ExceptionReportRecord;

public class TestExceptionQuery {
	private final String sql = "SELECT division_group.group_id, \n"
			+ "	division_group.name as group_name, \n"
			+ "	division_group.company_code,\n"
			+ "	division.division_id, \n"
			+ "	division.description, \n"
			+ "	CONCAT(division.division_nbr, '-', division.division_code) as div,\n"
			+ "	payroll_employee.employee_code, \n"
			+ "	payroll_employee.employee_first_name, \n"
			+ "	payroll_employee.employee_last_name, \n"
			+ "	payroll_employee.employee_mi, \n"
			+ "	payroll_employee.employee_status, \n"
			+ "	payroll_employee.employee_termination_date,\n"
			+ "	payroll_worksheet.week_ending, \n"
			+ "	payroll_worksheet.state, \n"
			+ "	payroll_worksheet.city, \n"
			+ "	payroll_worksheet.employee_name, \n"
			+ "	payroll_worksheet.regular_hours, \n"
			+ "	payroll_worksheet.regular_pay,	\n"
			+ "	payroll_worksheet.expenses, \n"
			+ "	payroll_worksheet.ot_hours,	\n"
			+ "	payroll_worksheet.ot_pay,	\n"
			+ "	payroll_worksheet.vacation_hours,	\n"
			+ "	payroll_worksheet.vacation_pay,\n"
			+ "	payroll_worksheet.holiday_hours,	\n"
			+ "	payroll_worksheet.holiday_pay, \n"
			+ "	payroll_worksheet.gross_pay,	\n"
			+ "	payroll_worksheet.expenses_submitted,	\n"
			+ "	payroll_worksheet.expenses_allowed, \n"
			+ "	payroll_worksheet.volume, \n"
			+ "	payroll_worksheet.direct_labor,	\n"
			+ "	payroll_worksheet.productivity \n"
			+ "FROM division_group\n"
			+ "INNER JOIN division on division.group_id = division_group.group_id\n"
			+ "LEFT OUTER JOIN payroll_employee on payroll_employee.division_id = division.division_id\n"
			+ "LEFT OUTER JOIN payroll_worksheet on payroll_worksheet.division_id = division.division_id \n"
			+ "	and payroll_worksheet.employee_code = payroll_employee.employee_code\n"
			+ "	-- and week_ending \n"
			+ "WHERE group_type = 'COMPANY' and division_group.company_code is not NULL \n"
			+ "ORDER BY company_code, group_name\n";
			
	public static void main(String[] args) {
		try {
			TestExceptionQuery te = new TestExceptionQuery();
			te.go();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void go() throws Exception {
		
		Connection con = null;
		try {
			con = AppUtils.getDevConn();
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String companyCode = rs.getString("company_code");
				System.out.println(companyCode);
				ExceptionReportRecord rec = new ExceptionReportRecord(rs);
			}
			rs.close();
		} finally {
			con.close();
		}
		
	}

}
