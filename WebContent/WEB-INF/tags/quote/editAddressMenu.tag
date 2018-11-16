<%@ tag description="" body-content="scriptless" %> 
<%@ attribute name="styleId" required="true" rtexprvalue="true" %>
<%@ attribute name="styleClass" required="false" rtexprvalue="true" %>
<%
	String idName = (String)jspContext.getAttribute("styleId");
	String idString = "id=\"" + idName + "\"";	
	String className = (String)jspContext.getAttribute("styleClass");
	String classString = className == null ? "" : className;
%>
<span class="green fas fa-pencil-alt tooltip <%= classString %>" <%= idString %> >
<span class="tooltiptext">
<%--  <jsp:doBody /> --%>
<table style="width:100%;">
<tr><td class="edit-address" data-type="jobsite">Edit Job Site</td></tr>
<tr><td class="edit-address" data-type="billto">Edit Bill To</td></tr>
<tr><td class="edit-contact" data-type="job">Job Contact</td></tr>
<tr><td class="edit-contact" data-type="site">Site Contact</td></tr>
<tr><td class="edit-contact" data-type="contract">Contract&nbsp;Contact</td></tr>
<tr><td class="edit-contact" data-type="billing">Billing Contact</td></tr>
</table>
</span>
</span>