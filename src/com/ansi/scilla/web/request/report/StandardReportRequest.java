package com.ansi.scilla.web.request.report;

import java.util.Calendar;
import java.util.HashMap;

import com.ansi.scilla.web.request.AbstractRequest;
import com.fasterxml.jackson.annotation.JsonFormat;

public class StandardReportRequest extends AbstractRequest {
	private static final long serialVersionUID = 1L;
	private Integer divisionId;
	private Calendar startDate;
	private Calendar endDate;
	private Integer month;
	private Integer year;
	private HashMap<String, String> reportDisplay;

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getStartDate() {
		return startDate;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getEndDate() {
		return endDate;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public HashMap<String, String> getReportDisplay() {
		return reportDisplay;
	}

	public void setReportDisplay(HashMap<String, String> reportDisplay) {
		this.reportDisplay = reportDisplay;
	}
}
