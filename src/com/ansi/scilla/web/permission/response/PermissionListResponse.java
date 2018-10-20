package com.ansi.scilla.web.permission.response;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.permission.query.PermissionItemLookupSearch;
import com.ansi.scilla.web.permission.query.PermissionItemSearchResponse;
import com.ansi.scilla.common.db.PermissionGroupLevel;

//import com.ansi.scilla.web.test.TestServlet;

public class PermissionListResponse extends MessageResponse {
	/**
	 * 
	 * @author kwagner
	 * 
	 * 		A class extending MessageResponse that contains a 
	 * 		list of Permissions for a given group id.. 
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	protected final Logger logger = LogManager.getLogger(TestServlet.class);
	
	private List<Permission> permissionList;

	public PermissionListResponse() {
		super();
		this.permissionList = new ArrayList<Permission>();
		for (Permission p : Permission.values()) {
			permissionList.add(p);
		}
	}

	public PermissionListResponse(Connection conn, Integer permGroupId) throws Exception {
		Integer offset=0; 
		Integer rowCount=1000;
		
		//17this.permissionItemList = new ArrayList<PermissionItem>();
		this.permissionList = new ArrayList<Permission>();
		
		//Get the info from the database.. 
		PermissionItemLookupSearch lookup = new PermissionItemLookupSearch(permGroupId.toString()); 
		List<PermissionItemSearchResponse> permissionItemSearchResponses = lookup.select(conn, offset, rowCount); 
		
		for(PermissionItemSearchResponse response:permissionItemSearchResponses) {
			this.permissionList.addAll(response.getPermissionList());
		}	
	}

	public List<Permission> getPermissionList() {
		return permissionList;
	}	
}
