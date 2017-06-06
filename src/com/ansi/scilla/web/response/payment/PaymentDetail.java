package com.ansi.scilla.web.response.payment;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Payment;
import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class PaymentDetail extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	private BigDecimal amount;
	private Integer paymentId;
	private Integer status;
	private String type;
	private Date paymentDate;
	private String paymentNote;
	private String checkNumber;
	private Date checkDate;
	private String paymentMethod;
	public PaymentDetail() {
		super();
	}
	public PaymentDetail(Payment payment) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this();
		PropertyUtils.copyProperties(this, payment);
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getAmount() {
		return amount;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Integer getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getPaymentDate() {
		return paymentDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getPaymentNote() {
		return paymentNote;
	}
	public void setPaymentNote(String paymentNote) {
		this.paymentNote = paymentNote;
	}
	public String getCheckNumber() {
		return checkNumber;
	}
	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getCheckDate() {
		return checkDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

}
