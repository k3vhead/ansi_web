package com.ansi.scilla.web.report.action;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.action.SessionPageDisplayAction;
import com.ansi.scilla.web.common.actionForm.IdForm;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.report.common.ReportType;
import com.thewebthing.commons.lang.StringUtils;

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
	public static final String REPORT_TYPE = "com_ansi_scilla_report_type";

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		
		ActionForward forward = mapping.findForward(FORWARD_IS_LOGIN);
		HttpSession session = request.getSession();
		if ( session != null ) {
			SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
			if ( sessionData != null ) {
				IdForm form = (IdForm)actionForm;
				
				if ( isValidReportType(form.getId())) {
					ReportType reportType = ReportType.valueOf(form.getId());
					
					String reportClassName = reportType.reportClassName();
					Class<?> reportClass = Class.forName(reportClassName);
					Field myField = reportClass.getDeclaredField("REPORT_TITLE");				
					String reportTitle = (String)myField.get(null);
					request.setAttribute(REPORT_TITLE, reportTitle);
					request.setAttribute(REPORT_TYPE, reportType.toString());
					
//					String jsp = reportType.jsp();
					forward = mapping.findForward(reportType.reportInputType().name());	
				} else {
					List<ReportRow> reportRowList = new ArrayList<ReportRow>();
					for ( ReportType reportType : ReportType.values()) {
						String reportClassName = reportType.reportClassName();
						Class<?> reportClass = Class.forName(reportClassName);
						Field field = reportClass.getDeclaredField("REPORT_TITLE");
						String title = (String)field.get(null);
						Permission requiredPermission = reportType.getPermission();
						reportRowList.add(new ReportRow(reportType.toString(), title, requiredPermission));
					}
					request.setAttribute("com_ansi_scilla_report_types", reportRowList);
					forward = mapping.findForward(FORWARD_IS_VALID);
				}
			
			}
		}
		return forward;

	}

	
	private boolean isValidReportType(String id) {
		boolean isValid = false;
		if ( StringUtils.isBlank(id) ) {
			isValid = false;
		} else {
			try {
				ReportType reportType = ReportType.valueOf(id);
				isValid = reportType != null;
			} catch (IllegalArgumentException e) {
				isValid = false;
			}
		}
		return isValid; 
	}
	
	public class ReportRow extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		public String reportTitle;
		public String reportType;
		public String requiredPermission;
		public ReportRow(String reportType, String reportTitle, Permission permission) {
			this.reportType = reportType;
			this.reportTitle = reportTitle;
			this.requiredPermission = permission.toString();
		}
		public String getReportTitle() {
			return reportTitle;
		}
		public String getReportType() {
			return reportType;
		}		
		public String getRequiredPermission() {
			return requiredPermission;
		}
	}
}
