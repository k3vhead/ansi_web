package com.ansi.scilla.web.test.user;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.user.query.UserLookup;
import com.ansi.scilla.web.user.query.UserLookupItem;


public class TestUserLookup {

	public static void main(String[] args) {
		try {
			new TestUserLookup().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			Integer permissionGroupId = 4;
			UserLookup ul = new UserLookup(permissionGroupId, 0, 10);
			ul.setSearchTerm("lewis");
			List<UserLookupItem> ulList = ul.select(conn);
			for ( UserLookupItem uli : ulList ) {
				System.out.println(uli);
			}
			Integer count = ul.selectCount(conn);
			Integer all = ul.countAll(conn);
			
			System.out.println(count + "/" + all);
		} finally {
			conn.close();
		}
	}
}
