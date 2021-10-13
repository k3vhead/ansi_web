package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

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
import com.thewebthing.commons.db2.RecordNotFoundException;

public class ExceptionReportServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "payroll/exceptionReport";

	public ExceptionReportServlet() {
		super(Permission.PAYROLL_WRITE);
		cols = new String[] { 
			"group_name",
			"company_code",
			"division_id",
			"description"
		};
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

}
