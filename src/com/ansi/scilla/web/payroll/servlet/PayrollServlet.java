package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.servlet.AbstractServlet;


public class PayrollServlet extends AbstractServlet {
	private static final long serialVersionUID = 1L;

	public static final String REALM = "payroll";
	
	public static final String ALIAS = "alias";
	public static final String ALIAS_LOOKUP = "aliasLookup";
	public static final String EMPLOYEE = "employee";
	public static final String EMPLOYEE_AUTOCOMPLETE = "employeeAutoComplete";
	public static final String EMPLOYEE_CODECOMPLETE = "employeeCodeComplete";
	public static final String EMPLOYEE_IMPORT = "employeeImport";
	public static final String EMPLOYEE_LOOKUP = "employeeLookup";
	public static final String TAX_PROFILE = "taxProfile";
	public static final String TAX_PROFILE_LOOKUP = "taxProfileLookup";
	public static final String TIMESHEET = "timesheet";
	public static final String TIMESHEET_IMPORT = "timesheetImport";
	public static final String TIMESHEET_LOOKUP = "timesheetLookup";
	public static final String EXCEPTION_REPORT = "exceptionReport";
	

	private final Logger logger = LogManager.getLogger(PayrollServlet.class);

	

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Payroll doGet");
		String uri = request.getRequestURI();
		logger.log(Level.DEBUG, "PayrollURI: " + uri);
		
		String trigger = REALM + "/";
		String destination = uri.substring(uri.indexOf(trigger)+trigger.length());
		Integer index = destination.indexOf("/");   // in case we have something like bcr/ticket/12345
		destination = index > 0 ? destination.substring(0, index) : destination;
		
		logger.log(Level.DEBUG, "GET: " + destination);
		
		switch (destination) {
		case ALIAS_LOOKUP:
			new AliasLookupServlet().doGet(request, response);
			break;
		case ALIAS:
			new AliasServlet().doGet(request,response);
			break;
		case EMPLOYEE:
			new EmployeeServlet().doGet(request, response);
			break;
		case EMPLOYEE_AUTOCOMPLETE:
			new EmployeeAutoCompleteServlet().doGet(request, response);
			break;
		case EMPLOYEE_CODECOMPLETE:
			new EmployeeCodeCompleteServlet().doGet(request, response);
			break;
		case EMPLOYEE_LOOKUP:
			new EmployeeLookupServlet().doGet(request, response);
			break;
		case EXCEPTION_REPORT:
			new ExceptionReportServlet().doGet(request, response);
			break;
		case TIMESHEET:
			new TimesheetServlet().doGet(request, response);
			break;
		case TIMESHEET_LOOKUP:
			new TimesheetLookupServlet().doGet(request, response);
			break;
		case TAX_PROFILE:
			new TaxProfileServlet().doGet(request, response);
			break;
		case TAX_PROFILE_LOOKUP:
			new TaxProfileLookupServlet().doGet(request, response);
		default:
			super.sendNotFound(response);
		}
		
	}



	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Payroll doPost");
		String uri = request.getRequestURI();
		logger.log(Level.DEBUG, "PayrollURI: " + uri);
		
		String trigger = REALM + "/";
		String destination = uri.substring(uri.indexOf(trigger)+trigger.length());
		Integer index = destination.indexOf("/");   // in case we have something like bcr/ticket/12345
		destination = index > 0 ? destination.substring(0, index) : destination;
		
		logger.log(Level.DEBUG, "DELETE: " + destination);
		
		switch (destination) {
		case ALIAS:
			new AliasServlet().doDelete(request,response);
			break;
		case EMPLOYEE:
			new EmployeeServlet().doDelete(request, response);
			break;
		case TAX_PROFILE:
			new TaxProfileServlet().doDelete(request, response);
			break;
		case TIMESHEET:
			new TimesheetServlet().doDelete(request, response);
			break;
		default:
			super.sendNotFound(response);
		};
	}



	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Payroll doPost");
		String uri = request.getRequestURI();
		logger.log(Level.DEBUG, "PayrollURI: " + uri);
		
		String trigger = REALM + "/";
		String destination = uri.substring(uri.indexOf(trigger)+trigger.length());
		Integer index = destination.indexOf("/");   // in case we have something like bcr/ticket/12345
		destination = index > 0 ? destination.substring(0, index) : destination;
		
		logger.log(Level.DEBUG, "POST: " + destination);
		
		switch (destination) {
		case ALIAS:
			new AliasServlet().doPost(request,response);
			break;
		case EMPLOYEE:
			new EmployeeServlet().doPost(request, response);
			break;
		case EMPLOYEE_IMPORT:
			new EmployeeImportServlet().doPost(request, response);
			break;		
		case TAX_PROFILE:
			new TaxProfileServlet().doPost(request, response);
			break;
		case TIMESHEET:
			new TimesheetServlet().doPost(request, response);
			break;
		case TIMESHEET_IMPORT:
			new TimesheetImportServlet().doPost(request, response);
			break;
		default:
			super.sendNotFound(response);
		}
	}



	

	


}
