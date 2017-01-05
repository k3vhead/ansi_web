package com.ansi.scilla.web.response.permissionGroup;

import java.util.List;

import com.ansi.scilla.common.ApplicationObject;

public class PermissionGroupRecord extends ApplicationObject {
	private PermGroupItem permGroupItem;
	private List<PermGroupLevelItem> permGroupLevelItemList;
	
	public PermGroupItem getPermGroupItem() {
		return permGroupItem;
	}
	public void setPermGroupItem(PermGroupItem permGroupItem) {
		this.permGroupItem = permGroupItem;
	}
	public List<PermGroupLevelItem> getPermGroupLevelItemList() {
		return permGroupLevelItemList;
	}
	public void setPermGroupLevelItemList(List<PermGroupLevelItem> permGroupLevelItemList) {
		this.permGroupLevelItemList = permGroupLevelItemList;
	}
}
