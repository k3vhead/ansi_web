package com.ansi.scilla.web.response.permissionGroup;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ansi.scilla.common.queries.PermissionGroupUserCount;
import com.ansi.scilla.web.response.MessageResponse;

public class PermissionGroupListResponse extends MessageResponse {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<PermGroupCountRecord> permGroupItemList;
	
	public PermissionGroupListResponse() {
		super();
	}
	
	
	public PermissionGroupListResponse(Connection conn) throws Exception {
		List<PermissionGroupUserCount> list = PermissionGroupUserCount.select(conn);
		this.permGroupItemList = new ArrayList<PermGroupCountRecord>();
		for ( PermissionGroupUserCount record : list ) {
			this.permGroupItemList.add(new PermGroupCountRecord(record));
		}
		Collections.sort(this.permGroupItemList);
	}
	
	public PermissionGroupListResponse(Connection conn, Integer permGroupId) throws Exception {
		PermissionGroupUserCount record = PermissionGroupUserCount.select(conn, permGroupId);
		this.permGroupItemList = new ArrayList<PermGroupCountRecord>();
		this.permGroupItemList.add(new PermGroupCountRecord(record));
	}
	
	public List<PermGroupCountRecord> getPermGroupItemList() {
		return permGroupItemList;
	}
	public void setPermGroupItemList(List<PermGroupCountRecord> permGroupItemList) {
		this.permGroupItemList = permGroupItemList;
	}

}
