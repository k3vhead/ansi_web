package com.ansi.scilla.web.common.fieldValidator;

import java.sql.Connection;

import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.thewebthing.commons.db2.RecordNotFoundException;

public abstract class AccountTypeValidator extends AbstractCodeTableValidator implements FieldValidator {

	private static final long serialVersionUID = 1L;	

	public AccountTypeValidator() {
		super("quote", "account_type");
	}
	
	@Override
	public void validate(Connection conn, String fieldName, Object value, WebMessages webMessages) throws Exception {
		if ( value == null ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			webMessages.addMessage(fieldName, messageText);
		} else {
			String leadType = (String)value;
			try {
				super.validate(conn, leadType);
			} catch ( RecordNotFoundException e) {
				String messageText = AppUtils.getMessageText(conn, MessageKey.INVALID_DATA, "Invalid Value");
				webMessages.addMessage(fieldName, messageText);
			}
		}
		
	}
}
