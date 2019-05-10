package com.ansi.scilla.web.common.query;

import java.sql.Connection;
import java.sql.ResultSet;

import com.ansi.scilla.common.ApplicationObject;

public abstract class AbstractQuery extends ApplicationObject {

	private static final long serialVersionUID = 1L;
	
	public abstract ResultSet select(Connection conn) throws Exception;
	public abstract Integer selectCount(Connection conn) throws Exception;
	public abstract Integer selectCountAll(Connection conn) throws Exception;

}
