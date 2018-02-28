package com.ansi.scilla.web.options.response;

import com.ansi.scilla.web.common.utils.Permission;

public class ReportTypeOption extends WebOption  {
	private static final long serialVersionUID = 1L;
	
	private String reportType;
	private String requiredPermission;
	
	public ReportTypeOption(String reportType, String title, Permission requiredPermission) {
		super();
		this.display = title;
		this.reportType = reportType;
		this.requiredPermission = requiredPermission.name();
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getRequiredPermission() {
		return requiredPermission;
	}

	public void setRequiredPermission(String requiredPermission) {
		this.requiredPermission = requiredPermission;
	}
	
}
