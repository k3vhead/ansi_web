package com.ansi.scilla.web.claims.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.claims.response.ClaimTicketResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;

public class ClaimTicketServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "claimTicket";

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doDelete(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		ClaimURL ansiURL = null;
		try {			
			conn = AppUtils.getDBCPConn();
			SessionData sessionData = AppUtils.validateSession(request, Permission.CLAIMS_READ);
			ansiURL = new ClaimURL(request); 
			logger.log(Level.DEBUG, ansiURL);
			ClaimTicketResponse data = ansiURL.claimType.equals(ClaimType.WASHER) ? ClaimTicketResponse.makeFromWasher(conn, ansiURL.id) : ClaimTicketResponse.makeFromTicket(conn, ansiURL.id); 
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(request, response);
	}

	
	public class ClaimURL extends ApplicationObject {

		private static final long serialVersionUID = 1L;
		public Integer id;
		public ClaimType claimType;
		
		public ClaimURL(HttpServletRequest request) throws ResourceNotFoundException {
			String ticketString = REALM + "/ticket/";
			String washerString = REALM + "/washer/";
			String uri = request.getRequestURI();
			
			if ( StringUtils.contains(uri, ticketString) ) {
				claimType = ClaimType.TICKET;
			} else if ( StringUtils.contains(uri, washerString) ) {
				claimType = ClaimType.WASHER;
			} else {
				throw new ResourceNotFoundException();
			}
			
			Integer idx = StringUtils.lastIndexOf(uri, "/");
			if ( idx < 0 ) {
				throw new ResourceNotFoundException();
			}
			String searchId = uri.substring(idx + 1);
			if ( StringUtils.isBlank(searchId) ) {
				throw new ResourceNotFoundException();
			}
			if ( ! StringUtils.isNumeric(searchId)) {
				throw new ResourceNotFoundException();
			}
			this.id = Integer.valueOf(searchId);
		}
	}
	
	public enum ClaimType {
		TICKET,
		WASHER,
		;
	}
	
}
