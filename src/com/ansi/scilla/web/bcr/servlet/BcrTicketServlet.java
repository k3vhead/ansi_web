package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.utils.WorkYear;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.bcr.request.BcrTicketRequest;
import com.ansi.scilla.web.bcr.request.BcrTicketRequestType;
import com.ansi.scilla.web.bcr.response.BcrTicketResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payment.response.PaymentResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

/**
 * Handle adds and updates for a ticket. This usually means crossing multiple claims.
 * 
 * @author dclewis
 *
 */
public class BcrTicketServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doGet(request, response);;
	}

	
	
	

	/**
	 * What we're expecting: http://&lt;domain &amp; path&gt;/bcr/ticket/nnn/xxx
	 * where nnn is a ticket number and xxx is a &quote;key&quot; from BcrTicketRequestType
	 * The ticket request type will tell us what inputs to expect, validations, and processing.
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			String[] uri = request.getRequestURI().split("/");
			BcrTicketRequestType requestType = BcrTicketRequestType.lookup(uri[uri.length-1]);
			Integer ticketId = Integer.valueOf(uri[uri.length-2]);
			
			try{
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				String jsonString = super.makeJsonString(request);
				logger.log(Level.DEBUG, jsonString);
				SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
				BcrTicketRequest bcrTicketRequest = new BcrTicketRequest();
				AppUtils.json2object(jsonString, bcrTicketRequest);
				SessionUser sessionUser = sessionData.getUser();
//				List<SessionDivision> divisionList = sessionData.getDivisionList();

				

				switch ( requestType ) {
				case CLAIM_WEEK:
					doClaimWeek(conn, response, sessionUser, ticketId, bcrTicketRequest);
					break;
				case DL:
					doDirectLabor(conn, response, sessionUser, ticketId, bcrTicketRequest);
					break;
				case EXPENSE:
					doExpense(conn, response, sessionUser, ticketId, bcrTicketRequest);
					break;
				case TOTAL_VOLUME:
					doTotalVolume(conn, response, sessionUser, ticketId, bcrTicketRequest);
					break;
				default:
					throw new Exception("Invalid BCR Ticket request type");				
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
		} catch ( ResourceNotFoundException | NumberFormatException e) {
			// handles non-numeric ticket ID, and invalid request type
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}





	


	private void doClaimWeek(Connection conn, HttpServletResponse response, SessionUser sessionUser, Integer ticketId, BcrTicketRequest bcrTicketRequest) throws Exception {
		BcrTicketResponse data = new BcrTicketResponse();
		WebMessages webMessages = bcrTicketRequest.validateUpdateClaimWeek(conn, ticketId);
		if ( webMessages.isEmpty() ) {
			String[] claimWeek = bcrTicketRequest.getNewClaimWeek().split("-");
			WorkYear workYear = new WorkYear(Integer.valueOf(claimWeek[0]));
			
			PreparedStatement ps = conn.prepareStatement("update ticket_claim set claim_year=?, claim_month=?, claim_week=? where ticket_id=?");
			ps.setInt(1, Integer.valueOf(claimWeek[0]));
			ps.setInt(2, workYear.getWorkMonth(Integer.valueOf(claimWeek[1])));
			ps.setInt(3, Integer.valueOf(claimWeek[1]));
			ps.setInt(4, ticketId);
			ps.executeUpdate();
			conn.commit();
			data = new BcrTicketResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		} else if ( webMessages.containsKey(BcrTicketRequest.TICKET_ID)) {
			super.sendNotFound(response);
		} else {
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
		}		
	}





	private void doDirectLabor(Connection conn, HttpServletResponse response, SessionUser sessionUser, Integer ticketId, BcrTicketRequest bcrTicketRequest) {
		// TODO Auto-generated method stub
		
	}





	private void doExpense(Connection conn, HttpServletResponse response, SessionUser sessionUser, Integer ticketId, BcrTicketRequest bcrTicketRequest) {
		// TODO Auto-generated method stub
		
	}





	private void doTotalVolume(Connection conn, HttpServletResponse response, SessionUser sessionUser, Integer ticketId, BcrTicketRequest bcrTicketRequest) {
		// TODO Auto-generated method stub
		
	}

	
}
