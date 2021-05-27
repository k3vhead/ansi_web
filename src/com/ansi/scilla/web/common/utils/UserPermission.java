package com.ansi.scilla.web.common.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.PermissionGroupLevel;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.utils.Permission;

public class UserPermission extends ApplicationWebObject implements Comparable<UserPermission> {

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
	
	@Override
	public int compareTo(UserPermission o) {
		int ret = this.getPermissionName().compareTo(o.getPermissionName());
		if ( ret == 0 ) {
			ret = this.getLevel().compareTo(o.getLevel());
		}
		return ret;
	}

	@Override
	public boolean equals(Object o) {
		boolean ret = true;
		if ( o instanceof UserPermission) {
			UserPermission up = (UserPermission)o;
			if ( ! this.getPermissionName().equals(up.getPermissionName())) {
				ret = false;
			}
			if ( ! this.getLevel().equals(up.getLevel())) {
				ret = false;
			}
		} else {
			ret = false;
		}
		return ret;
	}

	/**
	 * Get a list of permissions for a permission group
	 * @param conn
	 * @param permissionGroupId
	 * @return
	 */
	public static List<UserPermission> getUserPermissions(Connection conn, Integer permissionGroupId) throws Exception {
		Logger logger = LogManager.getLogger(UserPermission.class);
		List<UserPermission> userPermissionList = new ArrayList<UserPermission>();
//		String sql = "select permission_group_level.permission_name, permission_group_level.permission_level" +
//					" from permission_group " +
//					" inner join permission_group_level on permission_group_level.permission_group_id=permission_group.permission_group_id " + 
//					" where permission_group.permission_group_id=?";
//		PreparedStatement ps = conn.prepareStatement(sql);
//		ps.setInt(1,  permissionGroupId);
//		ResultSet rs = ps.executeQuery();
//		ResultSetMetaData rsmd = rs.getMetaData();
//		while ( rs.next() ) {
//			userPermissionList.add( new UserPermission(rsmd, rs) );			
//		}
//		rs.close();
		PermissionGroupLevel key = new PermissionGroupLevel();
		key.setPermissionGroupId(permissionGroupId);
		List<PermissionGroupLevel> pglList = PermissionGroupLevel.cast(key.selectSome(conn));
		List<String> tempList = new ArrayList<String>();
		List<String> badPermissionList = new ArrayList<String>();
		for ( PermissionGroupLevel pgl : pglList ) {
			try {
				Permission p = Permission.valueOf(pgl.getPermissionName());
				List<Permission> parentList = p.makeParentList();
				for ( Permission permission : parentList ) {
					tempList.add(permission.name());
				}
			} catch ( IllegalArgumentException e ) {
				// there is a permission in the database that we don't recognize
				badPermissionList.add(pgl.getPermissionName());
			}
		}
		for (String permissionName : tempList ) {
			UserPermission up = new UserPermission(permissionName, 0);
			if ( ! userPermissionList.contains(up)) {
				userPermissionList.add(up);
			}
		}
		if ( badPermissionList.size() > 0 ) {
			String badPermissions = StringUtils.join(", ", badPermissionList);
			logger.log(Level.FATAL, "Bad permissions in the database: " + badPermissions);
		}
		return userPermissionList;
		
	}
	
	
}
