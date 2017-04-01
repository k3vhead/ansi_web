package com.ansi.scilla.web.response.ticket;

import java.math.BigDecimal;
import java.sql.Connection;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.queries.InvoiceTotals;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class InvoiceDetail extends ApplicationObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer invoiceId; // (this is the invoice number)
	private BigDecimal sumInvPpc; // - sum(invoice.ticket.act_price_per_cleaning)
	private BigDecimal sumInvTax; // - sum(invoice.ticket.act_tax_amt)
	private BigDecimal sumInvPpcPaid; // - sum(invoice.ticket_payment.amount)
	private BigDecimal sumInvTaxPaid; // - sum(invoice.ticket_payment.tax_amt)
	private BigDecimal balance; //(sumInvPpc + sumInvTax - (sumInvPpcPaid + sumInvTaxPaid))
//	  			**invoice write off amount - stub for v 2.0
//	  			**invoice MSFC amount - stub for v 2.0
//	  			**invoice excess payment amount - stub for v 2.0
//	
	public InvoiceDetail() {
		super();
	}
	
	public InvoiceDetail(Connection conn, Integer invoiceId) throws RecordNotFoundException, Exception {
		InvoiceTotals invoiceTotals = InvoiceTotals.select(conn, invoiceId);
		this.invoiceId = invoiceId;
		this.sumInvPpc = invoiceTotals.getTotalVolInv();
		this.sumInvTax = invoiceTotals.getTotalTaxInv();
		this.sumInvPpcPaid = invoiceTotals.getTotalVolPaid();
		this.sumInvTaxPaid = invoiceTotals.getTotalTaxPaid();
		this.balance = sumInvPpc.add(sumInvTax).subtract(sumInvPpcPaid.add(sumInvTaxPaid));
	}

	public Integer getInvoiceId() {
		return invoiceId;
	}
	
	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getSumInvPpc() {
		return sumInvPpc;
	}
	
	public void setSumInvPpc(BigDecimal sumInvPpc) {
		this.sumInvPpc = sumInvPpc;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getSumInvTax() {
		return sumInvTax;
	}
	
	public void setSumInvTax(BigDecimal sumInvTax) {
		this.sumInvTax = sumInvTax;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getSumInvPpcPaid() {
		return sumInvPpcPaid;
	}
	
	public void setSumInvPpcPaid(BigDecimal sumInvPpcPaid) {
		this.sumInvPpcPaid = sumInvPpcPaid;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getSumInvTaxPaid() {
		return sumInvTaxPaid;
	}
	
	public void setSumInvTaxPaid(BigDecimal sumInvTaxPaid) {
		this.sumInvTaxPaid = sumInvTaxPaid;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getBalance() {
		return balance;
	}
	
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
}
