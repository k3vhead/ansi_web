package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payroll.request.ExportRequest;
import com.ansi.scilla.web.payroll.response.ExportPreviewResponse;

public class ExportPreviewServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			AppUtils.validateSession(request, Permission.PAYROLL_READ);
			try {				
				String companyCode = request.getParameter(ExportRequest.COMPANY_CODE);
				String weekEnding = request.getParameter(ExportRequest.WEEK_ENDING);
				ExportRequest exportRequest = new ExportRequest(companyCode, weekEnding);
				webMessages = exportRequest.validate(conn);
				ResponseCode responseCode = ResponseCode.EDIT_FAILURE;
				ExportPreviewResponse data = new ExportPreviewResponse();
				if ( webMessages.isEmpty() ) {
					data = new ExportPreviewResponse(conn, companyCode, exportRequest.getWeekEnding());
					responseCode = ResponseCode.SUCCESS;
				}
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, responseCode, data);
			} catch ( ParseException e) {
				webMessages.addMessage(ExportRequest.WEEK_ENDING, "Invalid Date");
				ExportPreviewResponse data = new ExportPreviewResponse();
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} finally {
				conn.close();
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);	
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		}
	}

}
