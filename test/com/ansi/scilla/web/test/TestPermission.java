package com.ansi.scilla.web.test;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.UserPermission;

public class TestPermission {

	public static void main(String[] args) {
		try {
			new TestPermission().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void go() throws Exception {
		Connection conn = null;
		
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);;
			List<UserPermission> plist = UserPermission.getUserPermissions(conn, 1);
			for ( UserPermission p : plist ) {
				System.out.println(p);
			}
			conn.rollback();
		} catch ( Exception e ) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}
}
