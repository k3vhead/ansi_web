package com.ansi.scilla.web.user.request;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.ansi.scilla.web.common.request.RequiredForUpdate;

public class MyAccountRequest extends AbstractRequest {
	
	
	private static final long serialVersionUID = 1L;
	
//	public static final String LAST_NAME = "lastName";
//	public static final String FIRST_NAME = "firstName";
//	public static final String TITLE = "title";
//	public static final String EMAIL = "email";
//	public static final String PHONE = "phone";
//	public static final String ADDRESS1 = "address1";
//	public static final String ADDRESS2 = "address2";
//	public static final String CITY = "city";
//	public static final String STATE = "state";
//	public static final String ZIP = "zip";
	public static final String PASSWORD = "password";
	public static final String NEW_PASSWORD = "newPassword";
	public static final String CONFIRM_PASSWORD = "confirmPassword";
	
	
//	private String lastName;
//	private String firstName;
//	private String title;
//	private String email;
//	private String phone;
//	private String address1;
//	private String address2;
//	private String city;
//	private String state;
//	private String zip;
	private String password;
	private String newPassword;
	private String confirmPassword;
	


	@RequiredForAdd
	@RequiredForUpdate
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@RequiredForAdd
	@RequiredForUpdate
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	@RequiredForAdd
	@RequiredForUpdate
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	
	


}
