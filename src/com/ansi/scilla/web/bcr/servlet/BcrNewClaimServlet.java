package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.bcr.common.BcrUtils;
import com.ansi.scilla.web.bcr.request.BcrExpenseRequest;
import com.ansi.scilla.web.bcr.request.BcrNewClaimRequest;
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
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class BcrNewClaimServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

		
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
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
				BcrNewClaimRequest bcrNewClaimRequest = new BcrNewClaimRequest();
				AppUtils.json2object(jsonString, bcrNewClaimRequest);
				SessionUser sessionUser = sessionData.getUser();
				List<SessionDivision> divisionList = sessionData.getDivisionList();

				WebMessages webMessages = bcrNewClaimRequest.validate(conn);
				BcrTicketClaimResponse data = new BcrTicketClaimResponse();
				
				if ( webMessages.isEmpty() ) {
					Integer laborClaimId = null;
					Integer expenseClaimId = null;
					if ( bcrNewClaimRequest.getDlAmt() != null ) {
						BcrTicketClaimRequest bcrTicketClaimRequest = new BcrTicketClaimRequest(bcrNewClaimRequest);
						laborClaimId = BcrUtils.addNewLaborClaim(conn, bcrTicketClaimRequest, sessionUser);
					}
					if ( ! StringUtils.isBlank(bcrNewClaimRequest.getExpenseType()) ) {
						BcrExpenseRequest bcrExpenseRequest = new BcrExpenseRequest(bcrNewClaimRequest);
						expenseClaimId = BcrUtils.insertExpenseClaim(conn, bcrExpenseRequest, sessionUser);
					}		
					Integer claimId = laborClaimId == null ? expenseClaimId : laborClaimId;
					data = BcrTicketClaimResponse.fromClaim(conn, sessionUser.getUserId(), divisionList, bcrNewClaimRequest.getDivisionId(), bcrNewClaimRequest.getWorkYear(), bcrNewClaimRequest.getWorkWeeks(), claimId);
					conn.commit();
					super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
				} else {
					data.setWebMessages(webMessages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
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
