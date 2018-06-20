package com.ansi.scilla.web.quote.common;

import java.sql.Connection;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Code;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class AccountTypeValidator extends ApplicationObject implements FieldValidator {

	private static final long serialVersionUID = 1L;
	private static String table = "quote";
	private static String field = "account_type";

	@Override
	public void validate(Connection conn, String fieldName, Object value, WebMessages webMessages) throws Exception {
		if ( value == null ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			webMessages.addMessage(fieldName, messageText);
		} else {
			String leadType = (String)value;
			Code code = new Code();
			code.setTableName(table);
			code.setFieldName(field);
			code.setValue(leadType);
			try {
				code.selectOne(conn);
			} catch ( RecordNotFoundException e) {
				String messageText = AppUtils.getMessageText(conn, MessageKey.INVALID_DATA, "Invalid Value");
				webMessages.addMessage(fieldName, messageText);
			}
		}
		
	}
}
