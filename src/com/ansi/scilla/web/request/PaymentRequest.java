package com.ansi.scilla.web.request;

import java.math.BigDecimal;
//import java.util.Date;
import java.util.Date;
import java.util.List;

import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * @author ggroce
 *
 *
 *
 */

public class PaymentRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private BigDecimal amount;
	private Integer paymentId;
	private String type;
	private Date paymentDate;
	private String paymentNote;
	private String checkNumber;
	private Date checkDate;
	
	private BigDecimal fees;
	private BigDecimal excessPayment;
	
	private List<PayTicketRequestItem> payTicketList;

	
	public PaymentRequest() {
		super();
	}
		
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}
	public Integer getPaymentId() {
		return this.paymentId;
	}

	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return this.type;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public Date getPaymentDate() {
		return this.paymentDate;
	}

	public void setPaymentNote(String paymentNote) {
		this.paymentNote = paymentNote;
	}
	public String getPaymentNote() {
		return this.paymentNote;
	}

	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}
	public String getCheckNumber() {
		return this.checkNumber;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}
	public Date getCheckDate() {
		return this.checkDate;
	}

	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public void setFees(BigDecimal fees) {
		this.fees = fees;
	}
	public BigDecimal getFees() {
		return this.fees;
	}

	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public void setExcessPayment(BigDecimal excessPayment) {
		this.excessPayment = excessPayment;
	}
	public BigDecimal getExcessPayment() {
		return this.excessPayment;
	}

	public void setPayTicketList(List<PayTicketRequestItem> payTicketList) {
		this.payTicketList = payTicketList;
	}
	public List<PayTicketRequestItem> getPayTicketList() {
		return this.payTicketList;
	}


}
