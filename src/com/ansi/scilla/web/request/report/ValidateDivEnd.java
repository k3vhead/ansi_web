package com.ansi.scilla.web.request.report;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Division;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class ValidateDivEnd extends ApplicationObject  {

	private static final long serialVersionUID = 1L;

	public static List<String> validate(Connection conn, ReportDefinition def) {
		List<String> messageList = new ArrayList<String>();
		
		if ( def.getDivisionId() == null ) {
			messageList.add("Missing Division Id");
		}
		if ( def.getEndDate() == null ) {
			messageList.add("Missing end date");
		}
		return messageList;
	}
	private static Division validateDivision(Connection conn, Integer divisionId) throws RecordNotFoundException, Exception {
		Division division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);
		return division;
	}

}
