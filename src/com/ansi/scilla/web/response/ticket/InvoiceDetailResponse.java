package com.ansi.scilla.web.response.ticket;

import com.ansi.scilla.common.ApplicationObject;

public class InvoiceDetailResponse extends ApplicationObject {
	/*
	 * 			invoice_id (this is the invoice number)
	 * 			sumInvPpc - sum(invoice.ticket.act_price_per_cleaning)
	 * 			sumInvTax - sum(invoice.ticket.act_tax_amt)
	 * 			sumInvPpcPaid - sum(invoice.ticket_payment.amount)
	 * 			sumInvTaxPaid - sum(invoice.ticket_payment.tax_amt)
	 * 			balance(sumInvPpc + sumInvTax - (sumInvPpcPaid + sumInvTaxPaid))
	 * 			**invoice write off amount - stub for v 2.0
	 * 			**invoice MSFC amount - stub for v 2.0
	 * 			**invoice excess payment amount - stub for v 2.0
	*/
	
}
