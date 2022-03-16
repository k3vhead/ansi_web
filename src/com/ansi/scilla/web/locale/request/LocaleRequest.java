package com.ansi.scilla.web.locale.request;

import java.sql.Connection;

import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class LocaleRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String LOCALE_ID = "localeId";
	public static final String NAME = "name";
	public static final String STATE_NAME = "stateName";
	public static final String ABBREVIATION = "abbreviation";
	public static final String LOCALE_TYPE_ID = "localeTypeId";
	public static final String PARENT_ID = "parentId";
		
	private Integer localeId;
	private String name;
	private String stateName;
	private String abbreviation;
	private String localeTypeId;
	private Integer parentId;
	
	public Integer getLocaleId() {
		return localeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public String getAbbreviation() {
		return abbreviation;
	}
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
	public void setLocaleId(Integer localeId) {
		this.localeId = localeId;
	}
	public String getLocaleTypeId() {
		return localeTypeId;
	}
	public void setLocaleTypeId(String localeTypeId) {
		this.localeTypeId = localeTypeId;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
	public WebMessages validateAdd(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateString(webMessages, NAME, this.name, true);
		RequestValidator.validateState(webMessages, STATE_NAME, this.stateName, true);
		RequestValidator.validateLocaleType(webMessages, LOCALE_TYPE_ID, this.localeTypeId, true);
		RequestValidator.validateString(webMessages, ABBREVIATION, this.abbreviation, false);
		RequestValidator.validateId(conn, webMessages, "locale", Locale.LOCALE_ID, PARENT_ID, this.parentId, false);
		if ( isDuplicate(conn)) {
			webMessages.addMessage(NAME, "Duplicate Entry");
		}

		return webMessages;
	}
	
	public WebMessages validateUpdate(Connection conn, Integer localeId) throws Exception {
		WebMessages webMessages = new WebMessages();
	
		RequestValidator.validateId(conn, webMessages, "locale", Locale.LOCALE_ID, "localeId", localeId, true);
		RequestValidator.validateString(webMessages, NAME, this.name, true);
		RequestValidator.validateState(webMessages, STATE_NAME, this.stateName, true);
		RequestValidator.validateLocaleType(webMessages, LOCALE_TYPE_ID, this.localeTypeId, true);
		RequestValidator.validateString(webMessages, ABBREVIATION, this.abbreviation, false);
		RequestValidator.validateId(conn, webMessages, "locale", Locale.LOCALE_ID, PARENT_ID, this.parentId, false);
		
		return webMessages;
	}
	
	
	private boolean isDuplicate(Connection conn) throws Exception {
		boolean dupeFound = false;
		Locale locale = new Locale();
		locale.setName(this.name);
		locale.setStateName(this.stateName);
		locale.setLocaleTypeId(this.localeTypeId);
		locale.setAbbreviation(this.abbreviation);
		
		try {
			locale.selectOne(conn);
			dupeFound = true;
		} catch ( RecordNotFoundException e) {
			dupeFound = false;
		}
		return dupeFound;
	}
	
}
