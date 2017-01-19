package com.ansi.scilla.web.response.permissionGroup;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ansi.scilla.common.db.PermissionGroup;
import com.ansi.scilla.web.response.MessageResponse;

public class PermissionGroupListResponse extends MessageResponse {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<PermGroupItem> permGroupItemList;
	
	public PermissionGroupListResponse() {
		super();
	}
	
	
	public PermissionGroupListResponse(Connection conn) throws Exception {
		List<PermissionGroup> list = PermissionGroup.cast(new PermissionGroup().selectAll(conn));
		
		this.permGroupItemList = new ArrayList<PermGroupItem>();
		for ( PermissionGroup record : list ) {
			this.permGroupItemList.add(new PermGroupItem(record));
		}
		Collections.sort(this.permGroupItemList);
	}
	
	public PermissionGroupListResponse(Connection conn, Integer permGroupId) throws Exception {
		PermissionGroup permissionGroup = new PermissionGroup();
		permissionGroup.setPermissionGroupId(permGroupId);
		permissionGroup.selectOne(conn);
		PermGroupItem permGroupItem = new PermGroupItem(permissionGroup);
		this.permGroupItemList = Arrays.asList(new PermGroupItem[] {permGroupItem});
		
	}
	
	public List<PermGroupItem> getPermGroupItemList() {
		return permGroupItemList;
	}
	public void setPermGroupItemList(List<PermGroupItem> permGroupItemList) {
		this.permGroupItemList = permGroupItemList;
	}

}
