package com.ansi.scilla.web.test.bcr;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

import com.ansi.scilla.common.utils.WorkWeek;
import com.ansi.scilla.common.utils.WorkYear;

public class TestClaimMonth {

	public void go2() {
		Integer claimYear = 2021;
		Integer claimWeek = 44;
		
		WorkYear workYear = new WorkYear(claimYear);
		List<HashMap<String, Object>> workWeekList = IterableUtils.toList(workYear.values());
		HashMap<String, Object> workWeek = IterableUtils.find(workWeekList, new WorkYearPredicate(claimWeek));
		Integer claimMonth = (Integer)workWeek.get(WorkYear.WORK_MONTH);
		System.out.println(claimMonth);
		
	}
	
	
	public void go() throws Exception {
			Integer claimYear = 2021;
			Integer claimWeek = 44;
			
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			WorkYear workYear = new WorkYear(claimYear);
//			System.out.println(workYear);
			List<Calendar> dateList = IterableUtils.toList(workYear.keySet());
			Collections.sort(dateList);
			for ( Calendar calendar : dateList ) {
				WorkWeek ww = new WorkWeek(calendar);
				if ( ww.getWeekOfYear() == claimWeek ) {
					Calendar firstOfWeek = ww.getFirstOfWeek();
					Calendar lastOfWeek = ww.getLastOfWeek();
					System.out.println(
							sdf.format(calendar.getTime()) + "\t" + 
							ww.getWeekOfYear() + "\t" + 
							sdf.format(ww.getFirstOfWeek().getTime()) + "\t" + 
							sdf.format(ww.getLastOfWeek().getTime()) + "\t" + 
							workYear.getWorkMonth(firstOfWeek) + "\t" + 
							workYear.getWorkMonth(lastOfWeek)  
					);
				}
			}
			
	}
	
	public static void main(String[] args) {
		new TestClaimMonth().go2();
//		new TestClaimMonth().go();

	}

	
	public class WorkYearPredicate implements Predicate<HashMap<String, Object>> {
		private Integer claimWeek;
		
		public WorkYearPredicate(Integer claimWeek) {
			super();
			this.claimWeek = claimWeek;
		}
		
		@Override
		public boolean evaluate(HashMap<String, Object> arg0) {
			return arg0.containsKey(WorkYear.WEEK_OF_YEAR) && (Integer)arg0.get(WorkYear.WEEK_OF_YEAR) == claimWeek;
		}
		
	}
}
