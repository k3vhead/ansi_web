package com.ansi.scilla.web.test;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.UserPermission;

public class TestUserPermission {

	public static void main(String[] args) {
		try {
			new TestUserPermission().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void go() throws Exception {
		Connection conn = null;
		
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			List<UserPermission> list = UserPermission.getUserPermissions(conn, 1);
			Collections.sort(list);
			for (UserPermission up : list ) {
				System.out.println(up.getPermissionName() + " " + up.getLevel());
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
