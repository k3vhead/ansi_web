package com.ansi.scilla.web.permission.response;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.utils.Permission;

public class PermissionTreeItem extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	private String permission;
	private String parent;
	private List<String> childList;
	public PermissionTreeItem(String permission, String parent, List<Permission> childList) {
		super();
		this.permission = permission;
		this.parent = parent;
		this.childList = makeChildList(childList);
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public List<String> getChildList() {
		return childList;
	}
	public void setChildList(List<String> childList) {
		this.childList = childList;
	}
	@SuppressWarnings("unchecked")
	private List<String> makeChildList(List<Permission> childList) {		
		return (List<String>) CollectionUtils.collect(childList, new PermissionTransformer());
	}
	
	public class PermissionTransformer implements Transformer {

		@Override
		public Object transform(Object arg0) {
			Permission p = (Permission)arg0;
			return p.name();
		}
		
	}
}
