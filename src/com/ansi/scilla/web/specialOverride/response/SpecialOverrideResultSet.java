package com.ansi.scilla.web.specialOverride.response;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.web.common.response.MessageResponse;

public class SpecialOverrideResultSet extends MessageResponse {
	
	private static final long serialVersionUID = 1L;
	
	private List<List<String>> itemList;
	
	public SpecialOverrideResultSet() {
		super();
	}
	
	public SpecialOverrideResultSet(ResultSet rs) throws Exception {
		this();		
		this.itemList = new ArrayList<List<String>>();
		ResultSetMetaData rsmd = rs.getMetaData();
		List<String> headerList = new ArrayList<String>();
		for ( int i = 0; i < rsmd.getColumnCount(); i++ ) {
			headerList.add(rsmd.getColumnName(i+1));
		}
		itemList.add(headerList);
		
		while ( rs.next() ) {
			List<String> row = new ArrayList<String>();
			for ( int i = 0; i < rsmd.getColumnCount(); i++ ) {
				int index = i+1;
				String className = rsmd.getColumnClassName(index);
				System.out.println(className);
				if ( className.equalsIgnoreCase("java.lang.Integer")) {
					row.add(String.valueOf(rs.getInt(index)));
				} else if ( className.equalsIgnoreCase("java.lang.String")) {
					row.add(rs.getString(index));
				} else if ( className.equalsIgnoreCase("java.math.BigDecimal")) {
					row.add(String.valueOf(rs.getBigDecimal(index)));
				} else if ( className.equalsIgnoreCase("java.sql.Timestamp")) {
					row.add(String.valueOf(rs.getTimestamp(index)));
				} else if ( className.equalsIgnoreCase("java.sql.Date")) {
					row.add(String.valueOf(rs.getDate(index)));
				} else {
					row.add( String.valueOf(rs.getObject(index)));
					//throw new InvalidFormatException(rsmd.getColumnClassName(i+1));
				}
			}
			itemList.add(row);
		}
		
		
	}

	public List<List<String>> getItemList() {
		return itemList;
	}

	public void setItemList(List<List<String>> itemList) {
		this.itemList = itemList;
	}

	
}
