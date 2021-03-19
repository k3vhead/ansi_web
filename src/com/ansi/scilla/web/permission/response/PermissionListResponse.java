package com.ansi.scilla.web.permission.response;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.permission.common.PermissionUtils;


public class PermissionListResponse extends MessageResponse {
	private static final long serialVersionUID = 1L;
	// key is top-level permission name. List is permissions that are subordinate to top-level
	// we are assuming only 2 levels.
	private HashMap<String, List<PermissionDisplayItem>> permissionList;
	private List<PermissionDisplayItem> functionalAreas;  // sorted list of functional area names (keys into the hashmap)
	
	public PermissionListResponse() {
		super();
		makeMasterList(new ArrayList<Permission>());
	}

	public PermissionListResponse(Connection conn, Integer permissionGroupId ) throws Exception {
		super();
		List<Permission> permissionList = makeGroupList(conn, permissionGroupId);
		makeMasterList(permissionList);
	}
	
	public HashMap<String, List<PermissionDisplayItem>> getPermissionList() {
		return permissionList;
	}

	public void setPermissionList(HashMap<String, List<PermissionDisplayItem>> permissionList) {
		this.permissionList = permissionList;
	}

	

	
	public List<PermissionDisplayItem> getFunctionalAreas() {
		return functionalAreas;
	}

	public void setFunctionalAreas(List<PermissionDisplayItem> functionalAreas) {
		this.functionalAreas = functionalAreas;
	}

	private void makeMasterList(List<Permission> groupPermissionList) {
		permissionList = new HashMap<String, List<PermissionDisplayItem>>();
		this.functionalAreas = new ArrayList<PermissionDisplayItem>();
		
		for ( Permission p : Permission.makeFunctionalAreaList() ) {
			PermissionDisplayItem functionalArea = new PermissionDisplayItem(p, false);
			functionalArea.setDescription(StringUtils.replace(functionalArea.getDescription(), "Functional Area:", ""));
			functionalArea.setDescription(StringUtils.replace(functionalArea.getDescription(), " ", "&nbsp;"));
			this.functionalAreas.add(functionalArea);
			List<PermissionDisplayItem> sublist = new ArrayList<PermissionDisplayItem>();
			makeSubList(p, sublist, groupPermissionList);
			permissionList.put(p.name(), sublist);
		}
		
		Collections.sort(this.functionalAreas);
	}

	private  void makeSubList(Permission p, List<PermissionDisplayItem> sublist, List<Permission> groupPermissionList) {
		for ( Permission permission : p.makeChildList() ) {
			sublist.add(new PermissionDisplayItem(permission, groupPermissionList.contains(permission)));
			makeSubList(permission, sublist, groupPermissionList);
		}
	}
	
	
	private List<Permission> makeGroupList(Connection conn, Integer permissionGroupId) throws Exception{
		
		List<Permission> permissionList = PermissionUtils.makeGroupList(conn, permissionGroupId);
		
		return permissionList;
	}

	
	
	
	public static void main(String[] args) {
		Connection conn = null;		
		try {
			conn = AppUtils.getDevConn();
			PermissionListResponse x = new PermissionListResponse(conn, 2209);
			String json = AppUtils.object2json(x);
			System.out.println(json);
		} catch ( Exception e) {
			e.printStackTrace();
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
		
	}
}
