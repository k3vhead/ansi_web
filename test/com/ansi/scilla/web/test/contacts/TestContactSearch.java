package com.ansi.scilla.web.test.contacts;

import java.net.URLDecoder;
import java.net.URLEncoder;

import com.ansi.scilla.web.test.TesterUtils;
import com.thewebthing.commons.lang.StringUtils;

public class TestContactSearch {

	public static void main(String[] args) {
		try {
			String term = URLEncoder.encode("bob thomas", "UTF-8");
			new TestContactSearch().goxx("term=&");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		String term = URLEncoder.encode("", "UTF-8");
//		String url = "http://127.0.0.1:8080/ansi_web/contactTypeAhead?term=" + term;
		String url = "http://127.0.0.1:8080/ansi_web/contactTypeAhead?term=th&trm=abc";
		String json = TesterUtils.getJson(url);
		System.out.println(json);
	}

	public void goxx(String qs) throws Exception {
		String term = "";
		if ( ! StringUtils.isBlank(qs)) {
			int idx = qs.indexOf("term=");
			if ( idx > -1 ) {
				term = qs.substring(idx);
				idx = qs.indexOf("&");
				if ( idx > -1 ) {
					term = qs.substring(0, idx);
				}
				if ( ! StringUtils.isBlank(term)) {
					term = URLDecoder.decode(term, "UTF-8");
				}
			}
		}
		System.out.println("[" + term + "]");
	}
}
