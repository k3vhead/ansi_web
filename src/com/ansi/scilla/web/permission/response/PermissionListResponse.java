package com.ansi.scilla.web.permission.response;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import com.ansi.scilla.common.db.PermissionGroupLevel;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.Permission;


public class PermissionListResponse extends MessageResponse {
	private static final long serialVersionUID = 1L;
	private List<List<PermissionDisplayItem>> permissionList;
	
	public PermissionListResponse() {
		super();
		makeMasterList(new ArrayList<Permission>());
	}

	public PermissionListResponse(Connection conn, Integer permissionGroupId ) throws Exception {
		super();
		List<Permission> permissionList = makeGroupList(conn, permissionGroupId);
		makeMasterList(permissionList);
	}
	
	public List<List<PermissionDisplayItem>> getPermissionList() {
		return permissionList;
	}

	public void setPermissionList(List<List<PermissionDisplayItem>> permissionList) {
		this.permissionList = permissionList;
	}

	

	
	private void makeMasterList(List<Permission> groupPermissionList) {
		permissionList = new ArrayList<List<PermissionDisplayItem>>();

		for ( Permission p : Permission.values() ) {
			if ( p.getParent() == null ) {
				List<PermissionDisplayItem> sublist = new ArrayList<PermissionDisplayItem>();
				sublist.add(new PermissionDisplayItem(p, false));
				makeSubList(p, sublist, groupPermissionList);
				permissionList.add(sublist);
			}
		}
	}

	private  void makeSubList(Permission p, List<PermissionDisplayItem> sublist, List<Permission> groupPermissionList) {
		for ( Permission permission : p.makeChildList() ) {
			sublist.add(new PermissionDisplayItem(permission, groupPermissionList.contains(permission)));
			makeSubList(permission, sublist, groupPermissionList);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private List<Permission> makeGroupList(Connection conn, Integer permissionGroupId) throws Exception{
		PermissionGroupLevel key = new PermissionGroupLevel();
		key.setPermissionGroupId(permissionGroupId);
		List<PermissionGroupLevel> groupPermissionList = PermissionGroupLevel.cast(key.selectSome(conn));
		List<Permission> permissionList = (List<Permission>) CollectionUtils.collect(groupPermissionList.iterator(), new Group2Permission());
		return permissionList;
	}

	
	public class Group2Permission implements Transformer {

		@Override
		public Object transform(Object arg0) {
			PermissionGroupLevel group = (PermissionGroupLevel)arg0;
			Permission p = Permission.valueOf(group.getPermissionName());
			return p;
		}
	}
	
	
//	public static void main(String[] args) {
//		Connection conn = null;		
//		try {
//			conn = AppUtils.getDevConn();
//			PermissionListResponse x = new PermissionListResponse(conn, 7);
//			for ( List<PermissionDisplayItem> itemList : x.getPermissionList() ) {
//				for ( PermissionDisplayItem item : itemList ) {
//					System.out.println(item.getPermissionName() + "\t" + item.getIncluded());
//				}
//				System.out.println("======================");
//			}
//		} catch ( Exception e) {
//			e.printStackTrace();
//		} finally {
//			AppUtils.closeQuiet(conn);
//		}
//		
//		
//	}
}
