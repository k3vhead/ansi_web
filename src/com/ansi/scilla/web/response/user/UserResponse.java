package com.ansi.scilla.web.response.user;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.queries.QuoteWriter;
import com.ansi.scilla.web.response.MessageResponse;

public class UserResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	/* This works for string values only. If you want to sort by userid, change the sort method */
	public static final String[] VALID_SORT_FIELDS = new String[] {"firstName","lastName","email"};

	private List<UserResponseItem> userList;

	public UserResponse() {
		super();
	}
	public UserResponse(Connection conn, UserListType listType) throws Exception {
		this();
		this.userList = new ArrayList<UserResponseItem>();

		if ( listType.equals(UserListType.LIST) ) {
			List<User> userList = User.cast(new User().selectAll(conn));
			for ( User user : userList ) {
				this.userList.add(new UserResponseItem(user));
			}
		} else if ( listType.equals(UserListType.MANAGER)) {
			List<QuoteWriter> quoteWriterList = QuoteWriter.selectAll(conn);
			for ( QuoteWriter quoteWriter : quoteWriterList ) {
				UserResponseItem item = new UserResponseItem();
				PropertyUtils.copyProperties(item, quoteWriter);
				this.userList.add(item);
			}
		} else {
			throw new Exception("Invalid user list type");
		}

		Collections.sort(this.userList, new Comparator<UserResponseItem>() {
			public int compare(UserResponseItem o1, UserResponseItem o2) {
				int ret = o1.getLastName().compareTo(o2.getLastName());
				if ( ret == 0 ) {
					ret = o1.getFirstName().compareTo(o2.getFirstName());
				}
				return ret;
			}
		});
	}
	public UserResponse(Connection conn, Integer userId) throws Exception {
		this();
		User user = new User();
		user.setUserId(userId);
		user.selectOne(conn);
		this.userList = new ArrayList<UserResponseItem>();
		UserResponseItem userResponseItem = new UserResponseItem(user);
		this.userList.add(userResponseItem);
	}
	
	public List<UserResponseItem> getUserList() {
		return userList;
	}

	public void setUserList(List<UserResponseItem> userList) {
		this.userList = userList;
	}

	
	public void sort(String sortField) throws Exception {
		String getterName = "get" + sortField.substring(0,1).toUpperCase() + sortField.substring(1);
		Method method = UserResponseItem.class.getMethod(getterName, (Class<?>[])null);
		Collections.sort(userList, new Comparator<UserResponseItem>() {
			public int compare(UserResponseItem o1, UserResponseItem o2) {
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
