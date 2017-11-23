package com.ansi.scilla.web.invoice.response;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.invoice.InvoiceStatus;
import com.ansi.scilla.web.common.response.MessageResponse;

public class InvoicePrintLookupResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private final String sql = "select division.division_id, concat(division.division_nbr, '-', division.division_code) as div, count(invoice_id) as invoice_count, sum(ticket_count) as ticket_count, sum(act_tax_amt) as tax_total, sum(act_price_per_cleaning) as invoice_total " + 
			" from division " +
			" left outer join   " +
			" (select ticket.act_division_id, ticket.invoice_id, count(*) as ticket_count, sum(act_tax_amt) as act_tax_amt, sum(ticket.act_price_per_cleaning) as act_price_per_cleaning " +
			" from ticket  " +
			" inner join invoice on invoice.invoice_id = ticket.invoice_id and invoice.invoice_status='" + InvoiceStatus.NEW.code() + "' " +
			" group by ticket.act_division_id, ticket.invoice_id) as xyz on act_division_id=division.division_id " +
			" group by division.division_id, division.division_nbr, division.division_code " +
			" order by div";

	private List<InvoicePrintLookupResponseItem> invoiceList;
	
	public InvoicePrintLookupResponse(Connection conn) throws Exception {
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery(sql);
		ResultSetMetaData rsmd = rs.getMetaData();
		this.invoiceList = new ArrayList<InvoicePrintLookupResponseItem>();
		while ( rs.next() ) {
			this.invoiceList.add(new InvoicePrintLookupResponseItem(rsmd, rs));
		}
		rs.close();
	}

	public List<InvoicePrintLookupResponseItem> getInvoiceList() {
		return invoiceList;
	}

	public void setInvoiceList(List<InvoicePrintLookupResponseItem> invoiceList) {
		this.invoiceList = invoiceList;
	}

}
