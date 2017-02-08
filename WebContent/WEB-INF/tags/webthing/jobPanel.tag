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
	<table id="<%=namespace%>_jobTable">
		<tr>
			<td class="jobTableCell" colspan="2">
				Job ID: <input type="text" name="jobId" id="<%= namespace %>_jobId" />
				Status: <input type="text" name="jobStatus" id="<%= namespace %>_jobStatus" />
				Div: <select name="divisionId" id="<%= namespace %>_divisionId"></select>
				<div style="float:right;">
					<input type="button" value="Activate Job" id="<%= namespace %>_activateJobButton" />
					<input type="button" value="Cancel Job" id="<%= namespace %>_cancelJobButton" />
				</div>
			</td>
		</tr>
	</table>
</div>
 