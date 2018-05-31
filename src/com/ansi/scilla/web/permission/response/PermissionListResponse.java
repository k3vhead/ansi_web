package com.ansi.scilla.web.permission.response;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.ansi.scilla.common.queries.PermissionGroupUserCount;
import com.ansi.scilla.web.common.response.MessageResponse;

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
	
	private List<PermissionGroupItem> permissionGroupItemList;
	
	public PermissionListResponse() {
		super();
	}
		
	public PermissionListResponse(Connection conn, Integer permGroupId) throws Exception {
		/**
		 *  Dave - I think I need a database object here to populate this list
		 *  		...maybe
		 */        
		// If I had the db object to use.. I would instantiate a collection of permissions
		// using it.. it would look something like this..
		// kjw
		//
		//  --Get the list from the database.. 
		// 	List<PermissionGroupItem> list = new PermissionGroupItemList(int groupId);
			
		//	--Add the list to collection for this class 
		//	for ( PermissionGroupItem record : list ) {
		//		this.permissionGroupItemList.add(new PermissionGroupItem(record));
		//	}
		//	Collections.sort(this.permissionGroupItemList);
		
		
		// for now I'll put in stub.. 
		// kjw
			//initialize the class member holding the collection.. 
			this.permissionGroupItemList = new ArrayList<PermissionGroupItem>();		
			
			PermissionGroupItem temp = new PermissionGroupItem();
			temp.setPermissionGroupId(-444);
			temp.setPermissionLevel(1);
			temp.setPermissionName("Some Permission Group Name");
			
			this.permissionGroupItemList.add(temp);
	}
	
	public List<PermissionGroupItem> getPermGroupItemList() {
		return permissionGroupItemList;
	}
	public void setPermGroupItemList(List<PermissionGroupItem> permGroupItemList) {
		this.permissionGroupItemList = permGroupItemList;
	}

}
