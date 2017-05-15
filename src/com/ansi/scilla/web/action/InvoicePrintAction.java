package com.ansi.scilla.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.actionForm.MessageForm;

public class InvoicePrintAction extends SessionPageDisplayAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		MessageForm form = (MessageForm)actionForm;
		
		ActionForward forward = super.execute(mapping, actionForm, request, response);
		if ( forward.equals(mapping.findForward(FORWARD_IS_VALID))) {
			request.setAttribute("invoice_gen_message", form.getMessage());
		}
		return forward;
	}

}
