package com.ansi.scilla.web.user.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.DivisionUser;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.MissingRequiredDataException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.user.request.DivisionUserRequest;
import com.ansi.scilla.web.user.response.DivisionUserListResponse;
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
			//AnsiURL url = new AnsiURL(request, "divisionuser", new String[] { ACTION_IS_LIST });
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
			String[] str = new String[] { ACTION_IS_LIST };
			AnsiURL url = new AnsiURL(request, REALM, str);	
			
	
			if( url.getId() == null && StringUtils.isBlank(url.getCommand())) {
				logger.log(Level.DEBUG, "divisionUser servlet 43");
				throw new ResourceNotFoundException();
			} else if (url.getId() != null) {
				logger.log(Level.DEBUG, "divisionUser servlet 46");
				User ansiUser = new User();
				ansiUser.setUserId(url.getId());
				ansiUser.selectOne(conn);
				userResponse = new DivisionUserResponse(conn, url.getId(), sessionUser.getUserId());//urlId is id making changes towards, sessionUser is the person doing changes
				logger.log(Level.DEBUG, "user servlet 55");
				messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
				userResponse.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, userResponse);
			} else if(! StringUtils.isBlank(url.getCommand()) && url.getCommand().equals(ACTION_IS_LIST)) {
				DivisionUserListResponse userListResponse = new DivisionUserListResponse();
				userListResponse = new DivisionUserListResponse(conn, sessionUser.getUserId()); 
				logger.log(Level.DEBUG, "user servlet 55");
				messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
				userListResponse.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, userListResponse);
			} else {
				// according to the URI parsing, this shouldn't happen, but it gives me warm fuzzies
				throw new ResourceNotFoundException();
			}
			
//			logger.log(Level.DEBUG, "user servlet 55");
//			messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
//			userResponse.setWebMessages(messages);
//			super.sendResponse(conn, response, ResponseCode.SUCCESS, userResponse);
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
				try {
					doUpdate(conn, sessionUser.getUserId(), url.getId(), userRequest.getDivisionId(), userRequest.isActive(), response);
					userResponse = new DivisionUserResponse(conn, url.getId(), sessionUser.getUserId());
				} catch (MissingRequiredDataException e) {
					messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid System State. Reload and try again");
					userResponse.setWebMessages(messages);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, userResponse);
				}
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
		} catch (NotAllowedException e) {
			super.sendForbidden(response);
		} catch (Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	
	}

	private void doUpdate(Connection conn, Integer userId, Integer urlId, Integer divisionId, boolean active, HttpServletResponse response) throws Exception {
		DivisionUser divUser = new DivisionUser(); //person recieving the change
		divUser.setUserId(urlId);
		divUser.setDivisionId(divisionId);
		DivisionUser divChange = new DivisionUser(); //person doing the change
		divChange.setUserId(userId);
		divChange.setDivisionId(divisionId);
		try {
			divChange.selectOne(conn);
			if(active) {
				divUser.setTitleId(17);
				divUser.setAddedBy(userId);
				divUser.setUpdatedBy(userId);
				divUser.insertWithNoKey(conn);
			} else if(!active) {
				divUser.delete(conn);
				/*
				 * delete from report_subscription where user_id=? and division_id=?
				 */
				String sql = "delete from report_subscription where user_id=? and division_id=?";
				PreparedStatement ps = conn.prepareStatement(sql);
				logger.log(Level.DEBUG, sql);
				ps.setInt(1, userId);
				ps.setInt(2, divisionId);
				ps.executeUpdate();
				
			}
		} catch(RecordNotFoundException e) {
			// This happens when we try to delete a non-existent record
			// or when we try to add a record that already exists
			throw new MissingRequiredDataException();
		}


	}
	
}



