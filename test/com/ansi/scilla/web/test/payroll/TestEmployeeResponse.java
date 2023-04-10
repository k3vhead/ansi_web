package com.ansi.scilla.web.test.payroll;

import java.sql.Connection;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.payroll.response.EmployeeResponseRecord;


public class TestEmployeeResponse {

	public static void main(String[] args) {
		try {
			new TestEmployeeResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			EmployeeResponseRecord rec = new EmployeeResponseRecord(conn, 1050);
			System.out.println(rec);
		} finally {
			conn.close();
		}
	}
}
