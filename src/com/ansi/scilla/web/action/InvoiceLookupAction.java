package com.ansi.scilla.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.common.action.SessionPageDisplayAction;
import com.ansi.scilla.web.invoice.actionForm.InvoiceLookupForm;

public class InvoiceLookupAction extends SessionPageDisplayAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		InvoiceLookupForm form = (InvoiceLookupForm)actionForm;
		if ( form != null ) {
			if ( ! StringUtils.isBlank(form.getId())) {
				request.setAttribute("ANSI_DIVISION_ID", form.getId());
			}
			if ( ! StringUtils.isBlank(form.getPpcFilter())) {
				request.setAttribute("ANSI_PPC_FILTER", form.getPpcFilter());
			}
		}
		return super.execute(mapping, actionForm, request, response);
	}

}
