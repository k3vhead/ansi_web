package com.ansi.scilla.web.dataload;

import java.sql.Connection;

import com.ansi.scilla.common.db.PermissionGroupLevel;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;

public class SetPermissions {

	public static void main(String[] args) {
		System.out.println("Start");
		try {
			new SetPermissions().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("ENd");
	}

	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			for ( Permission p : Permission.values() ) {
				System.out.println(p);
				PermissionGroupLevel pgl = new PermissionGroupLevel();
				pgl.setAddedBy(5);
				pgl.setPermissionGroupId(4); 	//special override group
				pgl.setPermissionLevel(1);
				pgl.setPermissionName(p.toString());
				pgl.setUpdatedBy(5);
				pgl.insertWithKey(conn);
			}
			conn.commit();
		} catch ( Exception e) {
			conn.rollback();
			throw(e);
		}
	}
}
