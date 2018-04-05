package com.ansi.scilla.web.permission.query;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import com.ansi.scilla.common.queries.ReportQuery;
import com.thewebthing.commons.db2.DBColumn;

public class PermissionGroupSearchResult extends ReportQuery {

	private static final long serialVersionUID = 1L;
	
	public static final String PERMISSION_GROUP_ID = "permission_group_id";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String PERMISSION_GROUP_STATUS = "permission_group_status";
	public static final String USER_COUNT = "user_count";
	
	private Integer permissionGroupId;
	private String name;
	private String description;
	private Integer permissionGroupStatus;
	private Integer userCount;
	
	public PermissionGroupSearchResult(ResultSet rs, ResultSetMetaData rsmd) throws Exception {
		super();
		super.rs2Object(this, rsmd, rs);
	}

	@DBColumn(PERMISSION_GROUP_ID)
	public Integer getPermissionGroupId() {
		return permissionGroupId;
	}
	@DBColumn(PERMISSION_GROUP_ID)
	public void setPermissionGroupId(Integer permissionGroupId) {
		this.permissionGroupId = permissionGroupId;
	}
	@DBColumn(NAME)
	public String getName() {
		return name;
	}
	@DBColumn(NAME)
	public void setName(String name) {
		this.name = name;
	}
	@DBColumn(DESCRIPTION)
	public String getDescription() {
		return description;
	}
	@DBColumn(DESCRIPTION)
	public void setDescription(String description) {
		this.description = description;
	}
	@DBColumn(PERMISSION_GROUP_STATUS)
	public Integer getPermissionGroupStatus() {
		return permissionGroupStatus;
	}
	@DBColumn(PERMISSION_GROUP_STATUS)
	public void setPermissionGroupStatus(Integer permissionGroupStatus) {
		this.permissionGroupStatus = permissionGroupStatus;
	}
	@DBColumn(USER_COUNT)
	public Integer getUserCount() {
		return userCount;
	}
	@DBColumn(USER_COUNT)
	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}


	
}
