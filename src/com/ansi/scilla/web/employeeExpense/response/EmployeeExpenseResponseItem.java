package com.ansi.scilla.web.employeeExpense.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class EmployeeExpenseResponseItem implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String washerName;
	private Date date;
	private String expenseType;
	private BigDecimal amount;
	private String notes;
	private Integer expenseId;
	
	public EmployeeExpenseResponseItem() {
		super();
	}
	
	public String getWasherName() {
		return washerName;
	}
	
	public void setWasherName(String washerName) {
		this.washerName = washerName;
	}
	
	public Date getDate() {
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
	
}
