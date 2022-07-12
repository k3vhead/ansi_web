package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.bcr.request.BcrSplitClaimRequest;
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

				BcrSplitClaimValidationResponse validationResponse = bcrSplitClaimRequest.validate(conn, sessionUser);
				BcrTicketClaimResponse data = new BcrTicketClaimResponse();
				
				if ( validationResponse.responseCode().equals(ResponseCode.SUCCESS) ) {
					logger.log(Level.DEBUG, "webMessages is Empty");
					Integer laborClaimId = null;
					Integer expenseClaimId = null;
//					if ( bcrSplitClaimRequest.isLaborClaim() ) {
//						BcrTicketClaimRequest bcrTicketClaimRequest = bcrSplitClaimRequest.makeBcrTicketClaimRequest();
//						laborClaimId = BcrUtils.addNewLaborClaim(conn, bcrTicketClaimRequest, sessionUser);
//					}
//					if ( bcrSplitClaimRequest.isExpenseClaim() ) {						
//						BcrExpenseRequest bcrExpenseRequest = bcrSplitClaimRequest.makeBcrExpenseRequest();
//						expenseClaimId = BcrUtils.insertExpenseClaim(conn, bcrExpenseRequest, sessionUser);
//					}		
//					Integer claimId = laborClaimId == null ? expenseClaimId : laborClaimId;
//					data = BcrTicketClaimResponse.fromClaim(conn, sessionUser.getUserId(), divisionList, bcrSplitClaimRequest.getDivisionId(), bcrSplitClaimRequest.getWorkYear(), bcrSplitClaimRequest.getWorkWeeks(), claimId);
//					conn.commit();
					super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
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
