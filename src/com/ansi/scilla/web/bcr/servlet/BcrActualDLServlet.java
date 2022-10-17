package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.bcr.request.BcrActualRequest;
import com.ansi.scilla.web.bcr.response.BudgetControlTotalsResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.SuccessMessage;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payment.response.PaymentResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class BcrActualDLServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			try{
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				String jsonString = super.makeJsonString(request);
				logger.log(Level.DEBUG, jsonString);
				SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
				List<SessionDivision> divisionList = sessionData.getDivisionList();
				BcrActualRequest actualRequest = new BcrActualRequest();
				AppUtils.json2object(jsonString, actualRequest);
//				final SimpleDateFormat sdfx = new SimpleDateFormat("MM/dd/yyyy E HH:mm:ss.S");
				SessionUser sessionUser = sessionData.getUser();
				WebMessages webMessages = actualRequest.validate(conn, sessionUser);				
				BudgetControlTotalsResponse  data = new BudgetControlTotalsResponse();
				if ( webMessages.isEmpty() ) {
					doUpdate(conn, actualRequest, sessionUser);
					data = new BudgetControlTotalsResponse(conn, sessionUser.getUserId(), divisionList, actualRequest.getDivisionId(), actualRequest.getClaimYear(), actualRequest.getClaimWeeks());
					super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
				} else {
					data.setWebMessages(webMessages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
				}
				
				
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				PaymentResponse data = new PaymentResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
				super.sendForbidden(response);			
			}
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_READ);
			SessionUser sessionUser = sessionData.getUser();
			List<SessionDivision> divisionList = sessionData.getDivisionList();
			Integer divisionId = Integer.valueOf(request.getParameter("divisionId"));
			Integer workYear = Integer.valueOf(request.getParameter("workYear"));
			String workWeek = request.getParameter("workWeek");  // comma-delimited list of work weeks.
			logger.log(Level.DEBUG, "Parms: " + divisionId + " " + workYear + " " + workWeek);
			BudgetControlTotalsResponse data = new BudgetControlTotalsResponse(conn, sessionUser.getUserId(), divisionList, divisionId, workYear, workWeek);
			WebMessages webMessages = new SuccessMessage();
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
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

	private void doUpdate(Connection conn, BcrActualRequest actualRequest, SessionUser sessionUser) throws Exception {
		String fieldName = null;
		PreparedStatement ps = null;
		Calendar today = Calendar.getInstance();
		java.sql.Date now = new java.sql.Date(today.getTime().getTime());
		if ( actualRequest.getType().equalsIgnoreCase(BcrActualRequest.TYPE_IS_ACTUAL)) {
			fieldName = "actual_dl";
		} else if ( actualRequest.getType().equalsIgnoreCase(BcrActualRequest.TYPE_IS_OM)) {
			fieldName = "om_dl";
		} else {
			// this should have been caught in validation, but belt & suspenders
			throw new Exception("Invalid Update Type: " + actualRequest.getType());
		}
		if ( actualExists(conn, actualRequest) ) {
			String sql = "update actual_direct_labor_totals set " + fieldName + "=?,updated_by=?,updated_date=? "
					+ "where division_id=? and claim_year=? and claim_week=?";
			logger.log(Level.DEBUG, sql);
			ps = conn.prepareStatement(sql);
			ps.setDouble(1, actualRequest.getValue());
			ps.setInt(2, sessionUser.getUserId());
			ps.setDate(3, now);
			ps.setInt(4, actualRequest.getDivisionId());
			ps.setInt(5, actualRequest.getClaimYear());
			ps.setInt(6, actualRequest.getClaimWeek());
		} else {
			String sql = "insert into actual_direct_labor_totals (division_id, claim_year, claim_week, actual_dl, om_dl, added_by, added_date, updated_by, updated_date) values (?,?,?,?,?,?,?,?,?)";
			logger.log(Level.DEBUG, sql);
			ps = conn.prepareStatement(sql);
			ps.setInt(1, actualRequest.getDivisionId());
			ps.setInt(2, actualRequest.getClaimYear());
			ps.setInt(3, actualRequest.getClaimWeek());
			ps.setDouble(4, actualRequest.getType().equalsIgnoreCase(BcrActualRequest.TYPE_IS_ACTUAL) ? actualRequest.getValue() : 0.0D);
			ps.setDouble(5, actualRequest.getType().equalsIgnoreCase(BcrActualRequest.TYPE_IS_OM) ? actualRequest.getValue() : 0.0D);
			ps.setInt(6, sessionUser.getUserId());
			ps.setDate(7, now);
			ps.setInt(8, sessionUser.getUserId());
			ps.setDate(9, now);
		}
		ps.executeUpdate();
		conn.commit();
	}

	
	private boolean actualExists(Connection conn, BcrActualRequest actualRequest) throws SQLException {
		boolean exists = false;
		String sql = "select count(*) as record_count from actual_direct_labor_totals where division_id=? and claim_year=? and claim_week=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, actualRequest.getDivisionId());
		ps.setInt(2, actualRequest.getClaimYear());
		ps.setInt(3, actualRequest.getClaimWeek());
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			if ( rs.getInt("record_count") > 0 ) {
				exists = true;
			}
		}
		return exists;
	}

	
}
