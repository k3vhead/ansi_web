package com.ansi.scilla.web.specialOverride.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.exception.InvalidFormatException;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
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
	
	private void sendEditErrors(Connection conn, HttpServletResponse response, SpecialOverrideType type, WebMessages webMessages) throws Exception {
		SpecialOverrideResponse data = new SpecialOverrideResponse(type.getSelectParms());
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);		
	}

	
	private void sendSelectResults(Connection conn, HttpServletRequest request, HttpServletResponse response,
			SpecialOverrideType type) throws Exception {
		WebMessages webMessages = new WebMessages();
		PreparedStatement ps = conn.prepareStatement(type.getSelectSql());
		for(ParameterType p : type.getSelectParms()) {
			Integer i = 1;
			p.setPsParm(ps, request.getParameter(p.getFieldName()), i);
			i++;
		}
		ResultSet rs = ps.executeQuery();
		
		SpecialOverrideResponse data = new SpecialOverrideResponse(type.getSelectParms());
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
		SpecialOverrideResponse data = new SpecialOverrideResponse(type.getSelectParms());
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}

	private void sendNameDescription(Connection conn, HttpServletResponse response) throws Exception {
		SpecialOverrideResponse data = new SpecialOverrideResponse();
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}
	
	
	
}
