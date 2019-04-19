package com.ansi.scilla.web.claims.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.claims.request.ClaimEntryRequest;
import com.ansi.scilla.web.claims.response.ClaimEntryResponse;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractCrudServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;


public class ClaimEntryServlet extends AbstractCrudServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "claimEntry";
	
	
	
	
	
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logger.log(Level.DEBUG, "I'm here");
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
		
		super.doPost(request, response);
		
	}


	@Override
	protected WebMessages validateAdd(Connection conn, HashMap<String, Object> addRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateClaimDetailRequestType(webMessages, WebMessages.GLOBAL_MESSAGE, (String)addRequest.get(ClaimEntryRequest.TYPE), true);
		RequestValidator.validateDate(webMessages, ClaimEntryRequest.WORK_DATE, (Date)addRequest.get(ClaimEntryRequest.WORK_DATE), false, null, null);
		RequestValidator.validateWasherId(conn, webMessages, ClaimEntryRequest.WASHER_ID, (Integer)addRequest.get(ClaimEntryRequest.WASHER_ID), true);
		RequestValidator.validateDouble(webMessages, ClaimEntryRequest.VOLUME, (Double)addRequest.get(ClaimEntryRequest.VOLUME), null, null, true);
		RequestValidator.validateDouble(webMessages, ClaimEntryRequest.DL_AMT, (Double)addRequest.get(ClaimEntryRequest.DL_AMT), null, null, true);
		RequestValidator.validateDouble(webMessages, ClaimEntryRequest.HOURS, (Double)addRequest.get(ClaimEntryRequest.HOURS), null, null, true);
		RequestValidator.validateString(webMessages, ClaimEntryRequest.NOTES, (String)addRequest.get(ClaimEntryRequest.NOTES), 1024, false);
		return webMessages;
	}


	@Override
	protected WebMessages validateUpdate(Connection conn, HashMap<String, Object> updateRequest) throws Exception {
		return new WebMessages();
	}

	
	
	

	
	
}
