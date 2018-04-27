<%@ tag 
    description="" 
    body-content="empty" 
    
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>

<%@ attribute name="cssId" required="true" rtexprvalue="true" %>


<%
	String cssId = (String)jspContext.getAttribute("cssId");
	
	String cssIdString = "id=\"" + cssId + "\"";			 
%>
<div id="<%= cssId %>">
	<table style="width:100%;">
		<tr>
			<td class="ansi-address-label-container"><span class="ansi-address-label">Bill To:</span></td>	
			<td><span class="ansi-default-jobsite-value ansi-jobsite-jobsiteBillToName"></span></td>		
		</tr>
		<tr>
			<td class="ansi-address-label-container"><span class="ansi-address-label">Job Contact:</span></td>
			<td><span class="ansi-default-jobsite-value ansi-jobsite-jobsiteJobContactName"></span></td>						
		</tr>
		<tr>
			<td class="ansi-address-label-container"><span class="ansi-address-label">Site Contact:</span></td>
			<td><span class="ansi-default-jobsite-value ansi-jobsite-jobsiteSiteContactName"></span></td>					
		</tr>
		<tr>
			<td class="ansi-address-label-container"><span class="ansi-address-label">Floors:</span></td>
			<td><span class="ansi-default-jobsite-value ansi-jobsite-jobsiteFloorsDefault"></span></td>
		</tr>
		<tr>
			<td class="ansi-address-label-container"><span class="ansi-address-label">Building Type:</span></td>
			<td><span class="ansi-default-jobsite-value ansi-jobsite-jobsiteBuildingTypeDefault"></span></td>			
		</tr>
	</table>
</div>
 