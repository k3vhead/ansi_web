package com.ansi.scilla.web.common.fieldValidator;

import java.sql.Connection;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.invoice.InvoiceGrouping;
import com.ansi.scilla.web.common.response.WebMessages;

public abstract class InvoiceGroupingValidator extends ApplicationObject implements FieldValidator {

	private static final long serialVersionUID = 1L;

	@Override
	public void validate(Connection conn, String fieldName, Object value, WebMessages webMessages) throws Exception {
		if ( value == null ) {
			webMessages.addMessage(fieldName, "Invoice Grouping is missing");
		} else {
			try {
				InvoiceGrouping.valueOf((String)value);
			} catch ( IllegalArgumentException e ) {
				webMessages.addMessage(fieldName, "Invoice Grouping is invalid");
			}
		}
	}

}
