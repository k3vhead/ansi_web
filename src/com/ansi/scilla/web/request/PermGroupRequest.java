package com.ansi.scilla.web.request;

import com.ansi.scilla.web.request.AbstractRequest;


/**
 * Permission request and item request 
 * @author jwlew
 *
 *
 */

public class PermGroupRequest extends AbstractRequest {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String description;
	private String name;
	private Integer permissionGroupId;
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
		return permissionGroupId;
	}

	public void setPermissionGroupId(Integer permissionGroupId) {
		this.permissionGroupId = permissionGroupId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
