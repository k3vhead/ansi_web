package com.ansi.scilla.web.request.report;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;

public class NoValidate extends ApplicationObject  {

	private static final long serialVersionUID = 1L;

	public static List<String> validate(Connection conn, ReportDefinition def) throws Exception {
		List<String> messageList = new ArrayList<String>();
				
		return messageList;
	}
	
}
