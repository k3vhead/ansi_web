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

import com.ansi.scilla.web.common.AnsiURL;
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
		AnsiURL ansiURL = null;
		String reportHtml = "Report Gen Failed";
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			ReportDefinition def = new ReportDefinition(request);
			List<String> messageList = validateReportDefinition(def);
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

	@SuppressWarnings("unchecked")
	private List<String> validateReportDefinition(ReportDefinition def) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String validatorClassName = def.getReportType().validatorClassName();
		Class<?> validatorClass = Class.forName(validatorClassName);
		Method method = validatorClass.getMethod("validate", new Class<?>[] {ReportDefinition.class});
		List<String> messageList = (List<String>)method.invoke(null, def);
		return messageList;
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

	private String generateReport(Connection conn, ReportDefinition def) {
		return "We made a report!";
	}

	
	
}
