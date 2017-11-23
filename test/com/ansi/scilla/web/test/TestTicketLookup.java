package com.ansi.scilla.web.test;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.common.queries.TicketLookupSearch;
import com.ansi.scilla.common.queries.TicketLookupSearchItem;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.ticket.response.TicketTableReturnItem;

public class TestTicketLookup {

	public static void main(String[] args) {
		try {
			new TestTicketLookup().goV2();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void goV2() throws Exception {
		Connection conn = null;
		System.out.println("Start");
		try {
			conn = AppUtils.getDevConn();
			TicketLookupSearch x = new TicketLookupSearch(0, 10);
//			x.setSearchTerm("710004");
			x.setJobId(17);
			List<TicketLookupSearchItem> ilist = x.select(conn);
			for ( TicketLookupSearchItem i : ilist ) {
				System.out.println(i);				
				System.out.println("*********************************");
				System.out.println("*********************************");
				System.out.println("*********************************");
				TicketTableReturnItem j = new TicketTableReturnItem(i);
				System.out.println(j);
			}
		} finally {
			conn.close();
		}
		System.out.println("end");
	}
}
