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
	public static final String GROUP_ID = "groupId";
		
	private String reportId;
	private Integer divisionId;
	private Boolean subscribe;
	private Boolean allDivisions = false;
	private String allReportType = AllReportType.NONE.name();
	private Integer groupId;
	
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
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	public void validate(Connection conn, WebMessages webMessages) throws Exception {
		// for now we're not doing an "all report" or "all group" request, so we're skipping this part
		/**
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
		
		if ( this.allDivisions == null ) {
			webMessages.addMessage(ALL_DIVISIONS, "Required Entry");
		}
		**/
		
		// required entries: report type and subscription off/on
		RequestValidator.validateReportId(webMessages, REPORT_ID, this.reportId, true);	
		RequestValidator.validateBoolean(webMessages, SUBSCRIBE, this.subscribe, true);	
		
		// if we have required entries, make sure associated data is valid
		if ( webMessages.isEmpty() ) {
			BatchReports batchReport = BatchReports.valueOf(this.reportId);
			boolean needsGroup = false;
			boolean needsDiv = false;
			
			if ( batchReport.isDivisionReport() ) {
				needsDiv = true;
			} 
			if ( batchReport.isAllAnsiReport() ) {
				// no division/group id validation required
			} 
			if ( batchReport.isSummaryReport() ) {
				needsGroup = true;
			} 
			if ( batchReport.isTrendReport() ) {
				needsGroup = true;				
			} 
			if ( batchReport.isUtilityReport() ) {
				// no division/group id validation required
			} 
			
			if ( needsGroup ) {
				if ( needsDiv ) {
					// needs group & div, so we really only need one
					if ( this.groupId == null && this.divisionId == null ) {
						webMessages.addMessage(DIVISION_ID, "Required Value");
						webMessages.addMessage(GROUP_ID, "Required Value");
					} else {
						if ( this.divisionId != null ) {
							RequestValidator.validateDivisionId(conn, webMessages, DIVISION_ID, this.divisionId, true);
						}
						if ( this.groupId != null ) {
							RequestValidator.validateDivisionGroupId(conn, webMessages, GROUP_ID, this.groupId, true);
						}
					}
				} else {
					// needs group & ! div, so we really need group
					RequestValidator.validateDivisionGroupId(conn, webMessages, GROUP_ID, this.groupId, true);
				}
			} else {
				if ( needsDiv ) {
					// needs div, but not group, so we really need div
					RequestValidator.validateDivisionId(conn, webMessages, DIVISION_ID, this.divisionId, true);
				} else {
					// doesn't need div or group, so go on with life
				}				
			}
			
			
			
			
		}
	}



	
	
		
		
	
	
	
}
