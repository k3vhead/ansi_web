package com.ansi.scilla.web.request;

/**
 * Permission request and item request 
 * @author jwlewis
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

	@RequiredForAdd
	@RequiredForUpdate
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@RequiredForUpdate
	public Integer getPermissionGroupId() {
		return permissionGroupId;
	}

	public void setPermissionGroupId(Integer permissionGroupId) {
		this.permissionGroupId = permissionGroupId;
	}
	
	@RequiredForAdd
	@RequiredForUpdate
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
