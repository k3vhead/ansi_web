package com.ansi.scilla.web.response.code;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Code;
import com.ansi.scilla.web.response.MessageResponse;

/** 
 * Used to return a list of "code" objects to the client
 * 
 * @author dclewis
 *
 */
public class CodeListResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<CodeResponseRecord> codeList;
	private List<FilterRecord> filterRecordList;

	public CodeListResponse() {
		super();
	}
	/**
	 * create a list of all code table records in the database, sorted by
	 * table, field, display value
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public CodeListResponse(Connection conn) throws Exception {
		List<Code> codeList = Code.cast(new Code().selectAll(conn));
		this.codeList = new ArrayList<CodeResponseRecord>();
		for ( Code code : codeList ) {
			this.codeList.add(new CodeResponseRecord(code));
		}
		Collections.sort(this.codeList);
		
		makeFilterList(conn);
	}

	public CodeListResponse(Connection conn, String tableName, String fieldName, String value) throws Exception {
		Code key = new Code();
		key.setTableName(tableName);
		key.setFieldName(fieldName);
		key.setValue(value);
		List<Code> codeList = Code.cast(key.selectSome(conn));
		this.codeList = new ArrayList<CodeResponseRecord>();
		for ( Code code : codeList ) {
			this.codeList.add(new CodeResponseRecord(code));
		}
		Collections.sort(this.codeList);
		
		makeFilterList(conn);
	}

	public List<CodeResponseRecord> getCodeList() {
		return codeList;
	}

	public void setCodeList(List<CodeResponseRecord> codeList) {
		this.codeList = codeList;
	}
	
	public List<FilterRecord> getFilterRecordList() {
		return filterRecordList;
	}
	public void setFilterRecordList(List<FilterRecord> filterRecordList) {
		this.filterRecordList = filterRecordList;
	}

	public void sortByDisplay() {
		Collections.sort(this.codeList, new Comparator<CodeResponseRecord>() {
			public int compare(CodeResponseRecord o1, CodeResponseRecord o2) {
				int ret = o1.getDisplayValue().compareTo(o2.getDisplayValue());
				return ret;
			}
		});
	}

	private void makeFilterList(Connection conn) throws SQLException {
		String sql = "select code.table_name, code.field_name from code order by table_name,field_name";
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery(sql);
		this.filterRecordList = new ArrayList<FilterRecord>();
		List<String> fieldNameList = new ArrayList<String>();
		String previousTable = null;
		
		while (rs.next()) {
			String tableName = rs.getString("table_name");
			String fieldName = rs.getString("field_name");
			if ( previousTable != null && ! tableName.equals(previousTable) ) {
				filterRecordList.add(new FilterRecord(previousTable, fieldNameList));
				fieldNameList = new ArrayList<String>();
			}
			previousTable = tableName;
			if ( ! fieldNameList.contains(fieldName)) {
				fieldNameList.add(fieldName);
			}
		}
		rs.close();
		
		Collections.sort(this.filterRecordList);
	}

	public class FilterRecord extends ApplicationObject implements Comparable<FilterRecord> {
		private static final long serialVersionUID = 1L;
		
		private String tableName;
		private List<String> fieldNameList;
		
		public FilterRecord(String tableName, List<String> fieldNameList) {
			super();
			this.tableName = tableName;
			this.fieldNameList = fieldNameList;
		}

		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public List<String> getFieldNameList() {
			return fieldNameList;
		}

		public void setFieldNameList(List<String> fieldNameList) {
			this.fieldNameList = fieldNameList;
		}

		@Override
		public int compareTo(FilterRecord o) {
			Collections.sort(this.fieldNameList);
			return tableName.compareTo(o.getTableName());
		}
		
	}
}
