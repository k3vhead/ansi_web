package com.ansi.scilla.web.common.fieldValidator;

import java.sql.Connection;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.response.WebMessages;

/**
 * All fields must be validated. For the fields that are free-form entry and not required, use this place holder
 * validation just so there will be a validator to call.
 * @author dclewis
 *
 */
public class AbstractPlaceHolder extends ApplicationObject implements FieldValidator {

	private static final long serialVersionUID = 1L;

	@Override
	public void validate(Connection conn, String fieldName, Object value, WebMessages webMessages) throws Exception {
		
	}

}
