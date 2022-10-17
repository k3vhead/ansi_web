package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;

import com.ansi.scilla.common.payroll.common.EmployeeStatus;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.payroll.query.EmployeeLookupQuery;

public class EmployeeLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "employeeLookup";
	
	public static final String EMPLOYEE_CODE = "payroll_employee.employee_code";
	public static final String COMPANY_CODE = "company_code";
	public static final String DIVISION = "concat(division.division_nbr, '-',division.division_code)";
//	public static final String "	division_id";
	public static final String FIRST_NAME = "employee_first_name";
	public static final String LAST_NAME = "employee_last_name";
	public static final String MI = "employee_mi";
	public static final String DESCRIPTION = "dept_description";
	public static final String EMPLOYEE_STATUS = "employee_status";
	public static final String TERMINATION_DATE = "employee_termination_date";
	public static final String NOTES = "notes";
	public static final String FORMATTED_TERMINATION_DATE = "formatted_termination_date";
	public static final String UNION_MEMBER = "union_member";
	public static final String UNION_CODE = "union_code";
	public static final String UNION_RATE = "union_rate";
	public static final String PROCESS_DATE = "process_date";
	public static final String FORMATTED_PROCESS_DATE = "formatted_process_date";


	public EmployeeLookupServlet() {
		super(Permission.PAYROLL_READ);
		cols = new String[] { 
			EMPLOYEE_CODE,
			COMPANY_CODE,
			DIVISION,
//				"	division_id";
			FIRST_NAME,
			LAST_NAME,
			MI,
			DESCRIPTION,
			EMPLOYEE_STATUS,
			TERMINATION_DATE,
			UNION_MEMBER,
			UNION_CODE,
			UNION_RATE,
			PROCESS_DATE,
			NOTES,
		};
		super.itemTransformer = new ItemTransformer();
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
			EmployeeLookupQuery lookupQuery = new EmployeeLookupQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
			return lookupQuery;
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	
	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

			java.sql.Date terminationDate = (java.sql.Date)arg0.get(TERMINATION_DATE);			
			String formattedDate = terminationDate == null ? null : sdf.format(terminationDate);
			arg0.put(FORMATTED_TERMINATION_DATE, formattedDate);

			EmployeeStatus employeeStatus = EmployeeStatus.valueOf((String)arg0.get(EMPLOYEE_STATUS));
			arg0.put(EMPLOYEE_STATUS, employeeStatus.display());
			
			java.sql.Date processDate = (java.sql.Date)arg0.get(PROCESS_DATE);
			String formattedProcessDate = processDate == null ? null : sdf.format(processDate);
			arg0.put(FORMATTED_PROCESS_DATE, formattedProcessDate);
			
			return arg0;
		}

	}
}
