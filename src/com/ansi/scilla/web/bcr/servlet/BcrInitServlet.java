package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.web.bcr.response.BcrInitResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.division.response.DivisionListResponse;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payment.response.PaymentResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class BcrInitServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
//	private static final String YEAR = "year";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			try{
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
//				String jsonString = super.makeJsonString(request);
//				logger.log(Level.DEBUG, jsonString);
				SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
				SessionUser sessionUser = sessionData.getUser();

				DivisionListResponse divisionListResponse = sessionData.hasPermission(Permission.SYSADMIN_READ.toString()) ?
					new DivisionListResponse(conn)
					:
					new DivisionListResponse(conn, sessionUser);
				
				Calendar workDay = Calendar.getInstance();
				WebMessages webMessages = new WebMessages();
				BcrInitResponse data = new BcrInitResponse(conn, divisionListResponse.getDivisionList(), workDay);
				
				data.setWebMessages(webMessages);
				if ( webMessages.isEmpty() ) {
					super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
				} else {
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
				}
				
				
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				PaymentResponse data = new PaymentResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
				super.sendForbidden(response);			
			}
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}	
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doPost(request, response);
	}

	
}
