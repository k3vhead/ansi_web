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
import com.ansi.scilla.report.pac.PacActivationListReport;
import com.ansi.scilla.report.pac.PacCancelledListReport;
import com.ansi.scilla.report.pac.PacDetailReport;
import com.ansi.scilla.report.pac.PacProposedListReport;
import com.ansi.scilla.report.pac.PacSummaryReport;
import com.ansi.scilla.report.reportBuilder.htmlBuilder.HTMLBuilder;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.report.request.PacReportRequest;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class PacReportServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "pacReport";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AnsiURL ansiURL = null;
		try {
			String jsonString = super.makeJsonString(request);
			ansiURL = new AnsiURL(request, REALM, (String[])null); 
			AppUtils.validateSession(request, Permission.QUOTE_READ);
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();

				logger.log(Level.DEBUG, REALM+":URL:"+request);

				Integer divisionId = ansiURL.getId();
				PacReportRequest reportRequest = new PacReportRequest();
				AppUtils.json2object(jsonString, reportRequest);


				Calendar startDate = (Calendar)Midnight.getInstance(new AnsiTime());
				startDate.set(Calendar.YEAR, reportRequest.getStartDate().get(Calendar.YEAR));
				startDate.set(Calendar.MONTH, reportRequest.getStartDate().get(Calendar.MONTH));
				startDate.set(Calendar.DAY_OF_MONTH, reportRequest.getStartDate().get(Calendar.DAY_OF_MONTH));

				Calendar endDate = (Calendar)Midnight.getInstance(new AnsiTime());
				endDate.set(Calendar.YEAR, reportRequest.getEndDate().get(Calendar.YEAR));
				endDate.set(Calendar.MONTH, reportRequest.getEndDate().get(Calendar.MONTH));
				endDate.set(Calendar.DAY_OF_MONTH, reportRequest.getEndDate().get(Calendar.DAY_OF_MONTH));

				PacReportRequest.PacReportType requestedType = PacReportRequest.PacReportType.valueOf(reportRequest.getPacType());
				logger.log(Level.DEBUG, REALM+":Start:"+startDate.getTime()+"\tEnd:"+endDate.getTime());
				
				String reportHtml = null;
				if ( requestedType.equals(PacReportRequest.PacReportType.SUMMARY)) {
					PacSummaryReport summary = new PacSummaryReport(conn, divisionId,startDate,endDate);
					reportHtml = HTMLBuilder.build(summary);
				} else {
					PacDetailReport report = null;
					if ( requestedType.equals(PacReportRequest.PacReportType.P)) {
						report = new PacProposedListReport(conn, divisionId,startDate,endDate);
					} else if ( requestedType.equals(PacReportRequest.PacReportType.A)) {
						report = new PacActivationListReport(conn, divisionId,startDate,endDate);
					} else if ( requestedType.equals(PacReportRequest.PacReportType.C)) {
						report = new PacCancelledListReport(conn, divisionId,startDate,endDate);
					} else { 
						throw new Exception("Invalid Report Type");
					}
					reportHtml = HTMLBuilder.build(report);
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
/*	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AnsiURL ansiURL = null;
		try {
			ansiURL = new AnsiURL(request, REALM, (String[])null); 
			AppUtils.validateSession(request, Permission.QUOTE, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				logger.log(Level.DEBUG, REALM+":URL:"+request);

				String divisionIdString = ansiURL.getQueryParameterMap().get("divisionId")[0];
				Integer divisionId = Integer.getInteger(divisionIdString);
//				Division division = validateDivision(conn, divisionId);

				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

				Calendar startDate = Calendar.getInstance();
				String start = ansiURL.getQueryParameterMap().get("startDate")[0];
				if ( ! StringUtils.isBlank(start)) {
					startDate.setTime(sdf.parse(start));
				}		
				startDate.set(Calendar.HOUR_OF_DAY, 0);
				startDate.set(Calendar.MINUTE, 0);
				startDate.set(Calendar.SECOND, 0);
				startDate.set(Calendar.MILLISECOND, 0);

				Calendar endDate = Calendar.getInstance();
				String end = ansiURL.getQueryParameterMap().get("endDate")[0];
				if ( ! StringUtils.isBlank(end)) {
					endDate.setTime(sdf.parse(end));
				}		
				endDate.set(Calendar.HOUR_OF_DAY, 0);
				endDate.set(Calendar.MINUTE, 0);
				endDate.set(Calendar.SECOND, 0);
				endDate.set(Calendar.MILLISECOND, 0);

				logger.log(Level.DEBUG, REALM+":Start:"+startDate.getTime()+"\tEnd:"+endDate.getTime());

				PacSummaryReport report1 = new PacSummaryReport(conn, ansiURL.getId(),startDate,endDate);
				String reportHtml1 = HTMLBuilder.build(report1); 

				PacProposedListReport report2 = new PacProposedListReport(conn, ansiURL.getId(),startDate,endDate);
				String reportHtml2 = HTMLBuilder.build(report2); 

				PacActivationListReport report3 = new PacActivationListReport(conn, ansiURL.getId(),startDate,endDate);
				String reportHtml3 = HTMLBuilder.build(report3); 

				PacCancelledListReport report4 = new PacCancelledListReport(conn, ansiURL.getId(),startDate,endDate);
				String reportHtml4 = HTMLBuilder.build(report4); 

				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("text/html");

				ServletOutputStream o = response.getOutputStream();
				OutputStreamWriter writer = new OutputStreamWriter(o);
				writer.write(reportHtml1+"\n"+reportHtml2+"\n"+reportHtml3+"\n"+reportHtml4);
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
	}	*/
}

