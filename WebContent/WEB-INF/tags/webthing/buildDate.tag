<%@ tag 
    description="" 
    body-content="empty" 
    import="com.ansi.scilla.web.common.utils.BuildDate"
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c"%>


<%
	BuildDate buildDate = new BuildDate();
%>
<span id="buildDate" class="trailerLink" 
	data-webBuildDate="<%= "[" + buildDate.getWebBranch() + "] [" + buildDate.getWebBuildDate() + "]" %>" 
	data-commonBuildDate="<%= "[" + buildDate.getCommonBranch() + "] [" + buildDate.getCommonBuildDate() + "]" %>"
	data-reportBuildDate="<%= "[" + buildDate.getReportBranch() + "] [" + buildDate.getReportBuildDate() + "]" %>" >Build: <%= buildDate.getWebBuildDate() %></span>
