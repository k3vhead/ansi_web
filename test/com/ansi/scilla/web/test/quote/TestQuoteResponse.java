package com.ansi.scilla.web.test.quote;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.UserPermission;
import com.ansi.scilla.web.quote.response.QuoteListResponse;

public class TestQuoteResponse {

	public static void main(String[] args) {
		try {
			new TestQuoteResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			List<UserPermission> permissionList = Arrays.asList(new UserPermission[] {new UserPermission("QUOTE_UPDATE",1)});
			QuoteListResponse qlr = new QuoteListResponse(conn, "18876", permissionList);
		} finally {
			if ( conn != null ) {
				conn.close();
			}
		}
	}
}
