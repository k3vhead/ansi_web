package com.ansi.scilla.web.user.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.User;
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
import com.thewebthing.commons.db2.RecordNotFoundException;

public class DivisionUserServlet extends AbstractServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			AppUtils.validateSession(request);
			//doGetWork(request, response);
		} catch (TimeoutException e) {
			super.sendForbidden(response);
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
			//url = new AnsiURL(request, REALM, new String[] {ACTION_IS_ADD});
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
}



