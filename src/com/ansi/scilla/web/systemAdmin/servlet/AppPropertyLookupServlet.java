package com.ansi.scilla.web.systemAdmin.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.systemAdmin.response.AppPropertyLookupResponse;

public class AppPropertyLookupServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "appPropertyLookup";
	
	
		
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			Statement s = conn.createStatement();
			AppUtils.validateSession(request, Permission.SYSADMIN_READ);
			try {
				AppPropertyLookupResponse data = new AppPropertyLookupResponse(s);

				data.setWebMessages(new WebMessages());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
			} finally {
				conn.close();
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);	
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		}
	}


	
	
	
}
