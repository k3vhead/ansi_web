package com.ansi.scilla.web.employeeExpense.response;

import java.io.Serializable;
import java.sql.Connection;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.EmployeeExpense;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.response.MessageResponse;

public class EmployeeExpenseResponse extends MessageResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private EmployeeExpenseResponseItem item;

	public EmployeeExpenseResponse() {
		super();
	}

	public EmployeeExpenseResponse(Connection conn, EmployeeExpense employeeExpense) throws Exception {
		this.item = new EmployeeExpenseResponseItem();
		this.item.setAmount(employeeExpense.getAmount());
		this.item.setDate(employeeExpense.getWorkDate());
		this.item.setExpenseType(employeeExpense.getExpenseType());
		if (!StringUtils.isBlank(employeeExpense.getNotes())) {
			this.item.setNotes(employeeExpense.getNotes());
		}
		User user = new User();
		user.setUserId(employeeExpense.getWasherId());
		user.selectOne(conn);
		this.item.setWasherName(user.getFirstName() + " " + user.getLastName());
		this.item.setExpenseId(employeeExpense.getExpenseId());
	}

	public EmployeeExpenseResponseItem getItem() {
		return item;
	}

	public void setItem(EmployeeExpenseResponseItem item) {
		this.item = item;
	}

}
