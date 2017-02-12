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
	<form name="<%= namespace %>_jobProposalForm">
		<table>
			<tr>
				<td><bean:message key="rpt.hdr.job"/>:</td>
				<td><input type="text" name="<%=namespace%>_jobNbr" id="<%=namespace%>_jobNbr" style="width:40px;" />
				<td><bean:message key="rpt.hdr.ppc" />:</td>
				<td><input type="text" name="<%=namespace%>_ppc" id="<%=namespace%>_ppc" style="width:100px;" /></td>
			</tr>
			<tr>
				<td colspan="2">&nbsp;</td>
				<td><bean:message key="rpt.hdr.frequency" />:</td>
				<td>
					<select name="<%=namespace%>_jobFrequency" id="<%=namespace%>_jobFrequency" >
						<option value=""></option>
					</select>
				</td>
			</tr>
			<tr>
				<td colspan="4"><bean:message key="rpt.hdr.servicedescription" /></td>
			</tr>
			<tr>
				<td colspan="4">
					<textarea cols="60" rows="6" name="<%=namespace%>_serviceDescription" id="<%=namespace%>_serviceDescription"></textarea>
				</td>
			</tr>
		</table>
	</form>
</div>
 