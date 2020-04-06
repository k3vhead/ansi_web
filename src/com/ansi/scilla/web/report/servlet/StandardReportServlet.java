package com.ansi.scilla.web.report.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.report.reportBuilder.AbstractReport;
import com.ansi.scilla.report.reportBuilder.AnsiReport;
import com.ansi.scilla.report.reportBuilder.CompoundReport;
import com.ansi.scilla.report.reportBuilder.CustomReport;
import com.ansi.scilla.report.reportBuilder.DataDumpReport;
import com.ansi.scilla.report.reportBuilder.HTMLBuilder;
import com.ansi.scilla.report.reportBuilder.HTMLSummaryBuilder;
import com.ansi.scilla.report.reportBuilder.StandardReport;
import com.ansi.scilla.report.reportBuilder.StandardSummaryReport;
import com.ansi.scilla.report.reportBuilder.XLSBuilder;
import com.ansi.scilla.report.reportBuilder.XLSSummaryBuilder;
import com.ansi.scilla.report.reportBuilder.reportBy.ReportByDivision;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.report.request.ReportDefinition;

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
			List<String> messageList = def.validate(conn);
			ReportAndFilename reportAndFilename = generateXLSReport(conn);
			workbook = reportAndFilename.report;
			fileName = reportAndFilename.fileName;
//			workbook = generateXLSReport(conn);
//			fileName = def.makeReportFileName(conn);
//			fileName = URLEncoder.encode(fileName, "UTF-8");
		} catch ( Exception e) 	{
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}		
		
		ServletOutputStream out = response.getOutputStream();
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String dispositionHeader = "attachment; filename=" + fileName + ".xlsx";
        logger.log(Level.DEBUG, "dispositionHeader: " + dispositionHeader);
        response.setHeader("Content-disposition",dispositionHeader);
        workbook.write(out);
        out.flush();
        out.close();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		Connection conn = null;
		String reportHtml = "Report Gen Failed";
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			this.def = new ReportDefinition(request);
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
		AnsiReport report = def.build(conn);	
		Method method = findAnHTMLMethod(report);
		String reportHtml = (String)method.invoke(this, new Object[] {report});
		return reportHtml;
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
		} catch ( NoSuchMethodException e1 ) {
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

	public String buildHTML(DataDumpReport report) throws Exception {
		return report.makeHTML();
	}
	
	public String buildHTML(CompoundReport report) throws Exception {
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
		return HTMLSummaryBuilder.build(report);
	}

	public String buildHTML(StandardReport report) throws Exception {
		return HTMLBuilder.build(report);
	}

	
	public String buildHTML(CustomReport report) throws Exception {
		return report.makeHTML();
	}
	
	
	
	
	private ReportAndFilename generateXLSReport(Connection conn) throws Exception {
		XSSFWorkbook reportXLS = new XSSFWorkbook();
		AnsiReport report = def.build(conn);
		String fileName = def.makeReportFileName(conn, report);
		
		Method method = findAnXLSMethod(report);
		method.invoke(this, new Object[] {report, reportXLS});
		return new ReportAndFilename(reportXLS, fileName);

	}

	/**
	 * See if the report has its own XLS builder. If not, try the standard builder hierarchy
	 * until you run out of options, or find the right method
	 * @param report
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public Method findAnXLSMethod(AnsiReport report) throws NoSuchMethodException, SecurityException {
		Method method = null;
		Class<?> reportClass = report.getClass();
		Class<?> workbookClass = XSSFWorkbook.class;
		try {
			method = this.getClass().getMethod("buildXLS", new Class<?>[] { reportClass, workbookClass });
		} catch ( NoSuchMethodException e1 ) {
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

	public void buildXLS(DataDumpReport report, XSSFWorkbook workbook) throws Exception {
		report.add2XLS(workbook);
	}

	public void buildXLS(CompoundReport report, XSSFWorkbook workbook) throws Exception {
		for ( AbstractReport subReport : report.getReports() ) {
			Method method = findAnXLSMethod(subReport);
			method.invoke(this, new Object[] {subReport, workbook});
		}
	}

	public void buildXLS(StandardSummaryReport report, XSSFWorkbook workbook) throws Exception {
		XLSSummaryBuilder.build(report, workbook);
	}

	public void buildXLS(StandardReport report, XSSFWorkbook workbook) throws Exception {
		XLSBuilder.build(report, workbook);
	}
	
	public void buildXLS(CustomReport report, XSSFWorkbook workbook) throws Exception {
		report.add2XLS(workbook);
	}
	
	
	public class ReportAndFilename extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		public XSSFWorkbook report;
		public String fileName;
		public ReportAndFilename(XSSFWorkbook report, String fileName) {
			super();
			this.report = report;
			this.fileName = fileName;
		}
		
	}
}
