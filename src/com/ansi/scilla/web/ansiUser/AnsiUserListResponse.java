package com.ansi.scilla.web.ansiUser;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ansi.scilla.common.queries.PermissionGroupUserCount;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.permission.response.PermGroupCountRecord;

public class AnsiUserListResponse extends MessageResponse {
	
private static final long serialVersionUID = 1L;
	
	private List<PermGroupCountRecord> permGroupItemList;
	
	
	public AnsiUserListResponse() {
		super();
	}
	
	
	public AnsiUserListResponse(Connection conn) throws Exception {
		List<PermissionGroupUserCount> list = PermissionGroupUserCount.select(conn);
		this.permGroupItemList = new ArrayList<PermGroupCountRecord>();
		for ( PermissionGroupUserCount record : list ) {
			this.permGroupItemList.add(new PermGroupCountRecord(record));
		}
		Collections.sort(this.permGroupItemList);
	}
	
	
	public AnsiUserListResponse(Connection conn, Integer permGroupId) throws Exception {
		PermissionGroupUserCount record = PermissionGroupUserCount.select(conn, permGroupId);
		this.permGroupItemList = new ArrayList<PermGroupCountRecord>();
		this.permGroupItemList.add(new PermGroupCountRecord(record));
	}
	
	
	/*
	public List<PermGroupCountRecord> getPermGroupItemList() {
		return permGroupItemList;
	}
	public void setPermGroupItemList(List<PermGroupCountRecord> permGroupItemList) {
		this.permGroupItemList = permGroupItemList;
	}
	*/
}
