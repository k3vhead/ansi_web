package com.ansi.scilla.web.response.login;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.UserPermission;
import com.ansi.scilla.web.response.MessageResponse;
import com.ansi.scilla.web.struts.SessionUser;

public class LoginResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private SessionUser user;
	private List<UserPermission> userPermissionList;
	private List<Division> divisionList;
	
	public LoginResponse() {
		super();
	}

	public LoginResponse(Connection conn, SessionUser user) throws Exception {
		super();
		this.user = user;
		this.userPermissionList = UserPermission.getUserPermissions(conn, user.getPermissionGroupId());
		this.divisionList = AppUtils.makeDivisionList(conn, user.getUserId());
	}

	public SessionUser getUser() {
		return user;
	}

	public void setUser(SessionUser user) {
		this.user = user;
	}

	public List<UserPermission> getUserPermissionList() {
		return userPermissionList;
	}

	public void setUserPermissionList(List<UserPermission> userPermissionList) {
		this.userPermissionList = userPermissionList;
	}

	public List<Division> getDivisionList() {
		return divisionList;
	}

	public void setDivisionList(List<Division> divisionList) {
		this.divisionList = divisionList;
	}
	
	
}
