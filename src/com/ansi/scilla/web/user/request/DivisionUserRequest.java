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
	
	public static Integer divisionId;
	public static boolean active;
	
	public static Integer getDivisionId() {
		return divisionId;
	}
	
	public static void setDivisionId(Integer divisionId) {
		DivisionUserRequest.divisionId = divisionId;
	}
	
	public static boolean isActive() {
		return active;
	}
	
	public static void setActive(boolean active) {
		DivisionUserRequest.active = active;
	}
	
	
}
