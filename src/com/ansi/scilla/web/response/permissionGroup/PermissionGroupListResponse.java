package com.ansi.scilla.web.response.permissionGroup;

import java.util.List;

import com.ansi.scilla.web.response.MessageResponse;

public class PermissionGroupListResponse extends MessageResponse {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<PermGroupItem> permGroupItemList;
	
	public List<PermGroupItem> getPermGroupItemList() {
		return permGroupItemList;
	}
	public void setPermGroupItemList(List<PermGroupItem> permGroupItemList) {
		this.permGroupItemList = permGroupItemList;
	}

}
