package com.ansi.scilla.web.specialOverride.response;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SpecialOverrideResultSet extends SpecialOverrideResponseItem {
	
	private static final long serialVersionUID = 1L;
	
	private ResultSet rs;
	private ResultSetMetaData rsmd;
	
	public SpecialOverrideResultSet() {
		super();
	}
	
	public SpecialOverrideResultSet(ResultSet rs) throws SQLException {
		this();
		this.rs = rs;
		this.rsmd = rs.getMetaData();
	}

	public ResultSet getRs() {
		return rs;
	}

	public void setRs(ResultSet rs) {
		this.rs = rs;
	}

	public ResultSetMetaData getRsmd() {
		return rsmd;
	}

	public void setRsmd(ResultSetMetaData rsmd) {
		this.rsmd = rsmd;
	}
}
