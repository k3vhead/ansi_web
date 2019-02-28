package com.ansi.scilla.web.employeeExpense.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.EmployeeExpense;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractCrudServlet;
import com.ansi.scilla.web.common.servlet.PermittedAction;
import com.ansi.scilla.web.common.utils.FieldMap;
import com.ansi.scilla.web.common.utils.JsonFieldFormat;
import com.ansi.scilla.web.common.utils.Permission;

public class EmployeeExpenseServlet extends AbstractCrudServlet {

	private static final long serialVersionUID = 1L;
	private static final String REALM = "employeeExpense";
	private static List<FieldMap> fieldMap;

	static {
		fieldMap = new ArrayList<FieldMap>();
		fieldMap.add(new FieldMap("expenseId", EmployeeExpense.EXPENSE_ID, JsonFieldFormat.INTEGER, true));
		fieldMap.add(new FieldMap("washerId", EmployeeExpense.WASHER_ID, JsonFieldFormat.INTEGER, true));
		fieldMap.add(new FieldMap("workDate", EmployeeExpense.WORK_DATE, JsonFieldFormat.DATE, true));
		fieldMap.add(new FieldMap("expenseType", EmployeeExpense.EXPENSE_TYPE, JsonFieldFormat.STRING, true));
		fieldMap.add(new FieldMap("amount", EmployeeExpense.AMOUNT, JsonFieldFormat.DECIMAL, true));
		fieldMap.add(new FieldMap("detail", EmployeeExpense.DETAIL, JsonFieldFormat.STRING, true));
		fieldMap.add(new FieldMap("notes", EmployeeExpense.NOTES, JsonFieldFormat.STRING, true));
		fieldMap.add(new FieldMap("firstName", User.FIRST_NAME, JsonFieldFormat.STRING, false));
		fieldMap.add(new FieldMap("lastName", User.LAST_NAME, JsonFieldFormat.STRING, false));
	}

	public EmployeeExpenseServlet() {
		super();
		final String displaySql = "select employee_expense.expense_id, employee_expense.washer_id, employee_expense.work_date,\n"
				+ "	employee_expense.expense_type, employee_expense.amount, employee_expense.detail, \n"
				+ "	employee_expense.notes, ansi_user.first_name, ansi_user.last_name\n" + "from employee_expense\n"
				+ "left outer join ansi_user on ansi_user.user_id=employee_expense.washer_id\n";

		super.setDisplaySql(displaySql);
		super.setPermittedActionList(Arrays.asList(new PermittedAction[] { PermittedAction.ADD, PermittedAction.GET }));
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		processGet(request, response, Permission.CLAIMS_READ, REALM, new String[] { ACTION_IS_LIST },
				new EmployeeExpense(), fieldMap);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processPost(request, response, Permission.CLAIMS_WRITE, REALM, new String[] { ACTION_IS_ADD },
				new EmployeeExpense(), fieldMap);
	}

	@Override
	protected WebMessages validateAdd(Connection conn, HashMap<String, Object> addRequest) throws Exception {
		WebMessages webMessages = new WebMessages();

		RequestValidator.validateWasherId(conn, webMessages, "washerId", (Integer) addRequest.get("washerId"), true);
		RequestValidator.validateDate(webMessages, "workDate", (String) addRequest.get("workDate"), standardDateFormat,
				true, null, null);
		RequestValidator.validateExpenseType(webMessages, "expenseType", (String) addRequest.get("expenseType"), true);
		RequestValidator.validateNumber(webMessages, "amount", addRequest.get("amount"), 0.0D, null, true);
		RequestValidator.validateString(webMessages, "detail", (String) addRequest.get("detail"), false);
		RequestValidator.validateString(webMessages, "notes", (String) addRequest.get("notes"), false);

		if (webMessages.isEmpty()) {
			RequestValidator.checkForDuplicates(conn, webMessages, new EmployeeExpense(), addRequest, fieldMap,
					standardDateFormat);
		}

		return webMessages;
	}

	@Override
	protected WebMessages validateUpdate(Connection conn, HashMap<String, Object> updateRequest) throws Exception {
		WebMessages webMessages = new WebMessages();

		RequestValidator.validateWasherId(conn, webMessages, "washerId", (Integer) updateRequest.get("washerId"), true);
		RequestValidator.validateDate(webMessages, "workDate", (String) updateRequest.get("workDate"),
				standardDateFormat, true, null, null);
		RequestValidator.validateExpenseType(webMessages, "expenseType", (String) updateRequest.get("expenseType"),
				true);
		RequestValidator.validateNumber(webMessages, "amount", updateRequest.get("amount"), 0.0D, null, true);
		RequestValidator.validateString(webMessages, "detail", (String) updateRequest.get("detail"), false);
		RequestValidator.validateString(webMessages, "notes", (String) updateRequest.get("notes"), false);

		Integer expenseId = (Integer) updateRequest.get("expenseId");
		RequestValidator.validateId(conn, webMessages, EmployeeExpense.TABLE, EmployeeExpense.EXPENSE_ID, "expenseId",
				expenseId, true);
		return webMessages;
	}

}
