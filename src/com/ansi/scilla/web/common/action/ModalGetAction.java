package com.ansi.scilla.web.common.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.common.actionForm.NamespaceForm;

public class ModalGetAction extends AbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		NamespaceForm form = (NamespaceForm)actionForm;
		request.setAttribute("panelname", form.getNamespace());
		return mapping.findForward("valid");
	}

}
