package com.ansi.scilla.web.organization.response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import com.ansi.scilla.common.organization.Organization;
import com.ansi.scilla.common.organization.OrganizationType;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class OrganizationDetailResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private Organization organization;
	private List<Organization> childList;
		
	public OrganizationDetailResponse() {
		super();
		
	}
	
	/**
	 * 
	 * @param conn
	 * @param type
	 * @param organizationId
	 * @param filter   Indicates whether childList should contain all potential children (false) or just the children of this organization (true)
	 * @throws RecordNotFoundException
	 * @throws Exception
	 */
	public OrganizationDetailResponse(Connection conn, OrganizationType type, Integer organizationId, Boolean filter) throws RecordNotFoundException, Exception {
		this();
		makeOrganization(conn, type, organizationId);
		makeComponents(conn, type, organizationId);
		if ( filter ) {
			ChildPredicate predicate = new ChildPredicate(organizationId);
			CollectionUtils.filter(this.childList, predicate);
		}
	}
	
	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	

	public List<Organization> getChildList() {
		return childList;
	}

	public void setChildList(List<Organization> childList) {
		this.childList = childList;
	}

	private void makeOrganization(Connection conn, OrganizationType type, Integer organizationId) throws RecordNotFoundException, Exception {
		this.organization = type.getOrganization(conn, organizationId);		
	}

	private void makeComponents(Connection conn, OrganizationType type, Integer organizationId) throws SQLException {
		this.childList = type.children(conn);
	}
	
	
	private class ChildPredicate implements Predicate<Organization> {

		private Integer organizationId;
		
		public ChildPredicate(Integer organizationId) {
			super();
			this.organizationId = organizationId;
		}

		@Override
		public boolean evaluate(Organization org) {
			return org.getParentId().equals(organizationId);
		}
		
	}
	
}
