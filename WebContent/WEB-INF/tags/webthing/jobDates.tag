<%@ tag 
    description="" 
    body-content="empty" 
    
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>

<%@ attribute name="namespace" required="true" rtexprvalue="true" %>
<%@ attribute name="cssId" required="true" rtexprvalue="true" %>
<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>



<%
    String namespace = (String)jspContext.getAttribute("namespace");
	String cssId = (String)jspContext.getAttribute("cssId");
	String cssClass = (String)jspContext.getAttribute("cssClass");
	
	String cssIdString = "id=\"" + cssId + "\"";			 
	String cssClassString = cssClass == null || cssClass.length()==0 ? "" : "class=\"" + cssClass + "\"";
%>

<div <%= cssIdString %> <%= cssClassString %> >
	<table>
		<tr>
			<td>Proposed Date:</td>
			<td>MM/DD/YYYY</td>
			<td>Activation Date:</td>
			<td>MM/DD/YYYY</td>
		</tr>
		<tr>
			<td>Start Date:</td>
			<td>MM/DD/YYYY</td>
			<td>Cancel Date:</td>
			<td>MM/DD/YYYY</td>
		</tr>
		<tr>
			<td colspan="4">
				Cancel Reason:
				Reason goes here
			</td>
		</tr>
	</table>
</div>
 