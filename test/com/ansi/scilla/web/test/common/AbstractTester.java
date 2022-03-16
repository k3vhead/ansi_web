package com.ansi.scilla.web.test.common;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.struts.SessionDivision;

public abstract class AbstractTester {
	
	protected List<SessionDivision> makeDivisionList(Connection conn) throws Exception {
		List<SessionDivision> divisionList = new ArrayList<SessionDivision>();
		List<Division> divisions = Division.cast(new Division().selectAll(conn));
		for ( Division d : divisions ) {
			divisionList.add(new SessionDivision(d));
		}
		return divisionList;
	}

}
