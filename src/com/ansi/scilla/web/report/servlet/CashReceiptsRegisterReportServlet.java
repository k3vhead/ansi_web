package com.ansi.scilla.web.report.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.Midnight;
import com.ansi.scilla.report.cashReceiptsRegister.CashReceiptsRegisterDetailReport;
import com.ansi.scilla.report.cashReceiptsRegister.CashReceiptsRegisterSummaryReport;
import com.ansi.scilla.report.reportBuilder.htmlBuilder.HTMLBuilder;
import com.ansi.scilla.report.reportBuilder.htmlBuilder.HTMLSummaryBuilder;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.report.request.CashReceiptsRegisterReportRequest;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class CashReceiptsRegisterReportServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "cashReceiptsRegisterReport";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		AnsiURL ansiURL = null;
		try {
			String jsonString = super.makeJsonString(request);
//			ansiURL = new AnsiURL(request, REALM, (String[])null); 
			AppUtils.validateSession(request, Permission.QUOTE_READ);
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();

				logger.log(Level.DEBUG, REALM+":URL:"+request);

//				Integer divisionId = 102;
				CashReceiptsRegisterReportRequest reportRequest = new CashReceiptsRegisterReportRequest();
				AppUtils.json2object(jsonString, reportRequest);


				Calendar startDate = (Calendar)Midnight.getInstance(new AnsiTime());
				startDate.set(Calendar.YEAR, reportRequest.getStartDate().get(Calendar.YEAR));
				startDate.set(Calendar.MONTH, reportRequest.getStartDate().get(Calendar.MONTH));
				startDate.set(Calendar.DAY_OF_MONTH, reportRequest.getStartDate().get(Calendar.DAY_OF_MONTH));

				Calendar endDate = (Calendar)Midnight.getInstance(new AnsiTime());
				endDate.set(Calendar.YEAR, reportRequest.getEndDate().get(Calendar.YEAR));
				endDate.set(Calendar.MONTH, reportRequest.getEndDate().get(Calendar.MONTH));
				endDate.set(Calendar.DAY_OF_MONTH, reportRequest.getEndDate().get(Calendar.DAY_OF_MONTH));

				CashReceiptsRegisterReportRequest.CrrReportType requestedType = CashReceiptsRegisterReportRequest.CrrReportType.valueOf(reportRequest.getCrrType());
				logger.log(Level.DEBUG, REALM+":Start:"+startDate.getTime()+"\tEnd:"+endDate.getTime());
				
				String reportHtml = null;
				if ( requestedType.equals(CashReceiptsRegisterReportRequest.CrrReportType.SUMMARY)) {
					CashReceiptsRegisterSummaryReport report = CashReceiptsRegisterSummaryReport.buildReport(conn, startDate, startDate);
					reportHtml = HTMLSummaryBuilder.build(report);
				} else {
					CashReceiptsRegisterDetailReport report = null;
					if ( requestedType.equals(CashReceiptsRegisterReportRequest.CrrReportType.DETAIL)) {
						report = CashReceiptsRegisterDetailReport.buildReport(conn,startDate,endDate);
					} else { 
						throw new Exception("Invalid Report Type");
					}
					reportHtml = HTMLBuilder.build(report);
//					XSSFWorkbook reportXLS = XLSBuilder.build(report);
//					reportXLS.write(new FileOutputStream("cashReceiptsRegister.xlsx"));
				}
				
 

				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("text/html");

				ServletOutputStream o = response.getOutputStream();
				OutputStreamWriter writer = new OutputStreamWriter(o);
				writer.write(reportHtml);
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
//		} catch (ResourceNotFoundException e1) {
//			super.sendNotFound(response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		}
	}	
}


