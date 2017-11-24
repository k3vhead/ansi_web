package com.ansi.scilla.web.test;

import java.sql.Connection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ansi.scilla.web.code.response.CodeListResponse;
import com.ansi.scilla.web.common.utils.AppUtils;

public class TestCodes {

	private final String[] urlList = new String[] {
			"/ansi_web/code/list",
			"/ansi_web/code/list/",
			"/ansi_web/code/list?id=123",
			"/ansi_web/code/message",
			"/ansi_web/code/message/message/SUCCESS",
			"/ansi_web/code/message/message/SUCCESS?id=123&x=abc",
			"/ansi_web/code/message/message/SUCCESS/?id=123&x=abc",
			"/ansi_web/code",
			"/ansi_web/code/"
		};

	public static void main(String[] args) {
		TesterUtils.makeLoggers();
		try {
//			new TestCodes().go();
//			new TestCodes().testUri();
//			new TestCodes().testUri2();
//			new TestCodes().testDelete();
			new TestCodes().makeCodeResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testDelete() throws Exception {
		String response = TesterUtils.doDelete("http://127.0.0.1:8080/ansi_web/code/address/county/ac1", "{}");
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
		Matcher matcher = uriPattern.matcher("/ansi_web/codes/getOne.json?id=123");
		
		if ( matcher.matches() ) {
			System.out.println( matcher.group(2));
		} else {
			System.out.println("Nope");
		}
	}
	
	public void testUri2() {
		for ( String url : urlList ) {
			System.out.println(url);
			int idx = url.indexOf("/code/");
			System.out.println("\t" + idx);
			String myString = url.substring(idx + "/code/".length());
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
				// ArrayIndexOutOfBoundsException
			}
		}
	}

	private void makeCodeResponse() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			CodeListResponse resp = new CodeListResponse(conn);
			System.out.println(resp);
		} finally {
			conn.close();
		}
	}
}
