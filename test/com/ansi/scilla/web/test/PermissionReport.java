package com.ansi.scilla.web.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.PermissionGroup;
import com.ansi.scilla.common.db.PermissionGroupLevel;
import com.ansi.scilla.web.common.utils.AppUtils;

public class PermissionReport {

	public void go() throws Exception {
		Connection conn = AppUtils.getDevConn();
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			Statement s = conn.createStatement();
			List<Stuff> permissionGroupList = makePermissionGroupList(s);
			for ( Stuff x : permissionGroupList ) {
				System.out.println(x.permissionGroupName + "\t" + x.permissionName);
			}
			conn.rollback();
		} catch ( Exception e) {
			AppUtils.rollbackQuiet(conn);
			throw e;
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	private List<Stuff> makePermissionGroupList(Statement s) throws Exception {
		List<Stuff> stuffList = new ArrayList<Stuff>();
		String sql = "select permission_group.permission_group_id, permission_group.name as permission_group_name, " + 
					"\n permission_group_level.permission_name, permission_group_level.permission_level " +
					"\n from permission_group " +
					"\n left outer join permission_group_level on permission_group_level.permission_group_id=permission_group.permission_group_id " +
					"\n order by permission_group_name";
		ResultSet rs = s.executeQuery(sql);
		while ( rs.next() ) {
			stuffList.add(new Stuff(rs));
		}
		rs.close();
		return stuffList;
	}

	public static void main(String[] args) {
		try {
			new PermissionReport().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public class Stuff extends ApplicationObject {
		private static final long serialVersionUID = 1L;

		public Integer permissionGroupId;
		public String permissionGroupName; 
		public String permissionName;
		public Integer permissionLevel;
		
		public Stuff(ResultSet rs) throws Exception {
			super();
			this.permissionGroupId = rs.getInt(PermissionGroup.PERMISSION_GROUP_ID);
			this.permissionGroupName = rs.getString("permission_group_name");
			this.permissionName = rs.getString(PermissionGroupLevel.PERMISSION_NAME);
			this.permissionLevel = rs.getInt(PermissionGroupLevel.PERMISSION_LEVEL);
		}
	}
}
