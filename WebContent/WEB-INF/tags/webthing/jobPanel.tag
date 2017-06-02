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
 
<!-- IF ACTIVE OR CANCELLED ALLOW RESQUEDULE OTHERWISE DON'T -->
<!-- Modify copies everything over and and increments revision -->
<!-- Copy copies everything with new quote id -->
 
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
						<span id="<%= namespace %>_jobLinkSpan">Job: <a href="jobMaintenance.html" id="<%= namespace %>_jobLink"></a><br /></span>
						<span id="<%= namespace %>_quoteLinkSpan">Quote: <a href="quoteMaintenance.html" id="<%= namespace %>_quoteLink"></a><br /></span>
				
					<i style="cursor:pointer;" class="fa fa-arrow-circle-right fa-3x tooltip" aria-hidden="true" id="<%= namespace %>_activateJobButton"><span class="tooltiptext">Activate</span></i>
					<i style="cursor:pointer;" class="clickableIcon, fa fa-ban fa-3x text-danger tooltip" id="<%= namespace %>_cancelJobButton"><span class="tooltiptext">Cancel</span></i>
					<i style="cursor:pointer;" class="clickableIcon, fa fa-calendar  fa-3x tooltip" aria-hidden="true" id="<%= namespace %>_scheduleJobButton"><span class="tooltiptext">Schedule</span></i>
					<i style="cursor:pointer;" class="clickableIcon, fa fa-trash fa-3x tooltip" aria-hidden="true" id="<%= namespace %>_deleteJobButton"><span class="tooltiptext">Delete</span></i>
					
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
 