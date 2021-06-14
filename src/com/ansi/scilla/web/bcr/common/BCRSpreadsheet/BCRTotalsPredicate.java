package com.ansi.scilla.web.bcr.common.BCRSpreadsheet;

import org.apache.commons.collections4.Predicate;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.utils.WorkWeek;
import com.ansi.scilla.web.bcr.response.BudgetControlTotalsResponse.BCRTotalsDetail;

public class BCRTotalsPredicate implements Predicate<BCRTotalsDetail> {

	public WorkWeek workWeek;
	public Logger logger;
	
	public BCRTotalsPredicate() {
		super();
		this.logger = LogManager.getLogger("bcr_logger");
	}
	@Override
	public boolean evaluate(BCRTotalsDetail arg0) {
		String[] cw = arg0.getClaimWeek().split("-");
		
//		logger.log(Level.DEBUG, "[" + workWeek.getWeekOfYear() + "] | [" + Integer.valueOf(cw[1]) + "]");

		
		
		
		return workWeek.getWeekOfYear().equals(Integer.valueOf(cw[1]));
	}
	
}
