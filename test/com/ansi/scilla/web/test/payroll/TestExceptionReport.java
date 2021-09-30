package com.ansi.scilla.web.test.payroll;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.payroll.common.ExceptionReportRecord;
import com.ansi.scilla.web.payroll.query.ExceptionReportQuery;

public class TestExceptionReport {

	public static void main(String[] args) {
		try {
			new TestExceptionReport().go();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void go() throws SQLException {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			ResultSet rs = ExceptionReportQuery.execute(conn);
			while (rs.next()) {
				ExceptionReportRecord record = new ExceptionReportRecord(rs);
				System.out.println(record);
			}
			rs.close();
		} catch (Exception e) {
			conn.rollback();
		} finally { 
			conn.close();
		}
		
	}

}
