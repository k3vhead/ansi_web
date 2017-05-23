package com.ansi.scilla.web.servlets.login;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.InvalidLoginException;
import com.ansi.scilla.web.exceptions.MissingRequiredDataException;
import com.ansi.scilla.web.request.LoginRequest;
import com.ansi.scilla.web.response.login.LoginResponse;
import com.ansi.scilla.web.response.ticket.TicketReturnResponse;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.lang.StringUtils;

public class LoginServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)	
			throws ServletException, IOException {
		Logger logger = AppUtils.getLogger();
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			String jsonString = super.makeJsonString(request);
			LoginResponse loginResponse = new LoginResponse();
			try {
				loginResponse = doWork(conn, jsonString);
				SessionData sessionData = new SessionData(loginResponse);
				HttpSession session = request.getSession();
				session.setAttribute(SessionData.KEY, sessionData);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, loginResponse);
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				LoginResponse data = new LoginResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.INVALID_LOGIN, data);
			} catch ( MissingRequiredDataException e) {
				logger.debug("missing login data");
				super.sendResponse(conn, response,ResponseCode.INVALID_LOGIN, loginResponse);
			} catch ( ExpiredLoginException e) {
				logger.debug("login expired");
				super.sendResponse(conn, response,ResponseCode.EXPIRED_LOGIN, loginResponse);
			} catch ( InvalidLoginException e) {
				logger.debug("login invalid");
				super.sendResponse(conn, response,ResponseCode.INVALID_LOGIN, loginResponse);
			}
			
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	protected LoginResponse doWork(Connection conn, String jsonString) throws ExpiredLoginException, InvalidLoginException, Exception {
		LoginRequest loginRequest = new LoginRequest(jsonString);
		if ( StringUtils.isBlank(loginRequest.getPassword()) || StringUtils.isBlank(loginRequest.getUserid())) {
			throw new MissingRequiredDataException();
		}
		User user = AppUtils.checkLogin(conn, loginRequest.getUserid(), loginRequest.getPassword());
		SessionUser sessionUser = new SessionUser(user);
		LoginResponse loginResponse = new LoginResponse(conn, sessionUser);
		return loginResponse;
		
	}

	
}
