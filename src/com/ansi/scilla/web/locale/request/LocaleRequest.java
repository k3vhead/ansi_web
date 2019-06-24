package com.ansi.scilla.web.locale.request;

import java.sql.Connection;

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
		
		RequestValidator.validateId(conn, webMessages, LOCALE_ID, "localeId", "localeId", this.localeId, true);
		RequestValidator.validateString(webMessages, NAME, this.name, true);
		RequestValidator.validateString(webMessages, STATE_NAME, this.stateName, true);
		RequestValidator.validateString(webMessages, ABBREVIATION, this.abbreviation, true);
		RequestValidator.validateString(webMessages, LOCALE_TYPE_ID, this.localeTypeId, true);
		
		return webMessages;
	}
	
}
