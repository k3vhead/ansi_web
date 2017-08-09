package com.ansi.scilla.web.servlets.report;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.report.PacActivationListReport;
import com.ansi.scilla.common.report.PacCancelledListReport;
import com.ansi.scilla.common.report.PacProposedListReport;
import com.ansi.scilla.common.reportBuilder.HTMLBuilder;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class CashReceiptsRegisterReportServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "cashReceiptsRegisterReport";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AnsiURL ansiURL = null;
		try {
			ansiURL = new AnsiURL(request, REALM, (String[])null); 
			AppUtils.validateSession(request, Permission.TICKET, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();

				PacProposedListReport report2 = new PacProposedListReport(conn, ansiURL.getId());
				String reportHtml2 = HTMLBuilder.build(report2); 

				PacActivationListReport report3 = new PacActivationListReport(conn, ansiURL.getId());
				String reportHtml3 = HTMLBuilder.build(report3); 

				PacCancelledListReport report4 = new PacCancelledListReport(conn, ansiURL.getId());
				String reportHtml4 = HTMLBuilder.build(report4); 

				
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("text/html");

				ServletOutputStream o = response.getOutputStream();
				OutputStreamWriter writer = new OutputStreamWriter(o);
				writer.write(reportHtml3);
				writer.flush();
				writer.close();

			} catch(RecordNotFoundException recordNotFoundEx) {
				super.sendNotFound(response);
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
		} catch (ResourceNotFoundException e1) {
			super.sendNotFound(response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		}
	}	
}

