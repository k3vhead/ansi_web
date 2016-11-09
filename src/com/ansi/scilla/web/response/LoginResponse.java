package com.ansi.scilla.web.response;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.ApplicationWebObject;
import com.ansi.scilla.web.common.UserPermission;

public class LoginResponse extends ApplicationWebObject implements MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private User user;
	private List<UserPermission> userPermissionList;
	private List<Division> divisionList;
	
	public LoginResponse() {
		super();
	}

	public LoginResponse(Connection conn, User user) throws Exception {
		super();
		this.user = user;
		this.userPermissionList = UserPermission.getUserPermissions(conn, user.getPermissionGroupId());
		this.divisionList = AppUtils.makeDivisionList(conn, user.getUserId());
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
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
