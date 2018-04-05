package com.ansi.scilla.web.user.query;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.ansi.scilla.common.queries.ReportQuery;

public class UserLookupItem extends ReportQuery {

	/**
	 * 
	 */
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
	
	public UserLookupItem() {
		super();
	}
	
	public UserLookupItem(ResultSet rs, ResultSetMetaData rsmd) throws SQLException  {
		this();
		this.userId = rs.getInt(USER_ID);
		this.userStatus = rs.getInt(USER_STATUS);
		this.firstName = rs.getString(FIRST_NAME);
		this.lastName = rs.getString(LAST_NAME);
		this.title = rs.getString(TITLE);
		this.email = rs.getString(EMAIL);
		this.phone = rs.getString(PHONE);
		this.city = rs.getString(CITY);
		this.state = rs.getString(STATE);
		this.permissionGroupId = rs.getInt(PERMISSION_GROUP_ID);
		this.permissionGroupName = rs.getString(PERMISSON_GROUP_NAME);
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
