package com.ansi.scilla.web.response.payment;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.ansi.scilla.common.db.Payment;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.response.MessageResponse;

public class DuplicatePaymentResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private PaymentDetail payment;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private List<Integer> invoiceIdList;
	
	public DuplicatePaymentResponse() {
		super();
	}
	
	public DuplicatePaymentResponse(Payment payment, User user, List<Integer> invoiceIdList) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this();
		this.payment = new PaymentDetail(payment);
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.email = user.getEmail();
		this.phone = user.getPhone();
		this.invoiceIdList = invoiceIdList;
	}
	public PaymentDetail getPayment() {
		return payment;
	}
	public void setPayment(PaymentDetail payment) {
		this.payment = payment;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public List<Integer> getInvoiceIdList() {
		return invoiceIdList;
	}

	public void setInvoiceIdList(List<Integer> invoiceIdList) {
		this.invoiceIdList = invoiceIdList;
	}
	
	
}
