package com.ansi.scilla.web.request;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.thewebthing.commons.lang.JsonException;
import com.thewebthing.commons.lang.JsonUtils;


public class LoginRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private String userid;
	private String password;
	public LoginRequest() {
		super();
	}
	public LoginRequest(String userid, String password) {
		this();
		this.userid = userid;
		this.password = password;
	}
	public LoginRequest(String jsonString) throws JsonException, IllegalAccessException, InvocationTargetException {
		this();
		LoginRequest req = (LoginRequest) JsonUtils.JSON2Object(jsonString, LoginRequest.class);
		BeanUtils.copyProperties(this, req);
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
