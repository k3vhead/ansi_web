package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.ClaimEquipment;
import com.ansi.scilla.common.db.TicketClaim;
import com.ansi.scilla.web.bcr.request.BcrExpenseRequest;
import com.ansi.scilla.web.bcr.request.BcrTicketClaimRequest;
import com.ansi.scilla.web.bcr.response.BcrTicketClaimResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payment.response.PaymentResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class BcrExpenseServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			try{
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				String jsonString = super.makeJsonString(request);
				logger.log(Level.DEBUG, jsonString);
				SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
				BcrExpenseRequest bcrExpenseRequest = new BcrExpenseRequest();
				AppUtils.json2object(jsonString, bcrExpenseRequest);
				SessionUser sessionUser = sessionData.getUser();
				List<SessionDivision> divisionList = sessionData.getDivisionList();
				
				logger.log(Level.DEBUG, request.getRequestURI());
				Integer index = request.getRequestURI().indexOf(BcrServlet.EXPENSE);
				String claimId = request.getRequestURI().substring(index + BcrServlet.EXPENSE.length());
				claimId = claimId.startsWith("/") ? claimId.substring(1) : claimId;
				
				BcrTicketClaimResponse data = new BcrTicketClaimResponse();
				
				if ( StringUtils.isBlank(claimId) ) {
					// this is a new expense
					webMessages = bcrExpenseRequest.validateAdd(conn);
					if ( webMessages.isEmpty() ) {
						Integer newClaimId = insertExpense(conn, bcrExpenseRequest, sessionUser);						
						data = BcrTicketClaimResponse.fromClaim(conn, sessionUser.getUserId(), divisionList, bcrExpenseRequest.getDivisionId(), bcrExpenseRequest.getWorkYear(), bcrExpenseRequest.getWorkWeeks(), newClaimId);
						super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
					} else {
						data.setWebMessages(webMessages);
						super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
					}
				} else if ( StringUtils.isNumeric(claimId) ) {
					// this is an update to an existing expense
					Integer claim =  Integer.valueOf(claimId);
					webMessages = bcrExpenseRequest.validateUpdate(conn, claim);
					if ( webMessages.isEmpty() ) {
						updateExpense(conn, claim, bcrExpenseRequest, sessionUser);
						data = BcrTicketClaimResponse.fromClaim(conn, sessionUser.getUserId(), divisionList, bcrExpenseRequest.getDivisionId(), bcrExpenseRequest.getWorkYear(), bcrExpenseRequest.getWorkWeeks(), claim);
						super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
					} else {
						if ( webMessages.containsKey(BcrTicketClaimRequest.CLAIM_ID)) {
							super.sendNotFound(response);
						} else {
							data.setWebMessages(webMessages);
							super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
						}						
					}	
				} else {
					// somebody fubar'd
					super.sendNotFound(response);
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
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			try{
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				String jsonString = super.makeJsonString(request);
				logger.log(Level.DEBUG, jsonString);
				SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
				BcrExpenseRequest bcrExpenseRequest = new BcrExpenseRequest();
				AppUtils.json2object(jsonString, bcrExpenseRequest);
				SessionUser sessionUser = sessionData.getUser();
				List<SessionDivision> divisionList = sessionData.getDivisionList();
				
				logger.log(Level.DEBUG, request.getRequestURI());
				Integer index = request.getRequestURI().indexOf(BcrServlet.EXPENSE);
				String claimId = request.getRequestURI().substring(index + BcrServlet.EXPENSE.length());
				claimId = claimId.startsWith("/") ? claimId.substring(1) : claimId;
				
				BcrTicketClaimResponse data = new BcrTicketClaimResponse();
				
				if ( StringUtils.isBlank(claimId) ) {
					super.sendNotFound(response);
				} else if ( StringUtils.isNumeric(claimId) ) {
					webMessages = BcrExpenseRequest.validateDelete(conn, Integer.valueOf(claimId));
					if ( webMessages.isEmpty() ) {
						// the claim response is predicated on having a claim id. So we get the response based on the claim we're
						// deleting, then scrub that info from the response object.
						TicketClaim ticketClaim = new TicketClaim();
						ticketClaim.setClaimId(Integer.valueOf(claimId));
						ticketClaim.selectOne(conn);
						
						data = BcrTicketClaimResponse.fromClaim(conn, sessionUser.getUserId(), divisionList, bcrExpenseRequest.getDivisionId(), bcrExpenseRequest.getWorkYear(), bcrExpenseRequest.getWorkWeeks(), Integer.valueOf(claimId));
						deleteExpense(conn, Integer.valueOf(claimId));
						data.scrubClaim(conn, ticketClaim);
						super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
					} else {
						super.sendNotFound(response); // we've got a bad claim id
					}					
				} else {
					// somebody fubar'd
					super.sendNotFound(response);
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




	private Integer insertExpense(Connection conn, BcrExpenseRequest bcrExpenseRequest, SessionUser sessionUser) throws Exception {
			logger.log(Level.DEBUG, "Inserting a Passthru Expense");
			TicketClaim ticketClaim = new TicketClaim();
			Calendar today = Calendar.getInstance(new AnsiTime());
			
			
			String[] claimWeek = bcrExpenseRequest.getClaimWeek().split("-");
			
			
			ticketClaim.setAddedBy(sessionUser.getUserId());
			ticketClaim.setAddedDate(today.getTime());
			ticketClaim.setClaimWeek(Integer.valueOf(claimWeek[1]));
			ticketClaim.setClaimYear(Integer.valueOf(claimWeek[0]));
			ticketClaim.setDlAmt(BigDecimal.ZERO);
			ticketClaim.setDlExpenses(BigDecimal.ZERO);
	//		ticketClaim.setEmployeeName(employeeName);
			ticketClaim.setHours(BigDecimal.ZERO);
			ticketClaim.setNotes(bcrExpenseRequest.getNotes());
			ticketClaim.setPassthruExpenseType(bcrExpenseRequest.getExpenseType());
			ticketClaim.setPassthruExpenseVolume(new BigDecimal(bcrExpenseRequest.getVolume()));
			ticketClaim.setServiceType(bcrExpenseRequest.getServiceTagId());
			ticketClaim.setTicketId(bcrExpenseRequest.getTicketId());
			ticketClaim.setUpdatedBy(sessionUser.getUserId());
			ticketClaim.setUpdatedDate(today.getTime());
			ticketClaim.setVolume(BigDecimal.ZERO);
			
			Integer claimId = ticketClaim.insertWithKey(conn);
			conn.commit();
			return claimId;
		}




	private void updateExpense(Connection conn, Integer claimId, BcrExpenseRequest bcrExpenseRequest, SessionUser sessionUser) throws Exception {
		Calendar today = Calendar.getInstance(new AnsiTime());
		TicketClaim ticketClaim = new TicketClaim();
		ticketClaim.setClaimId(claimId);
		ticketClaim.selectOne(conn);
		
		ticketClaim.setPassthruExpenseType(bcrExpenseRequest.getExpenseType());
		ticketClaim.setPassthruExpenseVolume(new BigDecimal(bcrExpenseRequest.getVolume()));
		ticketClaim.setNotes(bcrExpenseRequest.getNotes());
		ticketClaim.setUpdatedBy(sessionUser.getUserId());
		ticketClaim.setUpdatedDate(today.getTime());
		
		
		TicketClaim key = new TicketClaim();
		key.setClaimId(claimId);
		ticketClaim.update(conn, key);
		conn.commit();
	}




	private void deleteExpense(Connection conn, Integer claimId) throws Exception {
		try {
			ClaimEquipment claimEquipment = new ClaimEquipment();
			claimEquipment.setClaimId(claimId);
			claimEquipment.delete(conn);
		} catch ( RecordNotFoundException e) {
			// trying to delete something that isn't there; we don't care
		}
		
		TicketClaim ticketClaim = new TicketClaim();
		ticketClaim.setClaimId(claimId);
		ticketClaim.delete(conn);
		conn.commit();
		
	}
}
