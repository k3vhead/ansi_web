package com.ansi.scilla.web.bcr.common.BCRSpreadsheet;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;

/**
 * Filters by claim week. Used to create week-specific tabs in the spreadsheet
 *
 */
public class BCRRowUnclaimedEquipmentPredicate implements Predicate<BCRRow> {
	@Override
	public boolean evaluate(BCRRow arg0) {
		return ! StringUtils.isBlank(arg0.unclaimedEquipment);
	}
	
}
