package com.ansi.scilla.web.response.ticket;

import java.math.BigDecimal;

import com.ansi.scilla.common.ApplicationObject;

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
	
	public Integer getInvoiceId() {
		return invoiceId;
	}
	
	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}
	
	public BigDecimal getSumInvPpc() {
		return sumInvPpc;
	}
	
	public void setSumInvPpc(BigDecimal sumInvPpc) {
		this.sumInvPpc = sumInvPpc;
	}
	
	public BigDecimal getSumInvTax() {
		return sumInvTax;
	}
	
	public void setSumInvTax(BigDecimal sumInvTax) {
		this.sumInvTax = sumInvTax;
	}
	
	public BigDecimal getSumInvPpcPaid() {
		return sumInvPpcPaid;
	}
	
	public void setSumInvPpcPaid(BigDecimal sumInvPpcPaid) {
		this.sumInvPpcPaid = sumInvPpcPaid;
	}
	
	public BigDecimal getSumInvTaxPaid() {
		return sumInvTaxPaid;
	}
	
	public void setSumInvTaxPaid(BigDecimal sumInvTaxPaid) {
		this.sumInvTaxPaid = sumInvTaxPaid;
	}
	
	public BigDecimal getBalance() {
		return balance;
	}
	
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
}
