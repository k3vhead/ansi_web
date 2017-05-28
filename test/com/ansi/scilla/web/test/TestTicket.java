package com.ansi.scilla.web.test;

import java.sql.Connection;

import com.ansi.scilla.common.queries.TicketPaymentTotals;
import com.ansi.scilla.common.utils.AppUtils;

public class TestTicket {

	public static void main(String[] args) {
//		TicketReturnRequest test = new TicketReturnRequest();
//		
//		test.setAction("reject");
//		Date today = new Date();
//		test.setProcessDate(today);
//		test.setProcessNotes("Joshua's PlayGround");
//		try {
//			System.out.println(test.toJson());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		TesterUtils.makeLoggers();
		try {
			new TestTicket().goDetail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		String url = "http://127.0.0.1:8080/ansi_web/ticket/484252";
		String json = TesterUtils.getJson(url);
		System.out.println(json);
	}
	
	public void goDetail() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getConn();
//			TicketDetail td = new TicketDetail(conn, 651611);
			TicketPaymentTotals td = TicketPaymentTotals.select(conn, 651611);
			System.out.println(td);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

}
