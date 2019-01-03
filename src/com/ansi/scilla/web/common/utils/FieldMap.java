package com.ansi.scilla.web.common.utils;

import com.ansi.scilla.common.ApplicationObject;

/**
 * Maps json field to/from database field; defines format and validation for standard CRUD servlet I/O
 * 
 * @author dclewis
 *
 */
public class FieldMap extends ApplicationObject {

	private static final long serialVersionUID = 1L;


	public String jsonField;
	public String dbField;
	public JsonFieldFormat format;
	
	public FieldMap(String formField, String dbField, JsonFieldFormat format) {
		super();
		this.jsonField = formField;
		this.dbField = dbField;
		this.format = format;
	}
	
	
}