package com.ansi.scilla.web.test;

import java.util.HashMap;

import org.apache.http.Header;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KevinsServletTester extends TestServlet {
	
	protected final Logger logger = LogManager.getLogger(TestServlet.class);
	private boolean LogDebugMsgs;
	private String realm;
	//private string user_id;
	//private string password;
	
	public static void main(String[] args) 
	{
		try 
		{

			//this.user_id = "geo@whitehouse.gov";
			//this.password = "password1";
			new KevinsServletTester().go();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	
	private void TestListFunction() throws Exception 
	{
		//******************************************
		//*
		//* Test the /list function of the servlet.. 
		//*
		//******************************************kjw
		Header sessionCookie = doLogin();
		String pageContent1;
		
		if(LogDebugMsgs) this.logger.log(Level.DEBUG, "Testing /list function ");
		
		pageContent1 = super.doGet(sessionCookie, "/ansi_web/" + this.realm + "/list", new HashMap<String,String>());
		
		System.out.println("response to /list \n=================\n"+pageContent1);		
	}
	
	private void TestGetItem(int ItemRecordId) throws Exception 
	{
		//******************************************
		//*
		//* Test the /id# function of the servlet.. 
		//*
		Header sessionCookie = doLogin();
		String pageContent1;
		
		if(LogDebugMsgs) this.logger.log(Level.DEBUG, "Testing GetItem Method.. ");
		pageContent1 = super.doGet(sessionCookie, "/ansi_web/" + this.realm + "/" + ItemRecordId, new HashMap<String,String>());

		System.out.println("response to /" + ItemRecordId + "\n=================\n"+pageContent1);		
	}


	private void TestAddItem(String ItemName) throws Exception 
	{
		//******************************************
		//*
		//* Test the /id# function of the servlet.. 
		//*
		if(LogDebugMsgs) this.logger.log(Level.DEBUG, "begin");

		Header sessionCookie = doLogin();
		String pageContent1="Failed!";
		HashMap<String, String> ValuesToAdd;
		
		ValuesToAdd = new HashMap<String, String>();
		ValuesToAdd.put("name", ItemName);
		ValuesToAdd.put("description", "A group used to test adding groups");
		ValuesToAdd.put("status","0");
		
		if(LogDebugMsgs) this.logger.log(Level.DEBUG, "Testing Add Method via doPost.. ");
		//pageContent1 = super.doPost(sessionCookie, "/ansi_web/" + this.realm + "/add", ValuesToAdd);

		System.out.println("response to /add : " + pageContent1);		
	}

	
	
	private void testAddViaJSON(String ItemName) throws Exception {
		if(LogDebugMsgs) this.logger.log(Level.DEBUG, "Begin");

		Header sessionCookie = doLogin();

		String j;
		//member variables From PermGroupCountRecord...
		//private String name;
		//private String description;
		//private Integer status;
		//private Integer userCount;
		//private Integer permissionGroupId;		
		
		j = "{";
		j = j + "\"name\": \"" + ItemName + "\",";
		j = j + "\"description\": \"A permission group used to test adding permission groups\",";
		j = j + "\"status\": 0";
		j = j + "}";
		
		if(LogDebugMsgs) this.logger.log(Level.DEBUG, "Sending json :" + j);

		String URL = "http://127.0.0.1:8080/ansi_web/" + this.realm;
		
		String url = URL + "/add";

		if(LogDebugMsgs) this.logger.log(Level.DEBUG, "Sending url :" + url);
		
		System.out.println(url);
		String json = TesterUtils.postJson(url, j);
		System.out.println(json);
	}	
	
	public void go() throws Exception {

		
		Boolean RunWorkingTests;
		
		// Set this to true to spew debug messages to console..
		this.LogDebugMsgs = true;  
		
		
		this.realm = "permissionGroup";

		super.userId = "geo@whitehouse.gov";
		super.password = "password1";
		
		RunWorkingTests = false;
		
		if(RunWorkingTests) 
		{
			this.TestListFunction();	//works..
			this.TestGetItem(2);		//works..
			this.TestGetItem(6);		//works..
			this.TestGetItem(7);		//works..
		}
		else
		{
			this.logger.log(Level.DEBUG, "Skipping tests that work already..");
		}

		//Failed nicely.. 
		this.testAddViaJSON("TestGroup1");  	//testing this now..fails for some reason

		//Still working on this one.. 
		this.TestAddItem("AnotherTestGroup");	//started working on this way... 
		
		
		
		
	}
}
