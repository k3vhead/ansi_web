package com.ansi.scilla.web.test.permission;

import java.sql.Connection;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.permission.response.PermissionGroupListResponse;
import com.ansi.scilla.web.test.TesterUtils;

public class TestPermissionGroups {

	public static void main(String[] args) {
		TesterUtils.makeLoggers();
		try {
			new TestPermissionGroups().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			PermissionGroupListResponse response = new PermissionGroupListResponse(conn);
			System.out.println(response.toJson());
		} finally {
			if ( conn != null ) {
				conn.close();
			}
		}
	}
}
