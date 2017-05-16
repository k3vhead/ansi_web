package com.ansi.scilla.web.struts;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.User;

public class SessionUser extends ApplicationObject {

	private static final long serialVersionUID = 1L;
	
	private String address;
	private String email;
	private String firstName;
	private String lastName;
	private Integer permissionGroupId;
	private String phone;
	private Integer status;
	private Integer superUser;
	private String title;
	private Integer userId;
	
	public SessionUser() {
		super();
	}
	public SessionUser(User user) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this();
		PropertyUtils.copyProperties(this, user);
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Integer getPermissionGroupId() {
		return permissionGroupId;
	}
	public void setPermissionGroupId(Integer permissionGroupId) {
		this.permissionGroupId = permissionGroupId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getSuperUser() {
		return superUser;
	}
	public void setSuperUser(Integer superUser) {
		this.superUser = superUser;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	
}
