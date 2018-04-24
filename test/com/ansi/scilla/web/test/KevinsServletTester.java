package com.ansi.scilla.web.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.ansi.scilla.common.db.PermissionGroup;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.permission.response.PermissionGroupResponse;
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

	
	private String makeOutput(String message, String method, String url, String data, String result) {
		String s = "\n\n";
		s = s + "[ " + message + " ]";
		s = s + "(" + method + ")";
		s = s + "\nurl sent : \t";
		s = s + url;
		s = s + "\ndata sent :";
		s = s + "\t" + data;
		s = s + "\nreturned : \t"; 
		s = s + result; 
//		s = s + "\n";
		
		return s;		
	}
	
	private String testList(Header sessionCookie) throws Exception 
	{
		//* Test the /list function of the servlet.. 
		String sResult="failed!";
		String url = "/ansi_web/" + this.realm + "/list";
		String msg = "Testing Get List";
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
		String msg = "Testing Get by ID";
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
		String msg = "Testing Update";
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
	
	
	private String testDelete(Header sessionCookie, int itemId) throws Exception { 
		String sResult = null;
		String url = "/ansi_web/" + this.realm + "/" + itemId;
		String msg = "Testing Delete";
		String method = "DELETE";
				
		sResult = super.doDelete(sessionCookie, url, new HashMap<String,String>());

		String sOutput = makeOutput(msg, method, url, "n/a", sResult);
		if(logDebugMsgs) logger.log(Level.DEBUG, sOutput);
		return sOutput;
	}
	
	private String testAdd(Header sessionCookie, String itemName) throws Exception 
	{
		//*  Test the /add function of the servlet..

		String sResult="Failed!";
		String paramString = "";
		String url = "/ansi_web/" + this.realm + "/add";
		String msg = "Testing Add";
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

		//super.userId = "geo@whitehouse.gov";
		//super.password = "password1";
		
		super.userId = "kjw@ansi.com";
		super.password = "password1";
		
		Header sessionCookie = super.doLogin();
		
		s = s + testList(sessionCookie);
		s = s + testGetById(sessionCookie, 3);
		// Note : you need to determine id num to delete manually.. 
		s = s + testDelete(sessionCookie, 52);	 
		s = s + testAdd(sessionCookie, "The Tuesday Group 3");
		s = s + testUpdate(sessionCookie, 3);
		
		logger.log(Level.DEBUG, s);
		
		if(logDebugMsgs) 
			logger.log(Level.DEBUG, "End");
		
	}
}
