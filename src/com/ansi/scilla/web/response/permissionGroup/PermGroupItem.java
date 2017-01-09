package com.ansi.scilla.web.response.permissionGroup;

import java.util.Collections;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;

public class PermGroupItem extends ApplicationObject implements Comparable<PermGroupItem> {
	private static final long serialVersionUID = 1L;
	private String description;
	private String name;
	private Integer permGroupId;
	private Integer status;
	private List<PermissionItem> permissionItemList;
	
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
	public Integer getPermGroupId() {
		return permGroupId;
	}
	public void setPermGroupId(Integer permGroupId) {
		this.permGroupId = permGroupId;
	}
	public List<PermissionItem> getPermissionItemList() {
		return permissionItemList;
	}
	public void setPermissionItemList(List<PermissionItem> permissionItemList) {
		this.permissionItemList = permissionItemList;
	}
	@Override
	public int compareTo(PermGroupItem o) {
		Collections.sort(this.permissionItemList);
		int ret = this.name.compareTo(o.getName());
		if ( ret == 0 ) {
			ret = this.status.compareTo(o.getStatus());
		}
		return 0;
	}
	
	
}
