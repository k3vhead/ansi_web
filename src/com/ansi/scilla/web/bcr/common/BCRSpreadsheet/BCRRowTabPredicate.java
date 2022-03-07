package com.ansi.scilla.web.bcr.common.BCRSpreadsheet;

import org.apache.commons.collections4.Predicate;

/**
 * Filters by claim week. Used to create week-specific tabs in the spreadsheet
 *
 */
public class BCRRowTabPredicate implements Predicate<BCRRow> {
	private String tabName;
	
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	@Override
	public boolean evaluate(BCRRow arg0) {
		return arg0.claimWeek.equals(tabName);
	}
	
}
