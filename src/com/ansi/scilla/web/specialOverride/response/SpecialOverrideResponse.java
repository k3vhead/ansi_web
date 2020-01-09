package com.ansi.scilla.web.specialOverride.response;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.specialOverride.common.ParameterType;
import com.ansi.scilla.web.specialOverride.common.SpecialOverrideType;

public class SpecialOverrideResponse extends MessageResponse{

	private static final long serialVersionUID = 1L;
	private String specialOverrideType;
	private List<NameDescriptionResponseItem> scriptList;	// to fill the script selector dropdown
	private List<SpecialOverrideSelectItem> selectList;		// select parameters
	private List<SpecialOverrideSelectItem> updateList;		// update parameters
	private List<List<String>> resultSet;					// results of db query
	
	
	
	public SpecialOverrideResponse() {
		super();
		this.scriptList = makeScriptList();		
	}
	
	public SpecialOverrideResponse(SpecialOverrideType specialOverrideType) {
		this();
		this.specialOverrideType = specialOverrideType.name();
		this.selectList = makeParameterList(specialOverrideType.getSelectParms());
		this.updateList = makeParameterList(specialOverrideType.getUpdateParms());		
	}

	public SpecialOverrideResponse(SpecialOverrideType specialOverrideType, ResultSet rs) throws Exception {
		this(specialOverrideType);
		this.resultSet = makeResultSet(rs);
	}
	

	protected List<NameDescriptionResponseItem> makeScriptList() {
		List<NameDescriptionResponseItem> scriptList = new ArrayList<NameDescriptionResponseItem>();
		for ( SpecialOverrideType reference : SpecialOverrideType.values() ) {
			scriptList.add(new NameDescriptionResponseItem(reference));
		}
		return scriptList;
	}
	
	protected List<SpecialOverrideSelectItem> makeParameterList(ParameterType[] parameterTypes) {
		List<SpecialOverrideSelectItem> parmList = new ArrayList<SpecialOverrideSelectItem>();
		for ( ParameterType reference : parameterTypes ) {
			parmList.add(new SpecialOverrideSelectItem(reference));
		}
		return parmList;
	}

	protected List<List<String>> makeResultSet(ResultSet rs) throws SQLException {
		List<List<String>> itemList = new ArrayList<List<String>>();
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
					String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					timeStamp = String.valueOf(rs.getTimestamp(index));
					row.add(timeStamp);
				} else if ( className.equalsIgnoreCase("java.sql.Date")) {
					String date = String.valueOf(rs.getDate(index));
					date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					row.add(date);
				} else {
					row.add( String.valueOf(rs.getObject(index)));
					//throw new InvalidFormatException(rsmd.getColumnClassName(i+1));
				}
			}
			itemList.add(row);
		}
		
		return itemList;
	}

	public String getSpecialOverrideType() {
		return specialOverrideType;
	}

	public void setSpecialOverrideType(String specialOverrideType) {
		this.specialOverrideType = specialOverrideType;
	}

	public List<NameDescriptionResponseItem> getScriptList() {
		return scriptList;
	}

	public void setScriptList(List<NameDescriptionResponseItem> scriptList) {
		this.scriptList = scriptList;
	}

	public List<SpecialOverrideSelectItem> getSelectList() {
		return selectList;
	}

	public void setSelectList(List<SpecialOverrideSelectItem> selectList) {
		this.selectList = selectList;
	}

	public List<SpecialOverrideSelectItem> getUpdateList() {
		return updateList;
	}

	public void setUpdateList(List<SpecialOverrideSelectItem> updateList) {
		this.updateList = updateList;
	}

	public List<List<String>> getResultSet() {
		return resultSet;
	}

	public void setResultSet(List<List<String>> resultSet) {
		this.resultSet = resultSet;
	}
	
	
}
