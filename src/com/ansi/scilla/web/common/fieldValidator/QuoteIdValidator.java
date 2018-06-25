package com.ansi.scilla.web.common.fieldValidator;

import java.sql.Connection;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.web.common.fieldValidator.FieldValidator;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class QuoteIdValidator extends ApplicationObject implements FieldValidator {

	private static final long serialVersionUID = 1L;

	@Override
	public void validate(Connection conn, String fieldName, Object value, WebMessages webMessages) throws Exception {
		if ( value == null ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			webMessages.addMessage(fieldName, messageText);
		} else {
			Integer id = (Integer)value;
			Quote quote = new Quote();
			quote.setQuoteId(id);
			try {
				quote.selectOne(conn);
			} catch ( RecordNotFoundException e) {
				String messageText = AppUtils.getMessageText(conn, MessageKey.INVALID_DATA, "Invalid Value");
				webMessages.addMessage(fieldName, messageText);
			}
		}
		
	}

}
