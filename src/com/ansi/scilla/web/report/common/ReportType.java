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
	
	ACCOUNTS_RECEIVABLE_TOTALS_SUMMARY(
			"reportNoInput",
			"com.ansi.scilla.report.datadumps.AccountsReceivableTotalsSummary",
			"com.ansi.scilla.web.report.request.NoValidate", 
			new String[] {},
			Permission.TECH_ADMIN_READ
		),
	ACCOUNTS_RECEIVABLE_TOTALS_SUMMARY_BY_DIVISION(
			"reportByDiv",
			"com.ansi.scilla.report.datadumps.AccountsReceivableTotalsSummaryByDiv",
			"com.ansi.scilla.web.report.request.ValidateDiv", 
			new String[] {"divisionId"},
			Permission.INVOICE_READ
		),
	ACCOUNTS_RECEIVABLE_TOTALS_OVER_60_DETAIL(
			"reportNoInput",
			"com.ansi.scilla.report.datadumps.AccountsReceivableTotalsOver60Detail",
			"com.ansi.scilla.web.report.request.NoValidate", 
			new String[] {},
			Permission.TECH_ADMIN_READ
		),
	ACCOUNTS_RECEIVABLE_TOTALS_OVER_60_DETAIL_BY_DIVISION(
			"reportByDiv",
			"com.ansi.scilla.report.datadumps.AccountsReceivableTotalsOver60DetailByDiv",
			"com.ansi.scilla.web.report.request.ValidateDiv", 
			new String[] {"divisionId"},
			Permission.INVOICE_READ
		),
	ADDRESS_USAGE_REPORT(
			"reportNoInput",
			"com.ansi.scilla.report.datadumps.AddressUsage",
			"com.ansi.scilla.web.report.request.NoValidate", 
			new String[] {},
			Permission.QUOTE_UPDATE
		),
	AGING_AR_TOTALS_REPORT(
			"reportNoInput",
			"com.ansi.scilla.report.datadumps.AgingARTotals",
			"com.ansi.scilla.web.report.request.NoValidate", 
			new String[] {},
			Permission.TECH_ADMIN_READ
		),
	AGING_CASH_TOTALS_REPORT(
			"reportNoInput",
			"com.ansi.scilla.report.datadumps.AgingCashTotals",
			"com.ansi.scilla.web.report.request.NoValidate", 
			new String[] {},
			Permission.TECH_ADMIN_READ
		),
	AGING_SERVICE_TAX_TOTALS_REPORT(
			"reportNoInput",
			"com.ansi.scilla.report.datadumps.AgingServiceTaxTotals",
			"com.ansi.scilla.web.report.request.NoValidate", 
			new String[] {},
			Permission.TECH_ADMIN_READ
		),
	AGING_PAYMENT_TOTALS_REPORT(
			"reportNoInput",
			"com.ansi.scilla.report.datadumps.AgingPaymentTotals",
			"com.ansi.scilla.web.report.request.NoValidate", 
			new String[] {},
			Permission.TECH_ADMIN_READ
		),
	AGING_INVOICE_TOTALS_REPORT(
			"reportNoInput",
			"com.ansi.scilla.report.datadumps.AgingInvoiceTotals",
			"com.ansi.scilla.web.report.request.NoValidate", 
			new String[] {},
			Permission.TECH_ADMIN_READ
		),
	CLIENT_CONTACT_REPORT(
			"reportNoInput",
			"com.ansi.scilla.report.datadumps.ClientContact",
			"com.ansi.scilla.web.report.request.NoValidate", 
			new String[] {},
			Permission.QUOTE_UPDATE
		),
	CASH_RECEIPTS_REGISTER(
			"reportByStartEnd", 
			"com.ansi.scilla.report.cashReceiptsRegister.CashReceiptsRegisterReport",
			"com.ansi.scilla.web.report.request.ValidateStartEnd", 
			new String[] {"startDate", "endDate"},
			Permission.PAYMENT_READ
		),
	DISPATCHED_OUTSTANDING_TICKET_REPORT(
			"reportByDivEnd",
			"com.ansi.scilla.report.ticket.DispatchedOutstandingTicketReport",
			"com.ansi.scilla.web.report.request.ValidateDivEnd", 
			new String[] {"divisionId", "endDate"},
			Permission.TICKET_READ
		),
	INVOICE_REGISTER_REPORT(
			"reportByDivMonthYear",
			"com.ansi.scilla.report.invoiceRegisterReport.InvoiceRegisterReport",
			"com.ansi.scilla.web.report.request.ValidateDivMonthYear", 
			new String[] {"divisionId", "month", "year"},
			Permission.INVOICE_READ
		),
	JOB_SCHEDULE_REPORT(
			"reportByStartEnd",
			"com.ansi.scilla.report.jobSchedule.JobScheduleReport",
			"com.ansi.scilla.web.report.request.ValidateStartEnd", 
			new String[] {"startDate", "endDate"},
			Permission.TECH_ADMIN_READ
		),
	MONTHLY_SERVICE_TAX_BY_DAY_REPORT(
			"reportByStartEnd", 
			"com.ansi.scilla.report.monthlyServiceTaxReport.MonthlyServiceTaxByDayReport",
			"com.ansi.scilla.web.report.request.ValidateStartEnd", 
			new String[] {"startDate", "endDate"},
			Permission.PAYMENT_WRITE
		),
	MONTHLY_SERVICE_TAX_REPORT(
			"reportByStartEnd", 
			"com.ansi.scilla.report.monthlyServiceTaxReport.MonthlyServiceTaxReport",
			"com.ansi.scilla.web.report.request.ValidateStartEnd", 
			new String[] {"startDate", "endDate"},
			Permission.PAYMENT_WRITE
		),
	PAC_REPORT(
			"reportByDivStartEnd", 
			"com.ansi.scilla.report.pac.PacReport",
			"com.ansi.scilla.web.report.request.ValidateDivStartEnd", 
			new String[] {"divisionId", "startDate", "endDate"},
			Permission.QUOTE_READ
		),
	SIX_MONTH_ROLLING_VOLUME_REPORT(
			"reportByDivMonthYear", 
			"com.ansi.scilla.web.report.webReport.SixMonthRollingVolumeWebReport",
			"com.ansi.scilla.web.report.request.ValidateDivMonthYear",  
			new String[] {"divisionId", "month", "year"},
			Permission.TECH_ADMIN_READ
		),
	TICKET_STATUS_REPORT(
			"reportByDivStartEnd", 
			"com.ansi.scilla.report.ticket.TicketStatusReport",
			"com.ansi.scilla.web.report.request.ValidateDivStartEnd", 
			new String[] {"divisionId", "startDate", "endDate"},
			Permission.TICKET_READ
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

	public String getLink() {
		return "report.html?id=" + this.name();
	}
	
	public String getTitle() throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String reportClassName = this.reportClassName();
		Class<?> reportClass = Class.forName(reportClassName);
		java.lang.reflect.Field field = reportClass.getDeclaredField("REPORT_TITLE");
		String title = (String)field.get(null);
		return title;
	}
}
