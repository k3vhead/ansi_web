package com.ansi.scilla.web.test;

import com.ansi.scilla.web.request.TaxRateRequest;

public class TestTaxRate {

	private final String URL = "http://127.0.0.1:8080/ansi_web/taxRate/";
	
	public static void main(String[] args) {
		TesterUtils.makeLoggers();
		try {
//			new TestTaxRate().getList();
//			new TestTaxRate().getOne("xxx");
			new TestTaxRate().addOne();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getList() throws Exception {
		String json = TesterUtils.getJson(URL + "list");
		System.out.println(json);		
	}
	
	public void getOne(String taxRateId) throws Exception {
		String json = TesterUtils.getJson(URL + taxRateId);
		System.out.println(json);		
	}

	private void addOne() throws Exception {
		String jsonString = "{\"location\":\"Webthing HQ\",\"taxRate\":\"123\",\"taxAmount\":\"123\",\"effectiveDate\":\"12/01/2016\"}";
		System.out.println(jsonString);
		TaxRateRequest req = new TaxRateRequest(jsonString);
		System.out.println(req);
		String url = URL + "add";
		System.out.println(url);
		String json = TesterUtils.postJson(url, jsonString);
		System.out.println(json);
	}
	
	
}
