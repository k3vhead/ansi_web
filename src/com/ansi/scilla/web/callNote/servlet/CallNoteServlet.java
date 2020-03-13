package com.ansi.scilla.web.callNote.servlet;

import java.sql.Connection;
import java.sql.SQLException;

import com.ansi.scilla.web.callNote.response.CallNoteResponse;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class CallNoteServlet extends AbstractCallNoteServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "callNote";

	@Override
	protected CallNoteResponse makeResponse(Connection conn, String xrefType, Integer xrefId, SessionUser user) throws SQLException, RecordNotFoundException {
		return new CallNoteResponse(conn, xrefType, Integer.valueOf(xrefId), user);
	}

	@Override
	protected String makeRealm() {		
		return REALM;
	}

	

}
