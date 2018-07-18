package com.ansi.scilla.web.permission.response;
import com.ansi.scilla.web.common.utils.Permission;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.PermissionGroupLevel;

/**
 * name and permission level response
 * @author jwlewis
 *
 */

public class PermissionItem extends ApplicationObject implements Comparable<PermissionItem> {

	private static final long serialVersionUID = 1L;
	private String permissionName;
	private Integer permissionLevel;
	private List<Permission> permissionList;
	
	
	public PermissionItem() {
		super();
	}
	public PermissionItem(PermissionGroupLevel permissionGroupLevel) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this();
		PropertyUtils.copyProperties(this, permissionGroupLevel);
	}
	
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
		this.permissionList =  Permission.valueOf(permissionName).makeParentList();
	}
	public Integer getPermissionLevel() {
		return permissionLevel;
	}
	public void setPermissionLevel(Integer permissionLevel) {
		this.permissionLevel = permissionLevel;
	}
	
	public List<Permission> getPermissionList() {
		return permissionList;
	}
	public void setPermissionList(List<Permission> permissionList) {
		this.permissionList = permissionList;
	}
	
	
	@Override
	public int compareTo(PermissionItem o) {
		return this.permissionName.compareTo(o.getPermissionName());
	}
}
