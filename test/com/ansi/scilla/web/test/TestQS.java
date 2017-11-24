package com.ansi.scilla.web.test;

import java.util.HashMap;

import com.ansi.scilla.web.common.utils.AppUtils;

public class TestQS {

	public static void main(String[] args) {
		try {
			new TestQS().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		for ( String qs : new String[] {
				"term=abc",
				"term=ab+c",
				"abc=def&ghi=jkl",
				"abc=&def=xyz",
				"abc&def&ghi=jkl",
				null,
				""
		} ) {
			System.out.println(qs);
			HashMap<String, String> paramMap = AppUtils.getQueryMap(qs);
			for ( String key : paramMap.keySet() ) {
				System.out.println(key + "\t" + paramMap.get(key));
			}
			System.out.println("************\n*********");
		}
	}

}
