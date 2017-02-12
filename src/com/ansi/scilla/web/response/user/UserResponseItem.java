package com.ansi.scilla.web.response.user;

import java.lang.reflect.InvocationTargetException;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.User;
import com.thewebthing.commons.lang.BeanUtils;

public class UserResponseItem extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	private String email;
	private String firstName;
	private String lastName;
	private Integer userId;
	public UserResponseItem() {
		super();
	}

	public UserResponseItem(User user) throws IllegalAccessException, InvocationTargetException {
		this();
		BeanUtils.copyProperties(this, user);
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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	
}
