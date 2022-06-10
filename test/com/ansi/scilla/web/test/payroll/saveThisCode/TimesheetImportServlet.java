package com.ansi.scilla.web.test.payroll.saveThisCode;

import java.io.IOException;
import java.sql.Connection;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.payroll.parser.NotATimesheetException;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.response.WebMessagesStatus;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
//import com.ansi.scilla.web.common.struts.SessionData;
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
		logger.log(Level.DEBUG, "TimesheetImportServlet: doPost");
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			AppUtils.validateSession(request, Permission.PAYROLL_WRITE);
			TimesheetImportRequest uploadRequest = new TimesheetImportRequest(request);
			ResponseCode responseCode = null;
			WebMessages webMessages = uploadRequest.validate(conn);
			TimesheetImportResponse data = new TimesheetImportResponse();
			
			data.setWebMessages(webMessages);
			
			if ( webMessages.isEmpty() ) {
				try {
					data = new TimesheetImportResponse(conn, uploadRequest);
					// document isn't parsed until the response item is created 
					// so validation must occur there. 
					logger.log(Level.DEBUG, "TimesheetImportServlet: doPost - no error creating request");
					WebMessagesStatus responseStatus = data.validate(conn);
					responseCode = responseStatus.getResponseCode();
					data.setWebMessages(responseStatus.getWebMessages());
				} catch ( NotATimesheetException e) {
					responseCode = ResponseCode.EDIT_FAILURE;
					//String fName = new String(uploadRequest.getTimesheetFile().toString()); 
//					String fName = new String(uploadRequest.getTimesheetFile().getName()); 
					webMessages.addMessage(TimesheetImportRequest.TIMESHEET_FILE, "Not a Timesheet Worksheet");
				}
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
