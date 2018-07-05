package com.ansi.scilla.web.common.fieldValidator;

import java.sql.Connection;

import com.ansi.scilla.web.common.response.WebMessages;
import com.thewebthing.commons.db2.RecordNotFoundException;

public abstract class BuildingTypeValidator extends AbstractCodeTableValidator implements FieldValidator {

	private static final long serialVersionUID = 1L;	

	public BuildingTypeValidator() {
		super("job", "building_type");
	}
	
	@Override
	public void validate(Connection conn, String fieldName, Object value, WebMessages webMessages) throws Exception {
		if ( value == null ) {
			webMessages.addMessage(fieldName, "Building Type is missing");
		} else {
			String leadType = (String)value;
			try {
				super.validate(conn, leadType);
			} catch ( RecordNotFoundException e) {
				webMessages.addMessage(fieldName, "Building Type is invalid");
			}
		}
		
	}
}
