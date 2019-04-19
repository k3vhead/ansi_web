package com.ansi.scilla.web.claims.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.claims.request.ClaimEntryRequest;
import com.ansi.scilla.web.claims.request.ClaimEntryRequestType;
import com.ansi.scilla.web.claims.response.ClaimEntryResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;


/**
 * Add or Display ticket claims (Direct Labor and Passthru Expense)
 * Note: We're not using the AbstractCrudServlet as a base because the display output requires a couple extra fields
 * from separate queries. (That might change, but for now this is where we're at)
 * 
 * @author dclewis
 *
 */
public class ClaimEntryServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "claimEntry";
	
	
	
	
	
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		AnsiURL ansiURL = null;
		try {			
			conn = AppUtils.getDBCPConn();
			SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
			ansiURL = new AnsiURL(request, REALM, (String[])null); 
			logger.log(Level.DEBUG, ansiURL);
			ClaimEntryResponse data = new ClaimEntryResponse(conn, ansiURL.getId(), sessionData.getUser().getUserId());
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
//		} catch ( RecordNotFoundException e) {
//			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
			
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Connection conn = null;
		AnsiURL ansiURL = null;
		try {			
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_WRITE);
			ansiURL = new AnsiURL(request, REALM, (String[])null); 
			logger.log(Level.DEBUG, ansiURL);
			
			ClaimEntryRequest claimEntryRequest = new ClaimEntryRequest();
			AppUtils.json2object(jsonString, claimEntryRequest);
			try {
				ClaimEntryRequestType requestType = ClaimEntryRequestType.valueOf(claimEntryRequest.getType());
				if ( requestType.equals(ClaimEntryRequestType.DIRECT_LABOR)) {
					processDirectLaborRequest(conn, sessionData, claimEntryRequest, response);
				} else if ( requestType.equals(ClaimEntryRequestType.PASSTHRU_EXPENSE)) {
					processPassthruExpenseRequest(conn, sessionData, claimEntryRequest, response);
				}
			} catch ( IllegalArgumentException e) {
				processInvalidType(conn, claimEntryRequest, response);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
//		} catch ( RecordNotFoundException e) {
//			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}		
	}


	private void processDirectLaborRequest(Connection conn, SessionData sessionData,
			ClaimEntryRequest claimEntryRequest, HttpServletResponse response) throws Exception {
		ClaimEntryResponse data = new ClaimEntryResponse();
		WebMessages webMessages = new WebMessages();
		
		webMessages.addMessage(ClaimEntryRequest.TYPE, "It's a work in progress");
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);		
	}


	private void processPassthruExpenseRequest(Connection conn, SessionData sessionData,
			ClaimEntryRequest claimEntryRequest, HttpServletResponse response) throws Exception {
		ClaimEntryResponse data = new ClaimEntryResponse();
		WebMessages webMessages = new WebMessages();
		
		webMessages.addMessage(ClaimEntryRequest.TYPE, "Haven't Built this yet; come back tomorrow");
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);		
	}


	private void processInvalidType(Connection conn, ClaimEntryRequest claimEntryRequest,
			HttpServletResponse response) throws Exception {
		ClaimEntryResponse data = new ClaimEntryResponse();
		WebMessages webMessages = new WebMessages();
		
		webMessages.addMessage(ClaimEntryRequest.TYPE, "Invalid request; reload and try again");
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);		
	}


	


	

	
	
	

	
	
}
