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
		String results = testNDL(sessionCookie, MyTestType.UPDATE);

//		String results = super.doPost(sessionCookie, url, super.makeJson(parmMap));
//		String results = super.doGet(sessionCookie, url, (HashMap<String,String>)null);
//		String results = super.doDelete(sessionCookie, url, parmMap);
		super.doLogoff(sessionCookie);
		
		logger.log(Level.DEBUG, results);
	}




	private String testNDL(Header sessionCookie, MyTestType type) throws Exception {
		String url = null;
		String results = null;
		String json = null;

		if ( type.equals(MyTestType.ITEM)) {
			url = "/ansi_web/nonDirectLabor/1";
			results = super.doGet(sessionCookie, url, null);
		} else if ( type.equals(MyTestType.LIST)) {		
			url = "/ansi_web/nonDirectLabor/list";
			results = super.doGet(sessionCookie, url, null);
		} else if ( type.equals(MyTestType.ADD)) {	
			url = "/ansi_web/nonDirectLabor/add";
			json = "{\"washerId\":5,\"divisionId\":106,\"workDate\":\"12/21/2018\",\"hours\":4,\"hoursType\":\"V\",\"notes\":\"NGH\"}";
			logger.log(Level.DEBUG, json);
			results = super.doPost(sessionCookie, url, json);
		} else if ( type.equals(MyTestType.UPDATE)) {	
			url = "/ansi_web/nonDirectLabor/13";
			json = "{\"washerId\":1,\"divisionId\":106,\"workDate\":\"12/11/2018\",\"hours\":5,\"hoursType\":\"V\",\"notes\":\"NGH2\"}";
			logger.log(Level.DEBUG, json);
			results = super.doPost(sessionCookie, url, json);
		} else if ( type.equals(MyTestType.DELETE)) {
			url = "/ansi_web/nonDirectLabor/8";
			json = "{\"washerId\":1,\"divisionId\":106,\"workDate\":\"12/01/2018\",\"hours\":5,\"hoursType\":\"some third type\",\"notes\":\"NGH2\"}";
			logger.log(Level.DEBUG, json);
			results = super.doDelete(sessionCookie, url, null);
			return results;
		} else {
			throw new Exception("Huh?");
		}
		return results;

	}


	public enum MyTestType { ITEM,LIST,ADD,UPDATE,DELETE; }
	


	
}
