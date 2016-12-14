package com.ansi.scilla.web.test;

import java.sql.Connection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.response.division.DivisionListResponse;
import com.ansi.scilla.web.servlets.DivisionServlet;

public class TestDivisions {

	private final String[] urlList = new String[] {
			"/ansi_web/division/list",
			"/ansi_web/division/list/",
			//"/ansi_web/division/list?id=123",
			"/ansi_web/division/1",
			"/ansi_web/division/1/",
			//"/ansi_web/division/1?",
			//"/ansi_web/division/1?123",
			//"/ansi_web/division/1/?123",
			//"/ansi_web/division/2",
			//"/ansi_web/division/2/",
			//"/ansi_web/division/2?",
			//"/ansi_web/division/2?123",
			//"/ansi_web/division/2/?123",
			//"/ansi_web/division/message/message/SUCCESS",
			//"/ansi_web/division/message/message/SUCCESS?id=123&x=abc",
			//"/ansi_web/division/message/message/SUCCESS/?id=123&x=abc",
			//"/ansi_web/division",
			//"/ansi_web/division/"
		};

	public static void main(String[] args) {
		TesterUtils.makeLoggers();
		try {
			new TestDivisions().go2();
//			new TestCodes().testUri();
//			new TestCodes().testUri2();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go2() throws Exception {
		DivisionServlet divisionServlet = new DivisionServlet();
		Connection conn = null;
		try{
			conn=AppUtils.getConn();
		
		for(String url: urlList){
			System.out.println(url);
			int idx = url.indexOf("/division/");
			String myString = url.substring(idx + "/division/".length());
			String[] hello = url.split("\\?");
			String queryString = hello.length>1 ? hello[1]:null;
			DivisionListResponse divisionListResponse = divisionServlet.doGetWork(conn, myString, queryString);
			System.out.println(divisionListResponse);
			System.out.println("*******");
		}
		} finally {
			conn.close();
		}
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
	
}
