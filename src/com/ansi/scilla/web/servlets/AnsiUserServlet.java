package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.user.response.UserResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class AnsiUserServlet extends AbstractServlet {

	/**
	 * 
	 * @author jwlewis
	 */
	private static final long serialVersionUID = 1L;

	public static final String REALM = "user";

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {
			AppUtils.validateSession(request, Permission.USER_ADMIN, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			doGetWork(request, response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		}
	}

	private void doGetWork(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		Connection conn = null;
		UserResponse userResponse = new UserResponse();
		WebMessages messages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			AnsiURL url = new AnsiURL(request, REALM, new String[] {"list","manager"});	
			String sortField = request.getParameter("sortBy");
			System.out.println("Sortig by: " + sortField);
			if ( ! StringUtils.isBlank(sortField)) {
				if ( ! ArrayUtils.contains(UserResponse.VALID_SORT_FIELDS, sortField) ) {
					sortField = null;
				}
			}
			System.out.println("Still sorting by: " + sortField);

			if( url.getId() == null && StringUtils.isBlank(url.getCommand())) {
				System.out.println("user servlet 43");
				throw new ResourceNotFoundException();
			} else if (url.getId() != null) {
				System.out.println("user servlet 46");
				userResponse = new UserResponse(conn, url.getId());
			} else if ( ! StringUtils.isBlank(url.getCommand())) {
				System.out.println("user servlet 49");
				UserResponse.UserListType listType = UserResponse.UserListType.valueOf(url.getCommand().toUpperCase());
				userResponse = new UserResponse(conn, listType);
			} else {
				// according to the URI parsing, this shouldn't happen, but it gives me warm fuzzies
				throw new ResourceNotFoundException();
			}
			if ( ! StringUtils.isBlank(sortField)) {
				userResponse.sort(sortField);
			}
			System.out.println("user servlet 55");
			messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
			userResponse.setWebMessages(messages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, userResponse);
		} catch(RecordNotFoundException e) {
			System.out.println("user servlet 60");
			super.sendNotFound(response);
		} catch(ResourceNotFoundException e) {
			System.out.println("user servlet 63");
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}

	}



}