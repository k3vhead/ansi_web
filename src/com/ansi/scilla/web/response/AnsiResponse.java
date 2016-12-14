package com.ansi.scilla.web.response;

import java.sql.Connection;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ansi.scilla.common.db.Message;
import com.ansi.scilla.common.utils.PropertyNames;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.ApplicationWebObject;
import com.ansi.scilla.web.common.ResponseCode;
import com.thewebthing.commons.db2.RecordNotFoundException;

/**
 * Every servlet response is wrapped in an envelope with a simple header.
 * The header contains:
 * return code: one of the standard numerics that
 * 		indicates whether the request was successfully executed.
 * response code: a function-specific indication of success / failure / reason
 * response message: a plain language explanation of the response code
 * session expiration date: when the current user session expires.
 * 
 * data contains the function-specific payload 
 * 
 * @author dcl
 *
 */
public class AnsiResponse extends ApplicationWebObject {

	private static final long serialVersionUID = 1L;

	private ResponseHeader responseHeader;
	private MessageResponse data;

	public AnsiResponse() {
		super();
	}

	public AnsiResponse(Connection conn, ResponseCode responseCode, MessageResponse data) throws Exception {
		super();
		try {
			this.responseHeader = new ResponseHeader(conn, responseCode);
		} catch ( RecordNotFoundException e) {
			Logger logger = AppUtils.getLogger();
			logger.error("Expected message not found: " + responseCode);
			this.responseHeader = new ResponseHeader();
			this.responseHeader.setResponseCode(responseCode.toString());
			this.responseHeader.setResponseMessage("Message not found");
		}
		this.data = data;
	}

	public ResponseHeader getResponseHeader() {
		return responseHeader;
	}

	public void setResponseHeader(ResponseHeader responseHeader) {
		this.responseHeader = responseHeader;
	}
	public MessageResponse getData() {
		return data;
	}

	public void setData(MessageResponse data) {
		this.data = data;
	}








	public class ResponseHeader extends ApplicationWebObject {
		private static final long serialVersionUID = 1L;
		public String responseCode;
		public String responseMessage;
		public Calendar sessionExpirationTime;
		
		public ResponseHeader() {
			super();
		}
		public ResponseHeader(Connection conn, ResponseCode responseCode) throws Exception {
			this();
			makeMessage(conn, responseCode);
			makeSessionExpiration();
		}
		public String getResponseCode() {
			return responseCode;
		}
		public void setResponseCode(String responseCode) {
			this.responseCode = responseCode;
		}
		public String getResponseMessage() {
			return responseMessage;
		}
		public void setResponseMessage(String responseMessage) {
			this.responseMessage = responseMessage;
		}
		public Calendar getSessionExpirationTime() {
			return sessionExpirationTime;
		}
		public void setSessionExpirationTime(Calendar sessionExpirationTime) {
			this.sessionExpirationTime = sessionExpirationTime;
		}
		private void makeMessage(Connection conn, ResponseCode responseCode) throws Exception {
			Logger logger = AppUtils.getLogger();
			Message message = new Message();
			message.setKey(responseCode.toString());
			logger.debug(message);
			message.selectOne(conn);			
			this.responseCode = message.getKey();
			this.responseMessage = message.getMessage();
			
		}
		private void makeSessionExpiration() {
			String property = AppUtils.getProperty(PropertyNames.SESSION_EXPIRATION_TIME);
			if ( StringUtils.isBlank(property)) {
				property="30";
			}
			Calendar now = Calendar.getInstance();
			now.roll(Calendar.MINUTE, Integer.valueOf(property));
		}
		
		
	}
}
