package com.ansi.scilla.web.report.common;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

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
	PAC_WEEKLY("PAC Weekly",                false, false, true, false, false),
	DO_LIST(   "Dispatched & Outstanding",  false, false, true, false, false),
	PD45(      "45-Day Past Due",           false, false, true, false, false),

	TSR(       "Ticket Status",             false, false, true, false, false),

	// Monthly - before the month end
	DRV(       "Detailed Rolling Volume",   false, false, true, false, false),
	
	
	// Monthly - after the monthly close
	PAC_MONTHLY("PAC Monthly",              false, false, true, false, false),
	LIFT_GENIE( "Lift/Genie Monthly",       true,  false, false, false, false),
	CASH_RECEIPTS_REGISTER(
			"Cash Receipts Register",       true,  false, false, false, false),
	SERVICE_TAX("Service Taxes",            true,  false, false, false, false),
	IR_SUMMARY( "Invoice Register Summary", true,  false, false, false, false),
	IR(         "Invoice Register Detail",  false, false, true, false, false),
	AR_TOTALS(  "Accounts Receivable w over 60 day detail", // divisions can get this but not batched
											true,  false, false, false, false), 
	AR_LISTING( "AR Listing",				true,  false, false, false, false), 	//exec (trend)

	// Quarterly - after quarter close
	PAC_QUARTER("PAC Quarterly",            false, false, true, false, false),
	PAC_YTD(    "PAC Year-to-Date",         false, false, true, false, false),
	SMRV(       "Six-Month Rolling Volume", false, false, true, false, false),
	CREDIT_CARD_FEE_DISTRIBUTION(
			"Credit Card Fee Distribution", true,  false, false, false, false),
	WO_AND_FEES("WO & Fees",                true,  false, false, false, false), 	// M, Q, Y?

	// Executive reports - historical trending - generated quarterly
	ACCOUNTS_RECEIVABLE( "AR w Claimed vs Billed",					// claimed/billed/cash
											false, true,  true, false, false),
	SKIPPED_AND_DISPATCHED( 										// lost volume for this and last quarter
			"Skipped & Dispatched Counts",  false, true,  true, false, false),
	QSS("Quarterly Sales Summary", 			true,  false, false, false, false),	// PAC trending
	VOLUME_FORECAST("Volume Forecast",      false, true,  false, false, false),	// 6MRV trending
	VPP("Volume Per Person",                false, true,  true, false, false),	// volume per paycheck paid out
	;
	
	private String description;
	private Boolean allAnsiReport;
	private Boolean summaryReport;
	private Boolean divisionReport; // is division-specific
	private Boolean trendReport;
	private Boolean utilityReport;
	
	private BatchReports(String description, Boolean allAnsiReport, Boolean summaryReport, Boolean divisionReport, Boolean trendReport, Boolean utilityReport) {
		this.description = description;
		this.allAnsiReport = allAnsiReport;
		this.summaryReport = summaryReport;
		this.divisionReport = divisionReport;
		this.trendReport = trendReport;
		this.utilityReport = utilityReport;
	}
	
	public String abbreviation() { return this.name().replaceAll("_", " "); }
	public String description() { return this.description; }
	public Boolean isAllAnsiReport() { return this.allAnsiReport; }
	public Boolean isSummaryReport() { return this.summaryReport; }
	public Boolean isDivisionReport() { return this.divisionReport; }
	public Boolean isTrendReport() { return this.trendReport; }
	public Boolean isUtilityReport() { return this.utilityReport; }
	
	public static List<BatchReports> makeAllAnsiReportList() {
		return IterableUtils.toList(IterableUtils.filteredIterable( Arrays.asList(BatchReports.values()), new ReportTypePredicate(MatchType.allAnsiReport)));
	}
	
	public static List<BatchReports> makeTrendReportList() {
		return IterableUtils.toList(IterableUtils.filteredIterable( Arrays.asList(BatchReports.values()), new ReportTypePredicate(MatchType.trendReport)));
	}
	
	public static List<BatchReports> makeSummaryReportList() {
		return IterableUtils.toList(IterableUtils.filteredIterable( Arrays.asList(BatchReports.values()), new ReportTypePredicate(MatchType.summaryReport)));
	}
	
	public static List<BatchReports> makeDivisionReportList() {
		return IterableUtils.toList(IterableUtils.filteredIterable( Arrays.asList(BatchReports.values()), new ReportTypePredicate(MatchType.divisionReport)));
	}
	
	public static List<BatchReports> makeUtilityReportList() {
		return IterableUtils.toList(IterableUtils.filteredIterable( Arrays.asList(BatchReports.values()), new ReportTypePredicate(MatchType.utilityReport)));
	}
	
	private static class ReportTypePredicate implements Predicate<BatchReports> {
		private MatchType matchType;
		
		public ReportTypePredicate(MatchType matchType) {
			super();
			this.matchType = matchType;
		}

		@Override
		public boolean evaluate(BatchReports batchReport) {			
			return (matchType.equals(MatchType.allAnsiReport) && batchReport.isAllAnsiReport()) || 
					(matchType.equals(MatchType.summaryReport) && batchReport.isSummaryReport()) || 
					(matchType.equals(MatchType.divisionReport) && batchReport.isDivisionReport()) || 
					(matchType.equals(MatchType.trendReport) && batchReport.isTrendReport()) || 
					(matchType.equals(MatchType.utilityReport) && batchReport.isUtilityReport()); 
		}
		
	}
	
	private enum MatchType {
		allAnsiReport,
		summaryReport,
		divisionReport,
		trendReport,
		utilityReport,
		;
	}
	
	
}
