package com.ansi.scilla.web.test;

import java.util.Date;

import com.ansi.scilla.web.request.ticket.TicketReturnRequest;

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
			new TestTicket().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		String url = "http://127.0.0.1:8080/ansi_web/ticket/484252";
		String json = TesterUtils.getJson(url);
		System.out.println(json);
	}

}
