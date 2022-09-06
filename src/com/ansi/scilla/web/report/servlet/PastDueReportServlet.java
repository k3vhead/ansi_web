package com.ansi.scilla.web.report.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.Midnight;
import com.ansi.scilla.report.pastDue.PastDueReport2;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.MimeType;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class PastDueReportServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		AnsiURL ansiURL = null;
		try {
			AppUtils.validateSession(request, Permission.INVOICE_READ);
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				String requestDivId = request.getParameter("divisionId");
				String requestStartDate = request.getParameter("startDate");
				
				Integer divisionId = Integer.valueOf(requestDivId);
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Date startDate = sdf.parse(requestStartDate);

				Logger logger = LogManager.getLogger(getClass());
				logger.log(Level.DEBUG,"Division: " + divisionId);
				logger.log(Level.DEBUG, "Start: " + startDate);
				
				Calendar reportStart = (Calendar)Midnight.getInstance(new AnsiTime());
				reportStart.setTime(startDate);

				PastDueReport2 report = PastDueReport2.buildReport(conn, reportStart, divisionId);		
				XSSFWorkbook workbook = report.makeXLS();				

				SimpleDateFormat filenameFormat = new SimpleDateFormat("yyyy_MM_dd");
				String filename = "past_due_" + filenameFormat.format(startDate) + ".xlsx";
				response.setHeader("Expires", "0");
			    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			    response.setHeader("Pragma", "public");
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType(MimeType.EXCEL.contentType());
			    String dispositionHeader = "attachment; filename=" + filename;
				response.setHeader("Content-disposition",dispositionHeader);

				ServletOutputStream out = response.getOutputStream();
				workbook.write(out);
				out.flush();
				out.close();
			} catch(RecordNotFoundException recordNotFoundEx) {
				super.sendNotFound(response);
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
//		} catch (ResourceNotFoundException e1) {
//			super.sendNotFound(response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		}
	}	
}


