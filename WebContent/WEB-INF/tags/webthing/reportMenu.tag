<%@ tag description="Generate submenu for top-level menu option" body-content="empty" %> 
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>

<%
	for ( com.ansi.scilla.web.report.common.ReportType reportType : com.ansi.scilla.web.report.common.ReportType.values() ) {
		String reportClassName = reportType.reportClassName();
		Class<?> reportClass = Class.forName(reportClassName);
		java.lang.reflect.Field field = reportClass.getDeclaredField("REPORT_TITLE");
		String title = (String)field.get(null);
%>
<ansi:hasPermission permissionRequired="<%= reportType.getPermission().name() %>"><li><a href="report.html?id=<%= reportType.name() %>"><%= title %></a></li></ansi:hasPermission>
<% } %>


