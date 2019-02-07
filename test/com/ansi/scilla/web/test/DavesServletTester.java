package com.ansi.scilla.web.test;

import java.math.BigDecimal;

import org.apache.http.Header;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.EmployeeExpense;
import com.ansi.scilla.web.common.utils.FieldMap;
import com.ansi.scilla.web.common.utils.JsonFieldFormat;


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
//		String results = testNDL(sessionCookie, MyTestType.ADD);
		String results = testEmployeeExpense(sessionCookie, MyTestType.ADD);

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
		String baseUrl = "/ansi_web/claims/nonDirectLabor/";

		if ( type.equals(MyTestType.ITEM)) {
			url = baseUrl + "1";
			results = super.doGet(sessionCookie, url, null);
		} else if ( type.equals(MyTestType.LIST)) {		
			url = baseUrl + "list";
			results = super.doGet(sessionCookie, url, null);
		} else if ( type.equals(MyTestType.ADD)) {	
			url = baseUrl + "add";
			json = "{\"washerId\":5,\"divisionId\":\"106\",\"workDate\":\"12/28/2018\",\"hours\":\"abc\",\"hoursType\":\"V\",\"notes\":\"NGH\"}";
			logger.log(Level.DEBUG, json);
			results = super.doPost(sessionCookie, url, json);
		} else if ( type.equals(MyTestType.UPDATE)) {	
			url = baseUrl + "13";
			json = "{\"washerId\":1,\"divisionId\":106,\"workDate\":\"12/11/2018\",\"hours\":5,\"hoursType\":\"V\",\"notes\":\"NGH2\"}";
			logger.log(Level.DEBUG, json);
			results = super.doPost(sessionCookie, url, json);
		} else if ( type.equals(MyTestType.DELETE)) {
			url = baseUrl + "8";
			json = "{\"washerId\":1,\"divisionId\":106,\"workDate\":\"12/01/2018\",\"hours\":5,\"hoursType\":\"some third type\",\"notes\":\"NGH2\"}";
			logger.log(Level.DEBUG, json);
			results = super.doDelete(sessionCookie, url, null);
			return results;
		} else {
			throw new Exception("Huh?");
		}
		return results;

	}

	
	private String testEmployeeExpense(Header sessionCookie, MyTestType type) throws Exception {
		String url = null;
		String results = null;
		String json = null;

		if ( type.equals(MyTestType.ITEM)) {
			url = "/ansi_web/claims/employeeExpense/1";
			results = super.doGet(sessionCookie, url, null);
		} else if ( type.equals(MyTestType.LIST)) {		
			url = "/ansi_web/claims/employeeExpense/list";
			results = super.doGet(sessionCookie, url, null);
		} else if ( type.equals(MyTestType.ADD)) {	
			url = "/ansi_web/claims/employeeExpense/add";
			json = "{\"washerId\":5,\"workDate\":\"12/22/2018\",\"expenseType\":\"V\", \"amount\":5.0}";
			logger.log(Level.DEBUG, json);
			results = super.doPost(sessionCookie, url, json);
		} else if ( type.equals(MyTestType.UPDATE)) {	
			url = "/ansi_web/claims/employeeExpense/13";
			json = "{\"washerId\":1,\"divisionId\":106,\"workDate\":\"12/11/2018\",\"hours\":5,\"hoursType\":\"V\",\"notes\":\"NGH2\"}";
			logger.log(Level.DEBUG, json);
			results = super.doPost(sessionCookie, url, json);
		} else if ( type.equals(MyTestType.DELETE)) {
			url = "/ansi_web/claims/employeeExpense/8";
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
