package com.ansi.scilla.web.servlets.report;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.reportBuilder.AbstractReport;
import com.ansi.scilla.common.reportBuilder.AnsiReport;
import com.ansi.scilla.common.reportBuilder.CompoundReport;
import com.ansi.scilla.common.reportBuilder.HTMLBuilder;
import com.ansi.scilla.common.reportBuilder.HTMLSummaryBuilder;
import com.ansi.scilla.common.reportBuilder.StandardReport;
import com.ansi.scilla.common.reportBuilder.StandardSummaryReport;
import com.ansi.scilla.common.reportBuilder.XLSBuilder;
import com.ansi.scilla.common.reportBuilder.XLSSummaryBuilder;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.request.report.ReportDefinition;
import com.ansi.scilla.web.servlets.AbstractServlet;

public class StandardReportServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	private final String startHtml = "<html>\n" +
							"\t<head></head>\n" +
							"\t<body>\n";
	private final String endHtml = "\t</body>\n</html>";
			
	private ReportDefinition def;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Connection conn = null;
		XSSFWorkbook workbook = new XSSFWorkbook();
		String fileName = "ansiReport";
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			this.def = new ReportDefinition(request);
			System.out.println(def);
			List<String> messageList = def.validate(conn);
			workbook = generateXLSReport(conn);
			System.out.println("StandardServletReport 55");
			fileName = def.makeReportFileName();
			
		} catch ( Exception e) 	{
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}		
		
		System.out.println("StandardServletReport 66");
		ServletOutputStream out = response.getOutputStream();
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String dispositionHeader = "attachment; filename=" + fileName + ".xlsx";
        response.setHeader("Content-disposition",dispositionHeader);
        System.out.println("StandardServletReport 74");
        workbook.write(out);
        out.flush();
        out.close();
        System.out.println("StandardServletReport 79");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		Connection conn = null;
		String reportHtml = "Report Gen Failed";
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			this.def = new ReportDefinition(request);
			System.out.println("StandardReportServlet 91 " + this.def.getStartDate());
			List<String> messageList = def.validate(conn);
			reportHtml = messageList.isEmpty() ? generateHTMLReport(conn) : generateErrorHTML(messageList);
			
		} catch ( Exception e) 	{
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}		
		
		 
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		
		ServletOutputStream o = response.getOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(o);
		writer.write(startHtml + reportHtml + endHtml);
		writer.flush();
		writer.close();

	}


	private String generateHTMLReport(Connection conn) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		System.out.println("StandardReportServlet 117: " + this.def.getStartDate());
		AnsiReport report = def.build(conn);	
		printDate("StandardReportServlet 117: ", report);
		Method method = findAnHTMLMethod(report);
		String reportHtml = (String)method.invoke(this, new Object[] {report});
		return reportHtml;
	}

	private void printDate(String string, AnsiReport report) {
		try {
			Method method = report.getClass().getMethod("getStartDate", (Class<?>[])null);
			Calendar startDate = (Calendar)method.invoke(report, (Object[])null);
			System.out.println(string + " " + startDate);
		} catch ( Exception e ) {
			System.out.println(string + " " + e);
		}
	}

	private String generateErrorHTML(List<String> messageList) {
		StringBuffer buf = new StringBuffer();
		buf.append("<ul>");
		for ( String message : messageList ) {
			buf.append("<li><span class=\"err\">" + message + "</span></li>");
		}
		buf.append("</ul>");
		return buf.toString();
	}

	public Method findAnHTMLMethod(AnsiReport report) throws NoSuchMethodException, SecurityException {
		Method method = null;
		Class<?> reportClass = report.getClass();
		try {
			method = this.getClass().getMethod("buildHTML", new Class<?>[] { reportClass });
		} catch ( NoSuchMethodException e ) {
			Class<?> superclass = reportClass.getSuperclass();
			try {
				method = this.getClass().getMethod("buildHTML", new Class<?>[] { superclass });
			} catch ( NoSuchMethodException e2 ) {
				Class<?> superduperclass = superclass.getSuperclass();
				method = this.getClass().getMethod("buildHTML", new Class<?>[] { superduperclass });
			} 
		}
		return method;
	}

	public String buildHTML(CompoundReport report) throws Exception {
		printDate("StandardReportServlet 164: ", report);
		boolean doAccordion = false;
		String ul = "";
		String li = "";
		String title = "";
		if ( this.def.getReportDisplay() != null && ! this.def.getReportDisplay().isEmpty() ) {
			doAccordion = true;
			ul = "<ul class=\""+ def.getReportDisplay().get("ul") +"\">";
			li = "<li class=\"" + def.getReportDisplay().get("li") + "\">";			
		}
		StringBuffer buf = new StringBuffer();
		if ( doAccordion ) {
			buf.append(ul);
		}
		for ( AbstractReport subReport : report.getReports() ) {
			Method method = findAnHTMLMethod(subReport);
			String subReportHtml = (String)method.invoke(this, new Object[] {subReport});
			if ( doAccordion ) {
				String titleText = subReport.getTitle();
				title = "<" + def.getReportDisplay().get("titleTag") + " class=\""+ def.getReportDisplay().get("titleClass") +"\">"+ titleText +"</h4>";
				buf.append(li +"\n");
				buf.append(title +"\n");
				buf.append("<div>\n");
			}
			buf.append(subReportHtml);
			if ( doAccordion ) {
				buf.append("</div>\n</li>\n");
			} else {
				buf.append("<div style=\"clear:both;\"><br /><hr /><br /></div>");
			}
		}
		if ( doAccordion) {
			buf.append("</ul>");
		}
		return buf.toString();
	}

	public String buildHTML(StandardSummaryReport report) throws Exception {
		System.out.println("StandardREportServlet 202");
		printDate("StandardREportServlet 202", report);
		System.out.println(report.getSubtitle());
		System.out.println(report.getCompanySubtitle());
		System.out.println(report.getRegionSubtitle());
		System.out.println(report.getDivisionSubtitle());
		System.out.println(report.getClass().getName());
		printDate("StandardReportServlet 207: ", report);
		return HTMLSummaryBuilder.build(report);
	}

	public String buildHTML(StandardReport report) throws Exception {
		System.out.println("StandardREportServlet 212");
		printDate("StandardREportServlet 212", report);
		System.out.println(report.getSubtitle());
		System.out.println(report.getClass().getName());
		printDate("StandardReportServlet 214: ", report);
		return HTMLBuilder.build(report);
	}

	
	
	
	
	
	
	private XSSFWorkbook generateXLSReport(Connection conn) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		XSSFWorkbook reportXLS = new XSSFWorkbook();
		AnsiReport report = def.build(conn);
		Method method = findAnXLSMethod(report);
		System.out.println("StandardServletREport 177");
		System.out.println(method.getName());
		for ( Class<?> x : method.getParameterTypes()) {
			System.out.println(x.getName());
		}
		method.invoke(this, new Object[] {report, reportXLS});
		System.out.println("StandardServletReport 218");
		return reportXLS;

	}

	public Method findAnXLSMethod(AnsiReport report) throws NoSuchMethodException, SecurityException {
		Method method = null;
		Class<?> reportClass = report.getClass();
		Class<?> workbookClass = XSSFWorkbook.class;
		try {
			method = this.getClass().getMethod("buildXLS", new Class<?>[] { reportClass, workbookClass });
		} catch ( NoSuchMethodException e ) {
			Class<?> superclass = reportClass.getSuperclass();
			try {
				method = this.getClass().getMethod("buildXLS", new Class<?>[] { superclass, workbookClass });
			} catch ( NoSuchMethodException e2 ) {
				Class<?> superduperclass = superclass.getSuperclass();
				method = this.getClass().getMethod("buildXLS", new Class<?>[] { superduperclass, workbookClass });
			} 
		}
		return method;
	}

	public void buildXLS(CompoundReport report, XSSFWorkbook workbook) throws Exception {
		System.out.println("StandardREportServlet 206");
		for ( AbstractReport subReport : report.getReports() ) {
			Method method = findAnXLSMethod(subReport);
			method.invoke(this, new Object[] {subReport, workbook});
		}
	}

	public void buildXLS(StandardSummaryReport report, XSSFWorkbook workbook) throws Exception {
		System.out.println("StandardREportServlet 214");
		System.out.println(report.getSubtitle());
		System.out.println(report.getCompanySubtitle());
		System.out.println(report.getRegionSubtitle());
		System.out.println(report.getDivisionSubtitle());
		XLSSummaryBuilder.build(report, workbook);
	}

	public void buildXLS(StandardReport report, XSSFWorkbook workbook) throws Exception {
		System.out.println("StandardREportServlet 223");
		System.out.println(report.getSubtitle());
		XLSBuilder.build(report, workbook);
		System.out.println("StandardReportServlet 259");
	}
	
}
