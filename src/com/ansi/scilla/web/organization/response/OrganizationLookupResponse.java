package com.ansi.scilla.web.organization.response;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.ansi.scilla.common.organization.Organization;
import com.ansi.scilla.common.organization.OrganizationType;
import com.ansi.scilla.web.common.response.MessageResponse;


public class OrganizationLookupResponse extends MessageResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<Organization> orgList;
	private List<Organization> componentList;
	public OrganizationLookupResponse() {
		super();
	}
	
	public OrganizationLookupResponse(Connection conn, OrganizationType type) throws SQLException {
		super();
		this.orgList = type.list(conn);
		this.componentList = type.children(conn);
	}
	
	public List<Organization> getOrgList() {
		return orgList;
	}
	public void setOrgList(List<Organization> orgList) {
		this.orgList = orgList;
	}
	public List<Organization> getComponentList() {
		return componentList;
	}
	public void setComponentList(List<Organization> componentList) {
		this.componentList = componentList;
	}

	
}
