package com.ansi.scilla.web.organization.request;

import java.sql.Connection;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.response.WebMessages;

public class OrganizationDetailRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Boolean status;
	private String name;
	
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void validate(Connection conn, WebMessages webMessages, Integer organizationId) {
		
	}
}
