package com.ansi.scilla.web.common.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.thewebthing.commons.lang.StringUtils;

public abstract class AbstractAutocompleteServlet extends AbstractServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TERM = "term";
	private Permission permissionRequired;
	
	public AbstractAutocompleteServlet(Permission permissionRequired) {
		super();
		this.permissionRequired = permissionRequired;
	}

	
	public Permission getPermissionRequired() {
		return permissionRequired;
	}


	public void setPermissionRequired(Permission permissionRequired) {
		this.permissionRequired = permissionRequired;
	}


	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendNotAllowed(response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendNotAllowed(response);
	}
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String url = request.getRequestURI();
		logger.log(Level.DEBUG, "AutocompleteServlet(): doGet(): url =" + url);

		Connection conn = null;

		try {
			if ( this.permissionRequired == null ) {
				AppUtils.validateSession(request);
			} else {
				AppUtils.validateSession(request, this.permissionRequired);
			}

			String qs = request.getQueryString();
			logger.log(Level.DEBUG, "AutocompleteServlet(): doGet(): qs =" + qs);
			String term = request.getParameter(TERM);

			if ( StringUtils.isBlank(term) ) {
				super.sendNotFound(response);
			} else {
				conn = AppUtils.getDBCPConn();
				String json = doSearch(conn, request.getParameterMap());
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				
				ServletOutputStream o = response.getOutputStream();
				OutputStreamWriter writer = new OutputStreamWriter(o);
				writer.write(json);
				writer.flush();
				writer.close();
			}

		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			if ( conn != null ) {
				AppUtils.closeQuiet(conn);
			}
		}			

	}
	
	
	protected abstract String doSearch(Connection conn, Map<String, String[]> map) throws SQLException, IOException;
	
	
	
	
}
