package com.ansi.scilla.web.test;

import java.util.HashMap;

import org.apache.http.Header;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.permission.request.PermissionRequest;


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
		
		HashMap<String, String> parmMap = new HashMap<String, String>();
		String url = "/ansi_web/permission/2196"; // valid permission group id
//		String url = "/ansi_web/permission/xx"; // invalid permission group id
		
		
//		String results = super.doGet(sessionCookie, url, parmMap);
		
		
		
//		parmMap.put(PermissionRequest.PERMISSION_NAME, Permission.ADDRESS_WRITE.name());
//		parmMap.put(PermissionRequest.PERMISSION_IS_ACTIVE, String.valueOf(new Boolean(true)));
		String results = super.doPost(sessionCookie, url, super.makeJson(parmMap));
//		String results = super.doGet(sessionCookie, url, (HashMap<String,String>)null);
		
		
		
		
//		String results = super.doDelete(sessionCookie, url, parmMap);
		super.doLogoff(sessionCookie);
		
		logger.log(Level.DEBUG, results);
	}


	


	
}
