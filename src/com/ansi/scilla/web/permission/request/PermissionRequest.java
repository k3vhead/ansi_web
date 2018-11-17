package com.ansi.scilla.web.permission.request;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.ansi.scilla.web.common.request.RequiredForUpdate;

public class PermissionRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String PERMISSION_NAME = "permissionName";
	public static final String PERMISSION_IS_ACTIVE = "permissionIsActive";
	
	private String permissionName;
	private boolean permissionIsActive;
	@RequiredForAdd
	@RequiredForUpdate
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	@RequiredForAdd
	@RequiredForUpdate
	public boolean isPermissionIsActive() {
		return permissionIsActive;
	}
	public void setPermissionIsActive(boolean permissionIsActive) {
		this.permissionIsActive = permissionIsActive;
	}
	
	
}
