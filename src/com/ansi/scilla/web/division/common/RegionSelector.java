package com.ansi.scilla.web.division.common;

import java.sql.Connection;
import java.sql.SQLException;

import com.ansi.scilla.common.organization.OrganizationType;

public class RegionSelector extends OrganizationSelector {
	private static final long serialVersionUID = 1L;

	protected static RegionSelector instance;
	
	public RegionSelector(Connection conn) throws SQLException {
		super(conn, OrganizationType.REGION);
	}
	
//	public static RegionSelector getInstance(Connection conn) throws SQLException {
//		if ( instance == null ) {
//			// this makes this object thread-safe, more efficiently than "public static synchronized DivisionSelector ..."
//			synchronized (RegionSelector.class) { 
//				instance = new RegionSelector(conn);
//			}
//		}
//		return instance;
//	}
}
