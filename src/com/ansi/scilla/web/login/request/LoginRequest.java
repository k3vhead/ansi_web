package com.ansi.scilla.web.login.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;


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
	public LoginRequest(String jsonString) throws IllegalAccessException, InvocationTargetException, JsonParseException, JsonMappingException, IOException, NoSuchMethodException {
		this();
		LoginRequest req = (LoginRequest) AppUtils.json2object(jsonString, LoginRequest.class);
		PropertyUtils.copyProperties(this, req);
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
