package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.response.permissionGroup.PermissionGroupListResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class PermissionServlet extends AbstractServlet {
	
	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		
	}
	
	public void doDeleteWork(Connection conn, Integer divisionId) throws RecordNotFoundException, Exception {
		
	}
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String url = request.getRequestURI();
		int idx = url.indexOf("/division/");
		if ( idx > -1 ) {
			String queryString = request.getQueryString();
			
			// Figure out what we've got:
			// "myString" is the piece of the URL that we actually care about
			String myString = url.substring(idx + "/division/".length());
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];

			Connection conn = null;
			try {
				if ( StringUtils.isBlank(command)) {
					throw new RecordNotFoundException();
				}
				conn = AppUtils.getDBCPConn();

				PermissionGroupListResponse permListResponse = doGetWork(conn, myString);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, permListResponse);
			} catch(RecordNotFoundException recordNotFoundEx) {
				super.sendNotFound(response);
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}

		} else {
			super.sendNotFound(response);
		}
	}
	
	
	public PermissionGroupListResponse doGetWork(Connection conn, String url) throws RecordNotFoundException, Exception {
		PermissionGroupListResponse permListResponse = new PermissionGroupListResponse();
		String[] x = url.split("/");
		if(x[0].equals("list")){
			permListResponse = new PermissionGroupListResponse();
		} else if (StringUtils.isNumeric(x[0])) {
			Integer permGroupId = Integer.valueOf(x[0]);
			permListResponse = new PermissionGroupListResponse();
		} else {
			throw new RecordNotFoundException();
		}
		return permListResponse;
	}
	
	
	
}
