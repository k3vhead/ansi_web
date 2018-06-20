package com.ansi.scilla.web.quote.common;

import java.sql.Connection;

import com.ansi.scilla.web.common.response.WebMessages;

public interface FieldValidator {
	public void validate(Connection conn, String fieldName, Object value, WebMessages webMessages ) throws Exception;

}
