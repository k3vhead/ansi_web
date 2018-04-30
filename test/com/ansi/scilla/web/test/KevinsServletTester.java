package com.ansi.scilla.web.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.utils.AppUtils;
import com.fasterxml.jackson.core.JsonProcessingException;


public class KevinsServletTester extends TestServlet {
	
	protected final Logger logger = LogManager.getLogger(TestServlet.class);
	private boolean logDebugMsgs;
	private String realm;
	
	public static void main(String[] args) 
	{
		try 
		{
			new KevinsServletTester().go();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	
//	private String makeOutput(Header sessionCookie, String message, String method, String url, String data, String result) {
	private String makeOutput(String message, String method, String url, String data, String result) {
		String s = "\n";
//		sessionCookie.
		s = s + "\ntesting : \t";
		s = s + message;
		s = s + "\nas user : \t";
		s = s + super.getUserId();
		s = s + "\nhttp method : \t";
		s = s + method;
		s = s + "\nurl sent : \t";
		s = s + url;
		s = s + "\ndata sent :";
		s = s + "\t" + data;
		s = s + "\nreturned : \t"; 
		s = s + result; 
//		s = s + "\n";
		
		return s;		
	}

	private String testGetNoCmdOrID(Header sessionCookie) throws Exception 
	{
		//* Test the /list function of the servlet.. 
		String sResult="failed!";
		String url = "/ansi_web/" + this.realm + "/";
		String msg = "Get with no command or ID";
		String method = "GET";
		
		sResult = super.doGet(sessionCookie, url , new HashMap<String,String>());
		
		String sOutput = makeOutput(msg, method, url, "n/a", sResult);
		if(logDebugMsgs) logger.log(Level.DEBUG, sOutput);
		return sOutput;
	}
	
	
	private String testList(Header sessionCookie) throws Exception 
	{
		//* Test the /list function of the servlet.. 
		String sResult="failed!";
		String url = "/ansi_web/" + this.realm + "/list";
		String msg = "Get List";
		String method = "GET";
		
		sResult = super.doGet(sessionCookie, url , new HashMap<String,String>());
		
		String sOutput = makeOutput(msg, method, url, "n/a", sResult);
		if(logDebugMsgs) logger.log(Level.DEBUG, sOutput);
		return sOutput;
	}
	
	private String testGetById(Header sessionCookie, int itemId) throws Exception 
	{
		//* Test the /id# function of the servlet.. 
		String sResult = "Failed!";
		String url = "/ansi_web/" + this.realm + "/" + itemId;
		String msg = "Get by ID";
		String method = "GET";
		
		sResult = super.doGet(sessionCookie, url, new HashMap<String,String>());
		
		String sOutput = makeOutput(msg, method, url, "n/a", sResult);
		if(logDebugMsgs) logger.log(Level.DEBUG, sOutput);
		return sOutput;
	}

	private String testUpdate(Header sessionCookie, int itemId) {
		//* Test the /add function of the servlet.. 
		String sResult="Failed!";
		String url = "/ansi_web/" + this.realm + "/" + itemId;
		String msg = "Update";
		String method = "POST";
		String paramString="";
		HashMap<String, String> params = new HashMap<String, String>();;
				
		params.put("name", "This used to be test group 3");
		params.put("description", "Kevin Just Updated this Group");
		params.put("status","1");
		params.put("permissionGroupId", Integer.toString(itemId));
		
		try {
			paramString = AppUtils.object2json(params);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		try {
			sResult = super.doPost(sessionCookie, url , paramString);
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		String sOutput = makeOutput(msg, method, url, paramString, sResult);
		if(logDebugMsgs) logger.log(Level.DEBUG, sOutput);
		return sOutput;
	}


	private String testUpdateWithNoJsonData(Header sessionCookie, int itemId) {
		//* Test the /add function of the servlet.. 
		String sResult="Failed!";
		String url = "/ansi_web/" + this.realm + "/" + itemId;
		String msg = "Update with No json data";
		String method = "POST";
		String paramString="";
		String sOutput = "";
		HashMap<String, String> params = new HashMap<String, String>();
				
		paramString=null;

		try {
			paramString = AppUtils.object2json(params);
			sResult = super.doPost(sessionCookie, url , null);
			sOutput = makeOutput(msg, method, url, paramString, sResult);
			if(logDebugMsgs) logger.log(Level.DEBUG, sOutput);
		} catch (JsonProcessingException e_object2json) {
			if(logDebugMsgs) e_object2json.printStackTrace();
		} catch (ClientProtocolException e_doPost) {
			// TODO Auto-generated catch block
			if(e_doPost.getMessage() != null) sOutput = sOutput + e_doPost.getMessage(); 
			//e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sOutput;
		
		// works.. fails gracefully..
//		params.put("", "");
		
		// works.. fails gracefully..
//		params.put("name", "This used to be test group 3");

		// works..
//		params.put("name", "This used to be test group 3");
//		params.put("description", "Kevin Just Updated this Group");
//		params.put("status","1");
//		params.put("permissionGroupId", Integer.toString(itemId));

		// works.. completely empty params work as well
		
	}

	private String testUpdateWithPartialJsonData(Header sessionCookie, int itemId) {
		//* Test the /add function of the servlet.. 
		String sResult="Failed!";
		String url = "/ansi_web/" + this.realm + "/" + itemId;
		String msg = "Update with No json data";
		String method = "POST";
		String paramString="";
		HashMap<String, String> params = new HashMap<String, String>();;
		
		
//		params.put("", "");
		params.put("name", "This is - INTERRUPTING COW!! MMOOooo!!-- ed to be test group 3");
//		params.put("description", "Kevin Just Updated this Group");
//		params.put("status","1");
//		params.put("permissionGroupId", Integer.toString(itemId));
		
		try {
			paramString = AppUtils.object2json(params);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		try {
			sResult = super.doPost(sessionCookie, url , paramString);
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		String sOutput = makeOutput(msg, method, url, paramString, sResult);
		if(logDebugMsgs) logger.log(Level.DEBUG, sOutput);
		return sOutput;
	}

	
	
	private String testUpdateWithoutPermission() {
		String s="";
		
		super.userId = "geo@whitehouse.gov";
		super.password = "password1";
		Header sessionCookie;
		
		s = s + "\n\nswitching login to " + super.userId + " test access control";		
		sessionCookie = null;
		try {
			sessionCookie = super.doLogin();
		} catch (IOException | URISyntaxException e) {
			// who cares.. 
		}
		s = s + testUpdate(sessionCookie, 3);
		
		super.userId = "kjw@ansi.com";
		super.password = "password1";
		try {
			sessionCookie = super.doLogin();
		} catch (IOException | URISyntaxException e) {
			// who cares.. 
		}		
		return s;
	}
	
	
	private String testPostNoCmdOrId(Header sessionCookie) {
		//* Test the /add function of the servlet.. 
		String sResult="Failed!";
		String url = "/ansi_web/" + this.realm + "/";
		String msg = "Call to POST without command or id";
		String method = "POST";
		String paramString="";

		HashMap<String, String> params = new HashMap<String, String>();;
				
		params.put("name", "This used to be test group 3");
		params.put("description", "Kevin Just Updated this Group");
		params.put("status","1");
		params.put("permissionGroupId", Integer.toString(0));
		
		try {
			paramString = AppUtils.object2json(params);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		try {
			sResult = super.doPost(sessionCookie, url , paramString);
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		String sOutput = makeOutput(msg, method, url, paramString, sResult);
		if(logDebugMsgs) logger.log(Level.DEBUG, sOutput);
		return sOutput;
	}
	
	
	private String testPostWithNonNumericId(Header sessionCookie) {
		//* Test the /add function of the servlet.. 
		String sResult="Failed!";
		String url = "/ansi_web/" + this.realm + "/xxx";
		String msg = "Call to POST using a non numeric Id";
		String method = "POST";
		String paramString="";

		HashMap<String, String> params = new HashMap<String, String>();;
				
		params.put("name", "This used to be test group 3");
		params.put("description", "Kevin Just Updated this Group");
		params.put("status","1");
		params.put("permissionGroupId", Integer.toString(0));
		
		try {
			paramString = AppUtils.object2json(params);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		try {
			sResult = super.doPost(sessionCookie, url , paramString);
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			logger.log(Level.DEBUG, "oh, here?");
			e.printStackTrace();
		} catch (Exception e) {
			logger.log(Level.DEBUG, "oh, or... here?");
		}
		String sOutput = makeOutput(msg, method, url, paramString, sResult);
		// would be cool to be able to determine if 404 was returned here somehow.. 
		if(logDebugMsgs) logger.log(Level.DEBUG, sOutput);
		return sOutput;
	}
	
	
	private String testDelete(Header sessionCookie, int itemId) throws Exception { 
		String sResult = null;
		String url = "/ansi_web/" + this.realm + "/" + itemId;
		String msg = "Delete";
		String method = "DELETE";
				
		sResult = super.doDelete(sessionCookie, url, new HashMap<String,String>());

		String sOutput = makeOutput(msg, method, url, "n/a", sResult);
		if(logDebugMsgs) logger.log(Level.DEBUG, sOutput);
		return sOutput;
	}

	
	private String testAddWithoutSendingJsonData(Header sessionCookie) throws Exception 
	{
		//*  Test the /add function of the servlet..

		String sResult="Failed!";
		String paramString = "";
		String url = "/ansi_web/" + this.realm + "/add";
		String msg = "Add with empty JSON Data";
		String method = "POST";

		HashMap<String, String> params = new HashMap<String, String>(); 
		
		
//		params.put("name", "Some Item Name 1");
//		params.put("description", "A group used to test adding groups");
//		params.put("status","1");
		

		params.put("", "");
		
		paramString = AppUtils.object2json(params);
		
		sResult = super.doPost(sessionCookie, url , paramString);
		
		String sOutput = makeOutput(msg, method, url, paramString, sResult);
		if(logDebugMsgs) logger.log(Level.DEBUG, sOutput);
		return sOutput;
	}
	
	private String testAddPartialJsonData(Header sessionCookie) throws Exception 
	{
		//*  Test the /add function of the servlet..

		String sResult="Failed!";
		String paramString = "";
		String url = "/ansi_web/" + this.realm + "/add";
		String msg = "Add with partial JSON Data";
		String method = "POST";

		HashMap<String, String> params = new HashMap<String, String>(); 
		
		// doesn't work... 
//		params.put("", "");

		// works if all are secified.. 
//		params.put("name", "Some other name..");
//		params.put("description", "A group used to test adding groups");
//		params.put("status","1");

		// doesn't work..  
		params.put("name", "Some other name..");
//		params.put("description", "A group used to test adding groups");
//		params.put("status","1");

		
		paramString = AppUtils.object2json(params);
		
		sResult = super.doPost(sessionCookie, url , paramString);
		
		String sOutput = makeOutput(msg, method, url, paramString, sResult);
		if(logDebugMsgs) logger.log(Level.DEBUG, sOutput);
		return sOutput;
	}
	
	
	private String testAdd(Header sessionCookie, String itemName) throws Exception 
	{
		//*  Test the /add function of the servlet..

		String sResult="Failed!";
		String paramString = "";
		String url = "/ansi_web/" + this.realm + "/add";
		String msg = "Add";
		String method = "POST";

		HashMap<String, String> params = new HashMap<String, String>(); 

		params.put("name", itemName);
		params.put("description", "A group used to test adding groups");
		params.put("status","1");

		paramString = AppUtils.object2json(params);
		
		sResult = super.doPost(sessionCookie, url , paramString);
		
		String sOutput = makeOutput(msg, method, url, paramString, sResult);
		if(logDebugMsgs) logger.log(Level.DEBUG, sOutput);
		return sOutput;
	}
	
	public void go() throws Exception {		
		this.logDebugMsgs = false; 
		this.realm = "permissionGroup";
		
		String s ="";

		super.userId = "kjw@ansi.com";
		super.password = "password1";
		
		Header sessionCookie = super.doLogin();
		
//		s = s + testList(sessionCookie);						// works
//		s = s + testGetById(sessionCookie, 3);					// works
//		s = s + testGetNoCmdOrID(sessionCookie);				// works - returns 404
		 
		// Note : you need to determine id num to delete manually..
//		s = s + testDelete(sessionCookie, 64);					// works	 
//		s = s + testAdd(sessionCookie, "The Friday Group 5");	// works
//		s = s + testUpdate(sessionCookie, 3);					// works
		s = s + testUpdateWithoutPermission();					// works - returns 403 
//		s = s + testUpdateWithNoJsonData(sessionCookie, 3);		// works - returns 200
//		s = s + testPostNoCmdOrId(sessionCookie);		  		// works
//		s = s + testAddWithoutSendingJsonData(sessionCookie);	// works - returns 200 
//		s = s + this.testAddPartialJsonData(sessionCookie);		// works - returns 200 
//		s = s + testPostWithNonNumericId(sessionCookie);  		// works
		
		logger.log(Level.DEBUG, s);
		
		if(logDebugMsgs) 
			logger.log(Level.DEBUG, "End");
		
	}
}
