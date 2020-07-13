package com.ansi.scilla.web.report.request;

import java.sql.Connection;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.report.common.BatchReports;

public class SubscriptionRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String REPORT_ID = "reportId";
	public static final String DIVISION_ID = "divisionId";
	public static final String SUBSCRIBE = "subscribe";
	public static final String ALL_DIVISIONS = "allDivisions";
	public static final String ALL_REPORT_TYPE = "allReportType";
		
	private String reportId;
	private Integer divisionId;
	private Boolean subscribe;
	private Boolean allDivisions = false;
	private String allReportType = AllReportType.NONE.name();
	
	public String getReportId() {
		return reportId;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	public Boolean getSubscribe() {
		return subscribe;
	}
	public void setSubscribe(Boolean subscribe) {
		this.subscribe = subscribe;
	}
	public Boolean getAllDivisions() {
		return allDivisions;
	}
	public void setAllDivisions(Boolean allDivisions) {
		this.allDivisions = allDivisions;
	}
	public String getAllReportType() {
		return allReportType;
	}
	public void setAllReportType(String allReportType) {
		this.allReportType = allReportType;
	}

	public void validate(Connection conn, WebMessages webMessages) throws Exception {
		AllReportType allReportType = AllReportType.NONE;
		if ( ! StringUtils.isBlank(this.allReportType) ) {
			try {
				allReportType = AllReportType.valueOf(this.allReportType);
				if ( allReportType == null ) {
					webMessages.addMessage(ALL_REPORT_TYPE, "Invalid value");
				}
			} catch ( IllegalArgumentException e ) {
				webMessages.addMessage(ALL_REPORT_TYPE, "Invalid value");
			}
		}
		
//		if ( this.allDivisions == null ) {
//			webMessages.addMessage(ALL_DIVISIONS, "Required Entry");
//		}

		RequestValidator.validateBoolean(webMessages, SUBSCRIBE, this.subscribe, true);
		
		if (webMessages.isEmpty() && allReportType.equals(AllReportType.NONE)) {
			RequestValidator.validateReportId(webMessages, REPORT_ID, this.reportId, true);	
		}
		
		if ( webMessages.isEmpty() && allReportType.equals(AllReportType.NONE) && ! StringUtils.isBlank(this.reportId) ) {
			BatchReports batchReport = BatchReports.valueOf(this.reportId);
			if ( batchReport.isDivisionReport() ) {
				RequestValidator.validateDivisionId(conn, webMessages, DIVISION_ID, this.divisionId, true);
			}
		}
	}



	
	
		
		
	
	
	
}
