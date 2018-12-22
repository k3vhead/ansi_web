package com.ansi.scilla.web.user.request;

import java.math.BigDecimal;
import java.sql.Connection;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class AnsiUserRequest extends AbstractRequest {
	
	/**
	 * @author jwlewis
	 */
	private static final long serialVersionUID = 1L;
	
	public static Integer USER_STATUS_IS_ACTIVE = new Integer(1);
	public static Integer USER_STATUS_IS_INACTIVE = new Integer(0);
	public static Integer USER_STATUS_IS_LOCKED = new Integer(-1);
	
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
	private String title;
	private Integer userId;
	private BigDecimal minimumHourlyPay;
	
	
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
	
	
	public BigDecimal getMinimumHourlyPay() {
		return minimumHourlyPay;
	}

	public void setMinimumHourlyPay(BigDecimal minimumHourlyPay) {
		this.minimumHourlyPay = minimumHourlyPay;
	}

	public WebMessages validateAdd(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateString(webMessages, "address1", this.address1, false);
		RequestValidator.validateString(webMessages, "address2", this.address2, false);
		RequestValidator.validateString(webMessages, "city", this.city, false);
		RequestValidator.validateString(webMessages, "state", this.state, false);
		RequestValidator.validateString(webMessages, "zip", this.zip, false);
		RequestValidator.validateEmail(webMessages, "email", this.email, true);
		RequestValidator.validateString(webMessages, "firstName", this.firstName, true);
		RequestValidator.validateString(webMessages, "lastName", this.lastName, true);
		RequestValidator.validateString(webMessages, "password", this.password, true);
		RequestValidator.validateId(conn, webMessages, "permission_group", "permission_group_id", "permissionGroupId", this.permissionGroupId, true);
		RequestValidator.validateString(webMessages, "phone", this.phone, false);
		RequestValidator.validateBoolean(webMessages, "status", this.status, false);
		RequestValidator.validateString(webMessages, "title", this.title, false);
		RequestValidator.validateUserStatus(webMessages, "status", this.status, true);
		RequestValidator.validateBigDecimal(webMessages, "minimumHourlyPay", this.minimumHourlyPay, false);
		
		return webMessages;
	}
	
	public WebMessages validateUpdate(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateString(webMessages, "address1", this.address1, false);
		RequestValidator.validateString(webMessages, "address2", this.address2, false);
		RequestValidator.validateString(webMessages, "city", this.city, false);
		RequestValidator.validateString(webMessages, "state", this.state, false);
		RequestValidator.validateString(webMessages, "zip", this.zip, false);
		RequestValidator.validateEmail(webMessages, "email", this.email, true);
		RequestValidator.validateString(webMessages, "firstName", this.firstName, true);
		RequestValidator.validateString(webMessages, "lastName", this.lastName, true);
		RequestValidator.validateString(webMessages, "password", this.password, false);
		RequestValidator.validateId(conn, webMessages, "permission_group", "permission_group_id", "permissionGroupId", this.permissionGroupId, true);
		RequestValidator.validateString(webMessages, "phone", this.phone, false);
		RequestValidator.validateString(webMessages, "title", this.title, false);
		RequestValidator.validateId(conn, webMessages, "ansi_user", "user_id", "userId", this.userId, true);
		RequestValidator.validateUserStatus(webMessages, "status", this.status, true);
		RequestValidator.validateBigDecimal(webMessages, "minimumHourlyPay", this.minimumHourlyPay, false);
		
		return webMessages;
	}

	public WebMessages validateDelete(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateId(conn, webMessages, "ansi_user", "user_id", "userId", this.userId, true);
		return webMessages;
	}
}
