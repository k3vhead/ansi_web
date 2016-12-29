package com.ansi.scilla.web.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ansi.scilla.web.common.AppUtils;

public class TestAddress {

	private final String[] urlList = new String[] {
			"/ansi_web/address/list",
			"/asni_web/address/6"
		};

	public static void main(String[] args) {
		TesterUtils.makeLoggers();
		
		try {
			//new TestAddress().testDelete(3);
			new TestAddress().testAdd();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testDelete(Integer ID) throws Exception {
		String response = TesterUtils.getJson("http://127.0.0.1:8080/ansi_web/address/delete/"+ID);
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
		Matcher matcher = uriPattern.matcher("/ansi_web/quote/getOne.json?quoteId=22");
		
		if ( matcher.matches() ) {
			System.out.println( matcher.group(2));
		} else {
			System.out.println("Nope");
		}
	}
	
	
	private void testAdd() throws Exception {
		String jsonString = "{\"name\": \"Keegan's Western Bank\",\"status\": \"Good\",\"address1\": \"123 fake st\",\"address2\": \"Unit 123\",\"city\": \"Inverness\",\"county\": \"Cook\",\"state\": \"IL\",\"zip\": \"12345\"}";
		//String jsonString = "{\"address\":\"123 fake st\",\"billToAddressId\":2,\"jobSiteAddressId\":2,\"leadType\":\"1\",\"managerId\":1,\"name\":\"Keegans Midwest Bank\",\"paymentTerms\":\"60D\",\"quoteNumber\":1,\"revisionNumber\":1,\"status\":0,\"templateId\":3}";
		
		String URL = "http://127.0.0.1:8080/ansi_web/address/";
		String url = URL + "add";
		System.out.println(url);
		String json = TesterUtils.postJson(url, jsonString);
		System.out.println(json);
	}
	private void testDelete2(Integer ID) throws Exception {
	
		String URL = "http://127.0.0.1:8080/ansi_web/quote/delete/"+ID;
		
		//System.out.println(url);
		String json = TesterUtils.postJson(URL, "{\"quoteId\":"+ID+"}");
		
		//System.out.println(json);
	}
}
