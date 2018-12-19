package com.ansi.scilla.web.user.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.DivisionUser;
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
import com.ansi.scilla.web.user.request.DivisionUserRequest;
import com.ansi.scilla.web.user.response.DivisionUserResponse;
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
			SessionData sessionData = AppUtils.validateSession(request, Permission.USER_ADMIN_READ);
			SessionUser sessionUser = sessionData.getUser();
			doGetWork(request, response, sessionUser);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} 
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			SessionData sessionData = AppUtils.validateSession(request, Permission.USER_ADMIN_WRITE);
			SessionUser sessionUser = sessionData.getUser();
			doPostWork(request, response, sessionUser);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} 
	}
	
	private void doGetWork(HttpServletRequest request, HttpServletResponse response, SessionUser sessionUser) throws ServletException {
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
				userResponse = new DivisionUserResponse(conn, url.getId(), sessionUser.getUserId());
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
	
	private void doPostWork(HttpServletRequest request, HttpServletResponse response, SessionUser sessionUser) throws ServletException, UnsupportedEncodingException, IOException {
		Connection conn = null;
		DivisionUserResponse userResponse = new DivisionUserResponse();
		String jsonString = super.makeJsonString(request);
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
				ansiUser.selectOne(conn); //throws RecordNotFoundException
				DivisionUserRequest userRequest = new DivisionUserRequest();
				AppUtils.json2object(jsonString, userRequest);
				doUpdate(conn, sessionUser.getUserId(), userRequest.getDivisionId(), userRequest.isActive());
				userResponse = new DivisionUserResponse(conn, url.getId(), sessionUser.getUserId());
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

	private void doUpdate(Connection conn, Integer userId, Integer divisionId, boolean active) throws Exception {
		DivisionUser divUser = new DivisionUser();
		divUser.setUserId(userId);
		divUser.setDivisionId(divisionId);
		

		if(active) {
			divUser.setTitleId(17);
			divUser.setAddedBy(userId);
			divUser.setUpdatedBy(userId);
			divUser.insertWithNoKey(conn);
		} else if(!active) {
			divUser.delete(conn);
		}


	}
	
}



