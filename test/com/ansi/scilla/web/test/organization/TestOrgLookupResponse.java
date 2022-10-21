package com.ansi.scilla.web.test.organization;

import java.sql.Connection;

import com.ansi.scilla.common.organization.OrganizationType;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.organization.response.OrganizationLookupResponse;

public class TestOrgLookupResponse {

	public static void main(String[] args) {
		try {
			new TestOrgLookupResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			OrganizationLookupResponse response = new OrganizationLookupResponse(conn, OrganizationType.REGION);
			String json = AppUtils.object2json(response, true);
			System.out.println(json);
		} finally {
			conn.close();
		}
	}
}
