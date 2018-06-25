package com.ansi.scilla.web.common.fieldValidator;

import java.sql.Connection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.fieldValidator.FieldValidator;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class ManagerIdValidator extends ApplicationObject implements FieldValidator {

	private static final long serialVersionUID = 1L;
	
	Logger logger = LogManager.getLogger(this.getClass());

	@Override
	public void validate(Connection conn, String fieldName, Object value, WebMessages webMessages) throws Exception {
		if ( value == null ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			webMessages.addMessage(fieldName, messageText);
		} else {
			Integer id = (Integer)value;
			User user = new User();
			user.setUserId(id);
			try {
				user.selectOne(conn);
				logger.log(Level.DEBUG, "*** Make sure the manager has quote permissions ****");
				logger.log(Level.DEBUG, "*** Make sure the manager has quote permissions ****");
				logger.log(Level.DEBUG, "*** Make sure the manager has quote permissions ****");
				logger.log(Level.DEBUG, "*** Make sure the manager has quote permissions ****");
			} catch ( RecordNotFoundException e) {
				String messageText = AppUtils.getMessageText(conn, MessageKey.INVALID_DATA, "Invalid Value");
				webMessages.addMessage(fieldName, messageText);
			}
		}
		
	}

}
