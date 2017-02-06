package com.ansi.scilla.web.test;

public class TestDRV {

	public static void main(String[] args) {
		TesterUtils.makeLoggers();
		try {
			new TestDRV().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		String[] urlList = new String[] {
			"http://127.0.0.1:8080/ansi_web/ticketDRV",
			"http://127.0.0.1:8080/ansi_web/ticketDRV?divisionId=19",
			"http://127.0.0.1:8080/ansi_web/ticketDRV?divisionId=xx",
			"http://127.0.0.1:8080/ansi_web/ticketDRV?divisionId=3&month=1",
			"http://127.0.0.1:8080/ansi_web/ticketDRV?divisionId=3&month=2",
			"http://127.0.0.1:8080/ansi_web/ticketDRV?divisionId=3&month=3",
			"http://127.0.0.1:8080/ansi_web/ticketDRV?divisionId=3&month=13"
		};
		
		for ( String url : urlList ) {
			System.out.println(url);
			String json = TesterUtils.getJson(url);
			System.out.println(json);
			System.out.println("*****************\n***************");
		}
	}

}
