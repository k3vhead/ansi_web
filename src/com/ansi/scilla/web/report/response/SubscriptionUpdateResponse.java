package com.ansi.scilla.web.report.response;

import java.util.List;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.report.common.BatchReports;

public class SubscriptionUpdateResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private List<BatchReports> reportUpdates;

	public SubscriptionUpdateResponse() {
		super();
	}
	
	public SubscriptionUpdateResponse(List<BatchReports> reportUpdates) {
		this();
		this.reportUpdates = reportUpdates;
	}

	public List<BatchReports> getReportUpdates() {
		return reportUpdates;
	}

	public void setReportUpdates(List<BatchReports> reportUpdates) {
		this.reportUpdates = reportUpdates;
	}
	
	
}
