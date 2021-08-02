package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
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
import com.ansi.scilla.common.utils.Permission;
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
import com.thewebthing.commons.db2.RecordNotFoundException;

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
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			try{
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
			ticketClaim.setDlAmt(  (new BigDecimal(bcrRequest.getDlAmt())).round(MathContext.DECIMAL32)  );
			ticketClaim.setDlExpenses(BigDecimal.ZERO);
			ticketClaim.setEmployeeName(bcrRequest.getEmployee());
			ticketClaim.setHours(BigDecimal.ZERO);
			ticketClaim.setNotes(bcrRequest.getNotes());
//			ticketClaim.setPassthruExpenseType(passthruExpenseType);
			ticketClaim.setServiceType(bcrRequest.getServiceTagId());
			ticketClaim.setTicketId(bcrRequest.getTicketId());
			ticketClaim.setUpdatedBy(sessionUser.getUserId());
			ticketClaim.setUpdatedDate(today.getTime());
			ticketClaim.setVolume(  (new BigDecimal(bcrRequest.getVolumeClaimed())).round(MathContext.DECIMAL32) );
			logger.log(Level.DEBUG, ticketClaim);
			Integer claimId = ticketClaim.insertWithKey(conn);
			
			if ( ! StringUtils.isBlank(bcrRequest.getClaimedEquipment()) ) {
				String[] claimedEquipmentList = StringUtils.split(bcrRequest.getClaimedEquipment(),",");
				for ( String tagId : claimedEquipmentList ) {
					ClaimEquipment claimEquipment = new ClaimEquipment();
					claimEquipment.setClaimId(claimId);
					claimEquipment.setEquipmentId(Integer.valueOf(tagId));
					claimEquipment.setAddedBy(sessionUser.getUserId());
					claimEquipment.setAddedDate(today.getTime());
					claimEquipment.setUpdatedBy(sessionUser.getUserId());
					claimEquipment.setUpdatedDate(today.getTime());
					claimEquipment.insertWithNoKey(conn);
				}
				
			}
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
		String[] claimWeek = bcrRequest.getClaimWeek().split("-");

		TicketClaim ticketClaim = new TicketClaim();
		ticketClaim.setClaimId(claimId);
		ticketClaim.selectOne(conn);
		
		TicketClaim key = new TicketClaim();
		key.setClaimId(claimId);
		
		Calendar today = Calendar.getInstance(new AnsiTime());
		ticketClaim.setClaimId(claimId);
		ticketClaim.setClaimWeek(Integer.valueOf(claimWeek[1]));
		ticketClaim.setClaimYear(bcrRequest.getWorkYear());
		ticketClaim.setDlAmt(  (new BigDecimal(bcrRequest.getDlAmt())).round(MathContext.DECIMAL32)  );
		ticketClaim.setDlExpenses(BigDecimal.ZERO);
		ticketClaim.setEmployeeName(bcrRequest.getEmployee());
		ticketClaim.setHours(BigDecimal.ZERO);
		ticketClaim.setNotes(bcrRequest.getNotes());
//		ticketClaim.setPassthruExpenseType(passthruExpenseType);
		ticketClaim.setServiceType(bcrRequest.getServiceTagId());
		ticketClaim.setTicketId(bcrRequest.getTicketId());
		ticketClaim.setUpdatedBy(sessionUser.getUserId());
		ticketClaim.setUpdatedDate(today.getTime());
		ticketClaim.setVolume(  (new BigDecimal(bcrRequest.getVolumeClaimed())).round(MathContext.DECIMAL32)  );
		logger.log(Level.DEBUG, ticketClaim);
		ticketClaim.update(conn, key);
		
		if ( StringUtils.isBlank(bcrRequest.getClaimedEquipment())) {
			try {
				ClaimEquipment ce = new ClaimEquipment();
				ce.setClaimId(claimId);
				ce.delete(conn);
			} catch ( RecordNotFoundException e) {
				// tried to delete something that wasn't there. We don't care
			}
		} else {
			final String equipmentSql = 
					"if exists (select * from claim_equipment where claim_id=? and equipment_id=?)\n" + 
					"	update claim_equipment set updated_by=?,updated_date=? where claim_id=? and equipment_id=?\n" + 
					"ELSE \n" + 
					"	insert into claim_equipment(claim_id,equipment_id,added_by,added_date,updated_by,updated_date)\n" + 
					"	values (?,?,?,?,?,?)";
			PreparedStatement psUpsert = conn.prepareStatement(equipmentSql);
			PreparedStatement psDelete = conn.prepareStatement("delete from claim_equipment where claim_id=? and equipment_id not in (" + bcrRequest.getClaimedEquipment() + ")");
			for ( String equipmentTag : bcrRequest.getClaimedEquipment().split(",") ) {
				Integer equipmentId = Integer.valueOf(equipmentTag);
				java.sql.Date sessionDate = new java.sql.Date(today.getTime().getTime());
				
				int n = 1;
				//updates
				psUpsert.setInt(n, claimId);
				n++;
				psUpsert.setInt(n, equipmentId);
				n++;
				psUpsert.setInt(n, sessionUser.getUserId());
				n++;
				psUpsert.setDate(n, sessionDate);
				n++;
				psUpsert.setInt(n, claimId);
				n++;
				psUpsert.setInt(n, equipmentId);
				n++;
				//inserts
				psUpsert.setInt(n, claimId);
				n++;
				psUpsert.setInt(n, equipmentId);
				n++;
				psUpsert.setInt(n, sessionUser.getUserId());
				n++;
				psUpsert.setDate(n, sessionDate);
				n++;
				psUpsert.setInt(n, sessionUser.getUserId());
				n++;
				psUpsert.setDate(n, sessionDate);
				n++;
				psUpsert.executeUpdate();
			}
			
			psDelete.setInt(1, claimId);
			psDelete.executeUpdate();
		}

		
		
		conn.commit();
	}





	
	
	
	
	
	
}
