package com.ansi.scilla.web.test.claims;

import java.sql.Connection;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.claims.response.ClaimEntryResponse;

public class TestClaimEntryResponse {

	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			ClaimEntryResponse x = new ClaimEntryResponse(conn, 766552,5);
			System.out.println(x);
		} finally {
			conn.close();
		}
	}
	public static void main(String[] args) {
		try {
			new TestClaimEntryResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
