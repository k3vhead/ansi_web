package com.ansi.scilla.web.user.request;

import com.ansi.scilla.web.common.request.AbstractRequest;

public class AnsiUserRequest extends AbstractRequest {
	
	/**
	 * @author jwlewis
	 */
	private static final long serialVersionUID = 1L;
	
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zip;
	private String email;
	private String firstName;
	private String lastName;
	private String password;
	private Integer permissionGroupId;
	private String phone;
	private Integer status;
	private Integer superUser; //DON'T MESS WITH THIS!!!
	private String title;
	private Integer userId;
	
	
	public String getAddress1() {
		return address1;
	}
	
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	
	public String getAddress2() {
		return address2;
	}
	
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getZip() {
		return zip;
	}
	
	public void setZip(String zip) {
		this.zip = zip;
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
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
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
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}


}
