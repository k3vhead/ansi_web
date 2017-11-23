package com.ansi.scilla.web.servlets.tickets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;
import com.ansi.scilla.web.ticket.request.TicketApprovalOverride;
import com.ansi.scilla.web.ticket.response.TicketApprovalOverrideResponse;
import com.ansi.scilla.web.ticket.response.TicketReturnResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TicketApprovalOverrideServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
				TicketApprovalOverride ticketRequest = new TicketApprovalOverride();
				AppUtils.json2object(jsonString, ticketRequest);
				System.out.println("TicketReturnRequest:"+ticketRequest);
				ansiURL = new AnsiURL(request, "ticketApprovalOverride", (String[])null); //  .../ticket/etc

				SessionUser sessionUser = sessionData.getUser(); 
				ticket.setTicketId(ansiURL.getId());
				ticket.selectOne(conn);
				
				TicketApprovalOverrideResponse data = new TicketApprovalOverrideResponse();
				WebMessages webMessages = new WebMessages();
				ResponseCode responseCode = null;
				if ( isValidRequest(ticket, ticketRequest)) {
					doUpdates(conn, ticket, ticketRequest, sessionUser);
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
					responseCode = ResponseCode.SUCCESS;
					conn.commit();
				} else {
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "At least one approval is required");
					responseCode = ResponseCode.EDIT_FAILURE;
				}
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, responseCode, data);
				
				
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

	/**
	 * Something somewhere has to be marked true
	 * @param ticket
	 * @param ticketRequest
	 * @return
	 */
	private boolean isValidRequest(Ticket ticket, TicketApprovalOverride ticketRequest) {
		boolean isValid = false;
		Boolean managerApproval = false;
		Boolean customerSignature = false;
		Boolean billSheet = false;
		if ( ticket.getMgrApproval() == Ticket.MGR_APPROVAL_IS_YES || (ticketRequest.getManagerApproval() != null && ticketRequest.getManagerApproval())) {
			managerApproval = true;
		}
		if ( ticket.getCustomerSignature() == Ticket.CUSTOMER_SIGNATURE_IS_YES || (ticketRequest.getCustomerSignature() != null && ticketRequest.getCustomerSignature()) ) {
			customerSignature = true;
		}
		if ( ticket.getBillSheet() == Ticket.BILL_SHEET_IS_YES || (ticketRequest.getBillSheet() != null && ticketRequest.getBillSheet()) ) {
			billSheet = true;
		}
		if ( managerApproval || customerSignature || billSheet ) {
			isValid = true;
		}
		return isValid;
	}

	private void doUpdates(Connection conn, Ticket ticket, TicketApprovalOverride ticketRequest, SessionUser sessionUser) throws Exception {
		Ticket key = new Ticket();
		key.setTicketId(ticket.getTicketId());
		
		if ( ticketRequest.getBillSheet() != null && ticketRequest.getBillSheet()) {
			ticket.setBillSheet(Ticket.BILL_SHEET_IS_YES);
		}
		if ( ticketRequest.getManagerApproval() != null && ticketRequest.getManagerApproval()) {
			ticket.setMgrApproval(Ticket.MGR_APPROVAL_IS_YES);
		}
		if ( ticketRequest.getCustomerSignature() != null && ticketRequest.getCustomerSignature()) {
			ticket.setCustomerSignature(Ticket.CUSTOMER_SIGNATURE_IS_YES);
		}
		ticket.setUpdatedBy(sessionUser.getUserId());
		ticket.update(conn, key);
	}

	
}
