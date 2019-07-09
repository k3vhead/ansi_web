package com.ansi.scilla.web.locale.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Locale;
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
import com.ansi.scilla.web.locale.request.LocaleRequest;
import com.ansi.scilla.web.locale.response.LocaleResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;



public class LocaleServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "locale";
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		AnsiURL ansiURL = null;
		try {
			ansiURL = new AnsiURL(request, REALM, (String[])null); 
			AppUtils.validateSession(request, Permission.TAX_READ);
			Connection conn = null;
			LocaleResponse localeResponseResponse = null;
			try {
				conn = AppUtils.getDBCPConn();
				localeResponseResponse = new LocaleResponse(conn, ansiURL.getId());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, localeResponseResponse); 
				
			} catch(RecordNotFoundException recordNotFoundEx) {
				super.sendNotFound(response);
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
		} catch (ResourceNotFoundException e1) {
			AppUtils.logException(e1);
			super.sendNotFound(response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		}		
		
		
		
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AnsiURL ansiURL = null;
		Connection conn = null;
		try {
			ansiURL = new AnsiURL(request, REALM, (String[])null); 
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);

			SessionData sessionData = AppUtils.validateSession(request, Permission.TAX_WRITE);
			LocaleResponse data = new LocaleResponse();
			WebMessages webMessages = new WebMessages();

			
			try{
				LocaleRequest localeRequest = new LocaleRequest();
				AppUtils.json2object(jsonString, localeRequest);
								
				if(ansiURL.getId() == null) {
					//this is add
					webMessages = localeRequest.validateAdd(conn);
					if(webMessages.isEmpty() == true) {
						doAdd(conn, localeRequest, sessionData, response);
					} else {
						data.setWebMessages(webMessages);
						super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
					}
					
				} else {
					//this is update
					webMessages = localeRequest.validateUpdate(conn, ansiURL.getId());
					if(webMessages.isEmpty() == true) {
						doUpdate(conn, ansiURL.getId(), localeRequest, sessionData, response);
					} else {
						data.setWebMessages(webMessages);
						super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
					}
				}
						
			}  catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());				
				webMessages.addMessage(badField, "Invalid Format");
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (RecordNotFoundException e) {
				//send a Bad Ticket message back
				super.sendNotFound(response);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	private void doAdd(Connection conn, LocaleRequest localeRequest, SessionData sessionData, HttpServletResponse response) throws Exception {
		Locale locale = new Locale();
		makeLocale(locale, localeRequest, sessionData.getUser());
		Integer localeId = locale.insertWithKey(conn);
		conn.commit();
		locale.setLocaleId(localeId);
		LocaleResponse localeResponse = new LocaleResponse(locale);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, localeResponse);
	}

	

	private void doUpdate(Connection conn, Integer localeId, LocaleRequest localeRequest, SessionData sessionData, HttpServletResponse response) throws Exception {
		Locale locale = new Locale();
		locale.setLocaleId(localeId);
		locale.selectOne(conn);
		makeLocale(locale, localeRequest, sessionData.getUser());
		Locale key = new Locale();
		key.setLocaleId(localeId);
		locale.update(conn, key);
		conn.commit();
		LocaleResponse localeResponse = new LocaleResponse(locale);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, localeResponse);
	}

	protected void makeLocale(Locale locale, LocaleRequest localeRequest, SessionUser sessionUser)  {
		Date today = new Date();
		
		if ( localeRequest.getLocaleId() != null ) {
			locale.setLocaleId(localeRequest.getLocaleId());
		}
		locale.setName(localeRequest.getName());
		locale.setStateName(localeRequest.getStateName());
		locale.setAbbreviation(localeRequest.getAbbreviation());
		locale.setLocaleTypeId(localeRequest.getLocaleTypeId());
		if ( locale.getAddedBy() == null ) {
			locale.setAddedBy(sessionUser.getUserId());
			locale.setAddedDate(today);
		}
		locale.setUpdatedBy(sessionUser.getUserId());
		locale.setUpdatedDate(today);
	}
	
	
	
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		super.sendNotAllowed(response);
		
	}
	
}
