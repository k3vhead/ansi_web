package com.ansi.scilla.web.report.common;

import com.ansi.scilla.web.common.utils.Permission;

/**
 * These are here so we can make sure we didn't typo the object name in the enum
 */
//import com.ansi.scilla.report.datadumps.AddressUsage;
//import com.ansi.scilla.report.datadumps.ClientContact;
//import com.ansi.scilla.report.cashReceiptsRegister.CashReceiptsRegisterReport;
//import com.ansi.scilla.report.invoiceRegisterReport.InvoiceRegisterReport;
//import com.ansi.scilla.report.pac.PacReport;
//import com.ansi.scilla.web.report.webReport.SixMonthRollingVolumeWebReport;
//import com.ansi.scilla.report.ticket.TicketStatusReport;


public enum ReportType {
	/*
	 This is copied from struts-config.  They are the valid options for the destination JSP
    <forward name="reportByDivEnd" path="/reportByDivEnd.jsp" />
    <forward name="reportByDivMonthYear" path="/reportByDivMonthYear.jsp" />
    <forward name="reportByDivStartEnd" path="/reportByDivStartEnd.jsp" />
    <forward name="reportByStartEnd" path="/reportByStartEnd.jsp" />     
	*/
	
	ADDRESS_USAGE_REPORT(
			"reportNoInput",
			"com.ansi.scilla.report.datadumps.AddressUsage",
			"com.ansi.scilla.web.report.request.NoValidate", 
			new String[] {},
			Permission.SYSADMIN
		),
	CLIENT_CONTACT_REPORT(
			"reportNoInput",
			"com.ansi.scilla.report.datadumps.ClientContact",
			"com.ansi.scilla.web.report.request.NoValidate", 
			new String[] {},
			Permission.SYSADMIN
		),
	CASH_RECEIPTS_REGISTER(
			"reportByStartEnd", 
			"com.ansi.scilla.report.cashReceiptsRegister.CashReceiptsRegisterReport",
			"com.ansi.scilla.web.report.request.ValidateStartEnd", 
			new String[] {"startDate", "endDate"},
			Permission.INVOICE
		),
	DISPATCHED_OUTSTANDING_TICKET_REPORT(
			"reportByDivEnd",
			"com.ansi.scilla.report.ticket.DispatchedOutstandingTicketReport",
			"com.ansi.scilla.web.report.request.ValidateDivEnd", 
			new String[] {"divisionId", "endDate"},
			Permission.TICKET
		),
	INVOICE_REGISTER_REPORT(
			"reportByDivMonthYear",
			"com.ansi.scilla.report.invoiceRegisterReport.InvoiceRegisterReport",
			"com.ansi.scilla.web.report.request.ValidateDivMonthYear", 
			new String[] {"divisionId", "month", "year"},
			Permission.INVOICE
		),
	JOB_SCHEDULE_REPORT(
			"reportByStartEnd",
			"com.ansi.scilla.report.jobSchedule.JobScheduleReport",
			"com.ansi.scilla.web.report.request.ValidateStartEnd", 
			new String[] {"startDate", "endDate"},
			Permission.JOB_READ
		),
	PAC_REPORT(
			"reportByDivStartEnd", 
			"com.ansi.scilla.report.pac.PacReport",
			"com.ansi.scilla.web.report.request.ValidateDivStartEnd", 
			new String[] {"divisionId", "startDate", "endDate"},
			Permission.JOB
		),
	SIX_MONTH_ROLLING_VOLUME_REPORT(
			"reportByDivMonthYear", 
			"com.ansi.scilla.web.report.webReport.SixMonthRollingVolumeWebReport",
			"com.ansi.scilla.web.report.request.ValidateDivMonthYear",  
			new String[] {"divisionId", "month", "year"},
			Permission.JOB
		),
	TICKET_STATUS_REPORT(
			"reportByDivStartEnd", 
			"com.ansi.scilla.report.ticket.TicketStatusReport",
			"com.ansi.scilla.web.report.request.ValidateDivStartEnd", 
			new String[] {"divisionId", "startDate", "endDate"},
			Permission.TICKET
		);
		
	private final String jsp;
	private final String reportClassName;
	private final String validatorClassName;
	private final String[] builderParms;
	private final Permission permission;
	
	ReportType(String jsp, String reportClassName, String validatorClassName, String[] builderParms, Permission permission) {
		this.jsp = jsp;
		this.reportClassName = reportClassName;
		this.validatorClassName = validatorClassName;
		this.builderParms = builderParms;
		this.permission = permission;
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
	
	public String[] builderParms() {
		return this.builderParms;
	}


	public Permission getPermission() {
		return permission;
	}
}
