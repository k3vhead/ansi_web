package com.ansi.scilla.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.actionForm.ReportIdForm;
import com.ansi.scilla.web.common.ReportType;
import com.ansi.scilla.web.struts.SessionData;

/**
 * This is a traffic-cop struts action to determine which report JSP to display, dependent
 * on the report id passed in. The report form validates that the id reflects a valid report
 * (based on the ReportType enum). That same enum determines which JSP to display. Any new reports
 * require a change to the enum, not to this action module. 
 * @author dclewis
 *
 */
public class ReportAction extends SessionPageDisplayAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		
		ActionForward forward = mapping.findForward(FORWARD_IS_LOGIN);
		HttpSession session = request.getSession();
		if ( session != null ) {
			SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
			if ( sessionData != null ) {
				ReportIdForm form = (ReportIdForm)actionForm;
				String jsp = ReportType.valueOf(form.getId()).jsp();
				forward = mapping.findForward(jsp);				
			}
		}
		return forward;

	}

}
