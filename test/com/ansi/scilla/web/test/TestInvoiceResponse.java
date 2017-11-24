package com.ansi.scilla.web.test;

import java.sql.Connection;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.invoice.response.InvoiceTicketResponse;

public class TestInvoiceResponse {

	public static void main(String[] args) {
		try {
			TesterUtils.makeLoggers();
			new TestInvoiceResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
//			InvoicePrintLookupResponse resp = new InvoicePrintLookupResponse(conn);
//			System.out.println(resp.toJson());
			
			InvoiceTicketResponse resp = new InvoiceTicketResponse(conn, 240147);
			System.out.println(resp.toJson());
		} finally {
			conn.close();
		}
	}

}
