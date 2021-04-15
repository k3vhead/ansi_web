package com.ansi.scilla.web.permission.common;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.db.PermissionGroupLevel;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;


public class PermissionUtils extends MessageResponse {

	
	/**
	 * @author jwlewis
	 */
	private static final long serialVersionUID = 1L;

	public static List<Permission> makeGroupList(Connection conn, Integer permissionGroupId) throws Exception{
		PermissionGroupLevel key = new PermissionGroupLevel();
		key.setPermissionGroupId(permissionGroupId);
		List<PermissionGroupLevel> groupPermissionList = PermissionGroupLevel.cast(key.selectSome(conn));
		List<Permission> permissionList = new ArrayList<Permission>();
		for ( PermissionGroupLevel group : groupPermissionList) {
			try {
				Permission p = Permission.valueOf(group.getPermissionName());
				permissionList.add(p);
			} catch (IllegalArgumentException e) {
				AppUtils.logException(e);				
			}
		}
		return permissionList;
	}

	
	
	
	
	
}
