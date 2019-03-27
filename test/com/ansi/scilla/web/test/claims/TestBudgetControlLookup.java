package com.ansi.scilla.web.test.claims;

import java.sql.Connection;
import java.sql.ResultSet;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.claims.query.BudgetControlLookupQuery;

public class TestBudgetControlLookup {

	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
//			BudgetControlLookupQuery query = new BudgetControlLookupQuery(5);
			BudgetControlLookupQuery query = new BudgetControlLookupQuery(5, "harlem irving");
			query.setTicketFilter(780741);
			Integer countAll = query.countAll(conn);
			System.out.println("All:" + countAll);
			Integer selectCount = query.selectCount(conn);
			System.out.println("Some:" + selectCount);
			ResultSet rs = query.select(conn, 0, 10);
			while ( rs.next() ) {
				System.out.println(rs.getInt(BudgetControlLookupQuery.TICKET_ID) + " " + rs.getString(BudgetControlLookupQuery.JOB_SITE_NAME));
			}
			
			conn.rollback();
		} finally {
			conn.rollback();
			conn.close();
		}
	}
	public static void main(String[] args) {
		try {
			new TestBudgetControlLookup().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
