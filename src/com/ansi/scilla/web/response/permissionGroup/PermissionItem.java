package com.ansi.scilla.web.response.permissionGroup;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

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
	
	
	public PermissionItem() {
		super();
	}
	public PermissionItem(PermissionGroupLevel permissionGroupLevel) throws IllegalAccessException, InvocationTargetException {
		this();
		BeanUtils.copyProperties(this, permissionGroupLevel);
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
	@Override
	public int compareTo(PermissionItem o) {
		return this.permissionName.compareTo(o.getPermissionName());
	}
}
