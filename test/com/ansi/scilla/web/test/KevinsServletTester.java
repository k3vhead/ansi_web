package com.ansi.scilla.web.test;

import java.util.HashMap;

import org.apache.http.Header;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.URLEncoder;


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
		
		if(LogDebugMsgs == true) this.logger.log(Level.DEBUG, "Testing /list function ");
		
		pageContent1 = super.doGet(sessionCookie, "/ansi_web/" + this.realm + "/list", new HashMap<String,String>());
		
		System.out.println("response to /list \n=================\n"+pageContent1);		
	}
	
	private void TestGetItem(int itemRecordId) throws Exception 
	{
		//******************************************
		//*
		//* Test the /id# function of the servlet.. 
		//*
		Header sessionCookie = doLogin();
		String pageContent1;
		String _url;
		
		_url = "/ansi_web/" + this.realm + "/" + itemRecordId;
		System.out.println("Sending url : " + _url);		
		
		if(LogDebugMsgs == true) this.logger.log(Level.DEBUG, "Testing GetItem Method.. ");
		pageContent1 = super.doGet(sessionCookie, _url, new HashMap<String,String>());

		System.out.println("response to /" + itemRecordId + "\n=================\n"+pageContent1);		
	}


	private void TestAddItem(String ItemName) throws Exception 
	{
		//******************************************
		//*
		//* Test the /id# function of the servlet.. 
		//*
		//if(LogDebugMsgs == true) this.logger.log(Level.DEBUG, "begin");

		Header sessionCookie = doLogin();
		String pageContent1="Failed!";
		HashMap<String, String> ValuesToAdd;
		
		//String _url="";
		
		ValuesToAdd = new HashMap<String, String>();
		ValuesToAdd.put("name", ItemName);
		ValuesToAdd.put("description", "A group used to test adding groups");
		ValuesToAdd.put("status","0");
		
		String p ="";
		p = "?";
		p = p + "name=" + ItemName;
		p = p + "&description=A permission group used to test adding permission groups";
		p = p + "&status=0";

		p = URLEncoder.encode(p);
		
		
		if(LogDebugMsgs == true) this.logger.log(Level.DEBUG, "parmString = " + p);
		
		String _url="/ansi_web/" + this.realm + "/ADD";
		
		if(LogDebugMsgs == true) this.logger.log(Level.DEBUG, "Testing Add Method via doPost.. ");
		
		//pageContent1 = super.doPost(sessionCookie, _url , p);
		pageContent1 = super.doPost(sessionCookie, _url , ValuesToAdd);
		
		System.out.println("url sent was : " + _url);		
		System.out.println("response to /add : \n" + pageContent1);		
		System.out.println("p = " + p);		
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
		
		
		if(LogDebugMsgs == true) this.logger.log(Level.DEBUG, "Sending json :" + j);

		String URL = "http://127.0.0.1:8080/ansi_web/" + this.realm;
		
		String url = URL + "/add";

		if(LogDebugMsgs == true) this.logger.log(Level.DEBUG, "Sending url :" + url);
		
		System.out.println(url);
		String json = TesterUtils.postJson(url, j);
		System.out.println(json);
	}	
	
	public void go() throws Exception {		
		Boolean RunWorkingTests;
		Boolean StillTestingThese;
		
		
		// Set this to true to spew debug messages to console..
		this.LogDebugMsgs = super.LogDebugMsgs = true;  
		// = this.LogDebugMsgs; 
		
		this.realm = "permissionGroup";

		//super.userId = "geo@whitehouse.gov";
		//super.password = "password1";
		
		super.userId = "kjw@ansi.com";
		super.password = "password1";
		
		RunWorkingTests = false;
		StillTestingThese = true;
		
		if(RunWorkingTests) 
		{
			this.TestListFunction();	//works..
			this.TestGetItem(2);		//works..
			this.TestGetItem(6);		//works..
			this.TestGetItem(7);		//works..
		}
		else
		{
			if(LogDebugMsgs) this.logger.log(Level.DEBUG, "Skipping tests that work already..");
		}

		if(StillTestingThese)
		{
			//this.TestListFunction();	//works..
			//this.TestGetItem(2);		//works..
			this.TestListFunction();	//works..
			//Failed nicely.. 
			//this.testAddViaJSON("TestGroup1");  	//testing this now..fails for some reason

			//Still working on this one.. 
			//this.TestAddItem("AnotherTestGroup");	//started working on this way... 
		}
				
		if(LogDebugMsgs) this.logger.log(Level.DEBUG, "End");
		
	}
}
