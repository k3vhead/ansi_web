package com.ansi.scilla.web.bcr.common.BCRSpreadsheet;

import java.util.Calendar;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.common.utils.WorkWeek;

/**
 * Filters by matching first of week.
 *
 */
public class WorkWeekDatePredicate implements Predicate<WorkWeek> {

	public Calendar firstOfWeek;
	
	@Override
	public boolean evaluate(WorkWeek arg0) {
		return DateUtils.isSameDay(firstOfWeek, arg0.getFirstOfWeek());
	}
	
}
