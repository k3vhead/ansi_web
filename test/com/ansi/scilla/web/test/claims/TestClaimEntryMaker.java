package com.ansi.scilla.web.test.claims;

import java.sql.Connection;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.claims.response.ClaimEntryResponse;

public class TestClaimEntryMaker {

	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
//			ClaimEntryResponseMaker maker = new ClaimEntryResponseMaker(5);
//			HashMap<String, Object> x = maker.make(conn,662891);
//			for ( Map.Entry<String, Object> entry : x.entrySet()) {
//				System.out.println(entry.getKey() + "\t=>\t" + entry.getValue());
//			}
			
			ClaimEntryResponse response = new ClaimEntryResponse(conn, 662891, 5);
			System.out.println(response);
		} finally {
			conn.close();
		}
	}
	public static void main(String[] args) {
		try {
			new TestClaimEntryMaker().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
