package com.ansi.scilla.web.test.permission;

import java.sql.Connection;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.report.common.SubscriptionUtils;

public class TestSubscriptionCure {

	
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			SubscriptionUtils.cureReportSubscriptions(conn, 2211);
			conn.rollback();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}
	public static void main(String[] args) {
		try {
			new TestSubscriptionCure().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
