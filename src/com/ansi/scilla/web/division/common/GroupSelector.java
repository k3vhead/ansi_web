package com.ansi.scilla.web.division.common;

import java.sql.Connection;
import java.sql.SQLException;

import com.ansi.scilla.common.organization.OrganizationType;

public class GroupSelector extends OrganizationSelector {
	private static final long serialVersionUID = 1L;

	protected static GroupSelector instance;
	
	private GroupSelector(Connection conn) throws SQLException {
		super(conn, OrganizationType.GROUP);
	}
	
	public static GroupSelector getInstance(Connection conn) throws SQLException {
		if ( instance == null ) {
			// this makes this object thread-safe, more efficiently than "public static synchronized DivisionSelector ..."
			synchronized (GroupSelector.class) { 
				instance = new GroupSelector(conn);
			}
		}
		return instance;
	}
}
