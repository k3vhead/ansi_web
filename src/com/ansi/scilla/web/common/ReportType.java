package com.ansi.scilla.web.common;

public enum ReportType {
	/*
	 This is copied from struts-config.  They are the valid options for the destination JSP
    <forward name="reportByDivEnd" path="/reportByDivEnd.jsp" />
    <forward name="reportByDivMonthYear" path="/reportByDivMonthYear.jsp" />
    <forward name="reportByDivStartEnd" path="/reportByDivStartEnd.jsp" />
    <forward name="reportByStartEnd" path="/reportByStartEnd.jsp" />     
	*/
	
	INVOICE_REGISTER_REPORT("reportByDivEnd"),
	SIX_MONTH_ROLLING_VOLUME_REPORT("reportByDivMonthYear"),
	TICKET_STATUS_REPORT("reportByDivStartEnd");
	
	private final String jsp;
	
	ReportType(String jsp) {
		this.jsp = jsp;
	}
	
	public String jsp() {
		return this.jsp;
	}
}
