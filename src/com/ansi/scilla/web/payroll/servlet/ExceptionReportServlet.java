package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.payroll.common.EmployeeStatus;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.division.query.DivisionLookupQuery;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.InvalidParameterException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payroll.query.ExceptionReportQuery;
import com.ansi.scilla.web.payroll.response.ExceptionReportResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class ExceptionReportServlet extends AbstractLookupServlet {

//	public static final String COMPANY_CODE = "company_code";
	public static final String EMPLOYEE_CODE = "payroll_worksheet.employee_code";
	public static final String DIVISION_ID = "division.division_id";
	public static final String EMPLOYEE_FIRST_NAME = "employee_name";
	public static final String EMPLOYEE_LAST_NAME = "employee_last_name";
	public static final String EMPLOYEE_STATUS = "payroll_employee.employee_status";

	public static final String WEEK_ENDING = "week_ending";
	//	public static final String DESCRIPTION = "description";
	public static final String UNION_MEMBER = "union_member";
	public static final String UNION_CODE = "union_code";
	public static final String UNION_RATE = "union_rate";
	public static final String UNDER_UNION_MIN_PAY = "under_union_min_pay";
	public static final String MINIMUM_HOURLY_PAY = DivisionLookupQuery.MINIMUM_HOURLY_PAY;
	public static final String EXCESS_EXPENSE_PCT = "excess_expense_pct";
	public static final String EXCESS_EXPENSE_CLAIM = "excess_expense_claim";
	public static final String YTD_EXCESS_EXPENSE_PCT = "ytd_excess_expense_pct";
	public static final String YTD_EXCESS_EXPENSE_CLAIM = "ytd_excess_expense_claim";
	public static final String EXPENSES_SUBMITTED = "expenses_submitted";
	public static final String VOLUME = "volume";
	public static final String DIRECT_LABOR = "direct_labor";
	public static final String FOREIGN_COMPANY = "foreign_company";
	public static final String FOREIGN_DIVISION = "foreign_division";
	public static final String PRODUCTIVITY = "productivity";
	public static final String PROCESS_DATE = "process_date";
	public static final String COMPANY_CODE = "company_code";
	public static final String TERMINATION_DATE = "employee_termination_date";
	public static final String FORMATTED_TERMINATION_DATE = "formatted_termination_date";
	public static final String FORMATTED_PROCESS_DATE = "formatted_process_date";
	
	
	public static final String ROW_ID = "row_id";
	

	private static final long serialVersionUID = 1L;
	public static final String REALM = "payroll/exceptionReport";

	public ExceptionReportServlet() {
		super(Permission.PAYROLL_WRITE);
		cols = new String[] { 

				

		//		COMPANY_CODE,
				EMPLOYEE_CODE,
				DIVISION_ID,
				WEEK_ENDING,
				EMPLOYEE_FIRST_NAME,
				EMPLOYEE_LAST_NAME,
		//		DESCRIPTION,
				EMPLOYEE_STATUS,
				UNION_MEMBER,
				UNION_CODE,
				UNION_RATE,
				UNDER_UNION_MIN_PAY,
				MINIMUM_HOURLY_PAY,
				EXCESS_EXPENSE_PCT,
				EXCESS_EXPENSE_CLAIM,
				YTD_EXCESS_EXPENSE_PCT,
				YTD_EXCESS_EXPENSE_CLAIM,
				EXPENSES_SUBMITTED,
				VOLUME,
				DIRECT_LABOR,
				FOREIGN_COMPANY,
				FOREIGN_DIVISION,
				PRODUCTIVITY,
				PROCESS_DATE,
				COMPANY_CODE,
				TERMINATION_DATE,
				
				
								
		};
		super.itemTransformer = new ItemTransformer();
	}
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doGet(request, response);
	}


	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) throws InvalidParameterException {
		Integer index = request.getRequestURI().indexOf(REALM);
		String path = request.getRequestURI().substring(index);   // eg: /exceptionReport/102
		logger.log(Level.DEBUG, "Exception uri: " + request.getRequestURI());
		logger.log(Level.DEBUG, "Exception path: " + path);
		
		String groupId = path.substring(StringUtils.lastIndexOf(path, "/")+1);
		logger.log(Level.DEBUG, "groupId: " + groupId);
		
		if ( StringUtils.isNumeric(groupId) ) {
			SessionData sessionData = (SessionData)request.getSession().getAttribute(SessionData.KEY);
			Integer userId = sessionData.getUser().getUserId();
			List<SessionDivision> divisionList = sessionData.getDivisionList();
			LookupQuery exceptionReportQuery = new ExceptionReportQuery(userId, divisionList, Integer.valueOf(groupId));	
			return exceptionReportQuery;
		} else {
			throw new InvalidParameterException();
		}
	}


	protected void doGetXXX(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			AppUtils.validateSession(request, Permission.PAYROLL_WRITE);
			
			Integer index = request.getRequestURI().indexOf(REALM);
			String path = request.getRequestURI().substring(index);   // eg: /exceptionReport/102
			logger.log(Level.DEBUG, "Exception uri: " + request.getRequestURI());
			logger.log(Level.DEBUG, "Exception path: " + path);
			
			String groupId = path.substring(StringUtils.lastIndexOf(path, "/")+1);
			logger.log(Level.DEBUG, "division: " + groupId);
			
			if ( StringUtils.isNumeric(groupId) ) {
				try {
					ExceptionReportResponse data = new ExceptionReportResponse(conn, Integer.valueOf(groupId));
					data.setWebMessages(webMessages);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
				} catch ( RecordNotFoundException e ) {
					super.sendNotFound(response);
				}
			} else {
				super.sendNotFound(response);
			}

				
				
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}
	


	
	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

			java.sql.Date terminationDate = (java.sql.Date)arg0.get(TERMINATION_DATE);			
			String formattedDate = terminationDate == null ? null : sdf.format(terminationDate);
			arg0.put(FORMATTED_TERMINATION_DATE, formattedDate);

			if ( arg0.containsKey(EMPLOYEE_STATUS) && arg0.get(EMPLOYEE_STATUS) != null ) {
				EmployeeStatus employeeStatus = EmployeeStatus.valueOf((String)arg0.get(EMPLOYEE_STATUS));
				arg0.put(EMPLOYEE_STATUS, employeeStatus.display());
			}
			
			java.sql.Date processDate = (java.sql.Date)arg0.get(PROCESS_DATE);
			String formattedProcessDate = processDate == null ? null : sdf.format(processDate);
			arg0.put(FORMATTED_PROCESS_DATE, formattedProcessDate);
			
			
			
			Integer employeeCode = (Integer)arg0.get(EMPLOYEE_CODE);
			Integer divisionId = (Integer)arg0.get(DIVISION_ID);
			String weekEnding = (String)arg0.get("week_ending_display");

			List<String> source = new ArrayList<String>();
			source.add(employeeCode == null ? "" : String.valueOf(employeeCode));
			source.add(divisionId == null ? "" : String.valueOf(divisionId));
			source.add(weekEnding == null ? "" : weekEnding);
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] messageDigest = md.digest(StringUtils.join(source, "").getBytes());
				BigInteger number = new BigInteger(1, messageDigest);
				arg0.put(ROW_ID, number.toString(16));
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
					
			return arg0;
		}

	}

}
