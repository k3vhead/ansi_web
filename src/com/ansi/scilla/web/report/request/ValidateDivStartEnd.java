package com.ansi.scilla.web.report.request;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Division;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class ValidateDivStartEnd extends ApplicationObject  {

	private static final long serialVersionUID = 1L;

	public static List<String> validate(Connection conn, ReportDefinition def) throws Exception {
		List<String> messageList = new ArrayList<String>();
		
		if ( def.getDivisionId() == null ) {
			messageList.add("Missing Division Id");
		} else {
			try {
				validateDivision(conn, def.getDivisionId());
			} catch ( RecordNotFoundException e ) {
				messageList.add("Invalid Division");
			}
		}
		if ( def.getStartDate() == null ) {
			messageList.add("Missing start date");
		}
		if ( def.getEndDate() == null ) {
			messageList.add("Missing end date");
		}
		if ( def.getStartDate() != null && def.getEndDate() != null ) {
			if ( def.getEndDate().before(def.getStartDate())) {
				messageList.add("End date cannot be before start date");
			}
		}
		return messageList;
	}
	
	private static void validateDivision(Connection conn, Integer divisionId) throws RecordNotFoundException, Exception {
		Division division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);
	}

}
