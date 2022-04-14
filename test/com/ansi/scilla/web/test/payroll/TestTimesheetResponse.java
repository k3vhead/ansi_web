package com.ansi.scilla.web.test.payroll;

import java.sql.Connection;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.locale.common.LocaleUtils;
import com.ansi.scilla.web.payroll.response.TimesheetResponse;

public class TestTimesheetResponse {

	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			Integer divisionId = 115;
			Calendar weekEnding = new GregorianCalendar(2021, Calendar.AUGUST, 3);
			String state = "OH";
			String city="Cleveland";
			Integer employeeCode = 1869;
			
			Locale locale = LocaleUtils.makeLocale(conn, state, city);
			System.out.println(locale);
			TimesheetResponse response = new TimesheetResponse(conn,divisionId, weekEnding, employeeCode, locale);
			System.out.println(response);
		} finally {
			conn.close();
		}
	}
	
	
	public static void main(String[] args) {
		try {
			new TestTimesheetResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
