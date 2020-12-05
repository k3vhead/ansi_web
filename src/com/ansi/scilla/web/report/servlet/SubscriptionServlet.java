package com.ansi.scilla.web.report.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.ReportSubscription;
import com.ansi.scilla.common.organization.Div;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.SuccessMessage;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.report.common.BatchReports;
import com.ansi.scilla.web.report.request.AllReportType;
import com.ansi.scilla.web.report.request.SubscriptionRequest;
import com.ansi.scilla.web.report.response.SubscriptionResponse2;
import com.ansi.scilla.web.report.response.SubscriptionUpdateResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class SubscriptionServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "subscription";
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
				
		Connection conn = null;
//		AnsiURL url = null;
		WebMessages messages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			SessionData sessionData = AppUtils.validateSession(request, Permission.REPORT_SUBSCRIPTION_READ);
//			url = new AnsiURL(request, REALM, (String[])null, false);				
			SubscriptionResponse2 data = new SubscriptionResponse2(conn, sessionData.getUser().getUserId(), sessionData.getUserPermissionList());
			data.setWebMessages(messages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
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
			SubscriptionUpdateResponse data = new SubscriptionUpdateResponse();
			WebMessages webMessages = new WebMessages();

			
			try{
				SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
				AppUtils.json2object(jsonString, subscriptionRequest);
//				logger.log(Level.DEBUG, subscriptionRequest);
				SessionUser sessionUser = sessionData.getUser(); 
				
				subscriptionRequest.validate(conn, webMessages);
				if ( webMessages.isEmpty()) {
					doSubscription(conn, subscriptionRequest, sessionUser, response);
				} else {
					data.setWebMessages(webMessages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);		
				}

			}  catch ( InvalidFormatException e ) {
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




	private void doSubscription(Connection conn, SubscriptionRequest subscriptionRequest, SessionUser user, HttpServletResponse response) throws Exception {
		logger.log(Level.DEBUG, subscriptionRequest);
		
		if ( subscriptionExists(conn, subscriptionRequest, user) ) {
			if ( subscriptionRequest.getSubscribe().booleanValue() == true ) {
				// subscription already exists -- no need to do anything
				logger.log(Level.DEBUG, "Skipping duplicate add");
			} else {
				deleteSubscription(conn, subscriptionRequest, user);
			}
		} else {
			if ( subscriptionRequest.getSubscribe().booleanValue() == true ) {
				addSubscription(conn, subscriptionRequest, user);
			} else {
				// trying to delete non-existant description -- no need to do anything
				logger.log(Level.DEBUG, "Skipping duplicate delete");
			}
		}
		
		
		conn.commit();
		BatchReports updatedReport = BatchReports.valueOf(subscriptionRequest.getReportId());
		List<BatchReports> updatedReports = Arrays.asList(new BatchReports[] {updatedReport});
		SubscriptionUpdateResponse data = new SubscriptionUpdateResponse(updatedReports);		
		data.setWebMessages(new SuccessMessage());
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);

		
	}
	
	
	
	private boolean subscriptionExists(Connection conn, SubscriptionRequest req, SessionUser user) throws SQLException {
		boolean subscriptionExists = false;
		PreparedStatement ps = null;
		String sql = "select count(*) as subscription_count from report_subscription where report_id=? and user_id=? ";
		if ( req.getDivisionId() == null && req.getGroupId() == null ) {
			ps = conn.prepareStatement(sql);	
			ps.setString(1, req.getReportId());
			ps.setInt(2, user.getUserId());
		} else {
			if ( req.getDivisionId() != null ) {
				ps = conn.prepareStatement(sql+ "and division_id=?");	
				ps.setString(1, req.getReportId());
				ps.setInt(2, user.getUserId());
				ps.setInt(3, req.getDivisionId());
			} else if ( req.getGroupId() != null ) {
				ps = conn.prepareStatement(sql+ "and group_id=?");	
				ps.setString(1, req.getReportId());
				ps.setInt(2, user.getUserId());
				ps.setInt(3, req.getGroupId());
			} else {
				// this can't happen. We'll code for it anyway
				throw new RuntimeException("This can't happen");
			}
		}
		
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			subscriptionExists = rs.getInt("subscription_count") > 0;					
		}
		rs.close();
		logger.log(Level.DEBUG, "SubscriptionExists: " + subscriptionExists);
		return subscriptionExists;
	}




	private void deleteSubscription(Connection conn, SubscriptionRequest req, SessionUser user) throws SQLException {
		PreparedStatement ps = null;
		String sql = "delete from report_subscription where report_id=? and user_id=? ";
		if ( req.getDivisionId() == null && req.getGroupId() == null ) {
			ps = conn.prepareStatement(sql);	
			ps.setString(1, req.getReportId());
			ps.setInt(2, user.getUserId());
		} else {
			if ( req.getDivisionId() != null ) {
				ps = conn.prepareStatement(sql+ "and division_id=?");	
				ps.setString(1, req.getReportId());
				ps.setInt(2, user.getUserId());
				ps.setInt(3, req.getDivisionId());
			} else if ( req.getGroupId() != null ) {
				ps = conn.prepareStatement(sql+ "and group_id=?");	
				ps.setString(1, req.getReportId());
				ps.setInt(2, user.getUserId());
				ps.setInt(3, req.getGroupId());
			} else {
				// this can't happen. We'll code for it anyway
				throw new RuntimeException("This can't happen");
			}
		}
		logger.log(Level.DEBUG, sql);
		ps.executeUpdate();
		
	}




	private void addSubscription(Connection conn, SubscriptionRequest req, SessionUser user) throws Exception {
		logger.log(Level.DEBUG, "Doing Add: " + req.getReportId() + "\t" + req.getDivisionId() + "\t" + req.getGroupId());
		
		Date today = new Date();
		
		ReportSubscription subscription = new ReportSubscription();
		subscription.setUserId(user.getUserId());
		subscription.setReportId(req.getReportId());
		if ( req.getDivisionId() != null ) {
			subscription.setDivisionId(req.getDivisionId());
		}
		if ( req.getDivisionId() != null ) {
			subscription.setDivisionId(req.getDivisionId());
		} else if ( req.getGroupId() != null ) {
			subscription.setGroupId(req.getGroupId());
		}
		
		logger.log(Level.DEBUG, subscription);		
		subscription.setAddedBy(user.getUserId());
		subscription.setUpdatedBy(user.getUserId());
		subscription.setAddedDate(today);
		subscription.setUpdatedDate(today);
		subscription.insertWithKey(conn);
		logger.log(Level.DEBUG, "added subscription");
	}






	

	public class AllReportTypePredicate implements Predicate<BatchReports> {

		private AllReportType allReportType;
		
		public AllReportTypePredicate(AllReportType allReportType) {
			this.allReportType = allReportType;
		}
		@Override
		public boolean evaluate(BatchReports arg0) {
			Boolean isTrue = null;
			switch (allReportType) {
				case COMPANY:
					isTrue = arg0.isSummaryReport();
					break;
				case DIVISION:
					isTrue = arg0.isDivisionReport();
					break;
				case REGION:
					isTrue = arg0.isSummaryReport();
					break;
				case EXECUTIVE:
					isTrue = arg0.isAllAnsiReport();
					break;
				default:
					isTrue = false;
					break;
				
			}
			return isTrue.booleanValue();
		}		
	}
	
	
	public class Div2Id implements Transformer<Div, Integer> {

		@Override
		public Integer transform(Div arg0) {
			return arg0.getDivisionId();
		}
		
	}
}
