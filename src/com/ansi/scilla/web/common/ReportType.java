package com.ansi.scilla.web.common;

public enum ReportType {
	/*
	 This is copied from struts-config.  They are the valid options for the destination JSP
    <forward name="reportByDivEnd" path="/reportByDivEnd.jsp" />
    <forward name="reportByDivMonthYear" path="/reportByDivMonthYear.jsp" />
    <forward name="reportByDivStartEnd" path="/reportByDivStartEnd.jsp" />
    <forward name="reportByStartEnd" path="/reportByStartEnd.jsp" />     
	*/
	
	CASH_RECEIPTS_REGISTER(
			"reportByStartEnd", 
			"com.ansi.scilla.common.report.cashReceiptsRegister.CashReceiptsRegisterReport",
			"com.ansi.scilla.web.request.report.ValidateStartEnd"
		),
	INVOICE_REGISTER_REPORT(
			"reportByDivEnd",
			"com.ansi.scilla.common.report.invoiceRegisterReport.InvoiceRegisterReport",
			"com.ansi.scilla.web.request.report.ValidateDivEnd"
		),
	PAC_REPORT(
			"reportByDivStartEnd", 
			"com.ansi.scilla.common.report.pac.PacReport",
			"com.ansi.scilla.web.request.report.ValidateDivStartEnd"
		),
	SIX_MONTH_ROLLING_VOLUME_REPORT(
			"reportByDivMonthYear", 
			"com.ansi.scilla.common.report.sixMonthRollingVolume.SixMonthRollingVolumeReport",
			"com.ansi.scilla.web.request.report.ValidateDivMonthYear"
		),
	TICKET_STATUS_REPORT(
			"reportByDivStartEnd", 
			"com.ansi.scilla.common.report.ticketStatus.TicketStatusReport",
			"com.ansi.scilla.web.request.report.ValidateDivStartEnd"
		);
		
	private final String jsp;
	private final String reportClassName;
	private final String validatorClassName;
	
	ReportType(String jsp, String reportClassName, String validatorClassName) {
		this.jsp = jsp;
		this.reportClassName = reportClassName;
		this.validatorClassName = validatorClassName;
	}
	
	public String jsp() {
		return this.jsp;
	}
	
	public String reportClassName() {
		return this.reportClassName;
	}
	
	public String validatorClassName() {
		return this.validatorClassName;
	}
}
