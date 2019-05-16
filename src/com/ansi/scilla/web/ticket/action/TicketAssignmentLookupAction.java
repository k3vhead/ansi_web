package com.ansi.scilla.web.ticket.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.common.action.SessionPageDisplayAction;
import com.ansi.scilla.web.ticket.actionForm.TicketAssignmentLookupForm;

public class TicketAssignmentLookupAction extends SessionPageDisplayAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		TicketAssignmentLookupForm form = (TicketAssignmentLookupForm)actionForm;

		if ( form != null) {
			if ( ! StringUtils.isBlank(form.getJobId())) {
				request.setAttribute("ANSI_JOB_ID", form.getJobId());
			}
			if ( ! StringUtils.isBlank(form.getDivisionId())) {
				request.setAttribute("ANSI_DIVISION_ID", form.getDivisionId());
			}
			if ( ! StringUtils.isBlank(form.getTicketId())) {
				request.setAttribute("ANSI_TICKET_ID", form.getTicketId());
			}
			if ( ! StringUtils.isBlank(form.getWasherId())) {
				request.setAttribute("ANSI_WASHER_ID", form.getWasherId());
			}
			

		}
		return super.execute(mapping, actionForm, request, response);
	}

}
