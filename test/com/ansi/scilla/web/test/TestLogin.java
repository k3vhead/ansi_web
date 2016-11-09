package com.ansi.scilla.web.test;

import java.sql.Connection;

import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.request.LoginRequest;
import com.ansi.scilla.web.response.LoginResponse;
import com.ansi.scilla.web.servlets.LoginServlet;
import com.thewebthing.commons.lang.JsonUtils;

public class TestLogin extends LoginServlet {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		try {
			TesterUtils.makeLoggers();
			new TestLogin().goPost();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		Connection conn = null;
		LoginRequest request = new LoginRequest();
		request.setUserid("dclewis@thewebthing.com");
		request.setPassword("something cryptic");
		String json = JsonUtils.object2JSON(request);
		System.out.println(json);
		try {
			conn = AppUtils.getConn();
			LoginResponse loginResponse = doWork(conn, json);
			System.out.println(loginResponse);
		} catch ( Exception e) {
			e.printStackTrace();
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	public void goPost() throws Exception {
		LoginRequest request = new LoginRequest();
		request.setUserid("dclewis@thewebthing.com");
		request.setPassword("password1");
		String json = JsonUtils.object2JSON(request);
		System.out.println(json);
		String returnString = TesterUtils.PostJson("http://127.0.0.1:8080/ansi_web/login", json);
		System.out.println(returnString);
	}

}
