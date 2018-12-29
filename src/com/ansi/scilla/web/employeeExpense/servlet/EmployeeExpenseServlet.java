package com.ansi.scilla.web.employeeExpense.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.EmployeeExpense;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.division.response.DivisionListResponse;
import com.ansi.scilla.web.division.response.DivisionResponse;
import com.ansi.scilla.web.employeeExpense.request.EmployeeExpenseRequest;
import com.ansi.scilla.web.employeeExpense.response.EmployeeExpenseListResponse;
import com.ansi.scilla.web.employeeExpense.response.EmployeeExpenseResponse;
import com.ansi.scilla.web.employeeExpense.response.EmployeeExpenseResponseItem;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class EmployeeExpenseServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "employeeExpense";
	public static final String ACTION_IS_LIST = "list";
	public static final String ACTION_IS_ADD = "add";
	public static final String ACTION_IS_UPDATE = "update";
	public static final String ACTION_IS_DELETE = "delete";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			AnsiURL url = new AnsiURL(request, REALM, new String[] { ACTION_IS_LIST });
			SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_READ);

			Connection conn = null;

			try {
				conn = AppUtils.getDBCPConn();

				if (!StringUtils.isBlank(url.getCommand()) && url.getCommand().equals(ACTION_IS_LIST)) {
					sendListResponse(conn, url, sessionData, response);
				} else if (url.getId() != null) {
					sendSingleResponse(conn, url, sessionData, response);
				} else {
					throw new RecordNotFoundException();
				}

			} catch (RecordNotFoundException recordNotFoundEx) {
				super.sendNotFound(response);
			} finally {
				AppUtils.closeQuiet(conn);
			}

		} catch (ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendNotAllowed(response);
		} catch (Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		}

	}

	private void sendSingleResponse(Connection conn, AnsiURL url, SessionData sessionData, HttpServletResponse response)
			throws Exception {
		EmployeeExpenseResponse employeeExpenseResponse = new EmployeeExpenseResponse();
		EmployeeExpense employeeExpense = new EmployeeExpense();
		employeeExpense.setExpenseId(url.getId());
		employeeExpense.selectOne(conn);
		EmployeeExpenseResponseItem employeeExpenseResponseItem = makeEmployeeExpenseResponseItem(conn,
				employeeExpense);
		employeeExpenseResponse.setItem(employeeExpenseResponseItem);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, employeeExpenseResponse);

	}

	private void sendListResponse(Connection conn, AnsiURL url, SessionData sessionData, HttpServletResponse response)
			throws Exception {
		EmployeeExpenseListResponse employeeExpenseListResponse = new EmployeeExpenseListResponse();
		EmployeeExpense employeeExpense = new EmployeeExpense();
		List<EmployeeExpense> list = EmployeeExpense.cast(employeeExpense.selectAll(conn));
		for (EmployeeExpense item : list) {
			employeeExpenseListResponse.getItemList().add(makeEmployeeExpenseResponseItem(conn, item));
		}
		super.sendResponse(conn, response, ResponseCode.SUCCESS, employeeExpenseListResponse);
	}

	private EmployeeExpenseResponseItem makeEmployeeExpenseResponseItem(Connection conn,
			EmployeeExpense employeeExpense) throws Exception {
		EmployeeExpenseResponseItem employeeExpenseResponseItem = new EmployeeExpenseResponseItem();
		employeeExpenseResponseItem.setAmount(employeeExpense.getAmount());
		employeeExpenseResponseItem.setDate(employeeExpense.getWorkDate());
		employeeExpenseResponseItem.setExpenseType(employeeExpense.getExpenseType());
		if (!StringUtils.isBlank(employeeExpense.getNotes())) {
			employeeExpenseResponseItem.setNotes(employeeExpense.getNotes());
		}
		User user = new User();
		user.setUserId(employeeExpense.getWasherId());
		user.selectOne(conn);
		employeeExpenseResponseItem.setWasherName(user.getFirstName() + " " + user.getLastName());
		return employeeExpenseResponseItem;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		SessionUser sessionUser = AppUtils.getSessionUser(request);
		try {
			String jsonString = super.makeJsonString(request);
			AnsiURL url = new AnsiURL(request, REALM, new String[] { ACTION_IS_ADD });
			SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_WRITE);

			Connection conn = null;

			try {
				conn = AppUtils.getDBCPConn();
				EmployeeExpenseRequest employeeExpenseRequest = new EmployeeExpenseRequest();

				if (!StringUtils.isBlank(url.getCommand()) && url.getCommand().equals(ACTION_IS_ADD)) {
					processAddRequest(conn, response, sessionUser, employeeExpenseRequest);
				} else if (!StringUtils.isBlank(url.getCommand()) && url.getCommand().equals(ACTION_IS_UPDATE)) {
					processUpdateRequest(conn, response, url.getId(), sessionUser, employeeExpenseRequest);
				} else {
					throw new RecordNotFoundException();
				}

			} catch (RecordNotFoundException recordNotFoundEx) {
				super.sendNotFound(response);
			} finally {
				AppUtils.closeQuiet(conn);
			}

		} catch (ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendNotAllowed(response);
		} catch (Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		}

	}

	private void processUpdateRequest(Connection conn, HttpServletResponse response, Integer id,
			SessionUser sessionUser, EmployeeExpenseRequest employeeExpenseRequest) throws Exception {
		EmployeeExpense employeeExpense = null;
		ResponseCode responseCode = null;
		WebMessages webMessages = validateUpdate(conn, employeeExpenseRequest);
		if (webMessages.isEmpty()) {
			webMessages = validateFormat(conn, employeeExpenseRequest);
		}
		if (webMessages.isEmpty()) {
			try {
				EmployeeExpense key = new EmployeeExpense();
				key.setExpenseId(id);
				employeeExpense = doUpdate(conn, key, employeeExpenseRequest, sessionUser);
				String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
				responseCode = ResponseCode.SUCCESS;
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
				conn.commit();
			} catch (RecordNotFoundException e) {
				super.sendNotFound(response);
				conn.rollback();
			} catch (Exception e) {
				responseCode = ResponseCode.SYSTEM_FAILURE;
				AppUtils.logException(e);
				String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
				conn.rollback();
			}
		} else {
			responseCode = ResponseCode.EDIT_FAILURE;
		}

		EmployeeExpenseResponse employeeEspenseResponse = new EmployeeExpenseResponse();
		if (employeeExpense != null) {
			employeeEspenseResponse = new EmployeeExpenseResponse(conn, employeeExpense);
		}
		if (!webMessages.isEmpty()) {
			employeeEspenseResponse.setWebMessages(webMessages);
		}

		super.sendResponse(conn, response, responseCode, employeeEspenseResponse);

	}

	private EmployeeExpense doUpdate(Connection conn, EmployeeExpense key,
			EmployeeExpenseRequest employeeExpenseRequest, SessionUser sessionUser) throws Exception {
		Date today = new Date();
		EmployeeExpense employeeExpense = new EmployeeExpense();
		employeeExpense.setExpenseId(key.getExpenseId());
		employeeExpense.selectOne(conn);
		makeEmployeeExpense(employeeExpense, employeeExpenseRequest, sessionUser, today);

		try {
			employeeExpense.update(conn, key);
			conn.commit();
		} catch (SQLException e) {
			AppUtils.logException(e);
			throw e;
		}
		return employeeExpense;
	}

	private WebMessages validateUpdate(Connection conn, EmployeeExpenseRequest employeeExpenseRequest)
			throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredUpdateFields(employeeExpenseRequest);
		if (!missingFields.isEmpty()) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for (String field : missingFields) {
				webMessages.addMessage(field, messageText);
			}
		}
		if (webMessages.isEmpty()) {
			webMessages = employeeExpenseRequest.validateUpdate(conn);
		}
		return webMessages;
	}

	private void processAddRequest(Connection conn, HttpServletResponse response, SessionUser sessionUser,
			EmployeeExpenseRequest employeeExpenseRequest) throws Exception {
		EmployeeExpense employeeExpense = new EmployeeExpense();
		ResponseCode responseCode = null;

		WebMessages webMessages = validateAdd(conn, employeeExpenseRequest);

		if (webMessages.isEmpty()) {
			webMessages = validateFormat(conn, employeeExpenseRequest);
		}

		if (webMessages.isEmpty()) {
			try {
				employeeExpense = doAdd(conn, employeeExpenseRequest, sessionUser);
				String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
				responseCode = ResponseCode.SUCCESS;
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
				conn.commit();
			} catch (DuplicateEntryException e) {
				String messageText = AppUtils.getMessageText(conn, MessageKey.DUPLICATE_ENTRY, "Record already Exists");
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
				responseCode = ResponseCode.EDIT_FAILURE;
			} catch (Exception e) {
				responseCode = ResponseCode.SYSTEM_FAILURE;
				AppUtils.logException(e);
				String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
			}
		} else {
			responseCode = ResponseCode.EDIT_FAILURE;
		}

	}

	private EmployeeExpense doAdd(Connection conn, EmployeeExpenseRequest employeeExpenseRequest,
			SessionUser sessionUser) throws Exception {
		Date today = new Date();
		EmployeeExpense employeeExpense = new EmployeeExpense();
		makeEmployeeExpense(employeeExpense, employeeExpenseRequest, sessionUser, today);
		employeeExpense.setAddedBy(sessionUser.getUserId());
		employeeExpense.setAddedDate(today);

		try {
			Integer expenseId = employeeExpense.insertWithKey(conn);
			employeeExpense.setExpenseId(expenseId);
		} catch (SQLException e) {
			if (e.getMessage().contains("duplicate key")) {
				throw new DuplicateEntryException();
			} else {
				AppUtils.logException(e);
				throw e;
			}
		}
		return employeeExpense;

	}

	private EmployeeExpense makeEmployeeExpense(EmployeeExpense employeeExpense,
			EmployeeExpenseRequest employeeExpenseRequest, SessionUser sessionUser, Date today) {
		if (!StringUtils.isBlank(employeeExpenseRequest.getNotes())) {
			employeeExpense.setNotes(employeeExpenseRequest.getNotes());
		}
		employeeExpense.setAmount(employeeExpenseRequest.getAmount());
		employeeExpense.setExpenseType(employeeExpenseRequest.getExpenseType());
		employeeExpense.setWasherId(employeeExpenseRequest.getWasherId());
		employeeExpense.setWorkDate(employeeExpenseRequest.getDate());

		return employeeExpense;
	}

	private WebMessages validateFormat(Connection conn, EmployeeExpenseRequest employeeExpenseRequest)
			throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> badFormatFields = super.validateFormat(employeeExpenseRequest);
		if (!badFormatFields.isEmpty()) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.INVALID_DATA, "Invalid Format");
			for (String field : badFormatFields) {
				webMessages.addMessage(field, messageText);
			}
		}
		return webMessages;
	}

	private WebMessages validateAdd(Connection conn, EmployeeExpenseRequest employeeExpenseRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredAddFields(employeeExpenseRequest);
		if (!missingFields.isEmpty()) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for (String field : missingFields) {
				webMessages.addMessage(field, messageText);
			}
		}
		if (webMessages.isEmpty()) {
			webMessages = employeeExpenseRequest.validateAdd(conn);
		}
		return webMessages;
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendNotAllowed(response);
	}

}
