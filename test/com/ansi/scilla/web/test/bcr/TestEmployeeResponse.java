package com.ansi.scilla.web.test.bcr;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.bcr.response.BudgetControlEmployeesResponse;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.test.TesterUtils;

public class TestEmployeeResponse {

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
			
			BudgetControlEmployeesResponse response = new BudgetControlEmployeesResponse(conn, userId, divisionList, 101, 2020, "41,42,43,44");
//			System.out.println(response);
			String json = AppUtils.object2json(response);
			System.out.println(json);
			
		} finally {
			if ( conn != null ) {
				conn.close();
			}
		}
	}
	
	
	public static void main(String[] args) {
		try {
			new TestEmployeeResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
