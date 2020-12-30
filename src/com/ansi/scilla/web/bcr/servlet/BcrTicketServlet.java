package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.web.bcr.response.BcrTicketResponse;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.SuccessMessage;
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

public class BcrTicketServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_READ);
			SessionUser sessionUser = sessionData.getUser();
			List<SessionDivision> divisionList = sessionData.getDivisionList();
			Integer divisionId = Integer.valueOf(request.getParameter("divisionId"));
			Integer workYear = Integer.valueOf(request.getParameter("workYear"));
			String workWeeks = request.getParameter("workWeeks");  // comma-delimited list of work weeks.
			logger.log(Level.DEBUG, "Parms: " + divisionId + " " + workYear + " " + workWeeks);
			String[] uriPath = request.getRequestURI().split("/");
			String ticket = uriPath[uriPath.length-1];
			if ( StringUtils.isNumeric(ticket)) {
				RequestValidator.validateId(conn, webMessages, Ticket.TABLE, Ticket.TICKET_ID, WebMessages.GLOBAL_MESSAGE, Integer.valueOf(ticket), true);
				if ( webMessages.isEmpty() ) {
					BcrTicketResponse data = new BcrTicketResponse(conn, sessionUser.getUserId(), divisionList, divisionId, workYear, workWeeks, Integer.valueOf(ticket));
					data.setWebMessages(new SuccessMessage());
					super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
				} else {
					super.sendNotFound(response);
				}
			} else {
				super.sendNotFound(response);
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
	}
}
