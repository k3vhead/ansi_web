package com.ansi.scilla.web.permission.response;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.utils.Permission;

public class PermissionDisplayItem extends ApplicationObject implements Comparable<PermissionDisplayItem> {

	private static final long serialVersionUID = 1L;

	private String permissionName;
	private String description;
	private Boolean included;
	
	
	public PermissionDisplayItem() {
		super();
	}
	
	public PermissionDisplayItem(Permission permission, Boolean included) {
		this();
		this.permissionName = permission.name();
		this.description = permission.getDescription();
		this.included = included;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIncluded() {
		return included;
	}

	public void setIncluded(Boolean included) {
		this.included = included;
	}

	@Override
	public int compareTo(PermissionDisplayItem o) {
		return this.getPermissionName().compareTo(o.getPermissionName());
	}
	
	
}
