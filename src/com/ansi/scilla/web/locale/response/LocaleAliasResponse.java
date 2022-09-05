package com.ansi.scilla.web.locale.response;

import java.sql.Connection;

import com.ansi.scilla.common.db.LocaleAlias;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class LocaleAliasResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	private Integer localeAliasId;
	private Integer localeId;
	private String localeName;

	public LocaleAliasResponse() {
		super();
	}
	
	public LocaleAliasResponse(Connection conn, Integer localeAliasId) throws RecordNotFoundException, Exception {
		this();
		LocaleAlias alias = new LocaleAlias();
		alias.setLocaleAliasId(localeAliasId);
		alias.selectOne(conn);
		this.localeAliasId = alias.getLocaleAliasId();
		this.localeId = alias.getLocaleId();
		this.localeName = alias.getLocaleName();
	}

	public Integer getLocaleAliasId() {
		return localeAliasId;
	}

	public void setLocaleAliasId(Integer localeAliasId) {
		this.localeAliasId = localeAliasId;
	}

	public Integer getLocaleId() {
		return localeId;
	}

	public void setLocaleId(Integer localeId) {
		this.localeId = localeId;
	}

	public String getLocaleName() {
		return localeName;
	}

	public void setLocaleName(String localeName) {
		this.localeName = localeName;
	}

	
}
