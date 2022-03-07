package com.ansi.scilla.web.bcr.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.ansi.scilla.common.utils.WorkWeek;
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
	
	
	
	/**
	 * Calculate the workweeks for a given month
	 * @param firstOfMonth
	 * @param lastOfMonth
	 * @return
	 */
	public static List<WorkWeek> makeWorkCalendar(Calendar firstOfMonth, Calendar lastOfMonth) {
		List<WorkWeek> workCalendar = new ArrayList<WorkWeek>();
		Calendar calendar = new GregorianCalendar(firstOfMonth.get(Calendar.YEAR), firstOfMonth.get(Calendar.MONTH), firstOfMonth.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		while ( calendar.before(lastOfMonth) ) {
			workCalendar.add( new WorkWeek(calendar) );
			calendar.add(Calendar.DAY_OF_YEAR, 7);
		}
		return workCalendar;
	}
}
