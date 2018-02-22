package com.ansi.scilla.web.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.time.DateUtils;

public class TestCalendarWeirdness {

	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	
	public static void main(String[] args) {
		new TestCalendarWeirdness().go();
	}
	
	public void go() {
		Calendar feb = new GregorianCalendar(2018, Calendar.FEBRUARY, 12);
		Calendar mar = new GregorianCalendar(2018, Calendar.MARCH, 12);
		
		
		
		System.out.println("Before");
		System.out.println(sdf.format(feb.getTime()));
		System.out.println(sdf.format(mar.getTime()));
		
		doOption1((Calendar)feb.clone(), (Calendar)mar.clone());
//		doOption2((Calendar)feb.clone(), (Calendar)mar.clone());  /**   THIS ONE DOESN'T WORK **/
		doOption3((Calendar)feb.clone(), (Calendar)mar.clone());

		
		
	}

	private void doOption1(Calendar feb, Calendar mar) {
		feb.add(Calendar.MONTH, 1);
		feb.add(Calendar.DAY_OF_MONTH, -1);
		mar.add(Calendar.MONTH, 1);
		mar.add(Calendar.DAY_OF_MONTH, -1);	
		System.out.println("After");
		System.out.println(sdf.format(feb.getTime()));
		System.out.println(sdf.format(mar.getTime()));
	}
	
//	private void doOption2(Calendar feb, Calendar mar) {
	/**   THIS ONE DOESN'T WORK **/
//		feb = DateUtils.ceiling(feb, Calendar.DAY_OF_MONTH);
//		mar = DateUtils.ceiling(mar, Calendar.DAY_OF_MONTH);
//		System.out.println("After");
//		System.out.println(sdf.format(feb.getTime()));
//		System.out.println(sdf.format(mar.getTime()));
//	}
	
	private void doOption3(Calendar feb, Calendar mar) {
		feb.set(Calendar.DAY_OF_MONTH, feb.getActualMaximum(Calendar.DAY_OF_MONTH));
		mar.set(Calendar.DAY_OF_MONTH, mar.getActualMaximum(Calendar.DAY_OF_MONTH));
		System.out.println("After");
		System.out.println(sdf.format(feb.getTime()));
		System.out.println(sdf.format(mar.getTime()));
	}

}
