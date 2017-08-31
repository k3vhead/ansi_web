package com.ansi.scilla.web.servlets.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.Midnight;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.report.CashReceiptsRegisterDetailReport;
import com.ansi.scilla.common.report.PacSummaryReport;
import com.ansi.scilla.common.reportBuilder.HTMLBuilder;
import com.ansi.scilla.common.reportBuilder.XLSBuilder;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.request.report.CashReceiptsRegisterReportRequest;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class CashReceiptsRegisterReportServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "cashReceiptsRegisterReport";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AnsiURL ansiURL = null;
		try {
			String jsonString = super.makeJsonString(request);
			ansiURL = new AnsiURL(request, REALM, (String[])null); 
			AppUtils.validateSession(request, Permission.QUOTE, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();

				System.out.println(REALM+":URL:"+request);

				Integer divisionId = 102;
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
				System.out.println(REALM+":Start:"+startDate.getTime()+"\tEnd:"+endDate.getTime());
				
				String reportHtml = null;
				if ( requestedType.equals(CashReceiptsRegisterReportRequest.CrrReportType.SUMMARY)) {
					PacSummaryReport summary = new PacSummaryReport(conn,divisionId,startDate,endDate);
					reportHtml = HTMLBuilder.build(summary);
				} else {
					CashReceiptsRegisterDetailReport report = null;
					if ( requestedType.equals(CashReceiptsRegisterReportRequest.CrrReportType.DETAIL)) {
						report = new CashReceiptsRegisterDetailReport(conn,startDate,endDate);
					} else { 
						throw new Exception("Invalid Report Type");
					}
					reportHtml = HTMLBuilder.build(report);
					XSSFWorkbook reportXLS = XLSBuilder.build(report);
					reportXLS.write(new FileOutputStream("cashReceiptsRegister.xlsx"));
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
		} catch (ResourceNotFoundException e1) {
			super.sendNotFound(response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		}
	}	
}


