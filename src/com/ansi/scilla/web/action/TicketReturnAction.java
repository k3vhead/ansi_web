package com.ansi.scilla.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.web.actionForm.IdForm;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;

public class TicketReturnAction extends SessionPageDisplayAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		
		ActionForward forward = mapping.findForward(FORWARD_IS_LOGIN);
		try {
			AppUtils.validateSession(request, Permission.TECH_ADMIN, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			IdForm form = (IdForm)actionForm;
			if ( form != null && ! StringUtils.isBlank(form.getId())) {
				request.setAttribute("ANSI_TICKET_ID", form.getId());
			}
			forward = mapping.findForward(FORWARD_IS_VALID);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			forward = mapping.findForward(FORWARD_IS_LOGIN);
		}
		
		
		return forward;
	}

}
