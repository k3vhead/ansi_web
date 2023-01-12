package com.ansi.scilla.web.report.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.ReportSubscription;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.report.common.BatchReports;
import com.ansi.scilla.web.report.request.SubscriptionAdminRequest;
import com.ansi.scilla.web.report.response.SubscriptionAdminResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;

/**
 * Provide content and update for Report Subscription Admin
 * 
 * GET: return a list of user/division subscriptions for a given report type
 * POST: Add a subscription for a given user/report.
 * DELETE: Remove  a subscription for a given user/report.
 * 
 * @author dclewis
 *
 */
public class SubscriptionAdminServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "subscriptionAmin";
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
				
		Connection conn = null;
//		AnsiURL url = null;
		WebMessages messages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.REPORT_SUBSCRIPTION_OVERRIDE);
			String reportId = makeReportId(request.getRequestURI());
			SubscriptionAdminResponse data = new SubscriptionAdminResponse(conn, reportId);
			data.setWebMessages(messages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		} catch ( IllegalArgumentException e ) {
			super.sendNotFound(response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;

		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);

			SessionData sessionData = AppUtils.validateSession(request, Permission.REPORT_SUBSCRIPTION_WRITE);
			SubscriptionAdminResponse data = new SubscriptionAdminResponse();
			WebMessages webMessages = new WebMessages();


			try{
				SubscriptionAdminRequest subscriptionRequest = new SubscriptionAdminRequest();
				AppUtils.json2object(jsonString, subscriptionRequest);
				//				logger.log(Level.DEBUG, subscriptionRequest);
				SessionUser sessionUser = sessionData.getUser(); 
				String reportId = makeReportId( request.getRequestURI() );
				webMessages = subscriptionRequest.validate();
				if ( webMessages.isEmpty()) {
					makeSubscription(conn, response, sessionUser, reportId, subscriptionRequest);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, new SubscriptionAdminResponse(conn,reportId));
				} else {
					data.setWebMessages(webMessages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);		
				}
			} catch ( IllegalArgumentException e ) {
				super.sendNotFound(response);
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());				
				webMessages.addMessage(badField, "Invalid Format");
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (RecordNotFoundException e) {
				super.sendNotFound(response);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}




	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;

		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);

			SessionData sessionData = AppUtils.validateSession(request, Permission.REPORT_SUBSCRIPTION_WRITE);
			SubscriptionAdminResponse data = new SubscriptionAdminResponse();
			WebMessages webMessages = new WebMessages();


			try{
				SubscriptionAdminRequest subscriptionRequest = new SubscriptionAdminRequest();
				AppUtils.json2object(jsonString, subscriptionRequest);
				//				logger.log(Level.DEBUG, subscriptionRequest);
				SessionUser sessionUser = sessionData.getUser(); 
				String reportId = makeReportId( request.getRequestURI() );
				webMessages = subscriptionRequest.validate();
				if ( webMessages.isEmpty()) {
					deleteSubscription(conn, response, sessionUser, reportId, subscriptionRequest);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, new SubscriptionAdminResponse(conn,reportId));
				} else {
					data.setWebMessages(webMessages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);		
				}
			} catch ( IllegalArgumentException e ) {
				super.sendNotFound(response);
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());				
				webMessages.addMessage(badField, "Invalid Format");
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (RecordNotFoundException e) {
				super.sendNotFound(response);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}




	private String makeReportId(String requestURI) throws IllegalArgumentException {
		String[] path = StringUtils.split(requestURI, "/");
		String reportId = path[path.length -1 ];
		BatchReports report = BatchReports.valueOf(reportId);
		if ( ! report.adminSubscription() ) {
			throw new IllegalArgumentException(reportId);
		} 
		return reportId;
	}




	private void makeSubscription(Connection conn, HttpServletResponse response, SessionUser sessionUser, String reportId, SubscriptionAdminRequest subscriptionRequest) throws Exception {
		Calendar now = AppUtils.getToday();
		ReportSubscription subscription = new ReportSubscription();
		subscription.setAddedBy(sessionUser.getUserId());
		subscription.setAddedDate(now.getTime());
		subscription.setDivisionId(subscriptionRequest.getDivisionId());
		subscription.setReportId(reportId);
		subscription.setUpdatedBy(sessionUser.getUserId());
		subscription.setUpdatedDate(now.getTime());
		subscription.setUserId(subscriptionRequest.getUserId());
		subscription.insertWithKey(conn);
		conn.commit();
	}




	private void deleteSubscription(Connection conn, HttpServletResponse response, SessionUser sessionUser,
			String reportId, SubscriptionAdminRequest subscriptionRequest) throws Exception {
		try {
			ReportSubscription subscription = new ReportSubscription();
			subscription.setUserId(subscriptionRequest.getUserId());
			subscription.setDivisionId(subscriptionRequest.getDivisionId());
			subscription.setReportId(reportId);
			subscription.delete(conn);
		} catch ( RecordNotFoundException e ) {
			// we don't care
		}
		conn.commit();
		
	}
}
