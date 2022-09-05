package com.ansi.scilla.web.test.bcr;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.bcr.response.BudgetControlTotalsResponse;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.test.TesterUtils;

public class TestTotalsResponse {

	private final Integer userId = 5;
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> divisionList = TesterUtils.makeSessionDivisionList(conn, userId);
			
			
//			String uri = "/ansi_web/bcr/ticket/506645";
//			String[] uriPath = uri.split("/");
//			String ticket = uriPath[uriPath.length-1];
//			System.out.println(ticket);
			
			BudgetControlTotalsResponse data = new BudgetControlTotalsResponse(conn, userId, divisionList, 101, 2020, "45,46,47,48");
//			System.out.println(data);
//			String json = AppUtils.object2json(data);
//			System.out.println(json);
			
		} finally {
			if ( conn != null ) {
				conn.close();
			}
		}
	}
	
	
	public static void main(String[] args) {
		try {
			new TestTotalsResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
