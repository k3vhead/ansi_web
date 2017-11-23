package com.ansi.scilla.web.permission.response;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.db.PermissionGroup;
import com.ansi.scilla.web.common.response.MessageResponse;

public class PermissionGroupResponse extends MessageResponse implements Serializable {

	/**
	 * 
	 * @author jwlewis
	 */
	
	private static final long serialVersionUID = 1L;

	private PermGroupCountRecord permGroupCountRecord;
	
	public PermissionGroupResponse(){
		super();
	}
	
	public PermissionGroupResponse(PermGroupCountRecord permGroupItem) {
		super();
		this.permGroupCountRecord = permGroupItem;
	}
	
	
	public PermissionGroupResponse(Connection conn, PermissionGroup perm) throws IllegalAccessException, InvocationTargetException, SQLException, NoSuchMethodException {
		this();
		this.permGroupCountRecord = new PermGroupCountRecord();
		PropertyUtils.copyProperties(this.permGroupCountRecord, perm);
		Integer userCount = this.makeUserCount(conn, perm.getPermissionGroupId());
		this.permGroupCountRecord.setUserCount(userCount);
	}

	private Integer makeUserCount(Connection conn, Integer permissionGroupId) throws SQLException {
		String sql = "select count(*) as user_count from ansi_user where permission_group_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, permissionGroupId);
		ResultSet rs = ps.executeQuery();
		Integer userCount = 0;
		if(rs.next()){
			userCount=rs.getInt("user_count");
		}
		rs.close();
		return userCount;
	}
	
	
	public PermGroupCountRecord getPermGroupCountRecord() {
		return permGroupCountRecord;
	}

	public void setPermGroupCountRecord(PermGroupCountRecord permGroupCountRecord) {
		this.permGroupCountRecord = permGroupCountRecord;
	}
	
}
