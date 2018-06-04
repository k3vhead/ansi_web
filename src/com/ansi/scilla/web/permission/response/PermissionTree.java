package com.ansi.scilla.web.permission.response;

import java.util.HashMap;
import java.util.List;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.fasterxml.jackson.core.JsonProcessingException;

public class PermissionTree extends HashMap<Permission,PermissionTreeItem> {

	private static final long serialVersionUID = 1L;

	public PermissionTree() {
		super();
		for ( Permission p1 : Permission.values() ) {
			List<Permission> childList = p1.makeChildList();
			String parentName = p1.getParent()==null ? null : p1.getParent().name();
			this.put(p1, new PermissionTreeItem(p1.name(), parentName, childList));
		}
	}
	
	
	public static void main(String[] args) {
		try {
			String json = AppUtils.object2json(new PermissionTree());
			System.out.println(json);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
