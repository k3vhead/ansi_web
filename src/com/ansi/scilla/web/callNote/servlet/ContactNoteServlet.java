package com.ansi.scilla.web.callNote.servlet;

import java.sql.Connection;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.callNote.response.CallNoteResponse;
import com.ansi.scilla.web.callNote.response.ContactNoteResponse;
import com.ansi.scilla.web.common.struts.SessionUser;

public class ContactNoteServlet extends AbstractCallNoteServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "contactNote";
	
	@Override
	protected CallNoteResponse makeResponse(Connection conn, String xrefType, Integer xrefId, SessionUser user) throws Exception {
		logger.log(Level.DEBUG, "CallNoteResponse");
		ContactNoteResponse response = new ContactNoteResponse(conn, xrefType, Integer.valueOf(xrefId), user);
		logger.log(Level.DEBUG, response);
		return response;
	}

	@Override
	protected String makeRealm() {
		return REALM;
	}

}
