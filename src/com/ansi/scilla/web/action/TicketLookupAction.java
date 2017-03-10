package com.ansi.scilla.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.actionForm.JobIdForm;

public class TicketLookupAction extends SessionPageDisplayAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		JobIdForm form = (JobIdForm)actionForm;
		if ( form != null && ! StringUtils.isBlank(form.getJobId())) {
			request.setAttribute("ANSI_JOB_ID", form.getJobId());
		}
		return super.execute(mapping, actionForm, request, response);
	}

}
