package com.ansi.scilla.web.common.struts;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;

import com.ansi.scilla.common.db.ApplicationProperties;
import com.ansi.scilla.common.utils.ApplicationProperty;
import com.ansi.scilla.web.common.utils.AppUtils;

public class ScillaRequestProcessor extends RequestProcessor {

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		ServletContext cxt = request.getSession().getServletContext();
		String systemDB = (String)cxt.getAttribute("ANSI_SYSTEM_DB");
		if ( StringUtils.isBlank(systemDB)) {
			try {
				cxt.setAttribute("ANSI_SYSTEM_DB", makeDbName());
			} catch ( Exception e) {
				throw new ServletException(e);
			}
		}
		super.process(request, response);
	}

	@Override
	protected ActionForm processActionForm(HttpServletRequest request,
			HttpServletResponse response, ActionMapping mapping) {
		return super.processActionForm(request, response, mapping);
	}

	@Override
	protected boolean processValidate(HttpServletRequest request,
			HttpServletResponse response, ActionForm actionForm, ActionMapping mapping)
			throws IOException, ServletException {
		return super.processValidate(request, response, actionForm, mapping);
	}

	private String makeDbName() throws Exception {
		Connection conn = AppUtils.getDBCPConn();
		ApplicationProperties dbNameProperty = ApplicationProperty.get(conn, ApplicationProperty.DBNAME);
		conn.close();
		return dbNameProperty.getValueString();
	}

}
