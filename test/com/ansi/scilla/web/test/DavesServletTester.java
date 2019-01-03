package com.ansi.scilla.web.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.NonDirectLabor;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.FieldMap;
import com.ansi.scilla.web.common.utils.JsonFieldFormat;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.permission.request.PermissionRequest;
import com.ansi.scilla.web.user.request.DivisionUserRequest;


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
		super.userId = null;
		userId = "dclewis@thewebthing.com";
//		userId = "admin.tester@.com";  // admin
//		userId = "keegan.ovitt@gmail.com";  // permission group 1
//		userId = "geo@whitehouse.gov"; //division manager
//		userId = "dm.readonly@ansi.com"; // div mgr ro
//		userId = "dmt@ansi.com";  // special override
		super.password = "password1";
		
		Header sessionCookie = super.doLogin();
		String results = testNDL(sessionCookie);

//		String results = super.doPost(sessionCookie, url, super.makeJson(parmMap));
//		String results = super.doGet(sessionCookie, url, (HashMap<String,String>)null);
//		String results = super.doDelete(sessionCookie, url, parmMap);
		super.doLogoff(sessionCookie);
		
		logger.log(Level.DEBUG, results);
	}




	private String testNDL(Header sessionCookie) throws Exception {
		// check the get
//		String url = "/ansi_web/nonDirectLabor/1";
//		String results = super.doGet(sessionCookie, url, null);
//		return results;
		
		
		// check the list
//		String url = "/ansi_web/nonDirectLabor/list";
//		String results = super.doGet(sessionCookie, url, null);
//		return results;
		
		
		
		// check the add
		String url = "/ansi_web/nonDirectLabor/add";
		String json = "{\"washerId\":5,\"divisionId\":106,\"workDate\":\"12/24/2018\",\"hours\":455,\"hoursType\":\"T\",\"notes\":\"NGH\"}";
		logger.log(Level.DEBUG, json);
		String results = super.doPost(sessionCookie, url, json);
		return results;
		
		
		// check the update
//		String url = "/ansi_web/nonDirectLabor/8";
//		String json = "{\"washerId\":1,\"divisionId\":106,\"workDate\":\"12/01/2018\",\"hours\":5,\"hoursType\":\"some third type\",\"notes\":\"NGH2\"}";
//		logger.log(Level.DEBUG, json);
//		String results = super.doPost(sessionCookie, url, json);
//		return results;
	}


	


	
}
