package com.ansi.scilla.web.test.common;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.common.struts.SessionDivision;

public abstract class AbstractTester {

	

	protected void run() {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			go(conn);
		} catch ( Exception e ) {
			e.printStackTrace();
			AppUtils.closeQuiet(conn);
		} finally {
			if ( conn != null ) {
				AppUtils.closeQuiet(conn);
			}
		}
	}
	
	protected abstract void go(Connection conn) throws Exception;

	
	
	protected List<SessionDivision> makeDivisionList(Connection conn) throws Exception {
		List<SessionDivision> divisionList = new ArrayList<SessionDivision>();
		List<Division> divisions = Division.cast(new Division().selectAll(conn));
		for ( Division d : divisions ) {
			divisionList.add(new SessionDivision(d));
		}
		return divisionList;
	}
}
