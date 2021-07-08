package com.ansi.scilla.web.test.bcr;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.common.db.TicketClaim;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.bcr.response.BcrTicketClaimSummary;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.test.TesterUtils;

public class TestTicketClaimSummary {

	private final Integer userId = 5;
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> divisionList = TesterUtils.makeSessionDivisionList(conn, userId);
			TicketClaim ticketClaim = new TicketClaim();
			ticketClaim.setClaimId(28);
			ticketClaim.selectOne(conn);
			
			BcrTicketClaimSummary response = new BcrTicketClaimSummary(conn, ticketClaim, divisionList, 101, 2020);
			System.out.println(response);
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
			new TestTicketClaimSummary().go();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
