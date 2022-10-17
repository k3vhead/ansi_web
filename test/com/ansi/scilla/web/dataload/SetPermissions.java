package com.ansi.scilla.web.dataload;

import java.sql.Connection;

import com.ansi.scilla.common.db.PermissionGroupLevel;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;

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
			
//			PermissionGroup pg = new PermissionGroup();
//			pg.setAddedBy(5);
//			pg.setDescription("Lee, Danielle, Gary");
//			pg.setName("Special Override");
//			pg.setStatus(PermissionGroup.STATUS_IS_ACTIVE);
//			pg.setUpdatedBy(5);
//			Integer pgId = pg.insertWithKey(conn);
//			System.out.println("ID: " + pgId);
			
			Integer pgId = 7;
			
//			PermissionLevel pl = new PermissionLevel();
//			pl.setAddedBy(5);
//			pl.setLevel(PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
//			pl.setPermissionName(Permission.TICKET_SPECIAL_OVERRIDE.toString());
//			pl.setUpdatedBy(5);
//			pl.insertWithNoKey(conn);
			
			for ( Permission p : Permission.values() ) {
				if ( p != Permission.TICKET_SPECIAL_OVERRIDE ) {
					System.out.println(p);
					PermissionGroupLevel pgl = new PermissionGroupLevel();
					pgl.setAddedBy(5);
					pgl.setPermissionGroupId(pgId); 	//special override group
					pgl.setPermissionLevel(0);
					pgl.setPermissionName(p.toString());
					pgl.setUpdatedBy(5);
					pgl.insertWithKey(conn);
				}
			}
			conn.commit();
		} catch ( Exception e) {
			if ( conn != null ) {
				conn.rollback();
			}
			throw(e);
		}
	}
}
