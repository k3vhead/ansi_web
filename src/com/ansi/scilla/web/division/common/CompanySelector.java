package com.ansi.scilla.web.division.common;

import java.sql.Connection;
import java.sql.SQLException;

import com.ansi.scilla.common.organization.OrganizationType;

public class CompanySelector extends OrganizationSelector {
	private static final long serialVersionUID = 1L;

	protected static CompanySelector instance;
	
	private CompanySelector(Connection conn) throws SQLException {
		super(conn, OrganizationType.COMPANY);
	}
	
	public static CompanySelector getInstance(Connection conn) throws SQLException {
		if ( instance == null ) {
			// this makes this object thread-safe, more efficiently than "public static synchronized DivisionSelector ..."
			synchronized (CompanySelector.class) { 
				instance = new CompanySelector(conn);
			}
		}
		return instance;
	}
}
