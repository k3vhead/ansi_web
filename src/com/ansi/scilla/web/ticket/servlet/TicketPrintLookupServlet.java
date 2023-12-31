package com.ansi.scilla.web.ticket.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.ticket.request.TicketPrintLookupRequest;
import com.ansi.scilla.web.ticket.response.TicketPrintLookupResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;



public class TicketPrintLookupServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {		
		super.sendNotAllowed(response);
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		
		Connection conn = null;
		WebMessages messages = new WebMessages();
		
		try {
			conn = AppUtils.getDBCPConn();
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);

			AppUtils.validateSession(request, Permission.TICKET_WRITE);
			
			try{
				TicketPrintLookupRequest ticketRequest = new TicketPrintLookupRequest();
				AppUtils.json2object(jsonString, ticketRequest);
				logger.log(Level.DEBUG, "TicketReturnRequest:"+ticketRequest+"\tPrintDate:"+ticketRequest.getPrintDate());

				TicketPrintLookupResponse data = new TicketPrintLookupResponse(conn, ticketRequest.getPrintDate());
				messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
				
			} catch ( InvalidFormatException e) {
				String badField = super.findBadField(e.toString());
				TicketPrintLookupResponse data = new TicketPrintLookupResponse();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
		
		
		
//		Connection conn = null;
//		try {
//			conn = AppUtils.getDBCPConn();
//			AppUtils.validateSession(request, Permission.TICKET, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
//			TicketPrintLookupResponse lookupResponse = new TicketPrintLookupResponse(conn);			
//			WebMessages webMessages = new WebMessages();
//			lookupResponse.setWebMessages(webMessages);
//			super.sendResponse(conn, response, ResponseCode.SUCCESS, lookupResponse);
	}

	
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		super.sendNotAllowed(response);
	}







	
}
