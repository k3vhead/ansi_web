package com.ansi.scilla.web.permission.response;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.PermissionGroupLevel;

/**
 * 
 * @author kwagner
 *
 */

public class PermissionGroupItem extends ApplicationObject implements Comparable<PermissionGroupItem> {
	/*** 
	 *	A class to represent a list of Permission Group Items
	 *
	 * 		Members
	 * 		===================================
	 * 		permissionName
	 * 		permissionGroupId
	 * 		permissionLevel
	 */
	private static final long serialVersionUID = 1L;
	private String permissionName;
	private Integer permissionGroupId;
	private Integer permissionLevel;
		
	public PermissionGroupItem() {
		super();
	}
	public PermissionGroupItem(Integer permissionGroupId, String permissionGroupName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this();
		this.permissionGroupId = permissionGroupId;
		this.permissionName = permissionGroupName;
		
		//PropertyUtils.copyProperties(this, permissionGroupLevel);
	}
	
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	public Integer getPermissionLevel() {
		return permissionLevel;
	}
	public void setPermissionLevel(Integer permissionLevel) {
		this.permissionLevel = permissionLevel;
	}
	public Integer getPermissionGroupId() {
		return this.permissionGroupId;
	}
	public void setPermissionGroupId(Integer permissionGroupId) {
		this.permissionGroupId = permissionGroupId;
	}
	@Override
	public int compareTo(PermissionGroupItem o) {
		Integer result=0;
		
		result = this.permissionName.compareTo(o.getPermissionName());
		result = result + this.permissionLevel.compareTo(o.getPermissionLevel());
		
		return result;
	}
}
