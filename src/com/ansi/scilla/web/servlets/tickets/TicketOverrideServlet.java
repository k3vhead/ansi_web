package com.ansi.scilla.web.servlets.tickets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.request.ticket.TicketOverrideRequest;
import com.ansi.scilla.web.response.ticket.TicketReturnResponse;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TicketOverrideServlet extends TicketServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.processGetRequest(request, response, "ticketOverride");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("Ticket Override 23 doPost");
		AnsiURL ansiURL = null; 
		Connection conn = null;
		//TicketReturnResponse ticketReturnResponse = null;
		try {
			conn = AppUtils.getDBCPConn();
			String jsonString = super.makeJsonString(request);
			System.out.println("jsonstring:"+jsonString);

			SessionData sessionData = AppUtils.validateSession(request, Permission.TICKET, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);

			Ticket ticket = new Ticket();
			try{
				TicketOverrideRequest ticketOverrideRequest = new TicketOverrideRequest();
				AppUtils.json2object(jsonString, ticketOverrideRequest);
				ansiURL = new AnsiURL(request, "ticket", (String[])null); //  .../ticket/etc

				SessionUser sessionUser = sessionData.getUser(); 
				ticket.setTicketId(ansiURL.getId());
				ticket.selectOne(conn);
				if ( ticketOverrideRequest.getNewStatus().equals(TicketStatus.COMPLETED.code())) {
					processComplete(conn, response, ticket, ticketOverrideRequest, sessionUser);
				} else if ( ticketOverrideRequest.getNewStatus().equals(TicketStatus.SKIPPED.code())) {
					processSkip(conn, response, ticket, ticketOverrideRequest, sessionUser);
				} else if ( ticketOverrideRequest.getNewStatus().equals(TicketStatus.VOIDED.code())) {
					processVoid(conn, response, ticket, ticketOverrideRequest, sessionUser);
				} else if ( ticketOverrideRequest.getNewStatus().equals(TicketStatus.REJECTED.code())) {
					processReject(conn, response, ticket, ticketOverrideRequest, sessionUser);
				} else if ( ticketOverrideRequest.getNewStatus().equals(TicketStatus.NOT_DISPATCHED.code())) {
					processRequeue(conn, response, ticket, ticketOverrideRequest, sessionUser);
				} else {
					// this is an error -- a bad action was requested
					super.sendNotAllowed(response);
				}
			}  catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				TicketReturnResponse data = new TicketReturnResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
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

	
	

}
