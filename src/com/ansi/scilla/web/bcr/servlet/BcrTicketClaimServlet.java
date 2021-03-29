package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.TicketClaim;
import com.ansi.scilla.common.utils.AnsiDateUtils;
import com.ansi.scilla.web.bcr.request.BcrTicketClaimRequest;
import com.ansi.scilla.web.bcr.response.BcrTicketClaimResponse;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.SuccessMessage;
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

public class BcrTicketClaimServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_READ);
			SessionUser sessionUser = sessionData.getUser();
			List<SessionDivision> divisionList = sessionData.getDivisionList();
			Integer divisionId = Integer.valueOf(request.getParameter("divisionId"));
			Integer workYear = Integer.valueOf(request.getParameter("workYear"));
			String workWeeks = request.getParameter("workWeeks");  // comma-delimited list of work weeks.
			logger.log(Level.DEBUG, "Parms: " + divisionId + " " + workYear + " " + workWeeks);
			String[] uriPath = request.getRequestURI().split("/");
			String claim = uriPath[uriPath.length-1];
			if ( StringUtils.isNumeric(claim)) {
				Integer claimId = Integer.valueOf(claim);
				RequestValidator.validateId(conn, webMessages, TicketClaim.TABLE, TicketClaim.CLAIM_ID, WebMessages.GLOBAL_MESSAGE, claimId, true, null);
				if ( webMessages.isEmpty() ) {
					BcrTicketClaimResponse data = BcrTicketClaimResponse.fromClaim(conn, sessionUser.getUserId(), divisionList, divisionId, workYear, workWeeks, claimId);
					data.setWebMessages(new SuccessMessage());
					super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
				} else {
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

	
	
	
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		String realm = BcrServlet.REALM + "/" + BcrServlet.TICKET_CLAIM;
		try {
			try{
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				String jsonString = super.makeJsonString(request);
				logger.log(Level.DEBUG, jsonString);
				SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
				BcrTicketClaimRequest bcrRequest = new BcrTicketClaimRequest();
				AppUtils.json2object(jsonString, bcrRequest);
				final SimpleDateFormat sdfx = new SimpleDateFormat("MM/dd/yyyy E HH:mm:ss.S");
				SessionUser sessionUser = sessionData.getUser();
				List<SessionDivision> divisionList = sessionData.getDivisionList();
				
				int index = request.getRequestURI().indexOf(realm);
				logger.log(Level.DEBUG, request.getRequestURI().substring(index + realm.length()));
				String claim = request.getRequestURI().substring(index + realm.length());

						
				if ( StringUtils.isBlank(claim) ) {
					logger.log(Level.DEBUG, "We're trying to add a new D/L Claim");
					processAdd(conn, response, sessionUser, divisionList, bcrRequest);					
				} else {
					logger.log(Level.DEBUG, "We're trying to update an existing D/L Claim");
					if ( StringUtils.isNumeric(claim) ) {
						processUpdate(conn, response, claim, sessionUser, divisionList, bcrRequest);
					} else {
						super.sendNotFound(response);
					}
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

		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}





	


	private void processAdd(Connection conn, HttpServletResponse response, SessionUser sessionUser, List<SessionDivision> divisionList, BcrTicketClaimRequest bcrRequest) throws Exception {
		BcrTicketClaimResponse data = new BcrTicketClaimResponse();
		String[] claimWeek = bcrRequest.getClaimWeek().split("-");
		
		WebMessages webMessages = bcrRequest.validateAdd(conn, sessionUser);
		if ( webMessages.isEmpty() ) {
			Calendar today = Calendar.getInstance(new AnsiTime());
			TicketClaim ticketClaim = new TicketClaim();
			ticketClaim.setAddedBy(sessionUser.getUserId());
			ticketClaim.setAddedDate(today.getTime());
//			ticketClaim.setClaimId(claimId);
			ticketClaim.setClaimWeek(Integer.valueOf(claimWeek[1]));
			ticketClaim.setClaimYear(bcrRequest.getWorkYear());
			ticketClaim.setDlAmt(new BigDecimal(bcrRequest.getDlAmt()));
			ticketClaim.setDlExpenses(BigDecimal.ZERO);
			ticketClaim.setEmployeeName(bcrRequest.getEmployee());
			ticketClaim.setHours(BigDecimal.ZERO);
			ticketClaim.setNotes(bcrRequest.getNotes());
//			ticketClaim.setPassthruExpenseType(passthruExpenseType);
			ticketClaim.setServiceType(bcrRequest.getServiceTagId());
			ticketClaim.setTicketId(bcrRequest.getTicketId());
			ticketClaim.setUpdatedBy(sessionUser.getUserId());
			ticketClaim.setUpdatedDate(today.getTime());
			ticketClaim.setVolume(new BigDecimal(bcrRequest.getVolumeClaimed()));
			logger.log(Level.DEBUG, ticketClaim);
			Integer claimId = ticketClaim.insertWithKey(conn);
			conn.commit();
			data = BcrTicketClaimResponse.fromClaim(conn, sessionUser.getUserId(), divisionList, bcrRequest.getDivisionId(), bcrRequest.getWorkYear(), bcrRequest.getWorkWeeks(), claimId);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		} else {			
			data.setWebMessages(webMessages);								
			super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
		}		
	}





	private void processUpdate(Connection conn, HttpServletResponse response, String claim, SessionUser sessionUser, List<SessionDivision> divisionList, BcrTicketClaimRequest bcrRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		BcrTicketClaimResponse data = new BcrTicketClaimResponse();

		Integer claimId = Integer.valueOf(claim);
		RequestValidator.validateId(conn, webMessages, TicketClaim.TABLE, TicketClaim.CLAIM_ID, WebMessages.GLOBAL_MESSAGE, claimId, true, null);
		if ( webMessages.isEmpty() ) {
			webMessages = bcrRequest.validateUpdate(conn, claimId);
			if ( webMessages.isEmpty() ) {
				// do the update
				data = BcrTicketClaimResponse.fromClaim(conn, sessionUser.getUserId(), divisionList, bcrRequest.getDivisionId(), bcrRequest.getWorkYear(), bcrRequest.getWorkWeeks(), claimId);
				data.setWebMessages(new SuccessMessage());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
			} else {
				data.setWebMessages(webMessages);								
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			}
		} else {
			super.sendNotFound(response);
		}
		
	}





	





	private void updateClaim(Connection conn, Integer ticketId, BcrTicketClaimRequest bcrTicketRequest,
			SessionUser sessionUser) throws Exception {
		logger.log(Level.DEBUG, "Updating Claim " + ticketId + "\t" + bcrTicketRequest.getClaimId());
		logger.log(Level.DEBUG, bcrTicketRequest.toJson());
		
		TicketClaim ticketClaim = new TicketClaim();
		ticketClaim.setClaimId(bcrTicketRequest.getClaimId());
		ticketClaim.selectOne(conn);
		
		Date today = AnsiDateUtils.getToday();
		TicketClaim key = new TicketClaim();
		key.setClaimId(bcrTicketRequest.getClaimId());
		
		String[] claimDate = bcrTicketRequest.getClaimWeek().split("-");

		/** Ticket claim **/
		//private Integer claimId;
		//private Integer ticketId;
		//private Integer serviceType;
		ticketClaim.setClaimYear(Integer.valueOf(claimDate[0]));
		ticketClaim.setClaimWeek(Integer.valueOf(claimDate[1]));
		ticketClaim.setVolume(new BigDecimal(bcrTicketRequest.getTotalVolume()));
		ticketClaim.setDlAmt(new BigDecimal(bcrTicketRequest.getDlAmt()));
		//private BigDecimal dlExpenses;
		//private BigDecimal passthruExpenseVolume;
		//private String passthruExpenseType;
		//private BigDecimal hours;
		ticketClaim.setNotes(bcrTicketRequest.getNotes());
		ticketClaim.setEmployeeName(bcrTicketRequest.getEmployee());
		ticketClaim.setUpdatedBy(sessionUser.getUserId());
		ticketClaim.setUpdatedDate(today);
		
		
		
		/** bcr ticket request **/
		//private Integer ticketId;
		//private Double totalVolume;
		//private Double billedAmount;
		//private Integer claimId;
		

		logger.log(Level.DEBUG, ticketClaim);
		ticketClaim.update(conn, key);
		conn.commit();
	}





	
	
	
	
	
	
}
