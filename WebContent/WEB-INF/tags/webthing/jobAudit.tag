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
			<td class="jobTableCell">
				<div style="display:inline;">
					Created By:
					<span style="display:inline;" id="<%=namespace%>_createdBy"></span>
				</div>
				<div style="display:inline;">
					Last Changed:
					<span style="display:inline; margin-left:30px;" id="<%=namespace%>_lastChangeBy"></span>
					<span style="display:inline; margin-left:30px;" id="<%=namespace%>_lastChangeDate"></span>
				</div>
			</td>
		</tr>
	</table>
</div>
 