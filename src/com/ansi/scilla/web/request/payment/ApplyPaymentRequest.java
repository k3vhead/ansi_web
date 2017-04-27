package com.ansi.scilla.web.request.payment;

import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.request.AbstractRequest;

public class ApplyPaymentRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Integer paymentId;
	private Integer invoiceId;
	private Double feeAmount;
	private Double excessCash;
	List<VerifyPaymentRequestItem> ticketList;
	
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
	public List<VerifyPaymentRequestItem> getTicketList() {
		return ticketList;
	}
	public void setTicketList(List<VerifyPaymentRequestItem> ticketList) {
		this.ticketList = ticketList;
	}
	
	public class VerifyPaymentRequestItem extends ApplicationObject {
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
	
	
}
