package com.ansi.scilla.web.response.permissionGroup;

import com.ansi.scilla.common.ApplicationObject;

public class PermGroupItem extends ApplicationObject {
	private String description;
	private String name;
	private Integer permGroupId;
	private Integer status;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getPermissionGroupId() {
		return permGroupId;
	}
	public void setPermissionGroupId(Integer permissionGroupId) {
		this.permGroupId = permissionGroupId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
}
