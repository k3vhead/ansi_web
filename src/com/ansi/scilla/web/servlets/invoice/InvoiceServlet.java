package com.ansi.scilla.web.servlets.invoice;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.response.invoice.InvoiceTicketResponse;
import com.ansi.scilla.web.response.ticket.TicketReturnResponse;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class InvoiceServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AnsiURL url = null;
		Connection conn = null;
		try {
			try {
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				AppUtils.validateSession(request, Permission.INVOICE, PermissionLevel.PERMISSION_LEVEL_IS_READ);
				url = new AnsiURL(request, "invoice", (String[])null);
				if ( url.getId() == null ) {
					super.sendNotFound(response);
				} else {
					InvoiceTicketResponse data = new InvoiceTicketResponse(conn, url.getId());
					WebMessages webMessages = new WebMessages();
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
					data.setWebMessages(webMessages);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
				}
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				TicketReturnResponse data = new TicketReturnResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
				super.sendForbidden(response);
			} catch (ResourceNotFoundException e) {
				super.sendNotAllowed(response);
			} 
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
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


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendNotAllowed(response);
	}

}
