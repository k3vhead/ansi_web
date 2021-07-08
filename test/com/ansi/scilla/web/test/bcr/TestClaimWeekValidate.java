package com.ansi.scilla.web.test.bcr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestClaimWeekValidate {

	public static void main(String[] args) {
		Pattern pattern = Pattern.compile("^([0-9][0-9][0-9][0-9])(-)([0-9][0-9])$",Pattern.CASE_INSENSITIVE);
		for ( String x : new String[] {"2020-46","2020-4","202046","202-46","123x-46","2020-x5"} ) {
			Matcher matcher = pattern.matcher(x);
			String weekNum = matcher.matches() ? matcher.group(3) : "N/A";
			System.out.println(x + "\t" + matcher.matches() + "\t" + weekNum);
		}
	}

}
