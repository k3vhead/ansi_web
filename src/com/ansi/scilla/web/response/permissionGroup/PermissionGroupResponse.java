package com.ansi.scilla.web.response.permissionGroup;

import com.ansi.scilla.web.response.MessageResponse;

public class PermissionGroupResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private PermGroupItem permGroupItem;

	public PermGroupItem getPermGroupItem() {
		return permGroupItem;
	}

	public void setPermGroupItem(PermGroupItem permGroupItem) {
		this.permGroupItem = permGroupItem;
	}
	
}
