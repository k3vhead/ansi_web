package com.ansi.scilla.web.response.permissionGroup;

import com.ansi.scilla.common.ApplicationObject;

public class PermissionItem extends ApplicationObject implements Comparable<PermissionItem> {

	private static final long serialVersionUID = 1L;
	private String permissionName;
	private Integer permissionLevel;
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	public Integer getPermissionLevel() {
		return permissionLevel;
	}
	public void setPermissionLevel(Integer permissionLevel) {
		this.permissionLevel = permissionLevel;
	}
	@Override
	public int compareTo(PermissionItem o) {
		return this.permissionName.compareTo(o.getPermissionName());
	}
}
