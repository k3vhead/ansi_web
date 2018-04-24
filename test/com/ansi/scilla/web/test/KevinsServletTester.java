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

	
	private void testListFunction(Header sessionCookie) throws Exception 
	{
		//******************************************
		//*
		//* Test the /list function of the servlet.. 
		//*
		//******************************************kjw
		String jsonResult="failed!";
		String url="";
		
		if(logDebugMsgs) 
			logger.log(Level.DEBUG, "Testing /list function ");
		
		url = "/ansi_web/" + this.realm + "/list";
		
		jsonResult = super.doGet(sessionCookie, url , new HashMap<String,String>());
		
		if(logDebugMsgs) 
			logger.log(Level.DEBUG, url);
		
		if(logDebugMsgs) 
			logger.log(Level.DEBUG, jsonResult);
		
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
	
	
	private void testDelete(Header sessionCookie, int itemId) throws Exception { 
		String sResult = null;
		String url = "/ansi_web/" + this.realm + "/" + itemId;
		
		if(logDebugMsgs) logger.log(Level.DEBUG, "Sending url : " + url);
		sResult = super.doDelete(sessionCookie, url, new HashMap<String,String>());

		System.out.println("\nJSON Returned is :\n " + sResult);		
	}
	
	private void testAddItem(Header sessionCookie, String itemName) throws Exception 
	{
		//*  Test the /add function of the servlet..
		
		if(logDebugMsgs) 
			logger.log(Level.DEBUG, "begin");

		String sResult="Failed!";
		String paramString = "";
		String url = "/ansi_web/" + this.realm + "/add";

		HashMap<String, String> params = new HashMap<String, String>(); 

		params.put("name", itemName);
		params.put("description", "A group used to test adding groups");
		params.put("status","1");

		paramString = AppUtils.object2json(params);
		
		if(logDebugMsgs) 
			logger.log(Level.DEBUG, "\nSending url: " + url);

		if(logDebugMsgs) 
			logger.log(Level.DEBUG, "\nSending Data: \n" + paramString);
				
		sResult = super.doPost(sessionCookie, url , paramString);
		
		if(logDebugMsgs) 
			logger.log(Level.DEBUG, "\nReturned Json = \n " +  sResult);
	}

	private void testDeleteItem(Header sessionCookie, String itemName) {
		
	}
	
	public void go() throws Exception {		
		this.logDebugMsgs = true; 
		
		this.realm = "permissionGroup";

		//super.userId = "geo@whitehouse.gov";
		//super.password = "password1";
		
		super.userId = "kjw@ansi.com";
		super.password = "password1";
		
		Header sessionCookie = super.doLogin();
		
		testListFunction(sessionCookie);
		testAddItem(sessionCookie, "The Tuesday Group 1");
		testUpdate(sessionCookie, 3);
		testDelete(sessionCookie, 19);
		
		if(logDebugMsgs) 
			logger.log(Level.DEBUG, "End");
		
	}
}
