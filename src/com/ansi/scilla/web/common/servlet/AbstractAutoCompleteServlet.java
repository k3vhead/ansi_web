package com.ansi.scilla.web.common.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.thewebthing.commons.lang.StringUtils;


public abstract class AbstractAutoCompleteServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	protected Permission permission;
	

	/**
	 * 
	 * @param realm	This is the bit of the url 
	 * @param permission
	 * @param autoComplete
	 */
	protected AbstractAutoCompleteServlet(Permission permission) {
		this.permission = permission;
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
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			SessionData sessionData = AppUtils.validateSession(request, this.permission);
			SessionUser user = sessionData.getUser();
			processRequest(conn, user, request, response);

		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			throw new ServletException(e);						
		} finally {
			AppUtils.closeQuiet(conn);
		}

	}
	
	
	private void processRequest(Connection conn, SessionUser user, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HashMap<String, String> parameterMap = new HashMap<String, String>();
		Enumeration<String> parameterNames = request.getParameterNames();
		while ( parameterNames.hasMoreElements() ) {
			String parameter = parameterNames.nextElement();
			String value = request.getParameter(parameter);
			parameterMap.put( parameter, StringUtils.trimToNull(URLDecoder.decode(value, "UTF-8")));
		}

		List<AbstractAutoCompleteItem> resultList = makeResultList(conn, user, parameterMap);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");

		String json = AppUtils.object2json(resultList);
		ServletOutputStream o = response.getOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(o);
		writer.write(json);
		writer.flush();
		writer.close();
	}

	
	protected abstract List<AbstractAutoCompleteItem> makeResultList(Connection conn, SessionUser user, HashMap<String, String> parameterMap) throws Exception;


	
}
