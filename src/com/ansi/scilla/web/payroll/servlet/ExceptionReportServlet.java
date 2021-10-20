package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.payroll.EmployeeStatus;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.InvalidParameterException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payroll.query.ExceptionReportQuery;
import com.ansi.scilla.web.payroll.response.ExceptionReportResponse;
import com.ansi.scilla.web.payroll.servlet.EmployeeLookupServlet.ItemTransformer;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class ExceptionReportServlet extends AbstractLookupServlet {
	
	public static final String GROUP_NAME = "group_name";
	public static final String COMPANY_NAME = "company_code";
	public static final String DIVISION_ID = "division_id";
	public static final String DESCRIPTION = "description";
	public static final String EMPLOYEE_CODE = "employee_code";
	public static final String EMPLOYEE_FIRST_NAME = "employee_first_name";
	public static final String EMPLOYEE_LAST_NAME = "employee_last_name";
	public static final String EMPLOYEE_STATUS = "employee_status";
	public static final String TERMINATION_DATE = "employee_termination_date";
	public static final String FORMATTED_TERMINATION_DATE = "formatted_termination_date";
	public static final String UNION_MEMBER = "union_member";
	public static final String UNION_CODE = "union_code";
	public static final String UNION_RATE = "union_rate";
	public static final String PROCESS_DATE = "process_date";
	public static final String FORMATTED_PROCESS_DATE = "formatted_process_date";
	
	
	

	private static final long serialVersionUID = 1L;
	public static final String REALM = "payroll/exceptionReport";

	public ExceptionReportServlet() {
		super(Permission.PAYROLL_WRITE);
		cols = new String[] { 
				GROUP_NAME,
				COMPANY_NAME,
				DIVISION_ID,
				DESCRIPTION,
				EMPLOYEE_CODE,
				EMPLOYEE_FIRST_NAME,
				EMPLOYEE_LAST_NAME,
				EMPLOYEE_STATUS,
				TERMINATION_DATE,
				UNION_MEMBER,
				UNION_CODE,
				UNION_RATE,
				PROCESS_DATE,
				
								
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

			EmployeeStatus employeeStatus = EmployeeStatus.valueOf((String)arg0.get(EMPLOYEE_STATUS));
			arg0.put(EMPLOYEE_STATUS, employeeStatus.display());
			
			java.sql.Date processDate = (java.sql.Date)arg0.get(PROCESS_DATE);
			String formattedProcessDate = processDate == null ? null : sdf.format(processDate);
			arg0.put(FORMATTED_PROCESS_DATE, formattedProcessDate);
			
			return arg0;
		}

	}

}
