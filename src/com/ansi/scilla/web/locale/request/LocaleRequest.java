package com.ansi.scilla.web.locale.request;

import java.sql.Connection;

import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class LocaleRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String LOCALE_ID = "localeId";
	public static final String NAME = "name";
	public static final String STATE_NAME = "stateName";
	public static final String ABBREVIATION = "abbreviation";
	public static final String LOCALE_TYPE_ID = "localeTypeId";
		
	private Integer localeId;
	private String name;
	private String stateName;
	private String abbreviation;
	private String localeTypeId;
	
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
	
	
	public WebMessages validateAdd(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateString(webMessages, NAME, this.name, true);
		RequestValidator.validateState(webMessages, STATE_NAME, this.stateName, true);
		RequestValidator.validateLocaleType(webMessages, LOCALE_TYPE_ID, this.localeTypeId, true);
		
		return webMessages;
	}
	
	public WebMessages validateUpdate(Connection conn, Integer localeId) throws Exception {
		WebMessages webMessages = validateAdd(conn);
		
		RequestValidator.validateId(conn, webMessages, "locale", Locale.LOCALE_ID, "localeId", localeId, true);
		RequestValidator.validateString(webMessages, ABBREVIATION, this.abbreviation, false);
		
		return webMessages;
	}
	
}
