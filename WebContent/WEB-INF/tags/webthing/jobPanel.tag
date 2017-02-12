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
	<table id="<%=namespace%>_jobTable">
		<tr>
			<td class="jobTableCell" colspan="2">
				<bean:message key="rpt.hdr.job" /> <input type="text" name="jobId" id="<%= namespace %>_jobId" />
				<bean:message key="rpt.hdr.status" />:
					<% if ( page.equals("QUOTE") ) { %>
						<input type="text" name="jobStatus" id="<%= namespace %>_jobStatus" />
					<% } else { %>
						<span id="<%= namespace %>_jobStatus"></span> 						
					<% } %>
				<bean:message key="rpt.hdr.division" />:
					<% if ( page.equals("QUOTE")) { %> 
					<select name="divisionId" id="<%= namespace %>_divisionId"></select>
					<% } else { %>
						<span id="<%= namespace %>_divisionId"></span>
					<% } %>
				<div style="float:right;">
					<input type="button" value="Activate Job" id="<%= namespace %>_activateJobButton" />
					<input type="button" value="Cancel Job" id="<%= namespace %>_cancelJobButton" />
				</div>
			</td>
		</tr>
	</table>
</div>
 