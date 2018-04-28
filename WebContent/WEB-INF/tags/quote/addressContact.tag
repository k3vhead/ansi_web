<%@ tag 
    description="" 
    body-content="empty" 
    
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>

<%@ attribute name="cssId" required="true" rtexprvalue="true" %>
<%@ attribute name="label" required="false" rtexprvalue="true" %>


<%
	String cssId = (String)jspContext.getAttribute("cssId");
	String label = (String)jspContext.getAttribute("label");
	
	String cssIdString = "id=\"" + cssId + "\"";			 
	String labelString = label == null || label.length()==0 ? "Name" : label;
%>
<table style="width: 100%" <%= cssIdString %>>
	<tr>
		<td style="width: 30%;"><span style="font-weight: bold;"><%= label %>:</span></td>
		<td style="width: 30%;"><span>John Smith</span></td>
		<td style="width: 30%;">
			<span><i class="fa fa-phone" aria-hidden="true"></i></span> 
			<span>123-123-1234</span>
		</td>
	</tr>
</table>
