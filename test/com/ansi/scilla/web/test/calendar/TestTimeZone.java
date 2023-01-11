package com.ansi.scilla.web.test.calendar;

import java.time.ZoneId;

public class TestTimeZone {

	private void go() {
		try {
			ZoneId chicagoZone = ZoneId.of("US/Central");
		} catch ( Exception e ) {
			System.out.println("Err 1: ");
			e.printStackTrace();
		}
		System.out.println("****************");
		try {
			ZoneId chicagoZone = ZoneId.of("US/XXXXXXX");
		} catch ( Exception e ) {
			System.out.println("Err 1: ");
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new TestTimeZone().go();
	}

}
