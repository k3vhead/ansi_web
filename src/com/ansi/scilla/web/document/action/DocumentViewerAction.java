package com.ansi.scilla.web.document.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.common.action.AbstractAction;
import com.ansi.scilla.web.common.actionForm.IdForm;

public class DocumentViewerAction extends AbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		IdForm idForm = (IdForm)actionForm;
		request.setAttribute("ansi_document_id", idForm.getId());
		return super.execute(mapping, actionForm, request, response);
	}

}
