package com.ansi.scilla.web.payment.request;

import com.ansi.scilla.common.ApplicationObject;

public class ApplyPaymentRequestItem extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private Integer ticketId;
	private Double payInvoice;
	private Double payTax;
	private Double writeOff;  // this is for version 2
	
	public Integer getTicketId() {
		return ticketId;
	}
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	public Double getPayInvoice() {
		return payInvoice;
	}
	public void setPayInvoice(Double payInvoice) {
		this.payInvoice = payInvoice;
	}
	public Double getPayTax() {
		return payTax;
	}
	public void setPayTax(Double payTax) {
		this.payTax = payTax;
	}
	public Double getWriteOff() {
		return writeOff;
	}
	public void setWriteOff(Double writeOff) {
		this.writeOff = writeOff;
	}
	
}