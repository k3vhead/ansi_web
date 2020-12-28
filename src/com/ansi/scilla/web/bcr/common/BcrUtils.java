package com.ansi.scilla.web.bcr.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.ansi.scilla.common.utils.WorkYear;
import com.ansi.scilla.web.bcr.response.DisplayMonth;

public class BcrUtils {
	public static List<DisplayMonth> makeDisplayYear(Calendar workDay) {
		// if today is october, november, december, so we start october this year
		// if today is January - September, so we start last october last year
		// in either case, we end 3 months from today
		Integer startYear = workDay.get(Calendar.MONTH) > Calendar.SEPTEMBER ? workDay.get(Calendar.YEAR) : workDay.get(Calendar.YEAR) - 1;
		Calendar workDate = new GregorianCalendar(startYear, Calendar.OCTOBER, 15);
		Calendar endDate = new GregorianCalendar(workDay.get(Calendar.YEAR), workDay.get(Calendar.MONTH), workDay.get(Calendar.DAY_OF_MONTH),0,0,0);
		endDate.add(Calendar.MONTH, 4);

		Integer previousYear = workDate.get(Calendar.YEAR);
		WorkYear workYear = new WorkYear(workDate.get(Calendar.YEAR));
		List<DisplayMonth> displayYear = new ArrayList<DisplayMonth>();
		while ( workDate.before(endDate) ) {
			if ( workDate.get(Calendar.YEAR)!=previousYear ) {
				previousYear = workDate.get(Calendar.YEAR);
				workYear = new WorkYear(workDate.get(Calendar.YEAR));
			}
			displayYear.add(new DisplayMonth(workYear, workDate));
			workDate.add(Calendar.MONTH, 1);
		}
		return displayYear;
	}
}
