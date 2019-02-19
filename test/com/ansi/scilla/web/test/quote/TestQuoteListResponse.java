package com.ansi.scilla.web.test.quote;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.UserPermission;
import com.ansi.scilla.web.quote.response.QuoteListResponse;

public class TestQuoteListResponse {

	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			String quoteId = "19599";
			List<UserPermission> permissionList = new ArrayList<UserPermission>();
			QuoteListResponse quoteListResponse = new QuoteListResponse(conn, quoteId, permissionList);
			System.out.println(quoteListResponse);

		} finally {
			conn.close();
		}
	}
	
	public static void main(String[] args) {
		try {
			new TestQuoteListResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
