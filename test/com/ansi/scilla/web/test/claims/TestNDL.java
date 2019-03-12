package com.ansi.scilla.web.test.claims;

import java.lang.reflect.Method;
import java.sql.Connection;

import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.web.claims.servlet.NonDirectLaborServlet;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestNDL {

	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			
			String jsonString = "{\"workDate\":\"03/06/2019\",\"washerName\":\"Joshua Lewis\",\"washerId\":\"1\",\"hours\":\"8\",\"notes\":\"asd asdf\",\"divisionId\":\"102\",\"hoursType\":\"V\"}";
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			JsonNode jsonNode = mapper.readTree(jsonString);

			Method overrideAdd = NonDirectLaborServlet.class.getMethod("overrideAdd", new Class[] {Connection.class, HttpServletResponse.class, SessionUser.class, JsonNode.class} );
			Integer id = (Integer)overrideAdd.invoke(null, new Object[] {conn, null, null, jsonNode});
		} finally {
			conn.rollback();
			conn.close();
		}
		

	}
	
	public static void main(String[] args) {
		try {
			System.out.println("Start");
			new TestNDL().go();
			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
