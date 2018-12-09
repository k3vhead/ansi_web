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
	
	private Integer divisionId;
	private boolean active;
	
	public Integer getDivisionId() {
		return this.divisionId;
	}
	
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public WebMessages validate(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateId(conn, webMessages, "division_user", "division_id", "divisionId", divisionId, true);
		RequestValidator.validateBoolean(webMessages, "active", this.active, true);
		
		return webMessages;
	}
	
}
