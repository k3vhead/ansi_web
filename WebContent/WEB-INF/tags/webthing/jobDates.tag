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
<%@ attribute name="page" required="true" rtexprvalue="true" %>



<%
    String namespace = (String)jspContext.getAttribute("namespace");
	String cssId = (String)jspContext.getAttribute("cssId");
	String cssClass = (String)jspContext.getAttribute("cssClass");
	String page = (String)jspContext.getAttribute("page");
	
	String cssIdString = "id=\"" + cssId + "\"";			 
	String cssClassString = cssClass == null || cssClass.length()==0 ? "" : "class=\"" + cssClass + "\"";
%>

<div <%= cssIdString %> <%= cssClassString %> >
	<table>
		<tr>
			<td>Proposal Date:</td>
			<td><span id="<%=namespace %>_proposalDate"></span></td>
			<td>Activation Date:</td>
			<td><span id="<%=namespace %>_activationDate"></span></td>
		</tr>
		<tr>
			<td>Start Date:</td>
			<td><span id="<%=namespace %>_startDate"></span></td>
			<td>Cancel Date:</td>
			<td><span id="<%=namespace %>_cancelDate"></span></td>
		</tr>
		<tr>
			<td colspan="4">
				Cancel Reason:
				<span id="<%=namespace %>_cancelReason"></span>
			</td>
		</tr>
	</table>
</div>
 