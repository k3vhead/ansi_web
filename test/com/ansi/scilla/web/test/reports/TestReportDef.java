package com.ansi.scilla.web.test.reports;

import java.util.Calendar;

import com.ansi.scilla.web.common.ReportType;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.request.report.ReportDefinition;

public class TestReportDef extends ReportDefinition {

	private static final long serialVersionUID = 1L;

	public TestReportDef(String reportId, Calendar startDate, Calendar endDate) throws Exception {
		super();
		try {
			super.reportType = ReportType.valueOf(reportId);
		} catch ( IllegalArgumentException e ) {
			throw new ResourceNotFoundException(reportId);
		}
		super.startDate = startDate;
		super.endDate = endDate;
	}
	
	
}
