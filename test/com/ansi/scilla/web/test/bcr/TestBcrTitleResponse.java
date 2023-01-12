package com.ansi.scilla.web.test.bcr;

import java.sql.Connection;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.bcr.response.BcrTitleResponse;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.test.TesterUtils;

public class TestBcrTitleResponse {

	private final Integer userId = 5;
	Integer divisionId = 103;
	Calendar selectedDate = new GregorianCalendar(2023, Calendar.JANUARY, 1);
	
	public static void main(String[] args) {
		try {
			new TestBcrTitleResponse().go();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			SessionUser sessionUser = TesterUtils.makeSessionUser(conn, userId);
			BcrTitleResponse response = new BcrTitleResponse(conn, sessionUser, divisionId, selectedDate);
			System.out.println(response);
		} catch ( Exception e) {
			conn.rollback();
			conn.close();
			throw e;
		}
		
		
	}

}
