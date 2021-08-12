package com.ansi.scilla.web.organization.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.DivisionGroup;
import com.ansi.scilla.common.organization.OrganizationType;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.organization.request.OrganizationDetailRequest;
import com.ansi.scilla.web.organization.response.OrganizationDetailResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class OrganizationDetailServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	private OrganizationType type;
	
	public OrganizationDetailServlet(OrganizationType type) {
		super();
		this.type = type;
	}

	public OrganizationType getType() {
		return type;
	}

	public void setType(OrganizationType type) {
		this.type = type;
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doDelete(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Organization doDetailGet");
		
		Connection conn = null;
		WebMessages webMessages = new WebMessages();

		try {
			AppUtils.validateSession(request, Permission.SYSADMIN_READ);
			Integer organizationId = isValidUri(request);
			boolean filter = ( ! StringUtils.isBlank(request.getParameter("filter"))) && request.getParameter("filter").equals("true");

			try {
				conn = AppUtils.getDBCPConn();
				RequestValidator.validateOrganizationId(conn, webMessages, "organizationId", type, organizationId, true);
				if ( webMessages.isEmpty() ) {
					OrganizationDetailResponse data = new OrganizationDetailResponse(conn, type, organizationId, filter);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
				} else {
					super.sendNotFound(response);
				}
			} catch ( Exception e) {
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);	
		} catch ( InvalidOrgUriException e) {
			super.sendNotFound(response);
		}
	}

	

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		
		Connection conn = null;
		WebMessages webMessages = new WebMessages();

		try {
			SessionData sessionData = AppUtils.validateSession(request, Permission.SYSADMIN_READ);
			String jsonString = super.makeJsonString(request);
			Integer organizationId = isValidUri(request);
			boolean filter = ( ! StringUtils.isBlank(request.getParameter("filter"))) && request.getParameter("filter").equals("true");
			OrganizationDetailResponse data = new OrganizationDetailResponse();
			
			try {
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				RequestValidator.validateOrganizationId(conn, webMessages, "organizationId", type, organizationId, true);
				if ( webMessages.isEmpty() ) {
					OrganizationDetailRequest organizationDetailRequest = new OrganizationDetailRequest();
					AppUtils.json2object(jsonString, organizationDetailRequest);
					organizationDetailRequest.validate(conn, webMessages, organizationId);
					if ( webMessages.isEmpty() ) {
						doUpdate(conn, sessionData.getUser(), organizationId, organizationDetailRequest);
						conn.commit();
						data = new OrganizationDetailResponse(conn, type, organizationId, filter);
						super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
					} else {
						data.setWebMessages(webMessages);
						super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
					}
				} else {
					logger.log(Level.DEBUG, "Organization doDetailGet");
					super.sendNotFound(response);
				}
			} catch ( Exception e) {
				AppUtils.rollbackQuiet(conn);
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);	
		} catch ( InvalidOrgUriException e) {
			super.sendNotFound(response);
		}
	}


	
	
	
	private Integer isValidUri(HttpServletRequest request) throws InvalidOrgUriException {
		String uri = request.getRequestURI();
		String trigger = OrganizationServlet.REALM + "/";
		String uriDetail = uri.substring(uri.indexOf(trigger)+trigger.length());
		// destination[0] should be one of the OrganizationType values (except for division)
		String[] destination = uriDetail.split("/");
		
		
		if ( destination.length < 2 || StringUtils.isBlank(destination[1])) {
			// URL looks like xxx/<Organization type>/
			throw new InvalidOrgUriException();
		} else if ( ! StringUtils.isNumeric(destination[1])) {
			// URL looks like xxx/<Organization type>/NNNN  where NNNN is non-numeric
			throw new InvalidOrgUriException();
		} 
	
		Integer organizationId = Integer.valueOf( destination[1] );
		return organizationId;
	}

	
	
	private void doUpdate(Connection conn, SessionUser sessionUser, Integer organizationId, OrganizationDetailRequest orgRequest) throws RecordNotFoundException, Exception {
		Calendar today = Calendar.getInstance(new AnsiTime());
		java.sql.Date now = new java.sql.Date(today.getTime().getTime());
		
		DivisionGroup group = new DivisionGroup();
		group.setGroupId(organizationId);
		group.selectOne(conn);
		if (! StringUtils.isBlank(orgRequest.getName())) {
			group.setName(orgRequest.getName());
		}
		if ( orgRequest.getStatus() != null ) {
			if ( orgRequest.getStatus() == true ) {
				group.setStatus(1);
			} else if ( orgRequest.getStatus() == false ) {
				group.setStatus(0);
			} else {
				throw new Exception("This should never happen");
			}
		}
		group.setUpdatedBy(sessionUser.getUserId());
		group.setUpdatedDate(now);
		
		DivisionGroup key = new DivisionGroup();
		key.setGroupId(organizationId);
		group.update(conn, key);
		
		
	}

	private class InvalidOrgUriException extends Exception {
		private static final long serialVersionUID = 1L;		
	}
	
}
