package com.ansi.scilla.web.test.reports;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.report.reportBuilder.reportType.AbstractReport;
import com.ansi.scilla.report.reportBuilder.reportType.AnsiReport;
import com.ansi.scilla.report.reportBuilder.reportType.CompoundReport;
import com.ansi.scilla.report.reportBuilder.htmlBuilder.HTMLBuilder;
import com.ansi.scilla.report.reportBuilder.htmlBuilder.HTMLSummaryBuilder;
import com.ansi.scilla.report.reportBuilder.reportType.StandardReport;
import com.ansi.scilla.report.reportBuilder.reportType.StandardSummaryReport;
import com.ansi.scilla.report.reportBuilder.xlsBuilder.XLSBuilder;
import com.ansi.scilla.report.reportBuilder.xlsBuilder.XLSSummaryBuilder;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.report.common.ReportType;
import com.ansi.scilla.web.report.request.ReportDefinition;

public class TestStandardReportServlet {

	private final String startHtml = "<html>\n" +
							"\t<head></head>\n" +
							"\t<body>\n";
	private final String endHtml = "\t</body>\n</html>";
			
	private ReportDefinition def;

	public static void main(String[] args) {
		Calendar startDate = new GregorianCalendar(2017, Calendar.JULY, 5);
		Calendar endDate = new GregorianCalendar(2017, Calendar.JULY, 5);
		try {
			new TestStandardReportServlet().go(
					ReportType.CASH_RECEIPTS_REGISTER.toString(), 
					startDate, 
					endDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go(String reportId, Calendar startDate, Calendar endDate) throws Exception {

		Connection conn = null;
		String reportHtml = "Report Gen Failed";
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			this.def = new TestReportDef(reportId, startDate, endDate);
			System.out.println("StandardReportServlet 91 " + this.def.getStartDate());
			List<String> messageList = def.validate(conn);
			reportHtml = messageList.isEmpty() ? generateHTMLReport(conn) : generateErrorHTML(messageList);
			
		} catch ( Exception e) 	{
			AppUtils.logException(e);
			if ( conn != null) {
				AppUtils.rollbackQuiet(conn);
			}
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}		
		
		 
		FileUtils.write(new File("/home/dclewis/Documents/webthing_v2/projects/ANSI/testresults/testoutput.html"), startHtml + reportHtml + endHtml);

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
