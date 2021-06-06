package com.ansi.scilla.web.bcr.common.BCRSpreadsheet;

import org.apache.commons.collections4.Predicate;

import com.ansi.scilla.common.utils.WorkWeek;
import com.ansi.scilla.web.bcr.response.BudgetControlTotalsResponse.BCRTotalsDetail;

public class BCRTotalsPredicate implements Predicate<BCRTotalsDetail> {

	public WorkWeek workWeek;
	
	@Override
	public boolean evaluate(BCRTotalsDetail arg0) {
		String[] cw = arg0.getClaimWeek().split("-");
		return workWeek.getWeekOfYear().equals(Integer.valueOf(cw[1]));
	}
	
}
