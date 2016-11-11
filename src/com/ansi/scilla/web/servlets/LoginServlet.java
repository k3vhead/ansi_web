package com.ansi.scilla.web.servlets;

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
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.InvalidLoginException;
import com.ansi.scilla.web.request.LoginRequest;
import com.ansi.scilla.web.response.login.LoginResponse;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;

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
			logger.debug("Logging in");
			String jsonString = super.makeJsonString(request);
			logger.debug("Got json:");
			logger.debug(jsonString);
			LoginResponse loginResponse = new LoginResponse();
			try {
				loginResponse = doWork(conn, jsonString);
				logger.debug("login success");
				SessionData sessionData = new SessionData(loginResponse);
				HttpSession session = request.getSession();
				session.setAttribute(SessionData.KEY, sessionData);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, loginResponse);
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
		
		super.doPost(request, response);
	}

	protected LoginResponse doWork(Connection conn, String jsonString) throws ExpiredLoginException, InvalidLoginException, Exception {
		LoginRequest loginRequest = new LoginRequest(jsonString);
		User user = AppUtils.checkLogin(conn, loginRequest.getUserid(), loginRequest.getPassword());
		SessionUser sessionUser = new SessionUser(user);
		LoginResponse loginResponse = new LoginResponse(conn, sessionUser);
		return loginResponse;
		
	}

	
}
