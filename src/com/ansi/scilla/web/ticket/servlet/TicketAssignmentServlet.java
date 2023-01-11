package com.ansi.scilla.web.ticket.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.TicketAssignment;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.ticket.request.TicketAssignmentRequest;
import com.ansi.scilla.web.ticket.response.TicketAssignmentResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;



public class TicketAssignmentServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "ticketAssignment";
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		//TicketReturnResponse ticketReturnResponse = null;
		try {
			conn = AppUtils.getDBCPConn();
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);

			SessionData sessionData = AppUtils.validateSession(request, Permission.TICKET_WRITE);
			TicketAssignmentResponse data = new TicketAssignmentResponse();
			WebMessages webMessages = new WebMessages();

			
			try{
				TicketAssignmentRequest ticketAssignmentRequest = new TicketAssignmentRequest();
				AppUtils.json2object(jsonString, ticketAssignmentRequest);
				Date today = new Date();				
				SessionUser sessionUser = sessionData.getUser(); 
				
				TicketAssignment ticketAssignment = new TicketAssignment();
				ticketAssignment.setTicketId(ticketAssignmentRequest.getTicketId());
				ticketAssignment.setWasherId(ticketAssignmentRequest.getWasherId());
				data = new TicketAssignmentResponse(ticketAssignmentRequest.getWasherId(), ticketAssignmentRequest.getTicketId());
				try {
					ticketAssignment.selectOne(conn);
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Duplicate");
				} catch ( RecordNotFoundException e) {
					ticketAssignment.setAddedBy(sessionUser.getUserId());
					ticketAssignment.setAddedDate(today);
					ticketAssignment.setUpdatedBy(sessionUser.getUserId());
					ticketAssignment.setUpdatedDate(today);
					ticketAssignment.insertWithNoKey(conn);
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

	
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		super.sendNotAllowed(response);
	}







	
}
