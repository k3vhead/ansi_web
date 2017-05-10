package com.ansi.scilla.web.request.payment;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.web.request.AbstractRequest;
import com.ansi.scilla.web.request.RequiredForAdd;
import com.ansi.scilla.web.request.RequiredForUpdate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


public class PaymentRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	public static final String AMOUNT = "paymentAmount";
	public static final String PAYMENT_ID = "paymentId";
	public static final String TYPE = "type";
	public static final String PAYMENT_DATE = "paymentDate";
	public static final String PAYMENT_NOTE = "paymentNote";
	public static final String CHECK_NUMBER = "checkNumber";
	public static final String CHECK_DATE = "checkDate";
	public static final String FEES = "fees";
	public static final String EXCESS_PAYMENT = "excessPayment";
	public static final String PAYMENT_METHOD = "paymentMethod";
	
	
	private BigDecimal paymentAmount;
	private Integer paymentId;
	private String type;
	private Date paymentDate;
	private String paymentNote;
	private String checkNumber;
	private Date checkDate;
	private String paymentMethod;
	
	private BigDecimal fees;
	private BigDecimal excessPayment;
	
	private List<PayTicketRequestItem> payTicketList;

	
	public PaymentRequest() {
		super();
	}
		
	@RequiredForAdd
	@RequiredForUpdate
	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}


	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}
	@RequiredForUpdate
	public Integer getPaymentId() {
		return this.paymentId;
	}

	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return this.type;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	@RequiredForAdd
	@RequiredForUpdate
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
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
	@RequiredForAdd
	@RequiredForUpdate
	public String getCheckNumber() {
		return this.checkNumber;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}
	@RequiredForAdd
	@RequiredForUpdate
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
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
	@RequiredForAdd
	@RequiredForUpdate
	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}


}
