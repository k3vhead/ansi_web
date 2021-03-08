package com.ansi.scilla.web.test.bcr;

import java.sql.Connection;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.bcr.response.BcrInitResponse;
import com.ansi.scilla.web.division.response.DivisionListResponse;

public class TestInitResponse {

	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			GregorianCalendar workDate = new GregorianCalendar(2020, Calendar.NOVEMBER, 12);
			DivisionListResponse divisionListResponse = new DivisionListResponse(conn);			
			BcrInitResponse r = new BcrInitResponse(conn, divisionListResponse.getDivisionList(), workDate); //new BcrInitResponse(null, workDate);
//			System.out.println(r);
			String json = AppUtils.object2json(r);
			System.out.println(json);
		} finally {
			conn.close();
		}
		
	}
	
	
	public static void main(String[] args) {
		try {
			new TestInitResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
