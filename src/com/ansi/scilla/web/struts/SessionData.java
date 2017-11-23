package com.ansi.scilla.web.struts;

import java.util.List;

import com.ansi.scilla.web.common.ApplicationWebObject;
import com.ansi.scilla.web.common.UserPermission;
import com.ansi.scilla.web.login.response.LoginResponse;

public class SessionData extends ApplicationWebObject {

	private static final long serialVersionUID = 1L;
	
	public static final String KEY = "com_ansi_scilla_session_data";

	private List<UserPermission> userPermissionList;
	private SessionUser user;
	public SessionData() {
		super();
	}
	public SessionData(List<UserPermission> permissionList, SessionUser user) {
		this();
		this.userPermissionList = permissionList;
		this.user = user;
	}
	public SessionData(LoginResponse loginResponse) throws Exception {
		this();
		this.user = loginResponse.getUser();
		this.userPermissionList = loginResponse.getUserPermissionList();
	}
	
	public List<UserPermission> getUserPermissionList() {
		return userPermissionList;
	}
	public void setUserPermissionList(List<UserPermission> userPermissionList) {
		this.userPermissionList = userPermissionList;
	}
	public SessionUser getUser() {
		return user;
	}
	public void setUser(SessionUser user) {
		this.user = user;
	}

	public boolean hasPermission(String permissionName) {
		boolean foundIt = false;
		
		for ( UserPermission userPermission : this.userPermissionList ) {
			if ( userPermission.getPermissionName().equalsIgnoreCase(permissionName)) {
				foundIt = true;
			}
		}
		
		return foundIt;
	}
	
	
}
