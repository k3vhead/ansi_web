package com.ansi.scilla.web.response.permissionGroup;

import com.ansi.scilla.common.ApplicationObject;

public class PermGroupLevelItem extends ApplicationObject {
	private Integer permGroupId;
	private Integer permLevel;
	private String permName;
	
	public Integer getPermissionGroupId() {
		return permGroupId;
	}
	public void setPermissionGroupId(Integer permissionGroupId) {
		this.permGroupId = permissionGroupId;
	}
	public Integer getPermissionLevel() {
		return permLevel;
	}
	public void setPermissionLevel(Integer permissionLevel) {
		this.permLevel = permissionLevel;
	}
	public String getPermissionName() {
		return permName;
	}
	public void setPermissionName(String permissionName) {
		this.permName = permissionName;
	}
}
