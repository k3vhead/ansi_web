package com.ansi.scilla.web.test.taxes;

import java.sql.Connection;
import java.sql.ResultSet;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.tax.query.TaxRateLookupQuery;

public class TestTaxRateLookup {

	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			TaxRateLookupQuery query = new TaxRateLookupQuery();
			
//			Integer countAll = query.countAll(conn);
//			System.out.println("Count all: " + countAll);
			
//			query.setSearchTerm("OH");
//			Integer countSome = query.selectCount(conn);
//			System.out.println("Count some: " + countSome);
			
			ResultSet rs = query.select(conn, 0, 50);
			while ( rs.next() ) {
				System.out.println(rs.getString("name") + ", " + rs.getString("state_name"));
			}
			rs.close();
			
		} finally {
			conn.close();
		}
	}
	public static void main(String[] args) {
		try {
			new TestTaxRateLookup().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
