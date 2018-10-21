package com.ansi.scilla.web.permission.response;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.code.response.CodeResponseItem;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.permission.query.PermissionItemLookupSearch;
import com.ansi.scilla.web.permission.query.PermissionItemSearchResponse;
import com.ansi.scilla.common.db.Code;
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
	
	private List<PermissionGroupItem> permissionGroupLevelList;
	
	
	// Permission is an ENUM class.. 
	private List<Permission> permissionList;

	public PermissionListResponse() {
		super();
		this.permissionList = new ArrayList<Permission>();
		for (Permission p : Permission.values()) {
			permissionList.add(p);
		}
	}
	
	public PermissionListResponse(Connection conn) throws Exception{
		Integer offset=0; 
		Integer rowCount=1000;

		this.permissionList = new ArrayList<Permission>();
		this.permissionGroupLevelList = new ArrayList<PermissionGroupItem>();
		
		PermissionItemLookupSearch lookup = new PermissionItemLookupSearch();
		List<PermissionItemSearchResponse> permissionItemSearchResponses = lookup.select(conn, offset, rowCount); 
		
		PermissionGroupItem permissionGroupItem;
		
		for(PermissionItemSearchResponse response:permissionItemSearchResponses) {
			// Add it's List of Permissions to the complete list of permissions.
			permissionGroupItem = new PermissionGroupItem();
			permissionGroupItem.setPermissionGroupId(response.getPermissionGroupId());
			permissionGroupItem.setPermissionLevel(response.getPermissionLevel());
			permissionGroupItem.setPermissionName(response.getPermissionName());
			
			// Add it's List of Permissions to the complete list of permissions.
			this.permissionGroupLevelList.add(permissionGroupItem);
			this.permissionList.addAll(response.getPermissionList());
		}	
		
		/*
		this.codeList = new ArrayList<CodeResponseItem>();
		for ( Code code : codeList ) {
			this.codeList.add(new CodeResponseItem(code));
		}
		Collections.sort(this.codeList);
	    */		
	}

	public PermissionListResponse(Connection conn, int permGroupId) throws Exception {
		Integer offset=0; 
		Integer rowCount=1000;
		
		//17this.permissionItemList = new ArrayList<PermissionItem>();
		this.permissionList = new ArrayList<Permission>();
		this.permissionGroupLevelList = new ArrayList<PermissionGroupItem>();
		
		// Instantiate PermissionItemLookupSearch, an Extended ReportQuery Object. 
		   // Pass it the groupID so that it can use it to build the SQL statements 
		PermissionItemLookupSearch lookup = new PermissionItemLookupSearch(permGroupId);

		// Execute the query and get the info from the database.. 
		   // PermissionItemSearchResponse is also an extended ReportQuery Class
		   // but it is only used to bind to records in the database pass back by the select() method.
				
		List<PermissionItemSearchResponse> permissionItemSearchResponses = lookup.select(conn, offset, rowCount); 
		
		PermissionGroupItem permissionGroupLevelItem;
		// For each record(PermissionItemSearchResponse) returned by the select.. 
		for(PermissionItemSearchResponse response:permissionItemSearchResponses) {
			permissionGroupLevelItem = new PermissionGroupItem();
			permissionGroupLevelItem.setPermissionGroupId(response.getPermissionGroupId());
			permissionGroupLevelItem.setPermissionLevel(response.getPermissionLevel());
			permissionGroupLevelItem.setPermissionName(response.getPermissionName());
			
			// Add it's List of Permissions to the complete list of permissions.
			this.permissionGroupLevelList.add(permissionGroupLevelItem);
			this.permissionList.addAll(response.getPermissionList());
		}	
	}
	
	
	/*
	public PermissionListResponse(Connection conn) throws Exception {
		Integer offset=0; 
		Integer rowCount=1000;
		
		//17this.permissionItemList = new ArrayList<PermissionItem>();
		this.permissionList = new ArrayList<Permission>();
		
		// Instantiate PermissionItemLookupSearch, an Extended ReportQuery Object. 
		   // Pass it the groupID so that it can use it to build the SQL statements 
		PermissionItemLookupSearch lookup = new PermissionItemLookupSearch();
		
	    // Execute the query and get the info from the database.. 
		   // PermissionItemSearchResponse is also an extended ReportQuery Class
		   // but it is only used to bind to records in the database pass back by the select() method.
		List<PermissionItemSearchResponse> permissionItemSearchResponses = lookup.select(conn, offset, rowCount); 
		
		// For each record(PermissionItemSearchResponse) returned by the select.. 
		for(PermissionItemSearchResponse response:permissionItemSearchResponses) {

			// Add it's List of Permissions to the complete list of permissions. 
			this.permissionList.addAll(response.getPermissionList());
		}	
	}
	*/

	public List<Permission> getPermissionList() {
		return permissionList;
	}
	
	public List<PermissionGroupItem>  getPermissionGroupLevelList(){
		return this.permissionGroupLevelList;
	}
}
