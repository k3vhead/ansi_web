package com.ansi.scilla.web.test.invoices;

import java.sql.Connection;
import java.util.Date;

import com.ansi.scilla.web.invoice.request.InvoiceGenerationRequestTkt;
import com.ansi.scilla.web.test.common.AbstractTester;

public class TestInvoiceGenTktValidation extends AbstractTester {

	public static void main(String[] args) {
		new TestInvoiceGenTktValidation().run();
	}

	@Override
	protected void go(Connection conn) throws Exception {
//		test1(conn);
//		test2(conn);
//		test3(conn);
		test4(conn);
	}

	private void test4(Connection conn) throws Exception {
		InvoiceGenerationRequestTkt request = new InvoiceGenerationRequestTkt();		
		request.setInvoiceDate(new Date());
		request.setTicketList( new Integer[] {849568,828536});
		System.out.println(request.validate(conn));				
	}

	private void test3(Connection conn) throws Exception {
		InvoiceGenerationRequestTkt request = new InvoiceGenerationRequestTkt();		
//		request.setInvoiceDate(new Date());
		request.setTicketList( new Integer[] {849568,828536,848333,790677});
		System.out.println(request.validate(conn));		
	}

	private void test2(Connection conn) throws Exception {
		InvoiceGenerationRequestTkt request = new InvoiceGenerationRequestTkt();		
		request.setInvoiceDate(new Date());
		System.out.println(request.validate(conn));
	}

	private void test1(Connection conn) throws Exception {
		InvoiceGenerationRequestTkt request = new InvoiceGenerationRequestTkt();
		System.out.println(request.validate(conn));
		
	}

}
