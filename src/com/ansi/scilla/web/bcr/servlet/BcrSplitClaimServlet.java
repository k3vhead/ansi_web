package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.bcr.common.BcrUtils;
import com.ansi.scilla.web.bcr.request.BcrExpenseRequest;
import com.ansi.scilla.web.bcr.request.BcrSplitClaimRequest;
import com.ansi.scilla.web.bcr.request.BcrTicketClaimRequest;
import com.ansi.scilla.web.bcr.response.BcrSplitClaimValidationResponse;
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
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class BcrSplitClaimServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
//		String realm = BcrServlet.REALM + "/" + BcrServlet.NEW_CLAIM;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			try{
				String jsonString = super.makeJsonString(request);
				logger.log(Level.DEBUG, jsonString);
				SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
				BcrSplitClaimRequest bcrSplitClaimRequest = new BcrSplitClaimRequest();
				AppUtils.json2object(jsonString, bcrSplitClaimRequest);
				SessionUser sessionUser = sessionData.getUser();
				List<SessionDivision> divisionList = sessionData.getDivisionList();

				/**
				 * The request object is more complex than the norm, so we have a response object specific
				 * to split claim messaging. 
				 */
				BcrSplitClaimValidationResponse validationResponse = bcrSplitClaimRequest.validate(conn, sessionUser);
				BcrTicketClaimResponse data = new BcrTicketClaimResponse();
				
				if ( validationResponse.responseCode().equals(ResponseCode.SUCCESS) ) {
					try {
						logger.log(Level.DEBUG, "webMessages is Empty");
						if ( bcrSplitClaimRequest.hasLaborClaim() ) {
							List<BcrTicketClaimRequest> bcrTicketClaimRequests = bcrSplitClaimRequest.makeBcrTicketClaimRequests();
							for ( BcrTicketClaimRequest laborRequest : bcrTicketClaimRequests) {
								Integer claimId = BcrUtils.addNewLaborClaim(conn, laborRequest, sessionUser);
								logger.log(Level.DEBUG, "Making labor claim: " + claimId);
							}
						}
						if ( bcrSplitClaimRequest.hasExpenseClaim() ) {						
							BcrExpenseRequest bcrExpenseRequest = bcrSplitClaimRequest.makeBcrExpenseRequest();
							Integer claimId = BcrUtils.insertExpenseClaim(conn, bcrExpenseRequest, sessionUser);
							logger.log(Level.DEBUG, "Making expense claim: " + claimId);
						}		
						conn.commit();
						data.setClaimWeek(bcrSplitClaimRequest.getClaimWeek());
						data.setClaimYear(bcrSplitClaimRequest.getWorkYear());
						super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
					} catch ( Exception e ) {
						conn.rollback();
						throw e;
					}
				} else {
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, validationResponse);
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
	
	

}
