package com.ansi.scilla.web.user.response;

import java.util.List;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.user.query.UserLookupItem;

public class UserListResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private List<UserLookupItem> userList;

	public UserListResponse(List<UserLookupItem> userList) {
		super();
		this.userList = userList;
	}

	public List<UserLookupItem> getUserList() {
		return userList;
	}

	public void setUserList(List<UserLookupItem> userList) {
		this.userList = userList;
	}
	
	
}
