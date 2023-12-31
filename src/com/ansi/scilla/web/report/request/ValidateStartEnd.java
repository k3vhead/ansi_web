package com.ansi.scilla.web.report.request;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;

public class ValidateStartEnd extends ApplicationObject  {

	private static final long serialVersionUID = 1L;

	public static List<String> validate(Connection conn, ReportDefinition def) {
		List<String> messageList = new ArrayList<String>();
		
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
}
