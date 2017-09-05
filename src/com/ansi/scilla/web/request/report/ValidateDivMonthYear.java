package com.ansi.scilla.web.request.report;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;

public class ValidateDivMonthYear extends ApplicationObject  {

	private static final long serialVersionUID = 1L;

	public static List<String> validate(Connection conn, ReportDefinition def) {
		List<String> messageList = new ArrayList<String>();
		
		if ( def.getDivisionId() == null ) {
			messageList.add("Missing Division Id");
		}
		if ( def.getMonth() == null ) {
			messageList.add("Missing month");
		}
		if ( def.getYear() == null ) {
			messageList.add("Missing year");
		}
		return messageList;
	}
}
