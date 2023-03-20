package com.ansi.scilla.web.passwordReset.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.PasswordReset;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.utils.email.AnsiEmailException;
import com.ansi.scilla.common.utils.email.EmailUtils;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.passwordReset.common.ResetStep;
import com.ansi.scilla.web.passwordReset.request.PasswordResetRequest;
import com.ansi.scilla.web.passwordReset.response.PasswordResetResponse;
import com.ansi.scilla.web.payment.response.PaymentResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class PasswordResetServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			try{
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				String jsonString = super.makeJsonString(request);
				logger.log(Level.DEBUG, jsonString);
				PasswordResetRequest resetRequest = new PasswordResetRequest();
				AppUtils.json2object(jsonString, resetRequest);
				logger.log(Level.DEBUG, resetRequest);
				ResetStep resetStep = StringUtils.isBlank(resetRequest.getResetStep()) ? ResetStep.VERIFY : ResetStep.valueOf(resetRequest.getResetStep());
				logger.log(Level.DEBUG, "Reset step: " + resetStep);
				switch ( resetStep ) {
				case CONFIRM:
					processConfirm(conn, response, resetRequest);
					break;
				case GO:
					processGo(conn, response, resetRequest);
					break;
				case VERIFY:
					processVerify(conn, response, resetRequest);
					break;
				default:
					processInvalid(conn, response, resetRequest);
					break;
				
				}
				
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				PaymentResponse data = new PaymentResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (ExpiredLoginException e) {
				super.sendForbidden(response);			
			}
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}	
	}

	
	


	private void processConfirm(Connection conn, HttpServletResponse response, PasswordResetRequest resetRequest) throws Exception {
		logger.log(Level.DEBUG, "ProcessConfirm");
		WebMessages webMessages = resetRequest.validateConfirm(conn);
		logger.log(Level.DEBUG, "Confirm webMessages: " + webMessages.isEmpty());
		PasswordResetResponse data = new PasswordResetResponse();	
		ResponseCode responseCode = webMessages.isEmpty() ? ResponseCode.SUCCESS : ResponseCode.EDIT_FAILURE;
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, data);	
	}


	private void processGo(Connection conn, HttpServletResponse response, PasswordResetRequest resetRequest) throws Exception {
		WebMessages webMessages = resetRequest.validateGo(conn);		

		Date today = Calendar.getInstance(new AnsiTime()).getTime();
		
		if ( webMessages.isEmpty() ) {
			User user = new User();
			user.setEmail(resetRequest.getResetUserId());
			user.selectOne(conn);
			
			User userKey = new User();
			userKey.setUserId(user.getUserId());
			
			PasswordReset passwordReset = new PasswordReset();
			passwordReset.setEmail(resetRequest.getResetUserId());
			passwordReset.setSessionKey(resetRequest.getResetCode());
			passwordReset.selectOne(conn);

			PasswordReset resetKey = new PasswordReset();
			resetKey.setEmail(resetRequest.getResetUserId());
			resetKey.setSessionKey(resetRequest.getResetCode());
			
			passwordReset.setUpdateDate(today);
			passwordReset.setUpdatedDate(today);
			passwordReset.setUpdatedBy(user.getUserId());
			passwordReset.update(conn, resetKey);
			
			
			String encryptedPass = AppUtils.encryptPassword(resetRequest.getResetPassword(), user.getUserId());
			user.setPassword(encryptedPass);
			user.setUpdatedBy(user.getUserId());
			user.setUpdatedDate(today);
			user.update(conn, userKey);
			
			conn.commit();
			
		}
		
		PasswordResetResponse data = new PasswordResetResponse();	
		data.setWebMessages(webMessages);
		ResponseCode responseCode = webMessages.isEmpty() ? ResponseCode.SUCCESS : ResponseCode.EDIT_FAILURE;
		super.sendResponse(conn, response, responseCode, data);	
	}


	private void processVerify(Connection conn, HttpServletResponse response, PasswordResetRequest resetRequest) throws Exception {
		WebMessages webMessages = resetRequest.validateVerify(conn);
		PasswordResetResponse data = new PasswordResetResponse();
		if ( webMessages.isEmpty() ) {
			String sessionKey = AppUtils.makeSessionKey();
			User user = makeResetRecord(conn, resetRequest, sessionKey);
			conn.commit();
			try {
				sendEmail(conn, user, sessionKey);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
			} catch ( AnsiEmailException e ) {
				webMessages.addMessage(PasswordResetRequest.RESET_USER_ID, "Error Sending Confirmation code; contact Support");
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			}
		} else {
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
		}
		
	}


	private void processInvalid(Connection conn, HttpServletResponse response, PasswordResetRequest resetRequest) {
		// TODO Auto-generated method stub
		
	}





	private User makeResetRecord(Connection conn, PasswordResetRequest resetRequest, String sessionKey) throws Exception {
		User user = new User();
		user.setEmail(resetRequest.getResetUserId());
		user.selectOne(conn);  // we've already verified the user id; so, if something bad happens here, it's bad
		logger.log(Level.DEBUG, "Session key: " + sessionKey);
		
		Date today = Calendar.getInstance(new AnsiTime()).getTime();
		PasswordReset passwordReset = new PasswordReset();
		passwordReset.setAddedBy(user.getUserId());
		passwordReset.setAddedDate(today);
		passwordReset.setEmail(resetRequest.getResetUserId());
		passwordReset.setSessionKey(sessionKey);
		passwordReset.setUpdatedDate(today);
		passwordReset.setUpdatedBy(user.getUserId());
		passwordReset.setCreateDate(today);
		passwordReset.insertWithNoKey(conn);
		
		return user;
		
	}





	private void sendEmail(Connection conn, User user, String sessionKey) throws UnsupportedEncodingException, AnsiEmailException  {
		String subject = "ANSI Password Reset";
		String message = user.getFirstName() + " " + user.getLastName() + " - Your reset code is: " + sessionKey;
		List<InternetAddress> sendToList = Arrays.asList(new InternetAddress[] {
				new InternetAddress(user.getEmail(),user.getFirstName() + " " + user.getLastName())
		});
		try {
			EmailUtils.sendSimpleEmailTLS(conn, subject, message, sendToList);
		} catch ( Exception e) {
			throw new AnsiEmailException(e);
		}
	}


	
}
