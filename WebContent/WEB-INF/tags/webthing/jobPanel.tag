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
	<div id="<%= namespace %>_panelMessage" class="err"></div>
	<table id="<%=namespace%>_jobTable" style="width:1300px;">
		<tr>
			<td class="jobTableCell" colspan="2" >
				<bean:message key="rpt.hdr.job" /> 
				
				<% if ( page.equals("QUOTE") ) { %>
						<input type="text" name="jobId" id="<%= namespace %>_jobId" disabled="disabled"/>
				<% } else { %>
						<input type="text" name="jobId" id="<%= namespace %>_jobId" disabled="disabled"/> 						
				<% } %>
				
				
				<bean:message key="rpt.hdr.status" />:
					<% if ( page.equals("QUOTE") ) { %>
						<input type="text" name="jobStatus" id="<%= namespace %>_jobStatus" disabled="disabled"/>
					<% } else { %>
						<span id="<%= namespace %>_jobStatus"></span> 						
					<% } %>
				<bean:message key="rpt.hdr.division" />:

						<span id="<%= namespace %>_divisionId"></span>
					
				<div style="float:right; text-align:right;">
					<% if ( page.equals("QUOTE") ) { %>
						Job: <a href="jobMaintenance.html" id="<%= namespace %>_jobLink"></a><br />
					<% } else { %>
						Quote: <a href="quoteMaintenance.html"><span id="<%= namespace %>_quoteId"></span></a><br />
					<% } %>
					<i style="cursor:pointer;" class="fa fa-product-hunt fa-3x" aria-hidden="true" id="<%= namespace %>_proposeJobButton"></i>
					<i style="cursor:pointer;" class="fa fa-arrow-circle-right fa-3x" aria-hidden="true" id="<%= namespace %>_activateJobButton"></i>
					<i style="cursor:pointer;" class="clickableIcon, fa fa-ban fa-3x text-danger" id="<%= namespace %>_cancelJobButton"></i>
					<i style="cursor:pointer;" class="clickableIcon, fa fa-calendar  fa-3x" aria-hidden="true" id="<%= namespace %>_scheduleJobButton"></i>
					
					
					<%--
					<input type="button" value="Activate Job" id="<%= namespace %>_activateJobButton" />
					<input type="button" value="Cancel Job" id="<%= namespace %>_cancelJobButton" />
					<input type="button" value="Reschedule Job" id="<%= namespace %>_rescheduleJobButton" />
					 --%> 
				</div>
			</td>
		</tr>
	</table>
</div>
 