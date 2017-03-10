package com.ansi.scilla.web.test;

import java.sql.Connection;
import java.util.Calendar;
import java.util.List;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.response.ticket.TicketDRVResponse;

public class TestDRV {

	public static void main(String[] args) {
		TesterUtils.makeLoggers();
		try {
			new TestDRV().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		/*
		String[] urlList = new String[] {
			"http://127.0.0.1:8080/ansi_web/ticketDRV",
			"http://127.0.0.1:8080/ansi_web/ticketDRV?divisionId=19",
			"http://127.0.0.1:8080/ansi_web/ticketDRV?divisionId=xx",
			"http://127.0.0.1:8080/ansi_web/ticketDRV?divisionId=3&month=1",
			"http://127.0.0.1:8080/ansi_web/ticketDRV?divisionId=3&month=2",
			"http://127.0.0.1:8080/ansi_web/ticketDRV?divisionId=3&month=3",
			"http://127.0.0.1:8080/ansi_web/ticketDRV?divisionId=3&month=13"
		};
		
		for ( String url : urlList ) {
			System.out.println(url);
			String json = TesterUtils.getJson(url);
			System.out.println(json);
			System.out.println("*****************\n***************");
		}
		*/
		Connection conn = null;
		try {
			conn = AppUtils.getConn();
			TicketDRVResponse x = new TicketDRVResponse(conn, 10, 3, 2017);
			System.out.println(x.toJson());
			/*
			List<Division> divisionList = Division.cast(new Division().selectAll(conn));
			for ( Division d : divisionList ) {
				for (int month = Calendar.MARCH; month < 12; month++ ) {
					TicketDRVResponse x = new TicketDRVResponse(conn, d.getDivisionId(), month, 2017);
					System.out.println(d.getDivisionId() + "\t" + month + "\t" + x.getResponseItemList().size());
				}
			}
			*/
			
		} finally {
			conn.close();
		}
		
	}

}
