package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;

public class BcrKeepAliveServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, new BcrKeepAliveResponse());
		} catch (Exception e) {			
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}
	
	public class BcrKeepAliveResponse extends MessageResponse {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
	
}
