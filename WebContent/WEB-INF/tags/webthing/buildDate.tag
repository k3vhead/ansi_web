<%@ tag 
    description="" 
    body-content="empty" 
    import="com.ansi.scilla.web.common.BuildDate"
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c"%>


<%
	BuildDate buildDate = new BuildDate();
%>
<span id="buildDate" class="trailerLink" data-webBuildDate="<%= buildDate.getWebBuildDate() %>" data-commonBuildDate="<%= buildDate.getCommonBuildDate()%>">Build</span>
