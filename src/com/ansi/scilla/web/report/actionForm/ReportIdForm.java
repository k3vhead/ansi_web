package com.ansi.scilla.web.report.actionForm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.actionForm.IdForm;
import com.ansi.scilla.web.report.common.ReportType;
import com.thewebthing.commons.lang.StringUtils;

public class ReportIdForm extends IdForm {

	private static final long serialVersionUID = 1L;
	
	

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		if ( ! isValidReport(getId())) {
			errors.add(ID, new ActionMessage("err.invalid.reportId"));
		}
		
		List<ReportRow> reportRowList = new ArrayList<ReportRow>();
		try {
			for ( ReportType reportType : ReportType.values()) {
				String reportClassName = reportType.reportClassName();
				Class<?> reportClass = Class.forName(reportClassName);
				Field field = reportClass.getDeclaredField("REPORT_TITLE");
				String title = (String)field.get(null);
				reportRowList.add(new ReportRow(reportType.toString(), title));
			}
			request.setAttribute("com_ansi_scilla_report_types", reportRowList);
		} catch ( Exception e) {
			throw new RuntimeException(e);
		}
		
		return errors;
	}
	
	
	private boolean isValidReport(String id) {
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
		public ReportRow(String reportType, String reportTitle) {
			this.reportType = reportType;
			this.reportTitle = reportTitle;
		}
		public String getReportTitle() {
			return reportTitle;
		}
		public String getReportType() {
			return reportType;
		}		
		
	}
}
