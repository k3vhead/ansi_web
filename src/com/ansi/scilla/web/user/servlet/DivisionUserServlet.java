package com.ansi.scilla.web.user.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.user.request.AnsiUserRequest;
import com.ansi.scilla.web.user.response.DivisionUserResponse;
import com.ansi.scilla.web.user.response.UserResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class DivisionUserServlet extends AbstractServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "divisionUser";
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			AppUtils.validateSession(request, Permission.USER_ADMIN_READ);
			doGetWork(request, response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} 
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AnsiURL url = null;
		Connection conn = null;
		
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			SessionData sessionData = AppUtils.validateSession(request, Permission.USER_ADMIN_WRITE);
			SessionUser sessionUser = sessionData.getUser();
			String jsonString = super.makeJsonString(request);
			url = new AnsiURL(request, REALM, new String[] {ACTION_IS_ADD});
			if ( url.getId() == null && StringUtils.isBlank(url.getCommand())) {
				super.sendNotFound(response);
			} else {
				AnsiUserRequest userRequest = new AnsiUserRequest();
				AppUtils.json2object(jsonString, userRequest);
				User user = new User();
				if ( url.getId() == null ) {
					//addUser(conn, request, response, user, userRequest, sessionUser);
				} else {
					user.setUserId(url.getId());
					user.selectOne(conn);
					//updateUser(conn, request, response, user, userRequest, sessionUser);
				}
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch (ResourceNotFoundException e) {
			super.sendNotAllowed(response);
		} catch ( RecordNotFoundException e ) {
			super.sendNotFound(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	private void doGetWork(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		Connection conn = null;
		DivisionUserResponse userResponse = new DivisionUserResponse();
		WebMessages messages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			String[] str = new String[0];
			AnsiURL url = new AnsiURL(request, REALM, str);	
			
	
			if( url.getId() == null && StringUtils.isBlank(url.getCommand())) {
				logger.log(Level.DEBUG, "divisionUser servlet 43");
				throw new ResourceNotFoundException();
			} else if (url.getId() != null) {
				logger.log(Level.DEBUG, "divisionUser servlet 46");
				User ansiUser = new User();
				ansiUser.setUserId(url.getId());
				ansiUser.selectOne(conn);
				userResponse = new DivisionUserResponse(conn, url.getId());
			} else {
				// according to the URI parsing, this shouldn't happen, but it gives me warm fuzzies
				throw new ResourceNotFoundException();
			}
			
			logger.log(Level.DEBUG, "user servlet 55");
			messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
			userResponse.setWebMessages(messages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, userResponse);
		} catch(RecordNotFoundException e) {
			logger.log(Level.DEBUG, "user servlet 60");
			super.sendNotFound(response);
		} catch(ResourceNotFoundException e) {
			logger.log(Level.DEBUG, "user servlet 63");
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	
	}
}



