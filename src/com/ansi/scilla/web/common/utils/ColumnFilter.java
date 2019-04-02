package com.ansi.scilla.web.common.utils;

import com.ansi.scilla.common.ApplicationObject;

public class ColumnFilter extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private String columnName;
	private String searchValue;
	public ColumnFilter(String columnName, String searchValue) {
		super();
		this.columnName = columnName;
		this.searchValue = searchValue;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getSearchValue() {
		return searchValue;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	/**
	 * Explicitly case-insensitive pattern match
	 * @return  lower(columName) like '%searchValue%'
	 */
	public String toSqlLike() {
		return "lower(" + columnName + ") like '%" + searchValue.toLowerCase() + "%'";
	}
	/**
	 * Explicity case-insensitive equal match
	 * @return lower(columnName)='searchValue'
	 */
	public String toSqlEq() {
		return "lower(" + columnName + ")='" + searchValue.toLowerCase() + "'";
	}
}


