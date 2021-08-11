package com.ansi.scilla.web.organization.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.organization.OrganizationType;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.organization.response.OrganizationDetailResponse;

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
		String uri = request.getRequestURI();
		String trigger = OrganizationServlet.REALM + "/";
		String uriDetail = uri.substring(uri.indexOf(trigger)+trigger.length());
		// destination[0] should be one of the OrganizationType values (except for division)
		String[] destination = uriDetail.split("/");
		WebMessages webMessages = new WebMessages();
		boolean filter = ( ! StringUtils.isBlank(request.getParameter("filter"))) && request.getParameter("filter").equals("true");
		Connection conn = null;
		
		if ( destination.length < 2 || StringUtils.isBlank(destination[1])) {
			// URL looks like xxx/<Organization type>/
			logger.log(Level.DEBUG, "Organization doDetailGet");
			super.sendNotFound(response);
		} else if ( ! StringUtils.isNumeric(destination[1])) {
			// URL looks like xxx/<Organization type>/XXXX  where XXXX is non-numeric
			logger.log(Level.DEBUG, "Organization doDetailGet: " + destination[1]);
			super.sendNotFound(response);
		} else {
			Integer organizationId = Integer.valueOf( destination[1] );
			try {
				conn = AppUtils.getDBCPConn();
				RequestValidator.validateOrganizationId(conn, webMessages, "organizationId", type, organizationId, true);
				if ( webMessages.isEmpty() ) {
					logger.log(Level.DEBUG, "Organization doDetailGet");
					OrganizationDetailResponse data = new OrganizationDetailResponse(conn, type, organizationId, filter);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
				} else {
					logger.log(Level.DEBUG, "Organization doDetailGet");
					super.sendNotFound(response);
				}
			} catch ( Exception e) {
				logger.log(Level.DEBUG, "Organization doDetailGet");
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(request, response);
	}

	
}
