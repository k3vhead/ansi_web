package com.ansi.scilla.web.employeeExpense.request;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;

import com.ansi.scilla.common.db.EmployeeExpense;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.ansi.scilla.web.common.request.RequiredForUpdate;
import com.ansi.scilla.web.common.response.WebMessages;

public class EmployeeExpenseRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Date date;
	private Integer washerId;
	private String expenseType;
	private BigDecimal amount;
	private String notes;
	private Integer expenseId;

	public EmployeeExpenseRequest() {
		super();
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getWasherId() {
		return washerId;
	}

	public void setWasherId(Integer washerId) {
		this.washerId = washerId;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public String getExpenseType() {
		return expenseType;
	}

	public void setExpenseType(String expenseType) {
		this.expenseType = expenseType;
	}

	@RequiredForAdd
	@RequiredForUpdate
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

	@RequiredForUpdate
	public Integer getExpenseId() {
		return expenseId;
	}

	public void setExpenseId(Integer expenseId) {
		this.expenseId = expenseId;
	}

	public WebMessages validateAdd(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateBigDecimal(webMessages, "amount", this.amount, null, null, true);
		// RequestValidator.validateDate(webMessages, "date", this.date, true,
		// new Date(0), new Date(500, 1, 1));
		RequestValidator.validateId(conn, webMessages, "ansi_user", User.USER_ID, "washerId", this.washerId, true);
		return webMessages;
	}

	public WebMessages validateUpdate(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateId(conn, webMessages, EmployeeExpense.TABLE, EmployeeExpense.EXPENSE_ID, "expenseId",
				this.expenseId, true);
		return webMessages;

	}

}
