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
	private int addedItemId;
	//private string user_id;
	//private string password;
	
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

	
	private void testListFunction(Header sessionCookie) throws Exception 
	{
		//******************************************
		//*
		//* Test the /list function of the servlet.. 
		//*
		//******************************************kjw
		String pageContent1;
		
		if(logDebugMsgs == true) this.logger.log(Level.DEBUG, "Testing /list function ");
		
		pageContent1 = super.doGet(sessionCookie, "/ansi_web/" + this.realm + "/list", new HashMap<String,String>());
		
		System.out.println("response to /list \n=================\n"+pageContent1);		
	}
	
	private void testGetItem(Header sessionCookie, int itemId) throws Exception 
	{
		//******************************************
		//*
		//* Test the /id# function of the servlet.. 
		//*
		String pageContent1 = "Failed!";
				
		String _url = "/ansi_web/" + this.realm + "/" + itemId;
		System.out.println("Sending url : " + _url);		
		
		if(logDebugMsgs == true) this.logger.log(Level.DEBUG, "Testing GetItem Method.. ");
		pageContent1 = super.doGet(sessionCookie, _url, new HashMap<String,String>());
		
		String s = "response to /" + itemId + "\n=================\n"+pageContent1;
		if(logDebugMsgs == true) this.logger.log(Level.DEBUG, s);
	}

	private void testUpdate(Header sessionCookie, int itemId) {
		//******************************************
		//*
		//* Test the /add function of the servlet.. 
		//*
		//if(LogDebugMsgs == true) this.logger.log(Level.DEBUG, "begin");
		
		String _url = "/ansi_web/" + this.realm + "/" + itemId;				
		
		String pageContent1="Failed!";
		HashMap<String, String> valuesToAdd;
				
		valuesToAdd = new HashMap<String, String>();
		valuesToAdd.put("name", "This used to be test group 3");
		valuesToAdd.put("description", "Kevin Just Updated this Group");
		valuesToAdd.put("status","1");
		valuesToAdd.put("permissionGroupId", Integer.toString(itemId));
		
		String paramString="";
		try {
			paramString = AppUtils.object2json(valuesToAdd);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(paramString);
				
		//String _url="/ansi_web/" + this.realm + "/3";
		
		if(logDebugMsgs == true) this.logger.log(Level.DEBUG, "Testing Update Method via doPost.. ");
		
		try {
			pageContent1 = super.doPost(sessionCookie, _url , paramString);
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("url sent was : " + _url);		
		System.out.println("response to update of permissionGroupId " + itemId + " : \n" + pageContent1);		
	}
	
	
	private void testDelete(Header sessionCookie, int itemRecordId) throws Exception { 
		String pageContent = null;
		String url = "/ansi_web/" + this.realm + "/" + itemRecordId;
		System.out.println("Sending url : " + url);		
		
		if(logDebugMsgs == true) this.logger.log(Level.DEBUG, "Testing GetItem Method.. ");
		pageContent = super.doDelete(sessionCookie, url, new HashMap<String,String>());

		System.out.println("response to " + url + "\n=================\n"+pageContent);		
	}
	
	private void testAddItem(Header sessionCookie, String itemName) throws Exception 
	{
		//******************************************
		//*
		//* Test the /add function of the servlet.. 
		//*
		if(LogDebugMsgs == true) this.logger.log(Level.DEBUG, "begin");

		String returnedPageContent="Failed!";
		HashMap<String, String> valuesToAdd;
				
		valuesToAdd = new HashMap<String, String>();
		valuesToAdd.put("name", itemName);
		valuesToAdd.put("description", "A group used to test adding groups");
		valuesToAdd.put("status","1");
		String paramString = AppUtils.object2json(valuesToAdd);
		System.out.println(paramString);
				
		String _url="/ansi_web/" + this.realm + "/add";

		if(logDebugMsgs == true) this.logger.log(Level.DEBUG, "sending url: " + _url);
		
		String sResult="";
		
		sResult = super.doPost(sessionCookie, _url , paramString);		
		if(logDebugMsgs == true) this.logger.log(Level.DEBUG, "sending url: " + sResult);
	}

	
	public void go() throws Exception {		
//		Boolean RunWorkingTests;
//		Boolean StillTestingThese;
		
		
		// Set this to true to spew debug messages to console..
		// = this.LogDebugMsgs; 
		
		this.realm = "permissionGroup";

		//super.userId = "geo@whitehouse.gov";
		//super.password = "password1";
		super.userId = "kjw@ansi.com";
		super.password = "password1";
		Header sessionCookie = super.doLogin();
		
//		RunWorkingTests = false;
//		StillTestingThese = true;
		
//		if(RunWorkingTests) 
//		{
//			this.TestListFunction();	//works..
//			this.TestGetItem(2);		//works..
//			this.TestGetItem(6);		//works..
//			this.TestGetItem(7);		//works..
//		}
//		else
//		{
//			if(logDebugMsgs) this.logger.log(Level.DEBUG, "Skipping tests that work already..");
//		}
//
//		if(StillTestingThese)
//		{
			//this.TestListFunction();	//works..
//			this.TestGetItem(2);		//works..
//			this.TestListFunction();	//works..
			//Failed nicely.. 
			//this.testAddViaJSON("TestGroup1");  	//testing this now..fails for some reason

			//Still working on this one.. 
			//this.TestAddItem(sessionCookie, "AnotherTestGroup");	//started working on this way... 
//		}
		
		
//		this.testAddItem(sessionCookie, "Test Group 19");
//		this.testGetItem(sessionCookie, 9);
//		this.testListFunction(sessionCookie);
//		this.testDelete(sessionCookie, 8);

		this.testUpdate(sessionCookie, 3);
		
		if(logDebugMsgs) this.logger.log(Level.DEBUG, "End");
		
	}
}
