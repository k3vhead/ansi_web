package com.ansi.scilla.web.common.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

	public static final SimpleDateFormat standardDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	protected final Logger logger = LogManager.getLogger(this.getClass());
	
	private String displaySql;
	private List<PermittedAction> permittedActionList;
	private Method overrideAdd;
	private Method overrideUpdate;
	
	public AbstractCrudServlet() {
		super();
		this.permittedActionList = Arrays.asList(new PermittedAction[] {PermittedAction.ADD, PermittedAction.UPDATE, PermittedAction.DELETE, PermittedAction.GET});
	}
	
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
	
	public List<PermittedAction> getPermittedActionList() {
		return permittedActionList;
	}
	/**
	 * Indicate what actions are allowed to happen. Default values are ADD,UPDATE,DELETE,GET
	 * When a non-permitted action is attempted, a "Not Allowed" is returned.
	 * @param permittedActionList
	 */
	public void setPermittedActionList(List<PermittedAction> permittedActionList) {
		this.permittedActionList = permittedActionList;
	}
	
	/**
	 * When the standard method to populate the database record does not fit requirements, create a method to populate
	 * and insert the database record. The signature must match:<br />
	 * public static Integer overrideAdd(Connection conn, HttpServletResponse response, SessionUser sessionUser, JsonNode jsonNode) throws Exception {
	 * 
	 * @return
	 */
	public Method getOverrideAdd() {
		return overrideAdd;
	}

	public void setOverrideAdd(Method overrideAdd) {
		this.overrideAdd = overrideAdd;
	}

	/**
	 * When the standard method to populate the database record does not fit requirements, create a method to populate
	 * and insert the database record. The signature must match:<br />
	 * public void overrideUpdate(Connection conn, HttpServletResponse response, Integer id, SessionUser sessionUser, JsonNode jsonNode) throws Exception {
	 * @return
	 */
	public Method getOverrideUpdate() {
		return overrideUpdate;
	}

	public void setOverrideUpdate(Method overrideUpdate) {
		this.overrideUpdate = overrideUpdate;
	}

	protected abstract WebMessages validateAdd(Connection conn, HashMap<String, Object> addRequest) throws Exception;
	protected abstract WebMessages validateUpdate(Connection conn, HashMap<String, Object> updateRequest) throws Exception;
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param permission Permission required to see this data. Null indicates only a valid login is required
	 * @param realm
	 * @param actionList
	 * @param table
	 * @param fieldMap
	 * @throws ServletException
	 */
	protected void processGet(HttpServletRequest request, HttpServletResponse response, Permission permission,
			String realm, String[] actionList, MSTable table, List<FieldMap> fieldMap) throws ServletException {
		try {
			AnsiURL url = new AnsiURL(request, realm, actionList);
			if ( permission == null ) {
				AppUtils.validateSession(request);
			} else {
				AppUtils.validateSession(request, permission);
			}
			
			if ( ! this.permittedActionList.contains(PermittedAction.GET)) {
				throw new NotAllowedException();
			}
			
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
		} catch (TimeoutException  | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		}
	}


	
	protected void processPost(HttpServletRequest request, HttpServletResponse response, Permission permission, String realm, String[] actionList, MSTable table, List<FieldMap> fieldMap) throws ServletException {
		logger.log(Level.DEBUG, "Processing Post");
		SessionUser sessionUser = AppUtils.getSessionUser(request);
		try {
			String jsonString = super.makeJsonString(request);
			AnsiURL url = new AnsiURL(request, realm, actionList);
			AppUtils.validateSession(request, permission);
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
				AppUtils.logException(formatException);
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
		logger.log(Level.DEBUG, "Processing Delete");
		
		try {
			
			if ( ! this.permittedActionList.contains(PermittedAction.DELETE)) {
				throw new NotAllowedException();
			}
			
			AnsiURL url = new AnsiURL(request, realm, actionList);			 
			AppUtils.validateSession(request, permission);
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
		
		if ( ! this.permittedActionList.contains(PermittedAction.ADD)) {
			throw new NotAllowedException();
		}
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNode jsonNode = mapper.readTree(jsonString);
		HashMap<String, Object> addRequest = makeRequestMap(fieldMapList, jsonNode);
		WebMessages webMessages = this.validateAdd(conn, addRequest);
		
		if ( webMessages.isEmpty() ) {
			if ( overrideAdd == null ) {
				Integer id = doAdd(conn, response, table, sessionUser, jsonNode, fieldMapList);
				sendItemResponse(conn, response, table, fieldMapList, id);
			} else {
				try {
					Integer id = (Integer)overrideAdd.invoke(null, new Object[] {conn, response, sessionUser, jsonNode});
					sendItemResponse(conn, response, table, fieldMapList, id);
				} catch ( InvocationTargetException ite ) {
					Throwable t = ite.getCause();
					t.printStackTrace();
					throw new RuntimeException(t);
				}
			}		
			
		} else {
			CrudResponse crudResponse = new CrudResponse();
			crudResponse.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, crudResponse);
		}
	}
	
	

	
	private Integer doAdd(Connection conn, HttpServletResponse response, MSTable table, SessionUser sessionUser, JsonNode jsonNode, List<FieldMap> fieldMapList) throws Exception {		
		String tableName = table.getClass().getAnnotation(DBTable.class).value();
		String keyFieldName = table.getClass().getAnnotation(AutoIncrement.class).value();
		
		List<String> fieldList = new ArrayList<String>();
		java.sql.Date today = new java.sql.Date(new java.util.Date().getTime());
		
		for ( FieldMap mapItem : fieldMapList ) {
			if ( mapItem.updateField ) {
				if ( ! mapItem.dbField.equalsIgnoreCase(keyFieldName) ) {
					fieldList.add(mapItem.dbField);
				}
			}
		}
		fieldList.add(MSTable.ADDED_BY);
		fieldList.add(MSTable.ADDED_DATE);
		fieldList.add(MSTable.UPDATED_BY);
		fieldList.add(MSTable.UPDATED_DATE);
		
		
		String sql = "insert into " + tableName + " (" + StringUtils.join(fieldList, ",") + ") values " + AppUtils.makeBindVariables(fieldList);
		logger.log(Level.DEBUG, sql);
		PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		int n = 1;
		for ( FieldMap mapItem : fieldMapList ) {
			if ( mapItem.updateField ) {
				if ( ! mapItem.dbField.equalsIgnoreCase(keyFieldName) ) {
					JsonNode jsonValue = jsonNode.get(mapItem.jsonField);
					if ( jsonValue == null ) {
						ps.setNull(n, mapItem.format.getTypes());
					} else {
						if ( mapItem.format.equals(JsonFieldFormat.STRING)) {
							ps.setString(n, jsonValue.asText());
						} else if ( mapItem.format.equals(JsonFieldFormat.INTEGER)) {
							ps.setInt(n, jsonValue.asInt());
						} else if ( mapItem.format.equals(JsonFieldFormat.DECIMAL)) {
							ps.setDouble(n, jsonValue.asDouble());
						} else if ( mapItem.format.equals(JsonFieldFormat.DATE)) {
							String dateString = jsonNode.get(mapItem.jsonField).asText();
							java.util.Date date = standardDateFormat.parse(dateString);
							ps.setDate(n, new java.sql.Date(date.getTime()));
						} else {
							throw new Exception("Invalid json format");
						}
					}
					n++;
				}
			}
		}
		ps.setInt(n, sessionUser.getUserId());
		n++;
		ps.setDate(n, today);
		n++;
		ps.setInt(n, sessionUser.getUserId());
		n++;
		ps.setDate(n, today);
				
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
		return id;

	}



	private void processUpdateRequest(Connection conn, HttpServletResponse response, MSTable table, Integer id, SessionUser sessionUser, String jsonString, List<FieldMap> fieldMapList) throws Exception {
		logger.log(Level.DEBUG, "Processing Update");
		
		if ( ! this.permittedActionList.contains(PermittedAction.UPDATE)) {
			throw new NotAllowedException();
		}
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		
		String tableName = table.getClass().getAnnotation(DBTable.class).value();
		String keyFieldName = table.getClass().getAnnotation(AutoIncrement.class).value();
		
		PreparedStatement ps = conn.prepareStatement("select * from " + tableName + " where " + keyFieldName + " =?");
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {			
			JsonNode jsonNode = mapper.readTree(jsonString);
			HashMap<String, Object> updateRequest = makeRequestMap(fieldMapList, jsonNode);
			WebMessages webMessages = this.validateUpdate(conn, updateRequest);
			
			if ( webMessages.isEmpty() ) {
				if ( overrideUpdate == null ) {
					doUpdate(conn, response, table, id, sessionUser, jsonNode, fieldMapList);
				} else {
					try {
						overrideUpdate.invoke(null, new Object[] {conn, response, id, sessionUser, jsonNode});
						sendItemResponse(conn, response, table, fieldMapList, id);
					} catch ( InvocationTargetException ite ) {
						Throwable t = ite.getCause();
						t.printStackTrace();
						throw new RuntimeException(t);
					}
				}
				sendItemResponse(conn, response, table, fieldMapList, id);
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


	protected void sendItemResponse(Connection conn, HttpServletResponse response, MSTable table, List<FieldMap> fieldMap, Integer id) throws Exception {
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


//	private HashMap<String, FieldMap> makeJson2Db(List<FieldMap> fieldMap) {
//		HashMap<String, FieldMap> db2Json = new HashMap<String, FieldMap>();
//		for ( FieldMap map : fieldMap ) {
//			db2Json.put(map.jsonField, map);
//		}
//		return db2Json;
//	}



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
	
	
	private HashMap<String, Object> makeRequestMap(List<FieldMap> fieldMapList, JsonNode jsonNode) throws ParseException, Exception {
		HashMap<String, Object> requestMap = new HashMap<String, Object>();
				
		for ( FieldMap fieldMap: fieldMapList ) {
			if ( fieldMap.updateField ) {
				Object value = jsonNode.get(fieldMap.jsonField);
				if ( value == null ) {
					requestMap.put(fieldMap.jsonField, null);
				} else {
					logger.log(Level.DEBUG, fieldMap.jsonField + " " + value.getClass().getName());
					if ( fieldMap.format.equals(JsonFieldFormat.DATE)) {
//						String dateString = jsonNode.get(fieldMap.jsonField).asText();
//						Date date = this.standardDateFormat.parse(dateString);
						requestMap.put(fieldMap.jsonField, jsonNode.get(fieldMap.jsonField).asText());
					} else if (fieldMap.format.equals(JsonFieldFormat.STRING)) {
						requestMap.put(fieldMap.jsonField, jsonNode.get(fieldMap.jsonField).asText());
					} else if (fieldMap.format.equals(JsonFieldFormat.INTEGER)) {
						if ( value instanceof com.fasterxml.jackson.databind.node.TextNode ) {
							// this indicates that a non-integer value is in the input
							// but it could be a stringified number (eg "106" instead of 106) so we check for that, too
							try {
								Integer intValue = Integer.valueOf(jsonNode.get(fieldMap.jsonField).asText());
								requestMap.put(fieldMap.jsonField, intValue);
							} catch ( NumberFormatException e ) {
								requestMap.put(fieldMap.jsonField, jsonNode.get(fieldMap.jsonField).asText());
							}							
						} else {
							requestMap.put(fieldMap.jsonField, jsonNode.get(fieldMap.jsonField).asInt());
						}						
					} else if (fieldMap.format.equals(JsonFieldFormat.DECIMAL)) {
						if ( value instanceof com.fasterxml.jackson.databind.node.TextNode ) {
							// this indicates that a non-decimal value is in the input
							// but it could be a stringified number (eg "106.4" instead of 106.4) so we check for that, too
							try {
								Double doubleValue = Double.valueOf(jsonNode.get(fieldMap.jsonField).asText());
								requestMap.put(fieldMap.jsonField, doubleValue);
							} catch ( NumberFormatException e ) {
								requestMap.put(fieldMap.jsonField, jsonNode.get(fieldMap.jsonField).asText());
							}							
						} else {
							requestMap.put(fieldMap.jsonField, jsonNode.get(fieldMap.jsonField).asDouble());
						}	
					} else {
						throw new Exception("Invalid json field format: " + fieldMap.format);
					}
				}
			}
		}
		
		String jsonString = AppUtils.object2json(requestMap);
		logger.log(Level.DEBUG, jsonString);
		return requestMap;
	}


	public class SQLSetTransformer implements Transformer {

		@Override
		public Object transform(Object arg0) {
			String fieldName = (String)arg0;
			return fieldName + "=?";
		}
		
	}
	
	
}
