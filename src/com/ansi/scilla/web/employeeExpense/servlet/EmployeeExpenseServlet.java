package com.ansi.scilla.web.employeeExpense.servlet;

import java.sql.Connection;

import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractCrudServlet;
import com.fasterxml.jackson.databind.JsonNode;

public class EmployeeExpenseServlet extends AbstractCrudServlet {

	private static final long serialVersionUID = 1L;

	public EmployeeExpenseServlet() {
		super();
		final String displaySql = "select employee_expense.expense_id, employee_expense.washer_id, employee_expense.work_date,\n" + 
				"	employee_expense.expense_type, employee_expense.amount, employee_expense.detail, \n" + 
				"	employee_expense.notes, ansi_user.first_name, ansi_user.last_name\n" + 
				"from employee_expense\n" + 
				"left outer join ansi_user on ansi_user.user_id=employee_expense.washer_id\n";
		
		super.setDisplaySql(displaySql);
	}
	
	
	
	@Override
	protected WebMessages validateAdd(Connection conn, JsonNode addRequest) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected WebMessages validateUpdate(Connection conn, JsonNode updateRequest) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	

}
