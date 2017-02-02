package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.ansiUser.AnsiUserListResponse;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.ResponseCode;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TicketDRV extends AbstractServlet {
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String url = request.getRequestURI();
		/*
			Connection conn = null;
			try {
				
				conn = AppUtils.getDBCPConn();

				AnsiUserListResponse ansiUserListResponse = doGetWork(conn, myString);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, ansiUserListResponse);
			} catch(RecordNotFoundException recordNotFoundEx) {
				super.sendNotFound(response);
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
		*/
		
	}
	
	public AnsiUserListResponse doGetWork(Connection conn, String url) throws RecordNotFoundException, Exception {
		AnsiUserListResponse ansiUserListResponse = new AnsiUserListResponse();
		String[] x = url.split("/");
		if(x[0].equals("list")){
			ansiUserListResponse = new AnsiUserListResponse(conn);
		} else if (StringUtils.isNumeric(x[0])) {
			Integer permGroupId = Integer.valueOf(x[0]);
			ansiUserListResponse = new AnsiUserListResponse(conn, permGroupId);
		} else {
			throw new RecordNotFoundException();
		}
		return ansiUserListResponse;
	}
	
}
