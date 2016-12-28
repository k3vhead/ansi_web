package com.ansi.scilla.web.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestQuotes {

	private final String[] urlList = new String[] {
			"/ansi_web/quote/list"
			//"/ansi_web/quote/list/",
			//"/ansi_web/quote/list?id=123",
			//"/ansi_web/quote/message",
			//"/ansi_web/quote/message/message/SUCCESS",
			//"/ansi_web/quote/message/message/SUCCESS?id=123&x=abc",
			//"/ansi_web/quote/message/message/SUCCESS/?id=123&x=abc",
			//"/ansi_web/quote",
			//"/ansi_web/quote/"
		};

	public static void main(String[] args) {
		TesterUtils.makeLoggers();
		try {
			//new TestQuotes().testDelete2();
			new TestQuotes().testAdd();
			//new TestQuotes().go();
//			new TestQuotes().testUri();
//			new TestQuotes().testUri2();
			//new TestQuotes().testDelete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testDelete() throws Exception {
		String response = TesterUtils.doDelete("http://127.0.0.1:8080/ansi_web/quote/21/1/1", "{}");
		System.out.println(response);
		
	}

	public void go() throws Exception {
		for ( String url : urlList ) {
			
			String x = "http://127.0.0.1:8080" + url;
			System.out.println(x);
			String returnString = TesterUtils.getJson(x);
			System.out.println(returnString);
		}
	}
	
	public void testUri() {
		Pattern uriPattern = Pattern.compile("^(.*/)(.*)(\\.)(.*)$", Pattern.CASE_INSENSITIVE);
		Matcher matcher = uriPattern.matcher("/ansi_web/quote/getOne.json?id=123");
		
		if ( matcher.matches() ) {
			System.out.println( matcher.group(2));
		} else {
			System.out.println("Nope");
		}
	}
	
	public void testUri2() {
		for ( String url : urlList ) {
			System.out.println(url);
			int idx = url.indexOf("/quote/");
			System.out.println("\t" + idx);
			String myString = url.substring(idx + "/quote/".length());
			System.out.println("\t" + myString);
			
			int idx2 = myString.indexOf("?");
			if ( idx2 > -1 ) {
				String queryString = myString.substring(idx2 + 1);
				System.out.println("\tQS: " + queryString);
				myString = myString.substring(0, idx2);
				System.out.println("\tMine: " + myString);
			}
			
			String[] urlPieces = myString.split("/");
			System.out.println("\tcommand: " + urlPieces[0]);
			if ( urlPieces.length > 1 ) {
				System.out.println("\tTable: " + urlPieces[0]);
				System.out.println("\tField: " + urlPieces[1]);
				System.out.println("\tValue: " + urlPieces[2]);
				//  ArrayIndexOutOfBoundsException
			}
		}
	}
	
	private void testAdd() throws Exception {
		String jsonString = "{\"address\":\"123 fake st\",\"billToAddressId\":2,\"jobSiteAddressId\":2,\"leadType\":\"1\",\"managerId\":7,\"name\":\"Keegans Midwest Bank\",\"paymentTerms\":\"60D\",\"quoteNumber\":1,\"revisionNumber\":1,\"status\":0,\"templateId\":3}";
		String URL = "http://127.0.0.1:8080/ansi_web/quote/";
		String url = URL + "add";
		System.out.println(url);
		String json = TesterUtils.postJson(url, jsonString);
		System.out.println(json);
	}
	private void testDelete2() throws Exception {
		String jsonString = "{\"quote_id\": 21,\"quote_number\": 1,\"revision_number\": 1}";
		String URL = "http://127.0.0.1:8080/ansi_web/quote/";
		String url = URL + "add";
		System.out.println(url);
		String json = TesterUtils.postJson(url, jsonString);
		System.out.println(json);
	}
}
