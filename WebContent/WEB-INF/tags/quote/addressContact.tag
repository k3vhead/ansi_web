<%@ tag 
    description="" 
    body-content="empty" 
    
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>

<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ attribute name="label" required="false" rtexprvalue="true" %>


<%
	String styleId = (String)jspContext.getAttribute("id");	
	String label = (String)jspContext.getAttribute("label");
	String labelString = label == null || label.length()==0 ? "Name" : label;
%>
<table id="<%= styleId %>" class="ansi-contact-container">
	<tr>
		<td class="ansi-address-form-label-container"><span class="formLabel"><%= label %>:</span></td>
		<td class="ansi-address-value"><span class="ansi-contact-name">John Smith</span></td>
		<td>
			<span class="ansi-contact-method"><i class="fa fa-phone" aria-hidden="true"></i></span> 
			<span class="ansi-contact-number">123-123-1234</span>
		</td>
	</tr>
</table>
