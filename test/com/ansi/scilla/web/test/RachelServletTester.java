package com.ansi.scilla.web.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.permission.request.PermissionRequest;
import com.ansi.scilla.web.user.request.DivisionUserRequest;

public class RachelServletTester extends TestServlet {

	protected final Logger logger = LogManager.getLogger(TestServlet.class);

	public static void main(String[] args) {
		try {
			new RachelServletTester().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void go() throws Exception {
		super.userId = "harnesrs@rose-hulman.edu";
		// super.userId = "dclewis@thewebthing.com";
		// super.userId = "admin.tester@ansi.com";
		// super.userId = "geo@whitehouse.gov";
		super.password = "password1";

		Header sessionCookie = super.doLogin();

		// String results = testListOfAllPermission(sessionCookie);
		// String results = testListOfGroupPermission(sessionCookie);
		// String results = testChangePermissionsForGroup(sessionCookie);
		String results = testEmployeeExpense(sessionCookie);

		super.doLogoff(sessionCookie);

		logger.log(Level.DEBUG, results);
	}

	private String testEmployeeExpense(Header sessionCookie)
			throws ClientProtocolException, URISyntaxException, IOException {
		String url = "/ansi_web/employeeExpense/";
		String results = super.doGet(sessionCookie, url, (HashMap<String, String>) null);
		return results;
	}

	private void testLogin(Header sessionCookie) throws Exception {
		super.doLogin();

	}

	/**
	 * This should return a list of all permissions available
	 * 
	 * @param sessionCookie
	 * @return
	 * @throws Exception
	 */
	private String testListOfAllPermission(Header sessionCookie) throws Exception {
		String url = "/ansi_web/permission/list";
		String results = super.doGet(sessionCookie, url, (HashMap<String, String>) null);
		return results;
	}

	/**
	 * This should return a list of all permissions available for a single
	 * permission group
	 * 
	 * @param sessionCookie
	 * @return
	 * @throws Exception
	 */
	private String testListOfGroupPermission(Header sessionCookie) throws Exception {
		String url = "/ansi_web/permission/2196";
		String results = super.doGet(sessionCookie, url, (HashMap<String, String>) null);
		return results;
	}

	private String testChangePermissionsForGroup(Header sessionCookie) throws Exception {
		String url = "/ansi_web/permission/2196";

		PermissionRequest request = new PermissionRequest();
		request.setPermissionName(Permission.CONTACT_WRITE.name());
		// request.setPermissionName("xxx");
		request.setPermissionIsActive(false);
		String json = AppUtils.object2json(request);
		logger.log(Level.DEBUG, json);
		String results = super.doPost(sessionCookie, url, json);
		return results;
	}

	private String testDivUserServUpdate(Header sessionCookie) throws Exception {
		String url = "/ansi_web/divisionUser/1";

		DivisionUserRequest request = new DivisionUserRequest();
		request.setDivisionId(101);
		request.setActive(true);
		String json = AppUtils.object2json(request);
		logger.log(Level.DEBUG, json);
		String results = super.doPost(sessionCookie, url, json);
		return results;
	}

	private void xxxx(Header sessionCookie) throws Exception {
		String url = "/ansi_web/permission/list";

		HashMap<String, String> parmMap = new HashMap<String, String>();

		// String results = super.doGet(sessionCookie, url, parmMap);

		parmMap.put("name", "Dave's 3b Update Test");
		parmMap.put("description", "Dave's 3b Update Description");
		parmMap.put("status", "1");
		// String results = super.doPost(sessionCookie, url,
		// super.makeJson(parmMap));
		String results = super.doGet(sessionCookie, url, (HashMap<String, String>) null);
		// String results = super.doDelete(sessionCookie, url, parmMap);
	}

}
