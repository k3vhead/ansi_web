package com.ansi.scilla.web.permission.response;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.web.common.response.MessageResponse;

public class PermissionGroupItemResponse extends MessageResponse implements Serializable {
	/**
	 * 
	 * @author kwagner
	 * 
	 * A class extending MessageResponse that contains 
	 * just one permission item within a given permission group.
	 * [ one row from the permission_group_level table ]
	 * 
	 *  Members
	 *  =====================================
	 * 	permissionGroupItem
	 * 			permissionName;
	 *			permissionGroupId;
	 *			permissionLevel;
	 */
	
	private static final long serialVersionUID = 1L;

	// private PermGroupCountRecord permGroupCountRecord;
	private PermissionGroupItem permissionGroupItem;
	
	public PermissionGroupItemResponse(){
		super();
	}
	
	public PermissionGroupItemResponse(PermissionGroupItem permissionGroupItem) {
		super();
		this.permissionGroupItem = permissionGroupItem;
	}
	
	public PermissionGroupItemResponse(Connection conn, PermissionGroupItem permissionGroupItem) throws IllegalAccessException, InvocationTargetException, SQLException, NoSuchMethodException {
		this();
		this.permissionGroupItem = new PermissionGroupItem();
		PropertyUtils.copyProperties(this.permissionGroupItem, permissionGroupItem);
//		Integer userCount = this.makeUserCount(conn, permissionGroupItem.getPermissionGroupId());
//		this.permissionGroupItem.setUserCount(userCount);
	}

//	private Integer makeUserCount(Connection conn, Integer permissionGroupId) throws SQLException {
//		String sql = "select count(*) as user_count from ansi_user where permission_group_id = ?";
//		PreparedStatement ps = conn.prepareStatement(sql);
//		ps.setInt(1, permissionGroupId);
//		ResultSet rs = ps.executeQuery();
//		Integer userCount = 0;
//		if(rs.next()){
//			userCount=rs.getInt("user_count");
//		}
//		rs.close();
//		return userCount;
//	}
	
	
	public PermissionGroupItem getPermissionGroupItem() {
		return this.permissionGroupItem;
	}

	public void setPermGroupCountRecord(PermissionGroupItem permissionGroupItem) {
		this.permissionGroupItem = permissionGroupItem;
	}
}
