package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.payroll.common.PayrollWorksheetHeader;
import com.ansi.scilla.common.payroll.parser.NotATimesheetException;
import com.ansi.scilla.common.payroll.parser.PayrollWorksheetParser;
import com.ansi.scilla.common.payroll.validator.PayrollMessage;
import com.ansi.scilla.common.payroll.validator.PayrollWorksheetValidator;
import com.ansi.scilla.common.utils.ErrorLevel;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
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
			
			
			
			if ( webMessages.isEmpty() ) {
				try {
					Map<String,HashMap<String,List<PayrollMessage>>> employeeMsgs = null;
										
					FileItem requestFile = uploadRequest.getTimesheetFile();
					PayrollWorksheetParser parser = new PayrollWorksheetParser(requestFile.getName(), requestFile.getInputStream());
					
					PayrollWorksheetHeader header = PayrollWorksheetValidator.validateHeader(conn, parser);
					if ( ! header.maxErrorLevel().equals(ErrorLevel.ERROR)) {
						employeeMsgs = PayrollWorksheetValidator.validatePayrollEmployees(conn, header, parser);
					} else {
						employeeMsgs =  new HashMap<String, HashMap<String, List<PayrollMessage>>>();
					}
					logger.log(Level.DEBUG, "TimesheetImportServlet: employeeMsgs = " + employeeMsgs);

					data = new TimesheetImportResponse(conn, header, parser, employeeMsgs);
					switch ( header.maxErrorLevel() ) {
					case ERROR:
						responseCode = ResponseCode.EDIT_FAILURE;
						break;
					case OK:
						responseCode = ResponseCode.SUCCESS;
						break;
					case WARNING:
						responseCode = ResponseCode.EDIT_WARNING;
						break;
					default:
						throw new ServletException("Invalid validation error level: " + header.maxErrorLevel().name());
					}
				} catch ( NotATimesheetException e) {
					responseCode = ResponseCode.EDIT_FAILURE;
					webMessages.addMessage(TimesheetImportRequest.TIMESHEET_FILE, "Not a Timesheet Worksheet");
				}
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}

			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, responseCode, data);

			conn.close();
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);	
		} catch (Exception e) {
			throw new ServletException(e);
		}		
	}

}
