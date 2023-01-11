package com.ansi.scilla.web.test.user;

import java.sql.Connection;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.test.TesterUtils;
import com.ansi.scilla.web.user.query.UserLookupItem;
import com.ansi.scilla.web.user.response.UserResponse;

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
			conn = AppUtils.getDevConn();
			UserResponse r = new UserResponse(conn, UserResponse.UserListType.MANAGER);
//			r.sort("email");
//			System.out.println(r.toJson());
			for ( UserLookupItem i : r.getUserList() ) {
				System.out.println(i.getFirstName() + "\t" + i.getLastName() + "\t" + i.getEmail());
			}
		} finally {
			conn.close();
		}
	}

}
