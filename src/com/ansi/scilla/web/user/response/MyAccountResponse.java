package com.ansi.scilla.web.user.response;

import java.sql.Connection;

import com.ansi.scilla.common.db.PermissionGroup;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.response.MessageResponse;

public class MyAccountResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private Integer userId;
	private String permissionGroup;
	private String lastName;
	private String firstName;
	private String title;
	private String email;
	private String phone;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zip;

	public MyAccountResponse() {
		super();
	}
	
	public MyAccountResponse(Connection conn, Integer userId) throws Exception {
		this();
		makeResponse(conn, userId);
		
	}

	private void makeResponse(Connection conn, Integer userId) throws Exception {
		User user = new User();
		user.setUserId(userId);
		user.selectOne(conn);
		
		PermissionGroup permissionGroup = new PermissionGroup();
		permissionGroup.setPermissionGroupId(user.getPermissionGroupId());
		permissionGroup.selectOne(conn);
		
		this.userId = user.getUserId();
		this.permissionGroup = permissionGroup.getName();
		this.lastName = user.getLastName();
		this.firstName = user.getFirstName();
		this.title = user.getTitle();
		this.email = user.getEmail();
		this.phone = user.getPhone();
		this.address1 = user.getAddress1();
		this.address2 = user.getAddress2();
		this.city = user.getCity();
		this.state = user.getState();
		this.zip = user.getZip();
		
	}

	

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getPermissionGroup() {
		return permissionGroup;
	}

	public void setPermissionGroup(String permissionGroup) {
		this.permissionGroup = permissionGroup;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

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

	
	
	

	
	
}
