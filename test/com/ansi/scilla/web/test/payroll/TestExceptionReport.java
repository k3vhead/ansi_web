package com.ansi.scilla.web.test.payroll;

import java.sql.Connection;
import java.sql.SQLException;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.payroll.response.ExceptionReportResponse;

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
//			ResultSet rs = ExceptionReportQuery.execute(conn);
//			while (rs.next()) {
//				ExceptionReportRecord record = new ExceptionReportRecord(rs);
//				System.out.println(record);
//			}
//			rs.close();
			ExceptionReportResponse x = new ExceptionReportResponse(conn, 105);
			
		} catch (Exception e) {
			conn.rollback();
		} finally { 
			conn.close();
		}
		
	}

}
