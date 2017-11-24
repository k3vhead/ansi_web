package com.ansi.scilla.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.common.action.SessionPageDisplayAction;
import com.ansi.scilla.web.common.actionForm.IdForm;

public class QuoteMaintenanceAction extends SessionPageDisplayAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		IdForm form = (IdForm)actionForm;
		if ( form != null && ! StringUtils.isBlank(form.getId())) {
			request.setAttribute("ANSI_QUOTE_ID", form.getId());
		}
		return super.execute(mapping, actionForm, request, response);
	}

}
