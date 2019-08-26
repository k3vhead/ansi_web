package com.ansi.scilla.web.common.response;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ansi.scilla.common.ApplicationObject;

public abstract class AbstractAutocompleteReturnItem extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String label;
	private String value;
	
	public AbstractAutocompleteReturnItem(ResultSet rs) throws SQLException {
		super();
		this.id = formatId(rs);
		this.label = formatLabel(rs);
		this.value = formatName(rs);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	protected abstract Integer formatId(ResultSet rs) throws SQLException;
	protected abstract String formatLabel(ResultSet rs) throws SQLException;
	protected abstract String formatName(ResultSet rs) throws SQLException;
}
