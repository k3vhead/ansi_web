package com.ansi.scilla.web.test;

import java.util.Date;

import com.ansi.scilla.web.request.TicketReturnRequest;

public class TestTicket {

	public static void main(String[] args) {
		TicketReturnRequest test = new TicketReturnRequest();
		
		test.setAction("reject");
		Date today = new Date();
		test.setProcessDate(today);
		test.setProcessNotes("Joshua's PlayGround");
		try {
			System.out.println(test.toJson());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
