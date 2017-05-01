package com.ansi.scilla.web.response.payment;

import java.math.BigDecimal;

import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.web.request.payment.ApplyPaymentRequest;
import com.ansi.scilla.web.response.MessageResponse;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ApplyPaymentResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private BigDecimal availableFromPayment;
	private BigDecimal invoiceBalance;
	private BigDecimal totalPayInvoice;
	private BigDecimal totalPayTax;
	private BigDecimal excessCash;
	private BigDecimal feeAmount;
	private BigDecimal unappliedAmount;
	private ApplyPaymentRequest applyPaymentRequest;
	
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getAvailableFromPayment() {
		return availableFromPayment;
	}
	public void setAvailableFromPayment(BigDecimal availableFromPayment) {
		this.availableFromPayment = availableFromPayment;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getInvoiceBalance() {
		return invoiceBalance;
	}
	public void setInvoiceBalance(BigDecimal invoiceBalance) {
		this.invoiceBalance = invoiceBalance;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getTotalPayInvoice() {
		return totalPayInvoice;
	}
	public void setTotalPayInvoice(BigDecimal totalPayInvoice) {
		this.totalPayInvoice = totalPayInvoice;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getTotalPayTax() {
		return totalPayTax;
	}
	public void setTotalPayTax(BigDecimal totalPayTax) {
		this.totalPayTax = totalPayTax;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getExcessCash() {
		return excessCash;
	}
	public void setExcessCash(BigDecimal excessCash) {
		this.excessCash = excessCash;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(BigDecimal feeAmount) {
		this.feeAmount = feeAmount;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getUnappliedAmount() {
		return unappliedAmount;
	}
	public void setUnappliedAmount(BigDecimal unappliedAmount) {
		this.unappliedAmount = unappliedAmount;
	}
	public ApplyPaymentRequest getApplyPaymentRequest() {
		return applyPaymentRequest;
	}
	public void setApplyPaymentRequest(ApplyPaymentRequest applyPaymentRequest) {
		this.applyPaymentRequest = applyPaymentRequest;
	}
	
	
	
}
