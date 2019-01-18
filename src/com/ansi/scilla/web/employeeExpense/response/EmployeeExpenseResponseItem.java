package com.ansi.scilla.web.employeeExpense.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

<<<<<<< HEAD
public class EmployeeExpenseResponseItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String firstName;
	private String lastName;
	private String detail;
=======
import com.fasterxml.jackson.annotation.JsonFormat;

public class EmployeeExpenseResponseItem implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String lastName;
	private String firstName;
//>>>>>>> ff03802305f228cb58c33e2ce876d103012cdd08
	private Date date;
	private String expenseType;
	private BigDecimal amount;
	private String notes;
	private Integer expenseId;

	public EmployeeExpenseResponseItem() {
		super();
	}
//<<<<<<< HEAD
//
//	public String getFirstName() {
//		return firstName;
//	}
//
//	public String getLastName() {
//		return lastName;
//	}
//
//	public String getDetail() {
//		return detail;
//	}
//
//	public void setFirstName(String firstName) {
//		this.firstName = firstName;
//=======
	
	public String getLastName() {
		return lastName;
//>>>>>>> ff03802305f228cb58c33e2ce876d103012cdd08
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

//<<<<<<< HEAD
//	public void setDetail(String detail) {
//		this.detail = detail;
//	}
//
//	public Date getDate() {
//		return date;
//	}
//
//=======
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}



	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getDate() {
		return date;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy/ww", timezone="America/Chicago")
	public Date getWeek() {
		return date;
	}
	
//>>>>>>> ff03802305f228cb58c33e2ce876d103012cdd08
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
