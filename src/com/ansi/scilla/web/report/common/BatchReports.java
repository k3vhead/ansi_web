package com.ansi.scilla.web.report.common;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

import com.ansi.scilla.common.utils.Permission;

/**
 * AllAnsi - Report contains data from across the enterprise. It may or may not contain group or division-specific
 * 			data as supporting detail.
 * SummaryReport - Report contains data from a predefined group of divisions (Region, Company, other arbitrary set
 * 			of divisions). It may or may not contain division-specific data as supporting detail.
 * DivisionReport - Report contains data specific to a division
 * TrendReport - Historical data
 * UtiltyReport - Data that is not specific to any particular division or group of divisions
 * 
 * @author dclewis
 *
 */
public enum BatchReports {

	// Weekly
	PAC_WEEKLY("PAC Weekly",                false, false, true, false, false, Permission.QUOTE_READ),
	DO_LIST(   "Dispatched & Outstanding",  false, false, true, false, false, Permission.TICKET_READ),
	PD45(      "45-Day Past Due",           false, false, true, false, false, Permission.PAYMENT_READ),
	TSR(       "Ticket Status",             false, false, true, false, false, Permission.TICKET_READ ),
	
	// Monthly - before the month end
	DRV(       "Detailed Rolling Volume",   false, false, true, false, false, Permission.TICKET_READ ),
	
	
	// Monthly - after the monthly close
	PAC_MONTHLY("PAC Monthly",                              false, false, true,  false, false, Permission.QUOTE_READ ),
	LIFT_GENIE( "Lift/Genie Monthly",                       true,  false, false, false, false, Permission.TICKET_READ ),
	CASH_RECEIPTS_REGISTER("Cash Receipts Register",        true,  false, false, false, false, Permission.PAYMENT_READ ),
	SERVICE_TAX("Service Taxes",                            true,  false, false, false, false, Permission.PAYMENT_WRITE ),
	IR_SUMMARY( "Invoice Register Summary",                 true,  false, false, false, false, Permission.INVOICE_READ ),
	IR(         "Invoice Register Detail",                  false, false, true,  false, false, Permission.INVOICE_READ ),
	AR_TOTALS(  "Accounts Receivable w over 60 day detail", true,  false, true,  false, false, Permission.INVOICE_READ ), // divisions can get this but not batched
	AR_LISTING( "AR Listing",				                true,  false, false, false, false, Permission.INVOICE_READ ), 	//exec (trend)

	// Quarterly - after quarter close
	PAC_QUARTER(                 "PAC Quarterly",                false, false, true,  false, false, Permission.QUOTE_READ ),
	PAC_YTD(                     "PAC Year-to-Date",             false, false, true,  false, false, Permission.QUOTE_READ ),
	SMRV(                        "Six-Month Rolling Volume",     false, false, true,  false, false, Permission.TICKET_READ ),
	CREDIT_CARD_FEE_DISTRIBUTION("Credit Card Fee Distribution", true,  false, false, false, false, Permission.INVOICE_READ ),
	WO_AND_FEES_QUARTER(          "WO & Fees Quarterly",         true,  false, true,  false, false, Permission.INVOICE_READ), 	// Q, Y

	// Yearly - after year close
	PAC_YEARLY(        "PAC Yearly",        false, false, true,  false, false, Permission.QUOTE_READ ),
	WO_AND_FEES_YEARLY("WO & Fees Yearly",	true,  false, true,  false, false, Permission.INVOICE_READ), 	// Q, Y

	// Executive reports - historical trending - generated quarterly
	ACCOUNTS_RECEIVABLE(   "AR w Claimed vs Billed",	  false, true,  true,  false, false, Permission.INVOICE_READ ),	// claimed/billed/cash
	SKIPPED_AND_DISPATCHED("Skipped & Dispatched Counts", false, true,  true,  false, false, Permission.TICKET_READ ),
	QSS(                   "Quarterly Sales Summary", 	  true,  false, false, false, false, Permission.INVOICE_READ ),	// PAC trending
	VOLUME_FORECAST(       "Volume Forecast",             false, true,  false, false, false, Permission.INVOICE_READ ),	// 6MRV trending
	VPP(                   "Volume Per Person",           false, true,  true,  false, false, Permission.INVOICE_READ ),	// volume per paycheck paid out
	;
	
	private String description;
	private Boolean allAnsiReport;
	private Boolean summaryReport;
	private Boolean divisionReport; // is division-specific
	private Boolean trendReport;
	private Boolean utilityReport;
	private Permission permission;
	
	private BatchReports(String description, Boolean allAnsiReport, Boolean summaryReport, Boolean divisionReport, Boolean trendReport, Boolean utilityReport, Permission permission) {
		this.description = description;
		this.allAnsiReport = allAnsiReport;
		this.summaryReport = summaryReport;
		this.divisionReport = divisionReport;
		this.trendReport = trendReport;
		this.utilityReport = utilityReport;
		this.permission = permission;
	}
	
	public String abbreviation() { return this.name().replaceAll("_", " "); }
	public String description() { return this.description; }
	public Permission permission() { return this.permission; }
	public Boolean isAllAnsiReport() { return this.allAnsiReport; }
	public Boolean isSummaryReport() { return this.summaryReport; }
	public Boolean isDivisionReport() { return this.divisionReport; }
	public Boolean isTrendReport() { return this.trendReport; }
	public Boolean isUtilityReport() { return this.utilityReport; }
	
	
	
	
}
