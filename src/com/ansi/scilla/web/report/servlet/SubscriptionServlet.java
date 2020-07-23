package com.ansi.scilla.web.report.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
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
import com.ansi.scilla.web.report.common.SubscriptionUtils;
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
			SubscriptionResponse2 data = new SubscriptionResponse2(conn, sessionData.getUser().getUserId());
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
		List<BatchReports> updatedReports = new ArrayList<BatchReports>(); // this is the reports we have actually updated		
		List<BatchReports> reportsToUpdate = new ArrayList<BatchReports>(); // this is the reports that we plan to update
		
		AllReportType allReportType = StringUtils.isBlank(subscriptionRequest.getAllReportType()) ? AllReportType.NONE : AllReportType.valueOf(subscriptionRequest.getAllReportType());
		if ( allReportType.equals(AllReportType.NONE) ) {
			// This is a single report request
			reportsToUpdate.add( BatchReports.valueOf(subscriptionRequest.getReportId()) );
		} else {
			// this is an "all reports" request
			allReportType = AllReportType.valueOf(subscriptionRequest.getAllReportType());
			reportsToUpdate = (List<BatchReports>) CollectionUtils.select(Arrays.asList(BatchReports.values()), new AllReportTypePredicate(allReportType));
		}

		List<Integer> divisionsToUpdate = new ArrayList<Integer>();
		if ( subscriptionRequest.getAllDivisions().booleanValue() == true ) {
			divisionsToUpdate = (List<Integer>) CollectionUtils.collect(SubscriptionUtils.makeDivisionList(conn, user.getUserId()), new Div2Id());
		} else {
			divisionsToUpdate.add(subscriptionRequest.getDivisionId());
		}
		
		for ( BatchReports report : reportsToUpdate ) {
			logger.log(Level.DEBUG, "Updating report: " + report.abbreviation());
			for ( Integer div : divisionsToUpdate ) {
				logger.log(Level.DEBUG, "Updating report: " + report.abbreviation() + " for div " + div);
			}
		}
		
		for ( BatchReports report : reportsToUpdate ) {
			
			if ( report.isDivisionReport() ) {
				for ( Integer divisionId : divisionsToUpdate ) {
					if ( doUpdate(conn, report, divisionId, user, subscriptionRequest.getSubscribe()) && ! updatedReports.contains(report) ) {
						updatedReports.add(report);
					}
				}
			} else {
				if ( doUpdate(conn, report, (Integer)null, user, subscriptionRequest.getSubscribe()) && ! updatedReports.contains(report) ) {
					updatedReports.add(report);
				}
			}
				
		}
		conn.commit();
		SubscriptionUpdateResponse data = new SubscriptionUpdateResponse(updatedReports);		
		data.setWebMessages(new SuccessMessage());
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);

		
	}
	
	private boolean doUpdate(Connection conn, BatchReports report, Integer divisionId, SessionUser user, Boolean subscribe) throws Exception {
		logger.log(Level.DEBUG, "Doing Update: " + report.abbreviation() + "\t" + divisionId);
		
		boolean itWasUpdated = false;
		Date today = new Date();
		
		ReportSubscription subscription = new ReportSubscription();
		subscription.setUserId(user.getUserId());
		if ( divisionId != null ) {
			subscription.setDivisionId(divisionId);
		}
		subscription.setReportId(report.name());
		
		logger.log(Level.DEBUG, subscription);
		if ( subscribe.booleanValue() == true ) {
			try {
				subscription.selectOne(conn);
				// trying to add something that's already there
				logger.log(Level.DEBUG, "trying to add something that's already there");
			} catch ( RecordNotFoundException e) {
				subscription.setAddedBy(user.getUserId());
				subscription.setUpdatedBy(user.getUserId());
				subscription.setAddedDate(today);
				subscription.setUpdatedDate(today);
				subscription.insertWithKey(conn);
				itWasUpdated = true;
				logger.log(Level.DEBUG, "added subscription");
			}
		} else {
			try {
				subscription.delete(conn);
				itWasUpdated = true;
				logger.log(Level.DEBUG, "added subscription");
			} catch ( RecordNotFoundException e ) {
				// we don't care - deleting something that doesn't exist
				logger.log(Level.DEBUG, "deleting something that doesn't exist");
			}
		}
		return itWasUpdated;
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
