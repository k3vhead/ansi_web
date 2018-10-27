package com.ansi.scilla.web.test;

import java.util.HashMap;

import org.apache.http.Header;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.permission.request.PermissionRequest;


public class JoshuaServletTester extends TestServlet {
	
	protected final Logger logger = LogManager.getLogger(TestServlet.class);	
	
	/**
		Use this SQL to see the permissions for a given person
		JWL is user_id=1
		DCL is user_id=5
		GAG is user_id=6 
		admin tester is user_id=68
		
		select ansi_user.user_id, ansi_user.first_name, ansi_user.last_name,
			permission_group.permission_group_id, permission_group.name as permission_group_name,
			permission_group_level.permission_name
		from ansi_user
		inner join permission_group on permission_group.permission_group_id=ansi_user.permission_group_id
		inner join permission_group_level on permission_group_level.permission_group_id=permission_group.permission_group_id
		where ansi_user.user_id=1



		Use this SQL to see the permissions assigned to a given group.   Group 2196 is "Joshua's test group". 
		Do whatever you want with that one for your testing.
		
		select permission_group_level.permission_name from permission_group_level where permission_group_id=2196


	 */
	
	public static void main(String[] args) {
		try {
			new JoshuaServletTester().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
	public void go() throws Exception {		
		super.userId = "gag@ansi.com";
//		super.userId = "geo@whitehouse.gov";
		super.password = "password1";
		
		Header sessionCookie = super.doLogin();
		
		
//		String results = testListOfAllPermission(sessionCookie);
//		String results = testListOfGroupPermission(sessionCookie);
		String results = testChangePermissionsForGroup(sessionCookie);
		
		super.doLogoff(sessionCookie);
		
		logger.log(Level.DEBUG, results);
	}




	/**
	 * This should return a list of all permissions available
	 * @param sessionCookie
	 * @return
	 * @throws Exception
	 */
	private String testListOfAllPermission(Header sessionCookie) throws Exception {
		String url = "/ansi_web/permission/list";
		String results = super.doGet(sessionCookie, url, (HashMap<String,String>)null);
		return results;
	}


	/**
	 * This should return a list of all permissions available for a single permission group
	 * @param sessionCookie
	 * @return
	 * @throws Exception
	 */
	private String testListOfGroupPermission(Header sessionCookie) throws Exception {
		String url = "/ansi_web/permission/1158";
		String results = super.doGet(sessionCookie, url, (HashMap<String,String>)null);
		return results;
	}
	
	
	
	private String testChangePermissionsForGroup(Header sessionCookie) throws Exception {
		String url = "/ansi_web/permission/2196";
		
		PermissionRequest request = new PermissionRequest();
		request.setPermissionName(Permission.CONTACT_WRITE.name());
		request.setPermissionIsActive(true);
		String json = AppUtils.object2json(request);
		logger.log(Level.DEBUG,  json);
		String results = super.doPost(sessionCookie, url, json);
		return results;
	}




	private void xxxx(Header sessionCookie) throws Exception {
		String url = "/ansi_web/permission/list";
		
		HashMap<String, String> parmMap = new HashMap<String, String>();
		
//		String results = super.doGet(sessionCookie, url, parmMap);
		
		
		
		parmMap.put("name", "Dave's 3b Update Test");
		parmMap.put("description", "Dave's 3b Update Description");
		parmMap.put("status", "1");		
//		String results = super.doPost(sessionCookie, url, super.makeJson(parmMap));
		String results = super.doGet(sessionCookie, url, (HashMap<String,String>)null);
//		String results = super.doDelete(sessionCookie, url, parmMap);		
	}
	


	
}
