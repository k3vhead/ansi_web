package com.ansi.scilla.web.specialOverride.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.specialOverride.response.SpecialOverrideResponse;
import com.ansi.scilla.web.specialOverride.response.SpecialOverrideSelectItem;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class UnactivateJobServlet extends AbstractOverrideServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "UNACTIVATE_JOB";
	public static final String REALM_DESC = "Unactivate a Job";
	
	private final String FIELD_JOB_ID = "job_id";
	private final String FIELD_REASON = "reason";
	
	private final String RESPONSE_MESSAGE = "Job returned to Proposed and future tickets removed.";
	
//	Note: job_status = 'A' makes sure they do not try to change other job_status values.
	private final String selectSql = "select * from job where job_id=? and job_status= 'A'"; 
	private final String updateSql1 = "update job set job_status = 'P', \n"
			+ " activation_date = null, \n"
			+ " cancel_reason= ? \n" // -- unactivate_reason or can be "activated in error"
			+ "where job_id = ? and job_status = 'A'";
	private final String updateSql2 = "delete from job_schedule\n"
			+ "where job_id = ? and ticket_id is null\n"
			+ "and job_id in (select job_id from job where job_status = 'P')";
	private final String updateSelectSql = "select * from job where job_id=?";
	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.QUOTE_CREATE);
			
			if( request.getParameterNames().hasMoreElements() ) {
				webMessages = validateSelectParameters(conn, request);
				if(webMessages.isEmpty()) {
					sendSelectResults(conn, response, request.getParameter(FIELD_JOB_ID));
				} else {
					sendEditErrors(conn, response, webMessages);
				}
			} else {
				sendSelectParameterTypes(conn, response);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {	
			logger.log(Level.DEBUG, e);
			super.sendForbidden(response);			
		} catch ( RecordNotFoundException | ResourceNotFoundException e ) {					
			logger.log(Level.DEBUG, e);			
			super.sendNotFound(response);						
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);	
		}
	}
	
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.log(Level.DEBUG, "You are here");
		Connection conn = null;
		try {
			SessionData sessionData = AppUtils.validateSession(request, Permission.QUOTE_CREATE);
			SessionUser user = sessionData.getUser();
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
				
			WebMessages webMessages = validateUpdateParameters(conn, request);
			if ( webMessages.isEmpty() ) {
				doUpdate(conn, request, response, user );
				sendUpdateResults(conn, response, request);
			} else {
				sendEditErrors(conn, response, webMessages);
			}
			
			
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {							// these are thrown by session validation
			super.sendForbidden(response);
		} catch ( RecordNotFoundException | ResourceNotFoundException e) {		
			super.sendNotFound(response);
		} catch ( Exception e) {						// something bad happened
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);					// return the connection to the pool
		}	
	}


	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendForbidden(response);
		
	}


	private WebMessages validateSelectParameters(Connection conn, HttpServletRequest request) throws Exception {
		WebMessages webMessages = new WebMessages();
		String jobParm = request.getParameter(FIELD_JOB_ID);
		if ( StringUtils.isBlank(jobParm) ) {
			webMessages.addMessage(FIELD_JOB_ID, "Required Value");
		} else if ( ! StringUtils.isNumeric(jobParm) ) {
			webMessages.addMessage(FIELD_JOB_ID, "Invalid Format");
		} else {
			Integer jobId = Integer.valueOf(jobParm);
			RequestValidator.validateId(conn, webMessages, Job.TABLE_NAME, Job.JOB_ID, FIELD_JOB_ID, jobId, true);
		}
		return webMessages;
	}


	private WebMessages validateUpdateParameters(Connection conn, HttpServletRequest request) throws Exception {
		WebMessages webMessages = validateSelectParameters(conn, request);
		RequestValidator.validateString(webMessages, FIELD_REASON, request.getParameter(FIELD_REASON), true);
		return webMessages;
	}


	private void doUpdate(Connection conn, HttpServletRequest request, HttpServletResponse response, SessionUser user) throws SQLException {
		PreparedStatement ps1 = conn.prepareStatement(this.updateSql1);
		ps1.setString(1, request.getParameter(FIELD_REASON));
		ps1.setInt(2, Integer.valueOf(request.getParameter(FIELD_JOB_ID)));
		ps1.executeUpdate();
		
		PreparedStatement ps2 = conn.prepareStatement(this.updateSql2);	
		ps2.setInt(1, Integer.valueOf(request.getParameter(FIELD_JOB_ID)));
		ps2.executeUpdate();		
		
		conn.commit();
	}


	private void sendSelectParameterTypes(Connection conn, HttpServletResponse response) throws Exception {
		SpecialOverrideResponse data = new SpecialOverrideResponse();
		List<SpecialOverrideSelectItem> selectList = new ArrayList<SpecialOverrideSelectItem>();
		selectList.add(new SpecialOverrideSelectItem("Job ID",FIELD_JOB_ID,Integer.class.getSimpleName()));
		data.setSelectList(selectList);
		List<SpecialOverrideSelectItem> updateList = new ArrayList<SpecialOverrideSelectItem>();
		updateList.add(new SpecialOverrideSelectItem("Job ID",FIELD_JOB_ID,Integer.class.getSimpleName()));
		updateList.add(new SpecialOverrideSelectItem("Reason",FIELD_REASON,String.class.getSimpleName()));
		data.setUpdateList( updateList );
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}

	

	private void sendSelectResults(Connection conn, HttpServletResponse response, String jobParm) throws Exception {
		PreparedStatement ps = conn.prepareStatement(this.selectSql);
		ps.setInt(1, Integer.valueOf(jobParm));
		ResultSet rs = ps.executeQuery();
		
		SpecialOverrideResponse data = new SpecialOverrideResponse(rs);
		rs.close();
		List<SpecialOverrideSelectItem> selectList = new ArrayList<SpecialOverrideSelectItem>();
		selectList.add(new SpecialOverrideSelectItem("Job ID",FIELD_JOB_ID,Integer.class.getSimpleName()));
		data.setSelectList(selectList);
		List<SpecialOverrideSelectItem> updateList = new ArrayList<SpecialOverrideSelectItem>();
		updateList.add(new SpecialOverrideSelectItem("Job ID",FIELD_JOB_ID,Integer.class.getSimpleName()));
		updateList.add(new SpecialOverrideSelectItem("Reason",FIELD_REASON,String.class.getSimpleName()));
		data.setUpdateList( updateList );
		
		WebMessages webMessages = new WebMessages();
		webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}


	private void sendUpdateResults(Connection conn, HttpServletResponse response, HttpServletRequest request) throws Exception {
		PreparedStatement ps = conn.prepareStatement(this.updateSelectSql);
		ps.setInt(1, Integer.valueOf(request.getParameter(FIELD_JOB_ID)));
		ResultSet rs = ps.executeQuery();
		SpecialOverrideResponse data = new SpecialOverrideResponse(rs);
		rs.close();
		WebMessages webMessages = new WebMessages();
		webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, RESPONSE_MESSAGE);
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}


	private void sendEditErrors(Connection conn, HttpServletResponse response, WebMessages webMessages) throws Exception {
		SpecialOverrideResponse data = new SpecialOverrideResponse();
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);		
	}

}
