package com.ansi.scilla.web.response.permissionGroup;

import com.ansi.scilla.web.response.MessageResponse;

public class PermissionGroupResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private PermGroupCountRecord permGroupItem;

	public PermGroupCountRecord getPermGroupItem() {
		return permGroupItem;
	}

	public void setPermGroupItem(PermGroupCountRecord permGroupItem) {
		this.permGroupItem = permGroupItem;
	}
	
}
