package com.ansi.scilla.web.test;


import java.sql.Connection;

import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.request.LoginRequest;
import com.ansi.scilla.web.response.login.LoginResponse;
import com.ansi.scilla.web.servlets.login.LoginServlet;
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
		request.setPassword("password1");
		String json = JsonUtils.object2JSON(request);
		System.out.println(json);
		try {
			conn = AppUtils.getDevConn();
			LoginResponse loginResponse = doWork(conn, json);
			System.out.println(loginResponse);
		} catch ( Exception e) {
			e.printStackTrace();
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	public void goPost() throws Exception {
		LoginRequest request = makeGoodRequest();
//		LoginRequest request = makeBadId();
//		LoginRequest request = makeBadPass();
//		LoginRequest request = makeMissingId();
//		LoginRequest request = makeMissingPass();
//		LoginRequest request = makeNullPass();
//		LoginRequest request = makeNullId();
		
//		String json = JsonUtils.object2JSON(request);
		String json = AppUtils.object2json(request);
		System.out.println(json);
		String returnString = TesterUtils.postJson("http://127.0.0.1:8080/ansi_web/login", json);
		System.out.println(returnString);
	}

	private LoginRequest makeGoodRequest() {
		LoginRequest request = new LoginRequest();
		request.setUserid("jwlewis@thewebthing.com");
		request.setPassword("password1");
		return request;
	}

	private LoginRequest makeBadId() {
		LoginRequest request = new LoginRequest();
		request.setUserid("xxx@thewebthing.com");
		request.setPassword("password1");
		return request;
	}

	private LoginRequest makeBadPass() {
		LoginRequest request = new LoginRequest();
		request.setUserid("dclewis@thewebthing.com");
		request.setPassword("xxxx");
		return request;
	}

	private LoginRequest makeMissingId() {
		LoginRequest request = new LoginRequest();
		request.setUserid("");
		request.setPassword("password1");
		return request;
	}


	private LoginRequest makeMissingPass() {
		LoginRequest request = new LoginRequest();
		request.setUserid("dclewis@thewebthing.com");
		request.setPassword("");
		return request;
	}
	
	private LoginRequest makeNullPass() {
		LoginRequest request = new LoginRequest();
		request.setUserid("dclewis@thewebthing.com");
		request.setPassword("null");
		return request;
	}


	private LoginRequest makeNullId() {
		LoginRequest request = new LoginRequest();
		request.setUserid(null);
		request.setPassword("password1");
		return request;
	}


}
