package com.ansi.scilla.web.report.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.report.reportDistribution.ReportDistributionByReport;
import com.ansi.scilla.report.reportDistribution.RowData;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;

import au.com.bytecode.opencsv.CSVWriter;

public class ReportSubscriptionCSVServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	private final String NO_PREVIOUS = "noPreviousValue";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();

			AppUtils.validateSession(request, Permission.REPORT_SUBSCRIPTION_READ);
			List<String[]> csv = makeCSV(conn);
			sendCSV(response, csv);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch (Exception e) {
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}
	
	public List<String[]> makeCSV(Connection conn) throws Exception {
		List<String[]> csv = new ArrayList<String[]>();
		csv.add(new String[] {"Report", "Division", "Distribution"});
		ReportDistributionByReport report = ReportDistributionByReport.buildReport(conn);
		List<RowData> dataRows = (List<RowData>)CollectionUtils.collect(report.getDataRows(), new RowDataTransformer());
		
		Collections.sort(dataRows, new Comparator<RowData>() {
			public int compare(RowData o1,RowData o2) {				
				int retValue = o1.getReportId().compareTo(o2.getReportId());
				if ( retValue == 0 ) {
					if ( o1.getDivision() == null && o2.getDivision() == null ) {
						retValue = 0;
					} else if ( o1.getDivision() == null ) { 
						retValue = -1;
					} else if ( o2.getDivision() == null ) { 
						retValue = 1;
					} else {
						retValue = o1.getDivision().compareTo(o2.getDivision());
					}
				}
				return retValue;
			}
		});
		
		
		String previousRpt = NO_PREVIOUS;
		String previousDiv = NO_PREVIOUS;  // we do this because null is a valid input value
		List<String> emailList = new ArrayList<String>();

		// populate distribution
		for ( RowData rowData : dataRows ) {
			String reportId = rowData.getReportId();
			String div = rowData.getDivision();	
			String email = rowData.getEmail();
			// use StringUtils to make equals check null safe
			if ( ( ! StringUtils.equals(reportId, previousRpt) && ! StringUtils.equals(previousRpt, NO_PREVIOUS)) || ( ! StringUtils.equals(div,previousDiv) && ! StringUtils.equals(previousDiv,NO_PREVIOUS)) ) {
				csv.add(new String[] {previousRpt, previousDiv, StringUtils.join(emailList, ",")});
				emailList = new ArrayList<String>();
			}
			previousRpt = reportId;
			previousDiv = div;
			emailList.add(email);
		}
//		Collections.sort(csv, new Comparator<String[]>() {
//			public int compare(String[] o1,String[] o2) {
//				int retValue = o1[0].compareTo(o2[0]);
//				if ( retValue == 0 ) {
//					if ( o1[1] == null && o2[1] == null ) {
//						retValue = 0;
//					} else if ( o1[1] == null ) { 
//						retValue = -1;
//					} else if ( o2[1] == null ) { 
//						retValue = 1;
//					} else {
//						retValue = o1[1].compareTo(o2[1]);
//					}
//				}
//				return retValue;
//			}
//		});
		return csv;
	}

	private void sendCSV(HttpServletResponse response, List<String[]> csv) throws IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/csv");
		response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        String dispositionHeader = "attachment; filename=reportSubscriptions.csv";
        response.setHeader("Content-disposition",dispositionHeader);

		ServletOutputStream o = response.getOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(o);
		CSVWriter csvWriter = new CSVWriter(writer);
		
		csvWriter.writeAll(csv);
		csvWriter.flush();
		csvWriter.close();		
	}

	
	
	public class RowDataTransformer implements Transformer<Object, RowData> {
		@Override
		public RowData transform(Object arg0) {
			return (RowData)arg0;
		}
		
	}

	
}
