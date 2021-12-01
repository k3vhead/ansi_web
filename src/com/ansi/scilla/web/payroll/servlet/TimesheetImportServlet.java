package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payroll.request.TimesheetImportRequest;
import com.ansi.scilla.web.payroll.response.TimesheetImportResponse;


public class TimesheetImportServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "payroll/timesheetImport";
		
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.log(Level.DEBUG, "Employee Import post");

		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			SessionData sessionData = AppUtils.validateSession(request, Permission.PAYROLL_WRITE);
			TimesheetImportRequest uploadRequest = new TimesheetImportRequest(request);
			ResponseCode responseCode = null;
			WebMessages webMessages = uploadRequest.validate(conn);
			TimesheetImportResponse data = new TimesheetImportResponse();
			
			data.setWebMessages(webMessages);
			
			if ( webMessages.isEmpty() ) {
				data = new TimesheetImportResponse(conn, uploadRequest);
				responseCode = ResponseCode.SUCCESS;
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}

			super.sendResponse(conn, response, responseCode, data);

			conn.close();
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);	
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
		

		Enumeration<String> parmNames =  request.getParameterNames();
		while ( parmNames.hasMoreElements() ) {
			String parmName = parmNames.nextElement();
			logger.log(Level.DEBUG, parmName);
		}
	}

}
