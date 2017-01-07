package com.ansi.scilla.web.test;

import java.net.URLEncoder;

public class TestContactSearch {

	public static void main(String[] args) {
		TesterUtils.makeLoggers();
		try {
			new TestContactSearch().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		String term = URLEncoder.encode("th ", "UTF-8");
		String url = "http://127.0.0.1:8080/ansi_web/contactSearch?term=" + term;
		String json = TesterUtils.getJson(url);
		System.out.println(json);
	}

}
