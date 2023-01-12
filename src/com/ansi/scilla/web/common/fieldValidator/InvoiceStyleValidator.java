package com.ansi.scilla.web.common.fieldValidator;

import java.sql.Connection;

import com.ansi.scilla.common.invoice.InvoiceStyle;
import com.ansi.scilla.web.common.response.WebMessages;

public abstract class InvoiceStyleValidator implements FieldValidator {


	@Override
	public void validate(Connection conn, String fieldName, Object value, WebMessages webMessages) throws Exception {
		if ( value == null ) {			
			webMessages.addMessage(fieldName, "Invoice Style is missing");
		} else {
			InvoiceStyle key = InvoiceStyle.valueOf((String)value);
			if ( key == null ) {
				webMessages.addMessage(fieldName, "Invoice Style is invalid");
			}
		}
	}
}
