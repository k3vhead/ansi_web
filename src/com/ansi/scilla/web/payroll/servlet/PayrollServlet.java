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
	
	public static final String ALIAS_LOOKUP = "aliasLookup";
	public static final String EMPLOYEE = "employee";
	public static final String EMPLOYEE_LOOKUP = "employeeLookup";	
	public static final String TIMESHEET_LOOKUP = "timesheetLookup";

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
		case EMPLOYEE:
			new EmployeeServlet().doGet(request, response);
			break;
		case EMPLOYEE_LOOKUP:
			new EmployeeLookupServlet().doGet(request, response);
			break;
		case TIMESHEET_LOOKUP:
			new TimesheetLookupServlet().doGet(request, response);
			break;
		default:
			super.sendNotFound(response);
		}
		
	}



	

	


}
