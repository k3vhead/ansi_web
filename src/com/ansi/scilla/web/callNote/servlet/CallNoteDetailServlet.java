package com.ansi.scilla.web.callNote.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.web.callNote.response.CallNoteDetailResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;

/**
 * Retrieves call note info based on the call log id
 * @author dclewis
 *
 */
public class CallNoteDetailServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "callNoteDetail";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			AppUtils.validateSession(request, Permission.CALL_NOTE_READ);
			
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			
			if (url.getId() == null ) {
				super.sendNotFound(response);
			} else {
				CallNoteDetailResponse data = new CallNoteDetailResponse(conn, url.getId());
				WebMessages webMessages = new WebMessages();
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
			}			
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch (ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}


	

}
