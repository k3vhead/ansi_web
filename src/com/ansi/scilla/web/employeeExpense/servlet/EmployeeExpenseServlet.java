package com.ansi.scilla.web.employeeExpense.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.EmployeeExpense;
import com.ansi.scilla.common.db.NonDirectLabor;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractCrudServlet;
import com.ansi.scilla.web.common.utils.FieldMap;
import com.ansi.scilla.web.common.utils.JsonFieldFormat;
import com.ansi.scilla.web.common.utils.Permission;
import com.fasterxml.jackson.databind.JsonNode;
import com.thewebthing.commons.db2.RecordNotFoundException;

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
	protected WebMessages validateAdd(Connection conn, JsonNode addRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		Integer expenseId = addRequest.get("expenseId").asInt();
		RequestValidator.validateId(conn, webMessages, EmployeeExpense.TABLE, EmployeeExpense.EXPENSE_ID, "expenseId",
				expenseId, true);

		String workDateText = addRequest.get("workDate").asText();
		Date workDate = StringUtils.isBlank(workDateText) ? null : standardDateFormat.parse(workDateText);

		RequestValidator.validateWasherId(conn, webMessages, "washerId", addRequest.get("washerId").asInt(), true);
		RequestValidator.validateDate(webMessages, "workDate", workDate, true, null, null);
		RequestValidator.validateExpenseType(webMessages, "expenseType", addRequest.get("expenseType").asText(), true);
		// RequestValidator.validateBigDecimal(webMessages, "amount",
		// addRequest.get("amount").asLong(), true);
		RequestValidator.validateString(webMessages, "detail", addRequest.get("detail").asText(), false);
		RequestValidator.validateString(webMessages, "notes", addRequest.get("notes").asText(), false);

		return webMessages;
	}

	@Override
	protected WebMessages validateUpdate(Connection conn, JsonNode updateRequest) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
