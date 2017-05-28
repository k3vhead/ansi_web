package com.ansi.scilla.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.actionForm.TicketLookupForm;

public class TicketLookupAction extends SessionPageDisplayAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		TicketLookupForm form = (TicketLookupForm)actionForm;

		if ( form != null) {
			if ( ! StringUtils.isBlank(form.getJobId())) {
				request.setAttribute("ANSI_JOB_ID", form.getJobId());
			}
			if ( ! StringUtils.isBlank(form.getDivisionId())) {
				request.setAttribute("ANSI_DIVISION_ID", form.getDivisionId());
			}
			if ( ! StringUtils.isBlank(form.getStartDate())) {
				request.setAttribute("ANSI_TICKET_LOOKUP_START_DATE", form.getStartDate());
			}

		}
		return super.execute(mapping, actionForm, request, response);
	}

}
