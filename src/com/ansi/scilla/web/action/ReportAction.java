package com.ansi.scilla.web.action;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.common.reportBuilder.AbstractReport;
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
	
	public static final String REPORT_TITLE = "com_ansi_scilla_report_title";

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		
		ActionForward forward = mapping.findForward(FORWARD_IS_LOGIN);
		HttpSession session = request.getSession();
		if ( session != null ) {
			SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
			if ( sessionData != null ) {
				ReportIdForm form = (ReportIdForm)actionForm;
				ReportType reportType = ReportType.valueOf(form.getId());
				
				String reportClassName = reportType.reportClassName();
				Class<?> reportClass = Class.forName(reportClassName);
				AbstractReport report = (AbstractReport)reportClass.newInstance();
				Method method = reportClass.getMethod("getTitle", (Class<?>[])null);
				String reportTitle = (String)method.invoke(report, (Object[])null);
				request.setAttribute(REPORT_TITLE, reportTitle);
				
				String jsp = reportType.jsp();
				forward = mapping.findForward(jsp);				
			}
		}
		return forward;

	}

}
