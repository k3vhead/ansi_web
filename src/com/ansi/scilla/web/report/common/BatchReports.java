package com.ansi.scilla.web.report.common;

public enum BatchReports {

	// Weekly
	PAC_WEEKLY("PAC Weekly",                false, false, true),
	DO_LIST(   "Dispatched & Outstanding",  false, false, true),
	PD45(      "45-Day Past Due",           false, false, true),

	TSR(       "Ticket Status",             false, false, true),

	// Monthly - before the month end
	DRV(       "Detailed Rolling Volume",   false, false, true),
	
	
	// Monthly - after the monthly close
	PAC_MONTHLY("PAC Monthly",              false, false, true),
	LIFT_GENIE( "Lift/Genie Monthly",              true,  false, false),
	CASH_RECEIPTS_REGISTER(
			"Cash Receipts Register",       true,  false, false),
	SERVICE_TAX("Service Taxes",            true,  false, false),
	IR_SUMMARY( "Invoice Register Summary", true,  false, false),
	IR(         "Invoice Register Detail",  false, false, true),
	AR_TOTALS(  "Accounts Receivable w over 60 day detail", // divisions can get this but not batched
											true,  false, false), 
	AR_LISTING( "AR Listing",				true,  false, false), 	//exec (trend)

	// Quarterly - after quarter close
	PAC_QUARTER("PAC Quarterly",            false, false, true),
	PAC_YTD(    "PAC Year-to-Date",         false, false, true),
	SMRV(       "Six-Month Rolling Volume", false, false, true),
	CREDIT_CARD_FEE_DISTRIBUTION(
			"Credit Card Fee Distribution", true,  false, false),
	WO_AND_FEES("WO & Fees",                true,  false, false), 	// M, Q, Y?

	// Executive reports - historical trending - generated quarterly
	ACCOUNTS_RECEIVABLE( "AR w Claimed vs Billed",					// claimed/billed/cash
											false, true,  true),
	SKIPPED_AND_DISPATCHED( 										// lost volume for this and last quarter
			"Skipped & Dispatched Counts",  false, true,  true),
	QSS("Quarterly Sales Summary", 			true,  false, false),	// PAC trending
	VOLUME_FORECAST("Volume Forecast",      false, true,  false),	// 6MRV trending
	VPP("Volume Per Person",                false, true,  true),	// volume per paycheck paid out
	;
	
	private String description;
	private Boolean executiveReport;
	private Boolean summaryReport;
	private Boolean divisionReport; // is division-specific
	
	private BatchReports(String description, Boolean executiveReport, Boolean summaryReport, Boolean divisionReport) {
		this.description = description;
		this.executiveReport = executiveReport;
		this.summaryReport = summaryReport;
		this.divisionReport = divisionReport;
	}
	
	public String abbreviation() { return this.name().replaceAll("_", " "); }
	public String description() { return this.description; }
	public Boolean isExecutiveReport() { return this.executiveReport; }
	public Boolean isSummaryReport() { return this.summaryReport; }
	public Boolean isDivisionReport() { return this.divisionReport; }
	
}
