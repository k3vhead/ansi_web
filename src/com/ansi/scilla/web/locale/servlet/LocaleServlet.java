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
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.locale.request.LocaleRequest;
import com.ansi.scilla.web.locale.response.LocaleResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;



public class LocaleServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "locale";
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		//TicketReturnResponse ticketReturnResponse = null;
		try {
			conn = AppUtils.getDBCPConn();
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);

			SessionData sessionData = AppUtils.validateSession(request, Permission.TAX_WRITE);
			LocaleResponse data = new LocaleResponse();
			WebMessages webMessages = new WebMessages();

			
			try{
				LocaleRequest localeRequest = new LocaleRequest();
				AppUtils.json2object(jsonString, localeRequest);
				Date today = new Date();				
				SessionUser sessionUser = sessionData.getUser(); 
				
				Locale locale = new Locale();
				
				if(localeRequest.getLocaleId() == null) {
					//this is add
					localeRequest.validateAdd(conn);
					
					locale = doAdd(locale, localeRequest);
					
				} else {
					//this is update
					localeRequest.validateUpdate(conn);
					
					locale = doUpdate(locale, localeRequest);
				}
				
				
				data = new LocaleResponse(localeRequest.getLocaleId(), localeRequest.getName(), localeRequest.getStateName(), 
						localeRequest.getAbbreviation(), localeRequest.getLocaleTypeId());
				
				try {
					locale.selectOne(conn);
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Duplicate");
				} catch ( RecordNotFoundException e) {
					locale.setAddedBy(sessionUser.getUserId());
					locale.setAddedDate(today);
					locale.setUpdatedBy(sessionUser.getUserId());
					locale.setUpdatedDate(today);
					locale.insertWithNoKey(conn);
					conn.commit();
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
				}
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data);		
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

	protected Locale doAdd(Locale locale, LocaleRequest localeRequest) {
		locale.setName(localeRequest.getName());
		locale.setStateName(localeRequest.getStateName());
		if(localeRequest.getAbbreviation() != null) {
			locale.setAbbreviation(localeRequest.getAbbreviation());
		}
		locale.setLocaleTypeId(localeRequest.getLocaleTypeId());
		return locale;
	}
	
	protected Locale doUpdate(Locale locale, LocaleRequest localeRequest) {
		locale.setLocaleId(localeRequest.getLocaleId());
		locale.setName(localeRequest.getName());
		locale.setStateName(localeRequest.getStateName());
		if(localeRequest.getAbbreviation() != null) {
			locale.setAbbreviation(localeRequest.getAbbreviation());
		}
		locale.setLocaleTypeId(localeRequest.getLocaleTypeId());
		return locale;
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		super.sendNotAllowed(response);
		
	}
	
}
