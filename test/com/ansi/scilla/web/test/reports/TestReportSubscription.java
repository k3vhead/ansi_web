package com.ansi.scilla.web.test.reports;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.UserPermission;
import com.ansi.scilla.web.report.response.SubscriptionResponse2;

public class TestReportSubscription {

	final int DCL = 5;
	final int Terrence = 74;
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			make(conn, DCL);
			System.out.println("******************");
			make(conn, Terrence);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}
	
	private void make(Connection conn, int userId) throws Exception {
		final Integer SPECIAL_OVERRIDE = 6;
		final Integer OPERATIONS_MANAGER = 2205;
		List<UserPermission> userPermissions = UserPermission.getUserPermissions(conn, OPERATIONS_MANAGER);
		SubscriptionResponse2 r = new SubscriptionResponse2(conn, userId, userPermissions);
//		String json = AppUtils.object2json(r);
//		System.out.println(json);		
		System.out.println(r);
	}
	
	public static void main(String[] args) {
		try {
			new TestReportSubscription().go();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
