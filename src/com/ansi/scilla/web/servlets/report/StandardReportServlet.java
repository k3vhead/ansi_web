package com.ansi.scilla.web.servlets.report;

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

import com.ansi.scilla.common.reportBuilder.AbstractReport;
import com.ansi.scilla.common.reportBuilder.AnsiReport;
import com.ansi.scilla.common.reportBuilder.CompoundReport;
import com.ansi.scilla.common.reportBuilder.HTMLBuilder;
import com.ansi.scilla.common.reportBuilder.HTMLSummaryBuilder;
import com.ansi.scilla.common.reportBuilder.StandardReport;
import com.ansi.scilla.common.reportBuilder.StandardSummaryReport;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.request.report.ReportDefinition;
import com.ansi.scilla.web.servlets.AbstractServlet;

public class StandardReportServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	private final String startHtml = "<html>\n" +
							"\t<head></head>\n" +
							"\t<body>\n";
	private final String endHtml = "\t</body>\n</html>";
			

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		Connection conn = null;
		String reportHtml = "Report Gen Failed";
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			ReportDefinition def = new ReportDefinition(request);
			List<String> messageList = def.validate(conn);
			reportHtml = messageList.isEmpty() ? generateReport(conn, def) : generateErrors(messageList);
			
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


	private String generateReport(Connection conn, ReportDefinition def) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		AnsiReport report = def.build(conn);
		Method method = findABuildMethod(report);
		String reportHtml = (String)method.invoke(this, new Object[] {report});
		return reportHtml;
	}

	private String generateErrors(List<String> messageList) {
		StringBuffer buf = new StringBuffer();
		buf.append("<ul>");
		for ( String message : messageList ) {
			buf.append("<li><span class=\"err\">" + message + "</span></li>");
		}
		buf.append("</ul>");
		return buf.toString();
	}

	public Method findABuildMethod(AnsiReport report) throws NoSuchMethodException, SecurityException {
		Method method = null;
		Class<?> reportClass = report.getClass();
		try {
			method = this.getClass().getMethod("buildReport", new Class<?>[] { reportClass });
		} catch ( NoSuchMethodException e ) {
			Class<?> superclass = reportClass.getSuperclass();
			try {
				method = this.getClass().getMethod("buildReport", new Class<?>[] { superclass });
			} catch ( NoSuchMethodException e2 ) {
				Class<?> superduperclass = superclass.getSuperclass();
				method = this.getClass().getMethod("buildReport", new Class<?>[] { superduperclass });
			} 
		}
		return method;
	}

	public String buildReport(CompoundReport report) throws Exception {
		StringBuffer buf = new StringBuffer();
		for ( AbstractReport subReport : report.getReports() ) {
			Method method = findABuildMethod(subReport);
			String subReportHtml = (String)method.invoke(this, new Object[] {subReport});
			buf.append(subReportHtml);
			buf.append("<div style=\"clear:both;\"><br /><hr /><br /></div>");
		}
		return buf.toString();
	}

	public String buildReport(StandardSummaryReport report) throws Exception {
		return HTMLSummaryBuilder.build(report);
	}

	public String buildReport(StandardReport report) throws Exception {
		return HTMLBuilder.build(report);
	}

}
