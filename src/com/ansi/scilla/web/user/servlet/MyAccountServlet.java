package com.ansi.scilla.web.user.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.InvalidLoginException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.user.request.MyAccountRequest;
import com.ansi.scilla.web.user.response.MyAccountResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class MyAccountServlet extends AbstractServlet {

	
	private static final long serialVersionUID = 1L;

	public static final String REALM = "user";

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {
			SessionData sessionData = AppUtils.validateSession(request);
			SessionUser user = sessionData.getUser();
			doGetWork(request, response, user);
		} catch (TimeoutException e) {
			super.sendForbidden(response);
		}
	}

	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SessionData sessionData = null;
		try {
			sessionData = AppUtils.validateSession(request);
			SessionUser user = sessionData.getUser();
			doPostWork(request, response, user);
		} catch (TimeoutException e) {
			super.sendForbidden(response);
		}
		
	}


	private void doGetWork(HttpServletRequest request, HttpServletResponse response, SessionUser user) throws ServletException {
			Connection conn = null;
			MyAccountResponse myAccountResponse = new MyAccountResponse();
	//		WebMessages messages = new WebMessages();
			try {
				conn = AppUtils.getDBCPConn();
				myAccountResponse = new MyAccountResponse(conn, user.getUserId());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, myAccountResponse);
			} catch(RecordNotFoundException e) {
				super.sendNotFound(response);
			} catch(ResourceNotFoundException e) {
				super.sendNotFound(response);
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
	
		}


	private void doPostWork(HttpServletRequest request, HttpServletResponse response, SessionUser user) throws ServletException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			MyAccountRequest myAccountRequest = new MyAccountRequest();
			AppUtils.json2object(jsonString, myAccountRequest);
			doUpdate(conn, response, user, myAccountRequest);
		} catch(RecordNotFoundException e) {
			AppUtils.rollbackQuiet(conn);
			super.sendNotFound(response);
		} catch(ResourceNotFoundException e) {
			AppUtils.rollbackQuiet(conn);
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.rollbackQuiet(conn);
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}		
	}


	private void doUpdate(Connection conn, HttpServletResponse response, SessionUser sessionUser, MyAccountRequest myAccountRequest) throws Exception {
		MyAccountResponse myAccountResponse = new MyAccountResponse();
		WebMessages webMessages = new WebMessages();
		
		// check for required fields:
		webMessages.addRequiredFieldMessages(conn, super.validateRequiredUpdateFields(myAccountRequest));
		// if we have a new password, make sure we have a confirm and they match
		if ( ! StringUtils.isBlank(myAccountRequest.getNewPassword()) || ! StringUtils.isBlank(myAccountRequest.getConfirmPassword())) {
			// we have at least one, so mark the missing one as required
			if ( StringUtils.isBlank(myAccountRequest.getNewPassword())) {
				webMessages.addMessage(MyAccountRequest.NEW_PASSWORD, "Required Entry");
			} else if ( StringUtils.isBlank(myAccountRequest.getConfirmPassword())) {
				webMessages.addMessage(MyAccountRequest.CONFIRM_PASSWORD, "Required Entry");
			} else if ( ! myAccountRequest.getNewPassword().equals(myAccountRequest.getConfirmPassword())) {
				webMessages.addMessage(MyAccountRequest.NEW_PASSWORD, "Passwords Do Not Match");
			}
		}
		
		//validate password, if it's entered
		User user = null;
		if ( ! StringUtils.isBlank(myAccountRequest.getPassword())) {
			try {
				user = AppUtils.checkLogin(conn, sessionUser.getEmail(), myAccountRequest.getPassword());
			} catch ( RecordNotFoundException e ) {
				webMessages.addMessage(MyAccountRequest.PASSWORD, "Invalid User - Contact Support");
			} catch ( ExpiredLoginException | InvalidLoginException e) {
				webMessages.addMessage(MyAccountRequest.PASSWORD, "Invalid Password");
			}
		}
		
		// do update
		if ( webMessages.isEmpty()) {
//			user.setLastName(myAccountRequest.getLastName());
//			user.setFirstName(myAccountRequest.getFirstName());
//			user.setTitle(myAccountRequest.getTitle());
//			user.setEmail(myAccountRequest.getEmail());
//			user.setPhone(myAccountRequest.getPhone());
//			user.setAddress1(StringUtils.isBlank(myAccountRequest.getAddress1()) ? null : myAccountRequest.getAddress1()); 
//			user.setAddress2(StringUtils.isBlank(myAccountRequest.getAddress2()) ? null : myAccountRequest.getAddress2());
//			user.setCity(StringUtils.isBlank(myAccountRequest.getCity()) ? null : myAccountRequest.getCity()); 
//			user.setState(StringUtils.isBlank(myAccountRequest.getState()) ? null : myAccountRequest.getState());
//			user.setZip(StringUtils.isBlank(myAccountRequest.getZip()) ? null : myAccountRequest.getZip());			
			String encryptedPass = AppUtils.encryptPassword(myAccountRequest.getNewPassword(), sessionUser.getUserId());
			user.setPassword(encryptedPass);
			user.setUpdatedBy(sessionUser.getUserId());
			
			User key = new User();
			key.setUserId(sessionUser.getUserId());			
			user.update(conn, key);
			conn.commit();
		}

		ResponseCode responseCode = webMessages.isEmpty() ? ResponseCode.SUCCESS : ResponseCode.EDIT_FAILURE;
		myAccountResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, myAccountResponse);
	}

	



}