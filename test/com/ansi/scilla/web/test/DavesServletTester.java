package com.ansi.scilla.web.test;

import java.util.HashMap;

import org.apache.http.Header;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DavesServletTester extends TestServlet {
	
	protected final Logger logger = LogManager.getLogger(TestServlet.class);	
	
	
	
	public static void main(String[] args) {
		try {
			new DavesServletTester().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
	public void go() throws Exception {		
		super.userId = "kjw@ansi.com";
//		super.userId = "geo@whitehouse.gov";
		super.password = "password1";
		
		Header sessionCookie = super.doLogin();
		
		HashMap<String, String> parmMap = new HashMap<String, String>();
		String url = "/ansi_web/permissionGroup/";
		
		
//		String results = super.doGet(sessionCookie, url, parmMap);
		
		
		
//		parmMap.put("name", "Dave's 2 Update Test");
//		parmMap.put("description", "Dave's 2 Update Description");
//		parmMap.put("status", "0");		
//		String results = super.doPost(sessionCookie, url, super.makeJson(parmMap));
		
		
		
		
		String results = super.doDelete(sessionCookie, url, parmMap);
		
		logger.log(Level.DEBUG, results);
	}


	


	
}
