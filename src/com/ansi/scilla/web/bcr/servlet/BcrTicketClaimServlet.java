package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.TicketClaim;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.bcr.common.BcrUtils;
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
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

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
//		WebMessages webMessages = new WebMessages();
		String realm = BcrServlet.REALM + "/" + BcrServlet.TICKET_CLAIM;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			try{
				String jsonString = super.makeJsonString(request);
				logger.log(Level.DEBUG, jsonString);
				SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
				BcrTicketClaimRequest bcrRequest = new BcrTicketClaimRequest();
				AppUtils.json2object(jsonString, bcrRequest);
//				final SimpleDateFormat sdfx = new SimpleDateFormat("MM/dd/yyyy E HH:mm:ss.S");
				SessionUser sessionUser = sessionData.getUser();
				List<SessionDivision> divisionList = sessionData.getDivisionList();
				
				int index = request.getRequestURI().indexOf(realm);
				logger.log(Level.DEBUG, request.getRequestURI().substring(index + realm.length()));
				String claim = request.getRequestURI().substring(index + realm.length());

						
				if ( StringUtils.isBlank(claim) || claim.equals("/") ) {
					logger.log(Level.DEBUG, "We're trying to add a new D/L Claim");
					processAdd(conn, response, sessionUser, divisionList, bcrRequest);					
				} else {
					claim = claim.startsWith("/") ? claim.substring(1) : claim;
					logger.log(Level.DEBUG, "We're trying to update an existing D/L Claim: " + claim);
					if ( StringUtils.isNumeric(claim) ) {
						processUpdate(conn, response, claim, sessionUser, divisionList, bcrRequest);
					} else {
						logger.log(Level.DEBUG, "Not a good D/L Claim: " + claim);
						super.sendNotFound(response);
					}
				}				
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
				super.sendForbidden(response);		
			} catch ( InvalidFormatException e) {
				String badField = super.findBadField(e.toString());
                WebMessages messages = new WebMessages();
                messages.addMessage(badField, "Invalid Format");
                BcrTicketClaimResponse data = new BcrTicketClaimResponse();
                data.setWebMessages(messages);
                super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			}	

		} catch ( Exception e) {
			AppUtils.logException(e);
			if ( conn != null ) {
				AppUtils.rollbackQuiet(conn);
			}
			throw new ServletException(e);
		} finally {
			if ( conn != null ) {
				AppUtils.closeQuiet(conn);
			}
		}
	}





	


	private void processAdd(Connection conn, HttpServletResponse response, SessionUser sessionUser, List<SessionDivision> divisionList, BcrTicketClaimRequest bcrRequest) throws Exception {
		BcrTicketClaimResponse data = new BcrTicketClaimResponse();
		
		WebMessages webMessages = bcrRequest.validateAdd(conn, sessionUser);
		if ( webMessages.isEmpty() ) {
			Integer claimId = BcrUtils.addNewLaborClaim(conn, bcrRequest, sessionUser);
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
			webMessages = bcrRequest.validateUpdate(conn, sessionUser);
			if ( webMessages.isEmpty() ) {
				updateClaim(conn, claimId, bcrRequest, sessionUser);
				data = BcrTicketClaimResponse.fromClaim(conn, sessionUser.getUserId(), divisionList, bcrRequest.getDivisionId(), bcrRequest.getWorkYear(), bcrRequest.getWorkWeeks(), claimId);
				data.setWebMessages(new SuccessMessage());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
			} else {
				data.setWebMessages(webMessages);								
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			}
		} else {
			logger.log(Level.DEBUG, "could't find an existing D/L Claim: " + claim);
			super.sendNotFound(response);
		}
		
	}





	private void updateClaim(Connection conn, Integer claimId, BcrTicketClaimRequest bcrRequest, SessionUser sessionUser) throws Exception {
		BcrUtils.updateLaborClaim(conn, claimId, bcrRequest, sessionUser);
		conn.commit();
	}





	
	
	
	
	
	
}
