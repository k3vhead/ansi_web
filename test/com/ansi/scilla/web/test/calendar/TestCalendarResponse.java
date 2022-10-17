package com.ansi.scilla.web.test.calendar;

import java.sql.Connection;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.calendar.response.CalendarResponse;

public class TestCalendarResponse {

	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			CalendarResponse x = new CalendarResponse(conn, 2019);
			System.out.println(x);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	public static void main(String[] args) {
		try {
			new TestCalendarResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
