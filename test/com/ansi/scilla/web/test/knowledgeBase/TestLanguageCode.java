package com.ansi.scilla.web.test.knowledgeBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestLanguageCode {

	public static void main(String[] args) {
		new TestLanguageCode().go();
	}
	
	protected void go() {
		Pattern pattern = Pattern.compile("^([A-Za-z]{2,3})(-?[A-Za-z]{2,3})?");
				
		for ( String x : new String[] {"fr-CH","fr","xxx-xx","xxx-xxx","xxx","xxxx-x","xxx-xxxx","x","xx-"} ) {
			Matcher matcher = pattern.matcher(x);
			System.out.println(x + "\t" + matcher.matches());
			
		}
	}

}
