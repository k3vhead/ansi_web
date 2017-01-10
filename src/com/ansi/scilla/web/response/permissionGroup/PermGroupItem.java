package com.ansi.scilla.web.response.permissionGroup;

import com.ansi.scilla.common.ApplicationObject;

/**
 * permission group and the permissions assigned to that group
 * @author jwlew
 *
 */

public class PermGroupItem extends ApplicationObject implements Comparable<PermGroupItem> {
	private static final long serialVersionUID = 1L;
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
	public Integer getPermGroupId() {
		return permGroupId;
	}
	public void setPermGroupId(Integer permGroupId) {
		this.permGroupId = permGroupId;
	}
	
	@Override
	public int compareTo(PermGroupItem o) {
		
		int ret = this.name.compareTo(o.getName());
		if ( ret == 0 ) {
			ret = this.status.compareTo(o.getStatus());
		}
		return 0;
	}
	
	
}
