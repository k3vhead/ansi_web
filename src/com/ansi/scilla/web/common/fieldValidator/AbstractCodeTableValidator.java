package com.ansi.scilla.web.common.fieldValidator;

import java.sql.Connection;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Code;
import com.thewebthing.commons.db2.RecordNotFoundException;

public abstract class AbstractCodeTableValidator extends ApplicationObject {
	
	private static final long serialVersionUID = 1L;

	private String table;
	private String field;
	public AbstractCodeTableValidator(String table, String field) {
		super();
		this.table = table;
		this.field = field;
	}
	
	protected void validate(Connection conn, String value) throws RecordNotFoundException, Exception {
		Code code = new Code();
		code.setTableName(table);
		code.setFieldName(field);
		code.setValue(value);
		code.selectOne(conn);
	}
}
