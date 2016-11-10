package com.ansi.scilla.web.struts;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.web.common.ApplicationWebObject;
import com.ansi.scilla.web.common.UserPermission;
import com.ansi.scilla.web.response.LoginResponse;

public class SessionData extends ApplicationWebObject {

	private static final long serialVersionUID = 1L;
	
	public static final String KEY = "com_ansi_scilla_session_data";

	private List<UserPermission> permissionList;
	private SessionUser user;
	public SessionData() {
		super();
	}
	public SessionData(List<UserPermission> permissionList, SessionUser user) {
		this();
		this.permissionList = permissionList;
		this.user = user;
	}
	public SessionData(LoginResponse loginResponse) throws Exception {
		this();
		BeanUtils.copyProperties(this, loginResponse);
	}
	public List<UserPermission> getPermissionList() {
		return permissionList;
	}
	public void setPermissionList(List<UserPermission> permissionList) {
		this.permissionList = permissionList;
	}
	public SessionUser getUser() {
		return user;
	}
	public void setUser(SessionUser user) {
		this.user = user;
	}

	
	
	
}
