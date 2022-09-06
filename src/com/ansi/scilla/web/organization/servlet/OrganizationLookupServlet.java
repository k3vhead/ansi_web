package com.ansi.scilla.web.organization.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.organization.OrganizationType;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.organization.response.OrganizationLookupResponse;

public class OrganizationLookupServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	private OrganizationType type;
	
	
	public OrganizationLookupServlet() {
		super();
	}

	public OrganizationLookupServlet(OrganizationType type) {
		this();
		this.type = type;
	}

	public OrganizationType getType() {
		return type;
	}


	public void setType(OrganizationType type) {
		this.type = type;
	}


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "OrganizationLookup doGet");
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			OrganizationLookupResponse data = new OrganizationLookupResponse(conn, type);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);			
		} catch ( Exception e) {
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}


}
