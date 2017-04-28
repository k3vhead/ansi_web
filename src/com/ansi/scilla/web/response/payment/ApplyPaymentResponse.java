package com.ansi.scilla.web.response.payment;

import com.ansi.scilla.web.request.payment.ApplyPaymentRequest;
import com.ansi.scilla.web.response.MessageResponse;

public class ApplyPaymentResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private Double availableFromPayment;
	private Double invoiceBalance;
	private Double totalPayInvoice;
	private Double totalPayTax;
	private Double excessCash;
	private Double feeAmount;
	private Double unappliedAmount;
	private ApplyPaymentRequest applyPaymentRequest;
	
	public Double getAvailableFromPayment() {
		return availableFromPayment;
	}
	public void setAvailableFromPayment(Double availableFromPayment) {
		this.availableFromPayment = availableFromPayment;
	}
	public Double getInvoiceBalance() {
		return invoiceBalance;
	}
	public void setInvoiceBalance(Double invoiceBalance) {
		this.invoiceBalance = invoiceBalance;
	}
	public Double getTotalPayInvoice() {
		return totalPayInvoice;
	}
	public void setTotalPayInvoice(Double totalPayInvoice) {
		this.totalPayInvoice = totalPayInvoice;
	}
	public Double getTotalPayTax() {
		return totalPayTax;
	}
	public void setTotalPayTax(Double totalPayTax) {
		this.totalPayTax = totalPayTax;
	}
	public Double getExcessCash() {
		return excessCash;
	}
	public void setExcessCash(Double excessCash) {
		this.excessCash = excessCash;
	}
	public Double getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(Double feeAmount) {
		this.feeAmount = feeAmount;
	}
	public Double getUnappliedAmount() {
		return unappliedAmount;
	}
	public void setUnappliedAmount(Double unappliedAmount) {
		this.unappliedAmount = unappliedAmount;
	}
	public ApplyPaymentRequest getApplyPaymentRequest() {
		return applyPaymentRequest;
	}
	public void setApplyPaymentRequest(ApplyPaymentRequest applyPaymentRequest) {
		this.applyPaymentRequest = applyPaymentRequest;
	}
	
	
	
}
