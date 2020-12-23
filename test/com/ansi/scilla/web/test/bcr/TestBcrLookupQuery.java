package com.ansi.scilla.web.test.bcr;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.web.bcr.response.BudgetControlEmployeesResponse;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.test.TesterUtils;

public class TestBcrLookupQuery {

	private final Integer userId = 5;
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> divisionList = TesterUtils.makeSessionDivisionList(conn, userId);
			
			
//			BudgetControlTotalsResponse response = new BudgetControlTotalsResponse(conn, userId, divisionList, 101, 2020, "45,46,47,48");
//			System.out.println(response);
			
			BudgetControlEmployeesResponse response = new BudgetControlEmployeesResponse(conn, userId, divisionList, 101, 2020, "45,46,47,48");
			System.out.println(response);
			
//			LookupQuery lookupQuery = new BcrWeeklyTicketLookupQuery(userId, divisionList, 101, 2020, "45,46,47,48","46");
//			System.out.println("Count all: " + lookupQuery.countAll(conn));
//			ResultSet rs = lookupQuery.select(conn, 0, 10);
//			while (rs.next()) {
//				System.out.println(rs.getString("job_site_name"));
//			}
//			rs.close();

			
			
//			BcrTicketLookupQuery lookupQuery = new BcrTicketLookupQuery(userId, divisionList, 101, 2020, "45,46,47,48");
//			System.out.println("Count all: " + lookupQuery.countAll(conn));
//			ResultSet rs = lookupQuery.select(conn, 0, 10);
//			while (rs.next()) {
//				System.out.println(rs.getString("job_site_name"));
//			}
//			rs.close();
		} finally {
			if ( conn != null ) {
				conn.close();
			}
		}
	}
	public static void main(String[] args) {
		try {
			new TestBcrLookupQuery().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
