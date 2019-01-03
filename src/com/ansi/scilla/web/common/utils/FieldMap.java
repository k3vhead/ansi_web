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
	public Boolean updateField;
	
	/**
	 * 
	 * @param jsonField - key into the json node / field name from the HTML form
	 * @param dbField - field name in the database table
	 * @param format - Field format (for conversion purposes)
	 * @param updateField - "True" indicates field will be populated in the database; "False" indicates this is display only
	 */
	public FieldMap(String jsonField, String dbField, JsonFieldFormat format, Boolean updateField) {
		super();
		this.jsonField = jsonField;
		this.dbField = dbField;
		this.format = format;
		this.updateField = updateField;
	}
	
	
}