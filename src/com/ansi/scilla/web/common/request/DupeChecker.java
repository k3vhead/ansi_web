package com.ansi.scilla.web.common.request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.MSTable;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.utils.FieldMap;
import com.ansi.scilla.web.common.utils.JsonFieldFormat;
import com.thewebthing.commons.db2.DBTable;

public class DupeChecker {

	private MSTable table;
	private HashMap<String, Object> addRequest;
	private List<FieldMap> fieldMapList;
	private Logger logger;
	
	public DupeChecker(MSTable table, HashMap<String, Object> addRequest, List<FieldMap> fieldMapList) {
		super();
		this.logger = LogManager.getLogger(this.getClass());
		this.table = table;
		this.addRequest = addRequest;
		this.fieldMapList = fieldMapList;
	}

	public void checkForDupes(Connection conn, List<String> keyFieldList, WebMessages webMessages, SimpleDateFormat standardDateFormat) throws Exception {
		String tableName = table.getClass().getAnnotation(DBTable.class).value();
		String sql = makeSql(tableName, keyFieldList);
		logger.log(Level.DEBUG, sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		Integer n = 1;
		boolean foundAllFields = true;
		
		for ( String keyField : keyFieldList ) {
			for ( FieldMap fieldMap : fieldMapList ) {
				if ( keyField.equalsIgnoreCase(fieldMap.dbField)) {
					Object value = addRequest.get(fieldMap.jsonField);
					if ( value == null ) {
						foundAllFields = false;
					} else {
						logger.log(Level.DEBUG, keyField);
						
						if ( fieldMap.format.equals(JsonFieldFormat.DATE)) {
							java.util.Date date = standardDateFormat.parse((String)value);
							java.sql.Date sqlDate = new java.sql.Date(date.getTime());
							SimpleDateFormat x = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.S");
							
							logger.log(Level.DEBUG, x.format(sqlDate));
							ps.setDate(n, sqlDate);
						} else if ( fieldMap.format.equals(JsonFieldFormat.INTEGER)) {
							logger.log(Level.DEBUG, value);
							ps.setInt(n, (Integer)value);
						} else if ( fieldMap.format.equals(JsonFieldFormat.DECIMAL)) {
							logger.log(Level.DEBUG, value);
							ps.setDouble(n, (Double)value);
						} else if ( fieldMap.format.equals(JsonFieldFormat.STRING)) {
							logger.log(Level.DEBUG, value);
							ps.setString(n, (String)value);
						} else {
							throw new Exception("Invalid json field format");
						}
					}					
				}
			}
			n++;
		}
		if ( foundAllFields ) {
			ResultSet rs = ps.executeQuery();
			if ( rs.next() ) {
				Integer recordCount = rs.getInt("record_count");
				if ( recordCount > 0 ) {
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Duplicate Entry");
				}
			}
			rs.close();
		}
	}

	private String makeSql(String tableName, List<String> keyFieldList) {		
		List<String> variableList = new ArrayList<String>();
		for ( String key : keyFieldList ) {
			variableList.add(key + "=?");
		}
		return "select count(*) as record_count from " + tableName + " where " + StringUtils.join(keyFieldList, "=? and ") + "=?";
	}

	public MSTable getTable() {
		return table;
	}

	public void setTable(MSTable table) {
		this.table = table;
	}

	public HashMap<String, Object> getAddRequest() {
		return addRequest;
	}

	public void setAddRequest(HashMap<String, Object> addRequest) {
		this.addRequest = addRequest;
	}

	public List<FieldMap> getFieldMapList() {
		return fieldMapList;
	}

	public void setFieldMapList(List<FieldMap> fieldMapList) {
		this.fieldMapList = fieldMapList;
	}

	
}
