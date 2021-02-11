package com.ansi.scilla.web.test.bcr;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.bcr.response.BcrTicketClaimResponse;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.test.TesterUtils;

public class TestTicketResponse {

	private final Integer userId = 5;
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> divisionList = TesterUtils.makeSessionDivisionList(conn, userId);
			
			
			String uri = "/ansi_web/bcr/ticket/506645";
			String[] uriPath = uri.split("/");
			String ticket = uriPath[uriPath.length-1];
			System.out.println(ticket);
			
			
//			BcrTicketResponse response = new BcrTicketResponse(conn, userId, divisionList, 101, 2020, "45,46,47,48", 506645);
//			System.out.println(response);
			
		} finally {
			if ( conn != null ) {
				conn.close();
			}
		}
	}
	
	
	public static void main(String[] args) {
		try {
			new TestTicketResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
