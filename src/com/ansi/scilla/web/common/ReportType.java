package com.ansi.scilla.web.common;

public enum ReportType {
	/*
	 This is copied from struts-config.  They are the valid options for the destination JSP
    <forward name="reportByDivEnd" path="/reportByDivEnd.jsp" />
    <forward name="reportByDivMonthYear" path="/reportByDivMonthYear.jsp" />
    <forward name="reportByDivStartEnd" path="/reportByDivStartEnd.jsp" />
    <forward name="reportByStartEnd" path="/reportByStartEnd.jsp" />     
	*/
	
	INVOICE_REGISTER_REPORT("reportByDivEnd","com.ansi.scilla.common.report.InvoiceRegisterReport"),
	SIX_MONTH_ROLLING_VOLUME_REPORT("reportByDivMonthYear", "com.ansi.scilla.common.report.SixMonthRollingVolumeReport"),
	TICKET_STATUS_REPORT("reportByDivStartEnd", "com.ansi.scilla.common.report.TicketStatusReport");
		
	private final String jsp;
	private final String reportClassName;
	
	ReportType(String jsp, String reportClassName) {
		this.jsp = jsp;
		this.reportClassName = reportClassName;
	}
	
	public String jsp() {
		return this.jsp;
	}
	
	public String reportClassName() {
		return this.reportClassName;
	}
}
