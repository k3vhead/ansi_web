package com.ansi.scilla.web.employeeExpense.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.ansi.scilla.common.jsonFormat.AnsiBigDecimalFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class EmployeeExpenseResponseItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String lastName;
	private String firstName;
	private Date date;
	private String expenseType;
	private BigDecimal amount;
	private String notes;
	private Integer expenseId;
	private String detail;

	public EmployeeExpenseResponseItem() {
		super();
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

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy", timezone = "America/Chicago")
	public Date getDate() {
		return date;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/ww", timezone = "America/Chicago")
	public Date getWeek() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getExpenseType() {
		return expenseType;
	}

	public void setExpenseType(String expenseType) {
		this.expenseType = expenseType;
	}

	@JsonSerialize(using = AnsiBigDecimalFormatter.class)
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Integer getExpenseId() {
		return expenseId;
	}

	public void setExpenseId(Integer expenseId) {
		this.expenseId = expenseId;
	}

	public String getDetail() {
		return this.detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

}
