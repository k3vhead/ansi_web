package com.ansi.scilla.web.test.organization;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import com.ansi.scilla.common.organization.Organization;
import com.ansi.scilla.common.organization.OrganizationType;
import com.ansi.scilla.common.utils.AppUtils;

public class TestOrgChildList {

	private void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			List<Organization> childList = OrganizationType.COMPANY.children(conn);
			for ( Organization org : childList ) {
//				System.out.println(org.getOrganizationId() + "\t" + org.getName() + "\t" + org.getParentName());
			}
//			CollectionUtils.filter(childList, new ParentPredicate(7));
		} finally {
			conn.close();
		}
	}
	
	public static void main(String[] args) {
		try {
			new TestOrgChildList().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ParentPredicate implements Predicate<Organization> {

		public Integer organizationId;
		
		public ParentPredicate(Integer organizationId) {
			super();
			this.organizationId = organizationId;
		}

		@Override
		public boolean evaluate(Organization org) {
			return org.getOrganizationId().equals(organizationId);
		}
		
	}
}
