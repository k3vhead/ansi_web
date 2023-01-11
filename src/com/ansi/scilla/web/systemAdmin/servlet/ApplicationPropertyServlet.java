package com.ansi.scilla.web.systemAdmin.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.exceptions.InvalidValueException;
import com.ansi.scilla.common.utils.ApplicationPropertyName;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.systemAdmin.response.AppPropertyLookupResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class ApplicationPropertyServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "appProperty";
	
	private static final String FIELDNAME_PROPERTY_ID = "property_id";
	private static final String FIELDNAME_VALUE = "value";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "appProperty post");
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		ResponseCode responseCode = null;
		AppPropertyLookupResponse data = new AppPropertyLookupResponse();
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			try {
				SessionData sessionData = AppUtils.validateSession(request, Permission.SYSADMIN_WRITE);
				Calendar today = Calendar.getInstance(new AnsiTime());
				
				String propertyId = request.getParameter(FIELDNAME_PROPERTY_ID);
				String value = request.getParameter(FIELDNAME_VALUE);
				logger.log(Level.DEBUG, "Property: " + propertyId);
				logger.log(Level.DEBUG, "Value: " + value);
				
				webMessages = validate(propertyId, value);

				if ( webMessages.isEmpty() ) {
					doCrud(conn, propertyId, value, sessionData.getUser(), today);
					conn.commit();
					data = new AppPropertyLookupResponse( conn.createStatement() );
					responseCode = ResponseCode.SUCCESS;
				} else {
					responseCode = ResponseCode.EDIT_FAILURE;
				}
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, responseCode, data);
			} catch ( SQLException e) {
				conn.rollback();
				throw e;
			} catch ( RecordNotFoundException e) {
				super.sendNotFound(response);
			} finally {
				conn.close();
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		}
	}

	private WebMessages validate(String propertyId, String value) {
		WebMessages webMessages = new WebMessages();
		try {
			ApplicationPropertyName appPropertyName = ApplicationPropertyName.lookup(propertyId);
			if ( StringUtils.isBlank(value) ) {
				webMessages.addMessage(FIELDNAME_VALUE, "Required Value");
			} else {
				switch ( appPropertyName.fieldName() ) {
				case "value_int":
					try {
						Integer.parseInt(value);
					} catch ( NumberFormatException e) {
						webMessages.addMessage(FIELDNAME_VALUE, "Invalid Value");
					}
					break;
				case "value_string":
					if ( value.length() > 1024 ) {
						webMessages.addMessage(FIELDNAME_VALUE, "Value must be less than 1024 characters");
					}
					break;
				case "value_date":
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
					try {
						sdf.parse(value);
					} catch ( ParseException e) {
						webMessages.addMessage(FIELDNAME_VALUE, "Invalid Value");
					}
					break;
				case "value_float":
					try {
						Float.parseFloat(value);
					} catch ( NumberFormatException e) {
						webMessages.addMessage(FIELDNAME_VALUE, "Invalid Value");
					}
					break;
				default:
					break;
				}
			}
		} catch (InvalidValueException e) {
			webMessages.addMessage(FIELDNAME_PROPERTY_ID, "Invalid Property Name. Reload page and try again");
		}
		return webMessages;
	}

	private void doCrud(Connection conn, String propertyId, String value, SessionUser user, Calendar today) throws InvalidValueException, ParseException, SQLException {
		PreparedStatement ps = conn.prepareStatement("select count(*) as record_count from application_properties where property_id=?");
		ps.setString(1, propertyId);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			int count = rs.getInt("record_count");
			if ( count == 0 ) {
				doInsert(conn, propertyId, value, user, today);
			} else {
				doUpdate(conn, propertyId, value, user, today);
			}
		}
		rs.close();
		
	}
	
	private void doInsert(Connection conn, String propertyId, String value, SessionUser user, Calendar today) throws InvalidValueException, ParseException, SQLException {
		ApplicationPropertyName appPropertyName = ApplicationPropertyName.lookup(propertyId);
		Object valueObj = null;
		String fieldName = appPropertyName.fieldName();
		switch ( appPropertyName.fieldName() ) {
		case "value_int":
			valueObj = Integer.parseInt(value);
			break;
		case "value_string":
			valueObj = value;
			break;
		case "value_date":
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
			valueObj = sdf.parse(value);
			break;
		case "value_float":
			valueObj = Float.parseFloat(value);
			break;
		default:
			throw new RuntimeException("All the checks should have been done before this: " + appPropertyName.fieldName());
		}
		PreparedStatement ps = conn.prepareStatement("insert into application_properties (property_id," + fieldName + ",added_by,added_date,updated_by,updated_date) values (?,?,?,?,?,?)");
		ps.setString(1, propertyId);
		ps.setObject(2, valueObj);
		ps.setInt(3, user.getUserId());
		ps.setDate(4, new java.sql.Date(today.getTime().getTime()));
		ps.setInt(5, user.getUserId());
		ps.setDate(6, new java.sql.Date(today.getTime().getTime()));
		ps.executeUpdate();		
	}

	
	private void doUpdate(Connection conn, String propertyId, String value, SessionUser user, Calendar today) throws InvalidValueException, ParseException, SQLException {
		ApplicationPropertyName appPropertyName = ApplicationPropertyName.lookup(propertyId);
		Object valueObj = null;
		String fieldName = appPropertyName.fieldName();
		switch ( appPropertyName.fieldName() ) {
		case "value_int":
			valueObj = Integer.parseInt(value);
			break;
		case "value_string":
			valueObj = value;
			break;
		case "value_date":
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
			valueObj = sdf.parse(value);
			break;
		case "value_float":
			valueObj = Float.parseFloat(value);
			break;
		default:
			throw new RuntimeException("All the checks should have been done before this: " + appPropertyName.fieldName());
		}
		String sql = "update application_properties set " + fieldName + "=?, updated_by=?, updated_date=? where property_id=?";
		logger.log(Level.DEBUG, sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setObject(1, valueObj);
		ps.setInt(2, user.getUserId());
		ps.setDate(3, new java.sql.Date(today.getTime().getTime()));
		ps.setString(4, propertyId);
		ps.executeUpdate();
	}

	
}
