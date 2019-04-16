package com.ansi.scilla.web.test.claims;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.ansi.scilla.web.claims.query.DirectLaborLookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Division2SessionDivisionTransformer;

public class TestDirectLaborLookup {

	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			List<SessionDivision> sdList = (List<SessionDivision>) CollectionUtils.collect(AppUtils.makeDivisionList(conn, 5), new Division2SessionDivisionTransformer());
//			BudgetControlLookupQuery query = new BudgetControlLookupQuery(5);
			DirectLaborLookupQuery query = new DirectLaborLookupQuery(5, sdList, 766552);
			Integer countAll = query.countAll(conn);
			System.out.println("All:" + countAll);
			Integer selectCount = query.selectCount(conn);
			System.out.println("Some:" + selectCount);
			ResultSet rs = query.select(conn, 0, 10);
			while ( rs.next() ) {
				System.out.println(rs.getString(DirectLaborLookupQuery.WASHER_LAST_NAME) + " " + rs.getString(DirectLaborLookupQuery.WASHER_FIRST_NAME));
			}
			
			conn.rollback();
		} finally {
			conn.rollback();
			conn.close();
		}
	}
	public static void main(String[] args) {
		try {
			new TestDirectLaborLookup().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
