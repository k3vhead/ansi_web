package com.ansi.scilla.web.permission.request;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.ansi.scilla.web.common.request.RequiredForUpdate;

/**
 * Permission request and item request
 *  
 * @author kwagner
 *
 *
 * 	permission_group_level
 * 		permission_group_id
 * 		permission_name
 *		permission_level
 *
 */

public class PermissionGroupItemRequest extends AbstractRequest {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Integer permissionGroupId;
	private Integer level;
	
	@RequiredForAdd
	@RequiredForUpdate
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
	
	@RequiredForAdd
	@RequiredForUpdate
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

}
