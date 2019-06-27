<%@ tag description="Generate submenu for top-level menu option" body-content="empty" %> 
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>

<% for ( com.ansi.scilla.web.report.common.ReportType reportType : com.ansi.scilla.web.report.common.ReportType.values() ) { %>
<ansi:hasPermission permissionRequired="<%= reportType.getPermission().name() %>"><li><a href="<%= reportType.getLink() %>"><%= reportType.getTitle() %></a></li></ansi:hasPermission>
<% } %>


