package com.ansi.scilla.web.common.struts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.utils.ApplicationWebObject;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.utils.UserPermission;
import com.ansi.scilla.web.login.response.LoginResponse;

public class SessionData extends ApplicationWebObject {

	private static final long serialVersionUID = 1L;
	
	public static final String KEY = "com_ansi_scilla_session_data";

	private List<UserPermission> userPermissionList;
	private SessionUser user;
	private List<SessionDivision> divisionList;
	
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
		this.divisionList = new ArrayList<SessionDivision>();
		for ( Division division : loginResponse.getDivisionList()) {
			this.divisionList.add(new SessionDivision(division));
		}
		Collections.sort(this.divisionList);
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
	public List<SessionDivision> getDivisionList() {
		return divisionList;
	}
	public void setDivisionList(List<SessionDivision> divisionList) {
		this.divisionList = divisionList;
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
	
	public boolean hasPermission(Permission permissionName) {
		boolean foundIt = false;
		
		for ( UserPermission userPermission : this.userPermissionList ) {
			if ( userPermission.getPermissionName().equalsIgnoreCase(permissionName.toString())) {
				foundIt = true;
			}
		}
		
		return foundIt;
	}
}
