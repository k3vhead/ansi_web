package com.ansi.scilla.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.common.action.AbstractAction;
import com.ansi.scilla.web.common.struts.SessionData;

public class LogoffAction extends AbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HttpSession session =request.getSession();
		session.removeAttribute(SessionData.KEY);
		return mapping.findForward(FORWARD_IS_VALID);
	}

}
