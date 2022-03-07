package com.ansi.scilla.web.test.job;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.job.query.JobLookupQuery;
import com.ansi.scilla.web.test.TesterUtils;

public class TestJobLookupQuery {

	private final Integer userId = 5;
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> divisionList = TesterUtils.makeSessionDivisionList(conn, userId);
			JobLookupQuery lookupQuery = new JobLookupQuery(userId, divisionList);
			System.out.println("Count all: " + lookupQuery.countAll(conn));
			ResultSet rs = lookupQuery.select(conn, 0, 10);
			while (rs.next()) {
				System.out.println(rs.getInt("job_id"));
			}
			rs.close();
		} finally {
			if ( conn != null ) {
				conn.close();
			}
		}
	}
	public static void main(String[] args) {
		try {
			new TestJobLookupQuery().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
