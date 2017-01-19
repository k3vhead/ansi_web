package com.ansi.scilla.web.response.permissionGroup;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.queries.PermissionGroupUserCount;

/**
 * permission group and the permissions assigned to that group
 * @author jwlew
 *
 */

public class PermGroupCountRecord extends ApplicationObject implements Comparable<PermGroupCountRecord> {
	private static final long serialVersionUID = 1L;
	private String description;
	private String name;
	private Integer status;
	private Integer userCount;
	private Integer permissionGroupId;
	
	public PermGroupCountRecord(PermissionGroupUserCount record) throws IllegalAccessException, InvocationTargetException {
		super();
		BeanUtils.copyProperties(this, record.getPermissionGroup());
		this.userCount = record.getUserCount();
	}
	
	public Integer getUserCount() {
		return userCount;
	}
	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getPermissionGroupId() {
		return permissionGroupId;
	}
	public void setPermissionGroupId(Integer permissionGroupId) {
		this.permissionGroupId = permissionGroupId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
	@Override
	public int compareTo(PermGroupCountRecord o) {
		
		int ret = this.name.compareTo(o.getName());
		if ( ret == 0 ) {
			ret = this.status.compareTo(o.getStatus());
		}
		return ret;
	}
	
	
}
