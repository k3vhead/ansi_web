<%@ tag description="Budget Control Panel - Ticket Display" body-content="scriptless" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%
	String tableId = (String)jspContext.getAttribute("id");	
%>
<span class="ticketTableMsg"></span><br />
<table id="<%=tableId %>">
</table>