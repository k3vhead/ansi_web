package com.ansi.scilla.web.test.reports;

import java.sql.Connection;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.report.response.SubscriptionResponse;

public class TestReportSubscription {

	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			SubscriptionResponse r = new SubscriptionResponse(conn);
			String json = AppUtils.object2json(r);
			System.out.println(json);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}
	public static void main(String[] args) {
		try {
			new TestReportSubscription().go();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
