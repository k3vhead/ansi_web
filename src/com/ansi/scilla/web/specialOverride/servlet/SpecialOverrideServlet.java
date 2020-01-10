package com.ansi.scilla.web.specialOverride.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.common.exception.InvalidFormatException;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.specialOverride.common.ParameterType;
import com.ansi.scilla.web.specialOverride.common.SpecialOverrideType;
import com.ansi.scilla.web.specialOverride.response.SpecialOverrideResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class SpecialOverrideServlet extends AbstractServlet {

	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { //  Note : modeled after recommended uri parsing pattern 2018-04-19 kjw
		AnsiURL url = null;
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			
			String[] name = SpecialOverrideType.names();
			url = new AnsiURL(request, "specialOverrides", name, false);
			conn = AppUtils.getDBCPConn();
			
				
			if ( StringUtils.isBlank(url.getCommand() )) {
				sendNameDescription(conn, response);
			} else {
				SpecialOverrideType type = SpecialOverrideType.valueOf(url.getCommand());
				if(request.getParameterNames().hasMoreElements()) {
					webMessages = validateParameters(type.getSelectParms(), request);
					if(webMessages.isEmpty()) {
						sendSelectResults(conn, request, response, type);
					} else {
						sendEditErrors(conn, response, type, webMessages);
					}
				} else {
					sendParameterTypes(conn, response, url, request, type);
				}
			}
			
			
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {							// these are thrown by session validation
			super.sendForbidden(response);
		} catch ( RecordNotFoundException e ) {			// if they're asking for an id that doesn't exist
			super.sendNotFound(response);						
		} catch ( ResourceNotFoundException e) {		
			super.sendNotFound(response);
		} catch ( Exception e) {						// something bad happened
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);					// return the connection to the pool
		}
	}
	
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AnsiURL url = null;
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			SessionData sessionData = AppUtils.validateSession(request, Permission.SPECIAL_OVERRIDE_READ);
			SessionUser user = sessionData.getUser();
			String[] name = SpecialOverrideType.names();
			url = new AnsiURL(request, "specialOverrides", name, false);
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
				
			if ( StringUtils.isBlank(url.getCommand() )) {
				// since we're expecting to do an update, send a not-found if they don't tell us
				// what we're supposed to be updating.
				sendNotFound(response);
			} else {
				SpecialOverrideType type = SpecialOverrideType.valueOf(url.getCommand());
				logger.log(Level.DEBUG, "Doing a post");
				if(request.getParameterNames().hasMoreElements()) {
					logger.log(Level.DEBUG, "We've got parameters");
					webMessages = validateUpdateParameters(type.getUpdateParms(), request);
					if(webMessages.isEmpty()) {
						doUpdate(conn, response, user, url, request, type);
						sendUpdateResults(conn, request, response, type);
					} else {
						sendEditErrors(conn, response, type, webMessages);
					}
				} else {
					sendParameterTypes(conn, response, url, request, type);
				}
			}
			
			
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {							// these are thrown by session validation
			super.sendForbidden(response);
		} catch ( RecordNotFoundException e ) {			// if they're asking for an id that doesn't exist
			super.sendNotFound(response);						
		} catch ( ResourceNotFoundException e) {		
			super.sendNotFound(response);
		} catch ( Exception e) {						// something bad happened
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);					// return the connection to the pool
		}	
	}



	private WebMessages validateUpdateParameters(ParameterType[] updateParms, HttpServletRequest request) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		WebMessages webMessages = validateParameters(updateParms, request);
		if ( ! webMessages.isEmpty() ) {
			String confirm = request.getParameter("confirm_update");
			if ( StringUtils.isBlank(confirm)) {
				webMessages.addMessage("confirm", "Required Value");
			}
		}
		return webMessages;
	}



	private void doUpdate(Connection conn, HttpServletResponse response, SessionUser user, AnsiURL url,
			HttpServletRequest request, SpecialOverrideType type) throws Exception {
		int i = 1;
		try {
			conn.setAutoCommit(false);
			String sql = type.getUpdateSql().replaceAll(" where ", ", updated_by=?, updated_date=SYSDATETIME() where ");
			logger.log(Level.DEBUG, sql);
			
			
			// figure out how many parameters we need to skip before setting the bind variable
			// for the update user
			Pattern sqlPattern = Pattern.compile("^(update .*)( where )(.*)$", Pattern.CASE_INSENSITIVE);
			Pattern wherePattern = Pattern.compile("(.*=\\?)?", Pattern.CASE_INSENSITIVE);
			Matcher sqlMatcher = sqlPattern.matcher(sql);
			
			if ( ! sqlMatcher.matches() ) {
				throw new RuntimeException("Something's wrong with the sql: " + sql);
			}
			String whereClause = sqlMatcher.group(sqlMatcher.groupCount());
				
			Matcher whereMatcher = wherePattern.matcher(whereClause);
			int whereParmCount = 0;
			while ( whereMatcher.find()) {
				whereParmCount++;
			}
			int updateParmCount = type.getUpdateParms().length - whereParmCount;
			
			logger.log(Level.DEBUG, updateParmCount + "\t" + whereParmCount);
			
			
			PreparedStatement ps = conn.prepareStatement(sql);
			for(ParameterType p : type.getUpdateParms()) {
				logger.log(Level.DEBUG, i + " : " + p.getFieldName() + " : " + request.getParameter(p.getFieldName()));
				p.setPsParm(ps, request.getParameter(p.getFieldName()), i);
				if ( i == updateParmCount ) {
					i++;
					logger.log(Level.DEBUG, i + " : userId : " + user.getUserId());
					ps.setInt(i, user.getUserId());
				}
				i++;
			}
			
			ps.executeUpdate();
			conn.commit();
			
		} catch (Exception e) {
			conn.rollback();
			throw e;
		}
	}


	private void sendEditErrors(Connection conn, HttpServletResponse response, SpecialOverrideType type, WebMessages webMessages) throws Exception {
		SpecialOverrideResponse data = new SpecialOverrideResponse(type);
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);		
	}

	
	private void sendSelectResults(Connection conn, HttpServletRequest request, HttpServletResponse response,
			SpecialOverrideType type) throws Exception {
		WebMessages webMessages = new WebMessages();
		logger.log(Level.DEBUG, type.getSelectSql());
		PreparedStatement ps = conn.prepareStatement(type.getSelectSql());
		int i = 1;
		for(ParameterType p : type.getSelectParms()) {
			logger.log(Level.DEBUG, p.getFieldName() + " : " + request.getParameter(p.getFieldName()));
			p.setPsParm(ps, request.getParameter(p.getFieldName()), i);
			i++;
		}
		ResultSet rs = ps.executeQuery();
		
		SpecialOverrideResponse data = new SpecialOverrideResponse(type, rs);
		rs.close();
		
		webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}

	
	
	private void sendUpdateResults(Connection conn, HttpServletRequest request, HttpServletResponse response,
			SpecialOverrideType type) throws Exception {
		WebMessages webMessages = new WebMessages();
		logger.log(Level.DEBUG, type.getUpdateSelectSql());
		PreparedStatement ps = conn.prepareStatement(type.getUpdateSelectSql());
		int i = 1;
		for(ParameterType p : type.getUpdateSelectParms()) {
			logger.log(Level.DEBUG, p.getFieldName() + " : " + request.getParameter(p.getFieldName()));
			p.setPsParm(ps, request.getParameter(p.getFieldName()), i);
			i++;
		}
		ResultSet rs = ps.executeQuery();
		
		SpecialOverrideResponse data = new SpecialOverrideResponse(type, rs);
		rs.close();
		
		webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}

	
	private WebMessages validateParameters(ParameterType[] selectParms, HttpServletRequest request) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		WebMessages webMessages = new WebMessages();
		for(ParameterType p : selectParms) {
			String stringVal = request.getParameter(p.getFieldName());
			if(StringUtils.isBlank(stringVal)) {
				webMessages.addMessage(p.getFieldName(), "Required Value");
			} else {
				try {
					p.validate(stringVal);
				} catch (InvalidFormatException e) {
					webMessages.addMessage(p.getFieldName(), "Invalid Format");
				}
			}
		}
		return webMessages;
	}

	private void sendParameterTypes(Connection conn, HttpServletResponse response, AnsiURL url,
			HttpServletRequest request, SpecialOverrideType type) throws Exception {
		AppUtils.validateSession(request, type.getPermission());
		SpecialOverrideResponse data = new SpecialOverrideResponse(type);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}

	private void sendNameDescription(Connection conn, HttpServletResponse response) throws Exception {
		SpecialOverrideResponse data = new SpecialOverrideResponse();
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}
	
	
	
}
