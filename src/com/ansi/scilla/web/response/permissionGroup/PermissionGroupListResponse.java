package com.ansi.scilla.web.response.permissionGroup;

import java.util.List;

import com.ansi.scilla.web.response.MessageResponse;

public class PermissionGroupListResponse extends MessageResponse {
	private List<PermissionGroupRecord> permGroupRecordList;

	public List<PermissionGroupRecord> getPermGroupRecordList() {
		return permGroupRecordList;
	}
	public void setPermGroupRecordList(List<PermissionGroupRecord> permGroupRecordList) {
		this.permGroupRecordList = permGroupRecordList;
	}
}
