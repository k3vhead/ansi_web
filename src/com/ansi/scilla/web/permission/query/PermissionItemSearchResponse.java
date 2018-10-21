package com.ansi.scilla.web.permission.query;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

import com.ansi.scilla.common.db.PermissionGroupLevel;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.queries.ReportQuery;
import com.ansi.scilla.web.common.utils.Permission;
import com.thewebthing.commons.db2.DBColumn;

public class PermissionItemSearchResponse extends ReportQuery {

	private static final long serialVersionUID = 1L;
	
	public static final String PERMISSION_GROUP_ID = PermissionGroupLevel.PERMISSION_GROUP_ID;
	public static final String PERMISSION_NAME  = PermissionGroupLevel.PERMISSION_NAME;
	public static final String PERMISSION_LEVEL = PermissionGroupLevel.PERMISSION_LEVEL;
	
	private Integer permissionGroupId;
	private String permissionName;
	private Integer permissionLevel;	
	private List<Permission> permissionList;
		
	public PermissionItemSearchResponse(ResultSet rs, ResultSetMetaData rsmd) throws Exception {
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
	@DBColumn(PERMISSION_NAME)
	public String getPermissionName() {
		return permissionName;
	}
	@DBColumn(PERMISSION_NAME)
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	@DBColumn(PERMISSION_LEVEL)
	public Integer getPermissionLevel() {
		return this.permissionLevel;
	}
	@DBColumn(PERMISSION_LEVEL)
	public void SetPermissionLevel(Integer permissionLevel) {
		this.permissionLevel =  permissionLevel;
	}

	public List<Permission> getPermissionList() {
		if(this.permissionList == null) {
			this.permissionList = Permission.valueOf(permissionName).makeParentList();
		}
		return this.permissionList;
	}
	
	public void setPermissionList(List<Permission> permissionList) {
		this.permissionList = permissionList;
	}	
}