package com.ansi.scilla.web.report.common;

public enum BatchReports {

	PAC_WEEKLY("PAC Weekly",                false, false, true),
	
	DO_LIST(   "Dispatched & Outstanding",  false, false, true),
	PD45(      "45-Day Past Due",           false, false, true),
	TSR(       "Ticket Status",             false, false, true),
	
	
	PAC_MONTHLY("PAC Monthly",              false, false, true),
	LIFT_GENIE( "Lift/Genie",               false, false, true),
	PAC_QUARTER("PAC Quarterly",            false, false, true),
	PAC_YTD(    "PAC Year-to-Date",         false, false, true),
	SMRV(       "Six-Month Rolling Volume", false, false, true),
	SKIPPED_AND_DISPATCHED(
			"Skipped & Dispatched",         false, false, false),
	
	
	
	CASH_RECEIPTS_REGISTER(
			"Cash Receipts Register",       true,  true,  false),
	SERVICE_TAX("Service Tax",              false, false, true),
	CREDIT_CARD_FEE_DISTRIBUTION(
			"Credit Card Fee Distribution", false, false, false),
	
	
	
	IR_SUMMARY( "Invoice Register Summary", true,  true,  false),
	IR(         "Invoice Register Detail",  false, false, true),
	AR_TOTALS(  "Accounts Receivable",      true,  false, false),
	WO_AND_FEES("WO & Fees",                false, false, false),
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
