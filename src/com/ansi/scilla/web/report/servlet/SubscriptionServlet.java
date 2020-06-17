package com.ansi.scilla.web.report.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

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
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.report.request.SubscriptionRequest;
import com.ansi.scilla.web.report.response.SubscriptionResponse;
import com.ansi.scilla.web.report.response.SubscriptionUpdateResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class SubscriptionServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "subscription";
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
				
		Connection conn = null;
		AnsiURL url = null;
		WebMessages messages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			SessionData sessionData = AppUtils.validateSession(request, Permission.REPORT_SUBSCRIPTION_READ);
			url = new AnsiURL(request, REALM, (String[])null, false);				
			SubscriptionResponse data = new SubscriptionResponse(conn, sessionData.getUser().getUserId());
			data.setWebMessages(messages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;

		try {
			conn = AppUtils.getDBCPConn();
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);

			SessionData sessionData = AppUtils.validateSession(request, Permission.REPORT_SUBSCRIPTION_WRITE);
			SubscriptionUpdateResponse data = new SubscriptionUpdateResponse();
			WebMessages webMessages = new WebMessages();

			
			try{
				SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
				AppUtils.json2object(jsonString, subscriptionRequest);
				SessionUser sessionUser = sessionData.getUser(); 
				
				subscriptionRequest.validate(conn, webMessages);
				if ( webMessages.isEmpty()) {
					doUpdate(conn, subscriptionRequest, sessionUser, response);
				} else {
					data.setWebMessages(webMessages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);		
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




	private void doUpdate(Connection conn, SubscriptionRequest subscriptionRequest, SessionUser sessionUser, HttpServletResponse response) {
		
		
	}

	
	
	
}
