package com.ansi.scilla.web.test.job;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.job.query.JobNotesLookupQuery;
import com.ansi.scilla.web.test.TesterUtils;

public class TestJobNoteLookup {

	public static void main(String[] args) {
		try {
			new TestJobNoteLookup().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> divList = TesterUtils.makeSessionDivisionList(conn, 5);
			JobNotesLookupQuery query = new JobNotesLookupQuery(5,divList, 103, 11, 2022, true, true);
			query.setSearchTerm("");
			System.out.println("Count all: " + query.countAll(conn));
		} finally {
			conn.close();
		}
	}

}
