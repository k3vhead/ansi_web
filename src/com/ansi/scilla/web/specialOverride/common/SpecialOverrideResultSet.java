package com.ansi.scilla.web.specialOverride.common;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpecialOverrideResultSet {
	
	public static ArrayList<List<Object>> makeData(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		return makeData(rs, rsmd);
	}
	
	/**
	 *   select contact.contact_id, contact.name from contact
	 * @param rs
	 * @param rsmd
	 * @return
	 * @throws SQLException
	 */
	public static ArrayList<List<Object>> makeData(ResultSet rs, ResultSetMetaData rsmd) throws SQLException {
		ArrayList<List<Object>> dataRows = new ArrayList<List<Object>>();
		while ( rs.next() ) {
			List<Object> row = new ArrayList<Object>();
			for ( int i = 0; i < rsmd.getColumnCount(); i++ ) {
				int index = i + 1;
				row.add(rs.getObject(index));
			}
			dataRows.add(row);
		}
		return dataRows;
	}
	
	
	
}
