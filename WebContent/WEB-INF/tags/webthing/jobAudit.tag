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
			<td class="jobTableCell">
				<div style="display:inline;">
					<span style="display:inline;" id="<%= namespace %>_createdBy">Created By</span>
				</div>
				<div style="display:inline;">
					<span style="display:inline; margin-left:30px;" id="<%= namespace %>_modifiedBy">Last Changed By</span>
				</div>
				<div style="display:inline;">
					<span style="display:inline; margin-left:30px;" id="<%= namespace %>_modifiedDate">Mod Date</span>
				</div>
			</td>
		</tr>
	</table>
</div>
 