package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.bcr.request.BcrExpenseRequest;
import com.ansi.scilla.web.bcr.response.BcrExpenseResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payment.response.PaymentResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class BcrExpenseServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			try{
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				String jsonString = super.makeJsonString(request);
				logger.log(Level.DEBUG, jsonString);
				SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
				BcrExpenseRequest bcrTicketRequest = new BcrExpenseRequest();
				AppUtils.json2object(jsonString, bcrTicketRequest);
				SessionUser sessionUser = sessionData.getUser();
				List<SessionDivision> divisionList = sessionData.getDivisionList();
				
//				String[] uriPath = request.getRequestURI().split("/");
//				String ticket = uriPath[uriPath.length-1];
//				
//				if ( StringUtils.isNumeric(ticket)) {
//					RequestValidator.validateId(conn, webMessages, Ticket.TABLE, Ticket.TICKET_ID, WebMessages.GLOBAL_MESSAGE, Integer.valueOf(ticket), true);
//					if ( webMessages.isEmpty() ) {
//						webMessages = bcrTicketRequest.validate(conn, sessionUser, divisionList, divisionId, workYear, workWeeks);						
						BcrExpenseResponse data = new BcrExpenseResponse();
//						if ( webMessages.isEmpty() ) {
//							if ( bcrTicketRequest.getClaimId() == null ) {
//								insertClaim(conn, Integer.valueOf(ticket), bcrTicketRequest, sessionUser);
//							} else {
//								updateClaim(conn, Integer.valueOf(ticket), bcrTicketRequest, sessionUser);
//							}
//							data = new BcrTicketClaimResponse(conn, sessionUser.getUserId(), divisionList, divisionId, workYear, workWeeks, Integer.valueOf(ticket));
							super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
//						} else {
//							data.setWebMessages(webMessages);
//							super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
//						}
//					} else {
//						super.sendNotFound(response);
//					}
//				} else {
//					super.sendNotFound(response);
//				}
				
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				PaymentResponse data = new PaymentResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
				super.sendForbidden(response);			
			}
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
}
