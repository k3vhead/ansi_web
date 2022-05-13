package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.text.ParseException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.payroll.export.PayrollExportUtils;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payroll.request.ExportRequest;
import com.ansi.scilla.web.payroll.response.ExportPreviewResponse;

import au.com.bytecode.opencsv.CSVWriter;

public class ExportServlet extends AbstractServlet {

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

				List<String[]> data = PayrollExportUtils.makeExportCSV(conn, companyCode, exportRequest.getWeekEnding());
				ServletOutputStream o = response.getOutputStream();
				CSVWriter writer = new CSVWriter(new OutputStreamWriter(o));
								
				
				String fileName = "payroll_export_" + exportRequest.getCompanyCode() + "_" + weekEnding;
				String dispositionHeader = "attachment; filename=" + fileName + ".csv";
				response.setHeader("Content-disposition",dispositionHeader);
				response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/csv");

				writer.writeAll(data);
				writer.flush();
				writer.close();

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
