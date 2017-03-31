package com.ansi.scilla.web.test;

import java.sql.Connection;

import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.response.invoice.InvoicePrintLookupResponse;

public class TestInvoiceResponse {

	public static void main(String[] args) {
		try {
			new TestInvoiceResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getConn();
			InvoicePrintLookupResponse resp = new InvoicePrintLookupResponse(conn);
			System.out.println(resp.toJson());
		} finally {
			conn.close();
		}
	}

}
