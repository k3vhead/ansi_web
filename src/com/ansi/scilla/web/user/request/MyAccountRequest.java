package com.ansi.scilla.web.user.request;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.ansi.scilla.web.common.request.RequiredForUpdate;

public class MyAccountRequest extends AbstractRequest {
	
	
	private static final long serialVersionUID = 1L;
	
	public static final String LAST_NAME = "lastName";
	public static final String FIRST_NAME = "firstName";
	public static final String TITLE = "title";
	public static final String EMAIL = "email";
	public static final String PHONE = "phone";
	public static final String ADDRESS1 = "address1";
	public static final String ADDRESS2 = "address2";
	public static final String CITY = "city";
	public static final String STATE = "state";
	public static final String ZIP = "zip";
	public static final String PASSWORD = "password";
	public static final String NEW_PASSWORD = "newPassword";
	public static final String CONFIRM_PASSWORD = "confirmPassword";
	
	
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
	private String password;
	private String newPassword;
	private String confirmPassword;
	

	@RequiredForAdd
	@RequiredForUpdate
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	@RequiredForAdd
	@RequiredForUpdate
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
	@RequiredForAdd
	@RequiredForUpdate
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@RequiredForAdd
	@RequiredForUpdate
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
	@RequiredForAdd
	@RequiredForUpdate
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	
	


}
