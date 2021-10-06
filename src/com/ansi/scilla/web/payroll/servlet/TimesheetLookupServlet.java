package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.payroll.query.TimesheetLookupQuery;

public class TimesheetLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "timesheetLookup";
	
	public static final String DIVISION_ID = "division_id";
	public static final String DIV = "concat(division.division_nbr, '-', division.division_code)"; 
	public static final String WEEK_ENDING = "week_ending";
	public static final String STATE = "state";
	public static final String CITY = "city";
	public static final String EMPLOYEE_CODE = "employee_code";
//	-- payroll_employee.employee_first_name,
//	-- payroll_employee.employee_last_name,
//	-- payroll_employee.employee_mi,
//	-- concat(payroll_employee.employee_last_name, ', ', payroll_employee.employee_first_name, ' ', payroll_employee.employee_mi) as employee_name,
	public static final String EMPLOYEE_NAME = "employee_name";
	public static final String REGULAR_HOURS = "regular_hours";
	public static final String REGULAR_PAY = "regular_pay";
	public static final String EXPENSES = "expenses";
	public static final String OT_HOURS = "ot_hours";
	public static final String OT_PAY = "ot_pay";
	public static final String VACATION_HOURS = "vacation_hours";
	public static final String VACATION_PAY = "vacation_pay";
	public static final String HOLIDAY_HOURS = "holiday_hours";
	public static final String HOLIDAY_PAY = "holiday_pay";
	public static final String GROSS_PAY = "gross_pay";
	public static final String EXPENSES_SUBMITTED = "expenses_submitted";
	public static final String EXPENSES_ALLOWED = "expenses_allowed";
	public static final String VOLUME = "volume";
	public static final String DIRECT_LABOR = "direct_labor";
	public static final String PRODUCTIVITY = "productivity";


	public TimesheetLookupServlet() {
		super(Permission.PAYROLL_READ);
		cols = new String[] { 
//			DIVISION_ID,
			DIV, 
			WEEK_ENDING,
			STATE,
			CITY,
			EMPLOYEE_CODE,
			EMPLOYEE_NAME,
			REGULAR_HOURS,
			REGULAR_PAY,
			EXPENSES,
			OT_HOURS,
			OT_PAY,
			VACATION_HOURS,
			VACATION_PAY,
			HOLIDAY_HOURS,
			HOLIDAY_PAY,
			GROSS_PAY,
			EXPENSES_SUBMITTED,
			EXPENSES_ALLOWED,
			VOLUME,
			DIRECT_LABOR,
			PRODUCTIVITY,
		};
	}
	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doGet(request, response);
	}


	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		
		SessionUser user = sessionData.getUser();
		List<SessionDivision> divisionList = sessionData.getDivisionList();
		try {
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			String searchTerm = null;
			if(request.getParameter("search[value]") != null){
				searchTerm = request.getParameter("search[value]");
			}
			TimesheetLookupQuery lookupQuery = new TimesheetLookupQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
			return lookupQuery;
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
