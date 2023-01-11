package com.ansi.scilla.web.test.permission;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.user.query.PermissionUserLookup;
import com.ansi.scilla.web.user.query.UserLookupItem;

public class TestPermissionUserLookup {

	public static void main(String[] args) {
		try {
			new TestPermissionUserLookup().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void xx() {
		List<Permission> plist = Permission.QUOTE.makeChildTree();
		for ( Permission p : plist ) {
			System.out.println(p.name());
		}
	}
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			PermissionUserLookup pul = new PermissionUserLookup(Permission.QUOTE_CREATE.name());
			pul.setSortBy("first_name");
//			System.out.println("Count all: "  + pul.countAll(conn));
			List<UserLookupItem> userList = pul.selectAll(conn);
			int count = 1;
			for ( UserLookupItem item : userList ) {
				System.out.println(count + ". " + item.getLastName() + ", " + item.getFirstName());
				count++;
			}
			
			conn.rollback();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}
}
