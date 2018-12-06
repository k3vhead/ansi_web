package com.ansi.scilla.web.user.request;

import java.sql.Connection;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;


public class DivisionUserRequest extends AbstractRequest{
	/**
	 * @author jwlewis
	 */
	private static final long serialVersionUID = 1L;
	
	public static Integer USER_STATUS_IS_ACTIVE = new Integer(1);
	public static Integer USER_STATUS_IS_INACTIVE = new Integer(0);
	public static Integer USER_STATUS_IS_LOCKED = new Integer(-1);
	
	
}
