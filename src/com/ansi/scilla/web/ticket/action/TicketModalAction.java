package com.ansi.scilla.web.ticket.action;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.common.action.SessionPageDisplayAction;
import com.ansi.scilla.web.common.actionForm.IdForm;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.ticket.response.TicketReturnResponse;

public class TicketModalAction extends SessionPageDisplayAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		
		ActionForward forward = mapping.findForward(FORWARD_IS_LOGIN);
		IdForm form = (IdForm)actionForm;
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			AppUtils.validateSession(request, Permission.TICKET_READ);
			TicketReturnResponse ticketReturnResponse = new TicketReturnResponse(conn, Integer.valueOf(form.getId()));
			request.setAttribute(TicketReturnResponse.KEY, ticketReturnResponse);
			forward = mapping.findForward(FORWARD_IS_VALID);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			forward = mapping.findForward(FORWARD_IS_LOGIN);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
		
		return forward;
	}

}
