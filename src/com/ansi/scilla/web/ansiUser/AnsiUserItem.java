package com.ansi.scilla.web.ansiUser;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.PermissionGroupLevel;
import com.ansi.scilla.web.permission.response.PermissionItem;

public class AnsiUserItem extends ApplicationObject {
	
	private static final long serialVersionUID = 1L;
	private String userName;
	//private Integer permissionLevel;
	
	
	public AnsiUserItem() {
		super();
	}
	/*
	public PermissionItem(PermissionGroupLevel permissionGroupLevel) throws IllegalAccessException, InvocationTargetException {
		this();
		PropertyUtils.copyProperties(this, permissionGroupLevel);
	}
	*/
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/*
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
	*/
}
