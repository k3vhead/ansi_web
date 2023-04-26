package com.ansi.scilla.web.test.invoices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.invoice.query.InvoiceLookupQuery;
import com.ansi.scilla.web.test.common.AbstractTester;

public class TestInvoiceByTktResponse extends AbstractTester {

	public static void main(String[] args) {
		new TestInvoiceByTktResponse().run();
	}

	@Override
	protected void go(Connection conn) throws Exception {
		Integer[] invoiceList = new Integer[] {296496,296495};
		String sql = InvoiceLookupQuery.sqlSelectClause + InvoiceLookupQuery.sqlFromClause + "where invoice.invoice_id in " + AppUtils.makeBindVariables(invoiceList);
		System.out.println(sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, 5); //user id
		for ( int i = 0; i < invoiceList.length; i++ ) {
			ps.setInt(i+2, invoiceList[i]);
		}
		ResultSet rs = ps.executeQuery();
		while ( rs.next() ) {
			System.out.println(rs.getInt("invoice_id") + "\t" + rs.getString("div") + "\t" + rs.getInt("ticket_count") + "\t" + rs.getBigDecimal("invoice_amount"));
		}
		rs.close();
	}

}
