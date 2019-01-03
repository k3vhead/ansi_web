package com.ansi.scilla.web.common.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.AutoIncrement;
import com.ansi.scilla.common.db.MSTable;
import com.ansi.scilla.web.common.response.CrudListResponse;
import com.ansi.scilla.web.common.response.CrudResponse;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.FieldMap;
import com.ansi.scilla.web.common.utils.JsonFieldFormat;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.division.response.DivisionResponse;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.DBException;
import com.thewebthing.commons.db2.DBTable;
import com.thewebthing.commons.db2.RecordNotFoundException;

public abstract class AbstractCrudServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String ACTION_IS_LIST = "list";

	protected final SimpleDateFormat standardDateFormat = new SimpleDateFormat("MM/DD/yyyy");
	protected final Logger logger = LogManager.getLogger(this.getClass());
	
	private String displaySql;
	
	public String getDisplaySql() {
		return displaySql;
	}
	/**
	 * When the display of the data requires a more complex query than a simple "get one record from one table",
	 * populate displaySql with the more complex query. If populated, this query will be used as-is for a list
	 * response. "where key=value" will be appended for the item response, where "key" and "value" are deduced from the
	 * MSTable object passed into the processGet(), processPost() and processDelete() methods
	 * 
	 * @param displaySql
	 */
	public void setDisplaySql(String displaySql) {
		this.displaySql = displaySql;
	}
	
	
	protected abstract WebMessages validateAdd(Connection conn, JsonNode addRequest) throws Exception;
	protected abstract WebMessages validateUpdate(Connection conn, JsonNode updateRequest) throws Exception;
	
	
	protected void processGet(HttpServletRequest request, HttpServletResponse response, Permission claimsRead,
			String realm, String[] actionList, MSTable table, List<FieldMap> fieldMap) throws ServletException {
		try {
			AnsiURL url = new AnsiURL(request, realm, actionList);
			SessionData sessionData = AppUtils.validateSession(request);
		
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();

				if( ! StringUtils.isBlank(url.getCommand()) && url.getCommand().equals(ACTION_IS_LIST)){
					sendListResponse(conn, response, table, fieldMap);
				} else if (url.getId() != null) {
					sendItemResponse(conn, response, table, fieldMap, url.getId());
				} else {
					// according to the URI parsing, this shouldn't happen, but it gives me warm fuzzies
					throw new RecordNotFoundException();
				}

			} catch(RecordNotFoundException recordNotFoundEx) {
				super.sendNotFound(response);
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
		} catch (ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch (TimeoutException e) {
			super.sendForbidden(response);
		}
	}


	
	protected void processPost(HttpServletRequest request, HttpServletResponse response, Permission permission, String realm, String[] actionList, MSTable table, List<FieldMap> fieldMap) throws ServletException {
		logger.log(Level.DEBUG, "Processing Post");
		SessionUser sessionUser = AppUtils.getSessionUser(request);
		try {
			String jsonString = super.makeJsonString(request);
			AnsiURL url = new AnsiURL(request, realm, actionList);
			SessionData sessionData = AppUtils.validateSession(request, permission);
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);

				
				// figure out if this is an "add" or an "update"								
				try {
					if ( ! StringUtils.isBlank(url.getCommand()) && url.getCommand().equals(ACTION_IS_ADD)) {
						processAddRequest(conn, response, table, sessionUser, jsonString, fieldMap);
					} else if ( url.getId() != null ) {
						processUpdateRequest(conn, response, table, url.getId(), sessionUser, jsonString, fieldMap);
					} else {
						super.sendNotFound(response);
					}
				} catch ( InvalidFormatException formatException) {
					processBadPostRequest(conn, response, formatException);
				}
			} catch ( IOException formatException) {
				super.sendResponse(conn, response, ResponseCode.SYSTEM_FAILURE, null);
			} catch ( Exception e ) {
				AppUtils.logException(e);
				AppUtils.rollbackQuiet(conn);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}

		} catch ( ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch (TimeoutException  | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		}
	}
	
	
	
	protected void processDelete(HttpServletRequest request, HttpServletResponse response, Permission permission, String realm, String[] actionList, MSTable table, List<FieldMap> fieldMap) throws ServletException {
		logger.log(Level.DEBUG, "Processing Post");
//		SessionUser sessionUser = AppUtils.getSessionUser(request);
		try {
//			String jsonString = super.makeJsonString(request);
			AnsiURL url = new AnsiURL(request, realm, actionList);
			SessionData sessionData = AppUtils.validateSession(request, permission);
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);

				String tableName = table.getClass().getAnnotation(DBTable.class).value();
				String keyFieldName = table.getClass().getAnnotation(AutoIncrement.class).value();
				
				String sqlCheckId = "select * from " + tableName + " where " + keyFieldName + "=?";
				PreparedStatement ps = conn.prepareStatement(sqlCheckId);
				logger.log(Level.DEBUG, sqlCheckId);
				ps.setInt(1, url.getId());
				ResultSet rs = ps.executeQuery();
				if ( rs.next() ) {			
					String sqlDelete = "delete from " + tableName + " where " + keyFieldName + "=?";
					logger.log(Level.DEBUG, sqlDelete);
					PreparedStatement psDelete = conn.prepareStatement(sqlDelete);
					psDelete.setInt(1, url.getId());
					psDelete.executeUpdate();
					super.sendResponse(conn, response, ResponseCode.SUCCESS, null);
					conn.commit();
				} else {
					super.sendNotFound(response);
				}
				rs.close();
				
			} catch ( IOException formatException) {
				super.sendResponse(conn, response, ResponseCode.SYSTEM_FAILURE, null);
			} catch ( Exception e ) {
				AppUtils.logException(e);
				AppUtils.rollbackQuiet(conn);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}

		} catch ( ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch (TimeoutException  | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		}
	}
	
	
	
	
	
	
	private void processAddRequest(Connection conn, HttpServletResponse response, MSTable table, SessionUser sessionUser, String jsonString, List<FieldMap> fieldMapList) throws Exception {
		logger.log(Level.DEBUG, "Processing add");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNode jsonNode = mapper.readTree(jsonString);
		WebMessages webMessages = this.validateAdd(conn, jsonNode);
		
		if ( webMessages.isEmpty() ) {
			doAdd(conn, response, table, sessionUser, jsonNode, fieldMapList);
		} else {
			CrudResponse crudResponse = new CrudResponse();
			crudResponse.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, crudResponse);
		}
	}
	
	

	
	private void doAdd(Connection conn, HttpServletResponse response, MSTable table, SessionUser sessionUser, JsonNode jsonNode, List<FieldMap> fieldMapList) throws Exception {		
		String tableName = table.getClass().getAnnotation(DBTable.class).value();
		String keyFieldName = table.getClass().getAnnotation(AutoIncrement.class).value();
		
		List<String> fieldList = new ArrayList<String>();
		List<Object> valueList = new ArrayList<Object>();
		java.sql.Date today = new java.sql.Date(new java.util.Date().getTime());
		
		// insert into non_direct_labor (field, field) values (?,?)
		for ( FieldMap mapItem : fieldMapList ) {
			if ( mapItem.updateField ) {
				if ( ! mapItem.dbField.equalsIgnoreCase(keyFieldName) ) {
					fieldList.add(mapItem.dbField);
					if ( mapItem.format.equals(JsonFieldFormat.INTEGER)) {
						valueList.add(jsonNode.get(mapItem.jsonField).asInt());
					} else if (mapItem.format.equals(JsonFieldFormat.DECIMAL)) {
						valueList.add(jsonNode.get(mapItem.jsonField).asLong());
					} else if (mapItem.format.equals(JsonFieldFormat.STRING)) {
						valueList.add(jsonNode.get(mapItem.jsonField).asText());
					} else if (mapItem.format.equals(JsonFieldFormat.DATE)) {
						String dateString = jsonNode.get(mapItem.jsonField).asText();
						java.util.Date date = standardDateFormat.parse(dateString);
						valueList.add(new java.sql.Date(date.getTime()));
					} else {
						throw new ServletException("Invalid data format");
					}
				}
			}
		}
		fieldList.add(MSTable.ADDED_BY);
		valueList.add(sessionUser.getUserId());
		fieldList.add(MSTable.UPDATED_BY);
		valueList.add(sessionUser.getUserId());
		fieldList.add(MSTable.ADDED_DATE);
		valueList.add(today);
		fieldList.add(MSTable.UPDATED_DATE);
		valueList.add(today);
		
		String sql = "insert into " + tableName + "(" + StringUtils.join(fieldList, ",") + ") values " + AppUtils.makeBindVariables(valueList);
		logger.log(Level.DEBUG, sql);
		PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		int n = 1;
		for ( Object value : valueList ) {
			ps.setObject(n, value);
			n++;
		}
		ps.executeUpdate();
		
		Integer id = null;
		ResultSet rs = ps.getGeneratedKeys();
		if ( rs.next() ) {		
			id = rs.getInt("GENERATED_KEYS");
		} else {
			throw new DBException("No Key genned");
		}
		rs.close();
				
		conn.commit();

		sendItemResponse(conn, response, table, fieldMapList, id);		
	}



	private void processUpdateRequest(Connection conn, HttpServletResponse response, MSTable table, Integer id, SessionUser sessionUser, String jsonString, List<FieldMap> fieldMapList) throws Exception {
		logger.log(Level.DEBUG, "Processing Update");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		
		String tableName = table.getClass().getAnnotation(DBTable.class).value();
		String keyFieldName = table.getClass().getAnnotation(AutoIncrement.class).value();
		
		PreparedStatement ps = conn.prepareStatement("select * from " + tableName + " where " + keyFieldName + " =?");
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {			
			JsonNode jsonNode = mapper.readTree(jsonString);
			WebMessages webMessages = this.validateUpdate(conn, jsonNode);
			
			if ( webMessages.isEmpty() ) {
				doUpdate(conn, response, table, id, sessionUser, jsonNode, fieldMapList);
			} else {
				CrudResponse crudResponse = new CrudResponse();
				crudResponse.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, crudResponse);
			}
		} else {
			super.sendNotFound(response);
		}
		rs.close();
	}

	
	
	
	@SuppressWarnings("unchecked")
	private void doUpdate(Connection conn, HttpServletResponse response, MSTable table, Integer id, SessionUser sessionUser, JsonNode jsonNode, List<FieldMap> fieldMapList) throws Exception {
		String tableName = table.getClass().getAnnotation(DBTable.class).value();
		String keyFieldName = table.getClass().getAnnotation(AutoIncrement.class).value();
		
		List<String> fieldList = new ArrayList<String>();
		List<Object> valueList = new ArrayList<Object>();
		java.sql.Date today = new java.sql.Date(new java.util.Date().getTime());
		
		for ( FieldMap mapItem : fieldMapList ) {
			if ( mapItem.updateField ) {
				if ( ! mapItem.dbField.equalsIgnoreCase(keyFieldName) ) {
					fieldList.add(mapItem.dbField);
					if ( mapItem.format.equals(JsonFieldFormat.INTEGER)) {
						valueList.add(jsonNode.get(mapItem.jsonField).asInt());
					} else if (mapItem.format.equals(JsonFieldFormat.DECIMAL)) {
						valueList.add(jsonNode.get(mapItem.jsonField).asLong());
					} else if (mapItem.format.equals(JsonFieldFormat.STRING)) {
						valueList.add(jsonNode.get(mapItem.jsonField).asText());
					} else if (mapItem.format.equals(JsonFieldFormat.DATE)) {
						String dateString = jsonNode.get(mapItem.jsonField).asText();
						java.util.Date date = standardDateFormat.parse(dateString);
						valueList.add(new java.sql.Date(date.getTime()));
					} else {
						throw new ServletException("Invalid data format");
					}
				}
			}
		}
		fieldList.add(MSTable.UPDATED_BY);
		valueList.add(sessionUser.getUserId());
		fieldList.add(MSTable.UPDATED_DATE);
		valueList.add(today);
		
		List<String> sqlSetList = (List<String>) CollectionUtils.collect(fieldList, new SQLSetTransformer());
		String sql = "update " + tableName + " set " + StringUtils.join(sqlSetList, ",") + " where " + keyFieldName + "=?";
		logger.log(Level.DEBUG, sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		int n = 1;
		for ( Object value : valueList ) {
			ps.setObject(n, value);
			n++;
		}
		ps.setInt(n, id);
		ps.executeUpdate();
		
		conn.commit();

		sendItemResponse(conn, response, table, fieldMapList, id);
		
	}



	private void processBadPostRequest(Connection conn, HttpServletResponse response, InvalidFormatException formatException) throws Exception {
		WebMessages webMessages = new WebMessages();
		String field = findBadField(formatException.toString());
		String messageText = AppUtils.getMessageText(conn, MessageKey.INVALID_DATA, "Invalid Format");
		webMessages.addMessage(field, messageText);
		DivisionResponse divisionResponse = new DivisionResponse();
		divisionResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, divisionResponse);
		
	}
	
	
	
	
	private void sendListResponse(Connection conn, HttpServletResponse response, MSTable table, List<FieldMap> fieldMap) throws Exception {
		HashMap<String, FieldMap> db2json = makeDb2Json(fieldMap);
		List<HashMap<String, Object>> itemList = new ArrayList<HashMap<String,Object>>();

		String tableName = table.getClass().getAnnotation(DBTable.class).value();
		Statement s = conn.createStatement();
		ResultSet rs = StringUtils.isBlank(displaySql) ? s.executeQuery("select * from " + tableName) : s.executeQuery(displaySql);
		while ( rs.next() ) {
			itemList.add(makeItem(rs, db2json));
		}
		rs.close();
		CrudListResponse crudListResponse = new CrudListResponse(itemList);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, crudListResponse);
		
	}


	private void sendItemResponse(Connection conn, HttpServletResponse response, MSTable table, List<FieldMap> fieldMap, Integer id) throws Exception {
		HashMap<String, FieldMap> db2json = makeDb2Json(fieldMap);
		HashMap<String, Object> item = null;
		
		String tableName = table.getClass().getAnnotation(DBTable.class).value();
		String keyFieldName = table.getClass().getAnnotation(AutoIncrement.class).value();
		String sqlSelect = StringUtils.isBlank(displaySql) ? "select * from " + tableName : displaySql;
		String sql = sqlSelect + " where " + keyFieldName + "=?";
		logger.log(Level.DEBUG, sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {			
			item = makeItem(rs, db2json);
			CrudResponse crudResponse = new CrudResponse(item);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, crudResponse);
		} else {
			super.sendNotFound(response);
		}
		rs.close();

		
		
	}


	private HashMap<String, FieldMap> makeDb2Json(List<FieldMap> fieldMap) {
		HashMap<String, FieldMap> db2Json = new HashMap<String, FieldMap>();
		for ( FieldMap map : fieldMap ) {
			db2Json.put(map.dbField, map);
		}
		return db2Json;
	}


	private HashMap<String, FieldMap> makeJson2Db(List<FieldMap> fieldMap) {
		HashMap<String, FieldMap> db2Json = new HashMap<String, FieldMap>();
		for ( FieldMap map : fieldMap ) {
			db2Json.put(map.jsonField, map);
		}
		return db2Json;
	}



	private HashMap<String, Object> makeItem(ResultSet rs, HashMap<String, FieldMap> db2json) throws SQLException {
		HashMap<String, Object> item = new HashMap<String, Object>();
		for ( Map.Entry<String, FieldMap> mapEntry : db2json.entrySet() ) {
			FieldMap fieldMap = mapEntry.getValue();
			Object value = rs.getObject(mapEntry.getKey());
			if ( value != null ) {
				Object formattedValue = fieldMap.format.equals(JsonFieldFormat.DATE) ? makeDateString(rs, fieldMap.dbField) : value;
				item.put(fieldMap.jsonField, formattedValue);
			}
		}
		return item;
	}


	private Object makeDateString(ResultSet rs, String dbField) throws SQLException {
		String formattedDate = null;
		java.sql.Date sqlDate = rs.getDate(dbField);
		java.util.Date date = new java.util.Date(sqlDate.getTime());
		formattedDate = standardDateFormat.format(date);
		return formattedDate;
	}
	
	
	public class SQLSetTransformer implements Transformer {

		@Override
		public Object transform(Object arg0) {
			String fieldName = (String)arg0;
			return fieldName + "=?";
		}
		
	}
}
