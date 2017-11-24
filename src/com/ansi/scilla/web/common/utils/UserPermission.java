package com.ansi.scilla.web.common.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.db.PermissionLevel;

public class UserPermission extends ApplicationWebObject {

	private static final long serialVersionUID = 1L;

	private String permissionName;
	private Integer level;
	
	
	public UserPermission() {
		super();
	}


	public UserPermission(String permissionName, Integer level) {
		this();
		this.permissionName = permissionName;
		this.level = level;
	}
	
	public UserPermission(PermissionLevel permissionLevel) {
		this(permissionLevel.toString(), permissionLevel.getLevel());
	}


	public UserPermission(ResultSetMetaData rsmd, ResultSet rs) throws SQLException {
		for ( int i = 0; i < rsmd.getColumnCount(); i++) {
			int idx = i + 1;
			if ( rsmd.getColumnName(idx).equalsIgnoreCase("permission_name")) {
				this.permissionName = rs.getString(idx);
			}
			if ( rsmd.getColumnName(idx).equalsIgnoreCase("permission_level")) {
				this.level = rs.getInt(idx);
			}
		}
	}


	public String getPermissionName() {
		return permissionName;
	}


	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}


	public Integer getLevel() {
		return level;
	}


	public void setLevel(Integer level) {
		this.level = level;
	}
	
	/**
	 * Get a list of permissions for a permission group
	 * @param conn
	 * @param userId
	 * @return
	 */
	public static List<UserPermission> getUserPermissions(Connection conn, Integer permissionGroupId) throws Exception {
		List<UserPermission> userPermissionList = new ArrayList<UserPermission>();
		String sql = "select permission_group_level.permission_name, permission_group_level.permission_level" +
					" from permission_group " +
					" inner join permission_group_level on permission_group_level.permission_group_id=permission_group.permission_group_id " + 
					" where permission_group.permission_group_id=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1,  permissionGroupId);
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		while ( rs.next() ) {
			userPermissionList.add( new UserPermission(rsmd, rs) );			
		}
		rs.close();
		return userPermissionList;
		
	}
	
	
}
