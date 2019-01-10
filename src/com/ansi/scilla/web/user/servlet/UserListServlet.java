package com.ansi.scilla.web.user.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.user.query.DivisionUserLookup;
import com.ansi.scilla.web.user.query.UserLookupItem;
import com.ansi.scilla.web.user.response.DivisionUserResponse;
import com.ansi.scilla.web.user.response.UserListResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

/**
 * Get a list of users based on parameters
 * @author dclewis
 *
 */
public class UserListServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	private final String REALM = "userList";
	private final String FILTER_IS_DIVISION = "division";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			SessionData sessionData = AppUtils.validateSession(request);
			SessionUser sessionUser = sessionData.getUser();
			doGetWork(request, response, sessionUser);
		} catch (TimeoutException e) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} 
	}

	
	
	private void doGetWork(HttpServletRequest request, HttpServletResponse response, SessionUser sessionUser) throws ServletException {
		Connection conn = null;
		DivisionUserResponse userResponse = new DivisionUserResponse();
		WebMessages messages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			String[] filterArray = new String[] {FILTER_IS_DIVISION};
			AnsiURL url = new AnsiURL(request, REALM, filterArray);	
			
	
			if( url.getId() == null && StringUtils.isBlank(url.getCommand())) {
				throw new ResourceNotFoundException();
			} else if (url.getId() != null) {
				throw new ResourceNotFoundException();
			} else if ( url.getCommand().equalsIgnoreCase(FILTER_IS_DIVISION)) {
				doDivisionList(conn, request, response, sessionUser);
			} else {
				// according to the URI parsing, this shouldn't happen, but it gives me warm fuzzies
				throw new ResourceNotFoundException();
			}
			
			messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
			userResponse.setWebMessages(messages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, userResponse);
		} catch(RecordNotFoundException e) {
			super.sendNotFound(response);
		} catch(ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	
	}



	private void doDivisionList(Connection conn, HttpServletRequest request, HttpServletResponse response, SessionUser sessionUser) throws Exception {
		WebMessages messages = new WebMessages();
		String divisionString = request.getParameter(FILTER_IS_DIVISION);
		DivisionUserLookup lookup = new DivisionUserLookup(Integer.valueOf(divisionString));
		List<UserLookupItem> userLookupList = lookup.select(conn);
		
		UserListResponse userResponse = new UserListResponse(userLookupList);
		messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
		userResponse.setWebMessages(messages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, userResponse);		
	}
}
