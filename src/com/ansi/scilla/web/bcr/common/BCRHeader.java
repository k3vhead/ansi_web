package com.ansi.scilla.web.bcr.common;

import com.ansi.scilla.common.ApplicationObject;

public class BCRHeader extends ApplicationObject {
	private String dbField;
	private String headerName;
	private Double columnWidth;
	public BCRHeader(String dbField, String headerName, Double columnWidth) {
		super();
		this.dbField = dbField;
		this.headerName = headerName;
		this.columnWidth = columnWidth;
	}
	public String getDbField() {
		return dbField;
	}
	public void setDbField(String dbField) {
		this.dbField = dbField;
	}
	public String getHeaderName() {
		return headerName;
	}
	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}
	public Double getColumnWidth() {
		return columnWidth;
	}
	public void setColumnWidth(Double columnWidth) {
		this.columnWidth = columnWidth;
	}
	
	
}
