package com.ansi.scilla.web.test;

import java.sql.Connection;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.response.user.UserResponse;

public class TestUser {

	public static void main(String[] args) {
		TesterUtils.makeLoggers();
		try {
			new TestUser().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getConn();
			UserResponse r = new UserResponse(conn, UserResponse.UserListType.MANAGER);
			System.out.println(r.toJson());
		} finally {
			conn.close();
		}
	}

}
