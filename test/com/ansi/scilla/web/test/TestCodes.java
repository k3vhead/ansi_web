package com.ansi.scilla.web.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestCodes {

	public static void main(String[] args) {
		TesterUtils.makeLoggers();
		try {
//			new TestCodes().go();
			new TestCodes().testUri();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		String returnString = TesterUtils.getJson("http://127.0.0.1:8080/ansi_web/codes/getList.json");
		System.out.println(returnString);
	}
	
	public void testUri() {
		Pattern uriPattern = Pattern.compile("^(.*/)(.*)(\\.)(.*)$", Pattern.CASE_INSENSITIVE);
		Matcher matcher = uriPattern.matcher("/ansi_web/codes/getOne.json?id=123");
		
		if ( matcher.matches() ) {
			System.out.println( matcher.group(2));
		} else {
			System.out.println("Nope");
		}

	}
}
