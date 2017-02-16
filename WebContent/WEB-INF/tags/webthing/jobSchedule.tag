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
	<table style="width:100%;">
		<tr>
			<td>Last Run:</td>
			<td><span id="<%=namespace %>_lastRun"></span></td>
			<td>Repeat Annually:</td>
			<td><input value="1" type="checkbox" id="<%=namespace %>_annualRepeat"></td>
			<td rowspan="2" style="text-align:center;">
				<i class="fa fa-list-alt fa-2x" aria-hidden="true"></i>
				<%--  <input type="button" value="Ticket List" /> --%>
			</td>
		</tr>
		<tr>
			<td>Next Due:</td>
			<td><span id="<%=namespace%>_nextDue"></span></td>
			<td>Created Thru:</td>
			<td><span id="<%=namespace%>_createdThru"></span></td>
		</tr>
	</table>
</div>
 