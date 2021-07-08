package com.ansi.scilla.web.bcr.common.BCRSpreadsheet;

public enum TotalsRow {
	WEEK(1, "Week"),
	TOTAL_VOLUME(2, "Total Volume: "),
	VOLUME_CLAIMED(3, "Volume Claimed: "),
	CLAIMED_VOLUME_REMAINING(4, "Claimed Volume Remaining: "),
	TOTAL_BILLED(6, "Total Billed: "),
	VARIANCE(7, "Variance: "),
	TOTAL_DL_CLAIMED(9, "Total D/L Claimed: "),
	ACTUAL_DL(10, "Actual D/L: "),
	ACTUAL_OM_DL(11, "Actual OM D/L: "),
	TOTAL_ACTUAL_DL(12, "Total Actual D/L: "),
	DL_PERCENTAGE(14, "D/L Percentage: "),
	ACTUAL_DL_PERCENTAGE(15, "Actual D/L Percentage: "),
	;
	
	private Integer rowNum;
	private String label;
	
	private TotalsRow(Integer rowNum, String label) {
		this.rowNum = rowNum;
		this.label = label;
	}
	
	public Integer rowNum() { return this.rowNum; }
	public String label() { return this.label; }
}
