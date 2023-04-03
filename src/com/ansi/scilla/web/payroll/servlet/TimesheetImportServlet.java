package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.payroll.common.PayrollUtils;
import com.ansi.scilla.common.payroll.common.VersionStatus;
import com.ansi.scilla.common.payroll.exceptions.NotATimesheetException;
import com.ansi.scilla.common.payroll.parser.worksheet.PayrollWorksheetParser;
import com.ansi.scilla.common.payroll.validator.common.ValidatorUtils;
import com.ansi.scilla.common.payroll.validator.worksheet.HeaderValidator;
import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheet;
import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheetEmployee;
import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheetHeader;
import com.ansi.scilla.common.utils.ErrorLevel;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
//import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payroll.request.TimesheetImportRequest;
import com.ansi.scilla.web.payroll.response.TimesheetImportResponse;


public class TimesheetImportServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "payroll/timesheetImport";
	final Logger payrollLogger = LogManager.getLogger(PayrollUtils.PAYROLL_PARSER);
		
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
//					Map<String,HashMap<String,List<PayrollMessage>>> employeeMsgs = null;
					List<ValidatedWorksheetEmployee> validatedEmployees = new ArrayList<ValidatedWorksheetEmployee>();
										
					FileItem requestFile = uploadRequest.getTimesheetFile();
					PayrollWorksheetParser parser = new PayrollWorksheetParser(conn, requestFile.getName(), requestFile.getInputStream());
					
					ValidatedWorksheetHeader header = HeaderValidator.validateHeader(conn, parser.getHeader());
					ErrorLevel versionErrorLevel = PayrollUtils.verifyPayrollVersion(conn, requestFile.getInputStream()).errorLevel();
					if ( ! versionErrorLevel.equals(ErrorLevel.OK)) {
						webMessages.addMessage("VERSION", "Warning: Invalid spreadsheet version");
					}
					if ( ! header.maxErrorLevel().equals(ErrorLevel.ERROR)) {
						payrollLogger.log(Level.DEBUG, "Validated Employees:");
						validatedEmployees = ValidatorUtils.validatePayrollEmployees(conn, header, parser.getTimesheetRecords());
						for ( ValidatedWorksheetEmployee e : validatedEmployees ) {
							payrollLogger.log(Level.DEBUG, e);
						}
					}
					//logger.log(Level.DEBUG, "TimesheetImportServlet: employeeMsgs = " + employeeMsgs);

					ValidatedWorksheet validatedWorksheet = new ValidatedWorksheet(header, validatedEmployees);
					payrollLogger.log(Level.DEBUG, validatedWorksheet);
					data = new TimesheetImportResponse(conn, parser.getFileName(), validatedWorksheet);
					payrollLogger.log(Level.DEBUG, data);
					if ( validatedWorksheet.getHeader().maxErrorLevel().equals(ErrorLevel.WARNING) || versionErrorLevel.equals(ErrorLevel.WARNING) ) { 
							responseCode = ResponseCode.EDIT_WARNING;
					} else {
						responseCode = ResponseCode.SUCCESS;
					}
//					switch ( validatedWorksheet.maxErrorLevel() ) {
//					case ERROR:
//						responseCode = ResponseCode.EDIT_FAILURE;
//						break;
//					case OK:
//						responseCode = ResponseCode.SUCCESS;
//						break;
//					case WARNING:
//						responseCode = ResponseCode.EDIT_WARNING;
//						break;
//					default:
//						throw new ServletException("Invalid validation error level: " + header.maxErrorLevel().name());
//					}
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
