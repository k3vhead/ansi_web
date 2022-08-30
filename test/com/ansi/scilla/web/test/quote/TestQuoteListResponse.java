package com.ansi.scilla.web.test.quote;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.UserPermission;
import com.ansi.scilla.web.quote.response.QuoteListResponse;
import com.ansi.scilla.web.quote.response.QuoteResponseItem;

public class TestQuoteListResponse {

	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			String quoteId = "25308";
			List<UserPermission> permissionList = new ArrayList<UserPermission>();
//			QuoteListResponse quoteListResponse = new QuoteListResponse(conn, quoteId, permissionList);
//			System.out.println(quoteListResponse);
			Quote quote = new Quote();
			quote.setQuoteId(Integer.valueOf(quoteId));
			quote.selectOne(conn);
			QuoteResponseItem item = new QuoteResponseItem(conn, quote, permissionList);
			System.out.println(item);
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
