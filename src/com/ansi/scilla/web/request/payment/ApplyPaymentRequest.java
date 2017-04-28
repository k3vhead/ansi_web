package com.ansi.scilla.web.request.payment;

import java.util.List;

import com.ansi.scilla.web.request.AbstractRequest;

public class ApplyPaymentRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Integer paymentId;
	private Integer invoiceId;
	private Double feeAmount;
	private Double excessCash;
	List<ApplyPaymentRequestItem> ticketList;
	
	public ApplyPaymentRequest() {
		super();
	}
	public Integer getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}
	public Integer getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}
	public Double getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(Double feeAmount) {
		this.feeAmount = feeAmount;
	}
	public Double getExcessCash() {
		return excessCash;
	}
	public void setExcessCash(Double excessCash) {
		this.excessCash = excessCash;
	}
	public List<ApplyPaymentRequestItem> getTicketList() {
		return ticketList;
	}
	public void setTicketList(List<ApplyPaymentRequestItem> ticketList) {
		this.ticketList = ticketList;
	}
	

	
	
}
