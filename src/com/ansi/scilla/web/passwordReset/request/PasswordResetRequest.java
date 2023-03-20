package com.ansi.scilla.web.passwordReset.request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.PasswordReset;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.response.WebMessages;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class PasswordResetRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	public static final String RESET_STEP = "resetStep";
	public static final String RESET_USER_ID = "resetUserId";
	public static final String RESET_CODE = "resetCode";
	public static final String RESET_PASSWORD = "resetPassword";
	public static final String RESET_PASSWORD2 = "resetPassword2";
	
	
	private String resetStep;
	private String resetUserId;
	private String resetCode;
	private String resetPassword;
	private String resetPassword2;
	
	public String getResetStep() {
		return resetStep;
	}
	public void setResetStep(String resetStep) {
		this.resetStep = resetStep;
	}
	public String getResetUserId() {
		return resetUserId;
	}
	public void setResetUserId(String resetUserId) {
		this.resetUserId = resetUserId;
	}
	public String getResetCode() {
		return resetCode;
	}
	public void setResetCode(String resetCode) {
		this.resetCode = resetCode;
	}
	public String getResetPassword() {
		return resetPassword;
	}
	public void setResetPassword(String resetPassword) {
		this.resetPassword = resetPassword;
	}
	public String getResetPassword2() {
		return resetPassword2;
	}
	public void setResetPassword2(String resetPassword2) {
		this.resetPassword2 = resetPassword2;
	}
	
	
	
	
	public WebMessages validateVerify(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		if ( StringUtils.isBlank(this.resetUserId)) {
			webMessages.addMessage(RESET_USER_ID, "Required Entry");
		}
		if ( webMessages.isEmpty() ) {
			User user = new User();
			user.setEmail(this.resetUserId);
			try {
				user.selectOne(conn);
				if ( user.getStatus().intValue() != User.STATUS_IS_GOOD ) {
					webMessages.addMessage(RESET_USER_ID, "Login is locked; contact support");
				}
			} catch ( RecordNotFoundException e ) {
				webMessages.addMessage(RESET_USER_ID, "Invalid Login");
			}
			
		}
		
		return webMessages;
	}
	
	
	
	public WebMessages validateConfirm(Connection conn) throws Exception {
		WebMessages webMessages = validateVerify(conn);
		
		if ( StringUtils.isBlank(this.resetCode)) {
			webMessages.addMessage(RESET_CODE, "Required Entry");
		}

		if ( webMessages.isEmpty() ) {
			try {
				PasswordReset passwordReset = makePasswordReset(conn);
				if ( ! this.resetCode.equals(passwordReset.getSessionKey())) {
					webMessages.addMessage(RESET_CODE, "Invalid Code");
				}
			} catch ( RecordNotFoundException e ) {
				webMessages.addMessage(RESET_CODE, "Invalid Code");
			}
			
		}
		
		return webMessages;
	}
	
	
	public WebMessages validateGo(Connection conn) throws Exception {
		WebMessages webMessages = validateVerify(conn);
		webMessages.addAllMessages(validateConfirm(conn));
		
		if ( StringUtils.isBlank(this.resetPassword) ) {
			webMessages.addMessage(RESET_PASSWORD, "Required Entry");
		}
		if ( StringUtils.isBlank(this.resetPassword2)) {
			webMessages.addMessage(RESET_PASSWORD2, "Required Entry");
		}
		if ( webMessages.isEmpty() ) {
			if ( ! resetPassword.equals(resetPassword2) ) {
				webMessages.addMessage(RESET_PASSWORD, "Passwords do not match");
			}
		}
		return webMessages;
	}
	
	
	
	
	private PasswordReset makePasswordReset(Connection conn) throws RecordNotFoundException, Exception {
		PasswordReset passwordReset = new PasswordReset();
		Calendar expiration = Calendar.getInstance(new AnsiTime());
		expiration.add(Calendar.MINUTE, -31);
		java.sql.Date expirationDate = new java.sql.Date(expiration.getTime().getTime());
		
		// get the newest record that has not been used for the entered user id.
		String sql = "select top(1) * \n"
				+ "from password_reset where email=? and added_date > ? and update_date is null\n"
				+ "order by added_date desc";
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, this.resetUserId);
		ps.setDate(2, expirationDate);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			ResultSetMetaData rsmd = rs.getMetaData();
			passwordReset = (PasswordReset)passwordReset.rs2Object(rsmd, rs);
		} else {
			throw new RecordNotFoundException();
		}
		return passwordReset;
	}
	
	
	

}
