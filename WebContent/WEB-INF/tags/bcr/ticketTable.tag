<%@ tag description="Budget Control Panel - Ticket Display" body-content="scriptless" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ attribute name="filterClass" required="false" rtexprvalue="true" %>
<%
	String tableId = (String)jspContext.getAttribute("id");	
	String filterClass = (String)jspContext.getAttribute("filterClass"); 
	String styleClassString = filterClass == null || filterClass.equals("") ? "" : "styleClass=\"" + filterClass + "\"";
	String filterContainer = tableId + "Filter";
%>
<webthing:lookupFilter filterContainer="<%= filterContainer %>"  styleClass="<%= filterClass %>" />
<span class="ticketTableMsg"></span><br />
<table id="<%=tableId %>">
</table>