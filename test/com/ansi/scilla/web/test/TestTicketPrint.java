package com.ansi.scilla.web.test;

import java.sql.Connection;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.ticket.response.TicketPrintLookupResponse;

public class TestTicketPrint {

	public static void main(String[] args) {
		try {
			new TestTicketPrint().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			Calendar endDate = new GregorianCalendar(2017, Calendar.JUNE, 1);
			TicketPrintLookupResponse r = new TicketPrintLookupResponse(conn, endDate);
			System.out.println(r);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

}
