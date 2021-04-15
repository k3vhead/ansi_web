package com.ansi.scilla.web.user.response;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.user.query.PermissionUserLookup;
import com.ansi.scilla.web.user.query.UserLookupItem;

public class UserResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	/* This works for string values only. If you want to sort by userid, change the sort method */
	public static final String[] VALID_SORT_FIELDS = new String[] {"firstName","lastName","email"};

	private List<UserLookupItem> userList;

	public UserResponse() {
		super();
	}
	public UserResponse(Connection conn, UserListType listType) throws Exception {
		this();
		this.userList = new ArrayList<UserLookupItem>();

		if ( listType.equals(UserListType.LIST) ) {
			List<User> userList = User.cast(new User().selectAll(conn));
			for ( User user : userList ) {
				this.userList.add(new UserLookupItem(user));
			}
			Collections.sort(this.userList, new Comparator<UserLookupItem>() {
				public int compare(UserLookupItem o1, UserLookupItem o2) {
					int ret = o1.getLastName().compareTo(o2.getLastName());
					if ( ret == 0 ) {
						ret = o1.getFirstName().compareTo(o2.getFirstName());
					}
					return ret;
				}
			});
		} else if ( listType.equals(UserListType.MANAGER)) {
			PermissionUserLookup lookup = new PermissionUserLookup(Permission.CAN_WRITE_QUOTE);
			lookup.setSortBy(UserLookupItem.FIRST_NAME);
			List<UserLookupItem> userList = lookup.selectAll(conn);
			this.userList = userList;
//			List<QuoteWriter> quoteWriterList = QuoteWriter.selectAll(conn);
//			for ( QuoteWriter quoteWriter : quoteWriterList ) {
//				UserResponseItem item = new UserResponseItem();
//				PropertyUtils.copyProperties(item, quoteWriter);
//				this.userList.add(item);
//			}
		} else {
			throw new Exception("Invalid user list type");
		}

		
	}
	public UserResponse(Connection conn, Integer userId) throws Exception {
		this();
		User user = new User();
		user.setUserId(userId);
		user.selectOne(conn);
		this.userList = new ArrayList<UserLookupItem>();
		UserLookupItem userResponseItem = new UserLookupItem(user);
		this.userList.add(userResponseItem);
	}
	
	public List<UserLookupItem> getUserList() {
		return userList;
	}

	public void setUserList(List<UserLookupItem> userList) {
		this.userList = userList;
	}

	
	public void sort(String sortField) throws Exception {
		String getterName = "get" + sortField.substring(0,1).toUpperCase() + sortField.substring(1);
		Method method = UserLookupItem.class.getMethod(getterName, (Class<?>[])null);
		Collections.sort(userList, new Comparator<UserLookupItem>() {
			public int compare(UserLookupItem o1, UserLookupItem o2) {
				try {
					String value1 = (String)method.invoke(o1, (Object[])null);
					String value2 = (String)method.invoke(o2, (Object[])null);
					int ret = value1.compareTo(value2);
					return ret;
				} catch ( Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}


	public enum UserListType {
		LIST,
		MANAGER;
	}
	
}
