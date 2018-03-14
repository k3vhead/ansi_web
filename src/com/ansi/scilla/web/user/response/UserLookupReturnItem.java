package com.ansi.scilla.web.user.response;

import com.ansi.scilla.common.queries.ReportQuery;
import com.ansi.scilla.common.queries.UserLookupItem;

public class UserLookupReturnItem extends ReportQuery {
	private static final long serialVersionUID = 1L;

	public static final String USER_ID = "user_id";
	public static final String USER_STATUS = "user_status";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String TITLE = "title";
	public static final String EMAIL = "email";
	public static final String PHONE = "phone";
	public static final String CITY = "city";
	public static final String STATE = "state";
	public static final String PERMISSION_GROUP_ID = "permission_group_id";
	public static final String PERMISSON_GROUP_NAME = "permission_group_name";
	
	private Integer userId;
	private Integer userStatus;
	private String firstName;
	private String lastName;
	private String title;
	private String email;
	private String phone;
	private String city;
	private String state;
	private Integer permissionGroupId;
	private String permissionGroupName;

	public UserLookupReturnItem()  {
		super();
	}
	

	public UserLookupReturnItem(UserLookupItem item) throws Exception {
		this();
		this.userId = item.getUserId();
		this.userStatus = item.getUserStatus();
		this.firstName = item.getFirstName();
		this.lastName = item.getLastName();
		this.title = item.getTitle();
		this.email = item.getEmail();
		this.phone = item.getPhone();
		this.city = item.getCity();
		this.state = item.getState();
		this.permissionGroupId = item.getPermissionGroupId();
		this.permissionGroupName = item.getPermissionGroupName();

	}


	public Integer getUserId() {
		return userId;
	}


	public void setUserId(Integer userId) {
		this.userId = userId;
	}


	public Integer getUserStatus() {
		return userStatus;
	}


	public void setUserStatus(Integer userStatus) {
		this.userStatus = userStatus;
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


	public Integer getPermissionGroupId() {
		return permissionGroupId;
	}


	public void setPermissionGroupId(Integer permissionGroupId) {
		this.permissionGroupId = permissionGroupId;
	}


	public String getPermissionGroupName() {
		return permissionGroupName;
	}


	public void setPermissionGroupName(String permissionGroupName) {
		this.permissionGroupName = permissionGroupName;
	}
	
	
	
}