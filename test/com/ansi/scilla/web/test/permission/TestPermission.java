package com.ansi.scilla.web.test.permission;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.common.utils.QMarkTransformer;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.UserPermission;

public class TestPermission {

	public static void main(String[] args) {
		try {
//			new TestPermission().go();
//			new TestPermission().testPermissionTree();
			new TestPermission().testTransformer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void testTransformer() {
		List<Permission> plist = Permission.QUOTE_PROPOSE.makeFunctionalAreaTree();
//		QMarkTransformer x = new QMarkTransformer();
//		List<String> qmarks = (List<String>)CollectionUtils.collect(plist, x);
//		String whereClause = "(" + StringUtils.join(qmarks, ",") + ")";
		String whereClause = AppUtils.makeBindVariables(plist);
		System.out.println(whereClause);
	}
	
	public String x(List<? extends Object> plist) {
		return "123";
	}

	private void testPermissionTree() {
		List<Permission> childList = Permission.QUOTE_PROPOSE.makeChildList();
		List<Permission> childTree = Permission.QUOTE_PROPOSE.makeChildTree();
		List<Permission> parentList = Permission.QUOTE_PROPOSE.makeParentList();
		List<Permission> faList = Permission.QUOTE_PROPOSE.makeFunctionalAreaTree();
		
		
		System.out.println("**childlist");
		for ( Permission p : childList ) { System.out.println(p); }
		System.out.println("\n\n**childTree");
		for ( Permission p : childTree ) { System.out.println(p); }
		System.out.println("\n\n**parentList");
		for ( Permission p : parentList ) { System.out.println(p); }
		System.out.println("\n\n**faList");
		for ( Permission p : faList ) { System.out.println(p); }
		
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
