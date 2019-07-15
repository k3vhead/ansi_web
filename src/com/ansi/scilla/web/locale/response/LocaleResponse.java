package com.ansi.scilla.web.locale.response;

import java.sql.Connection;

import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class LocaleResponse extends MessageResponse {

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
	
	public LocaleResponse() {
		super();
	}
	
	public LocaleResponse(Locale locale) {
		this();
		make(locale);		
	}

	public LocaleResponse(Connection conn, Integer localeId) throws RecordNotFoundException, Exception {
		this();
		Locale locale = new Locale();
		locale.setLocaleId(localeId);
		locale.selectOne(conn);
		make(locale);
		
	}

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

	private void make(Locale locale) {
		this.localeId = locale.getLocaleId();
		this.name = locale.getName();
		this.stateName = locale.getStateName();
		this.abbreviation = locale.getAbbreviation();
		this.localeTypeId = locale.getLocaleTypeId();
	}
	
}
