<%@ tag 
    description="" 
    body-content="empty" 
    
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>

<%@ attribute name="cssId" required="true" rtexprvalue="true" %>
<%@ attribute name="label" required="false" rtexprvalue="true" %>


<%
	String cssId = (String)jspContext.getAttribute("cssId");
	String label = (String)jspContext.getAttribute("label");
	
	String cssIdString = "id=\"" + cssId + "\"";			 
	String labelString = label == null || label.length()==0 ? "Name" : label;
%>
<div id="<%= cssId %>">
	<table style="width:100%;">
		<tr>
			<td class="ansi-address-label-container"><span class="ansi-address-label"><%= labelString %>:</span></td>
			<td colspan="3">
				<span class="ansi-address-name ansi-address-value" style="width:315px"></span>
			</td>
		</tr>
		<tr>
			<td class="ansi-address-label-container"><span class="ansi-address-label"><bean:message key="field.label.address" />:</span></td>
			<td colspan="3">
				<span class="ansi-address-address1 ansi-address-value" style="width:315px"></span>
			</td>						
		</tr>
		<tr>
			<td class="ansi-address-label-container"><span class="ansi-address-label"><bean:message key="field.label.address2" />:</span></td>
			<td colspan="3">
				<span class="ansi-address-address2 ansi-address-value" style="width:315px"></span>
			</td>						
		</tr>
		<tr>
			<td class="ansi-address-label-container"><span class="ansi-address-label"><bean:message key="field.label.city" />/<bean:message key="field.label.state" />/<bean:message key="field.label.zip" />:</span></td>
			<td colspan="3" style="padding:0; margin:0;">
				<span class="ansi-address-city ansi-address-value" style="width:90px;"></span>, 
				<span class="ansi-address-state ansi-address-value" style="width:85px"></span>
				<span class="ansi-address-zip ansi-address-value" style="width:47px"></span>
		</tr>
		<tr>
			<td class="ansi-address-label-container"><span class="ansi-address-label"><bean:message key="field.label.county" />:</span></td>
			<td>
				<span class="ansi-address-county ansi-address-value" style="width:90%"></span>
			</td>
			<td colspan="2">
				<table>
					<tr>
						<td><span class="ansi-address-label"><bean:message key="field.label.countryCode" />:</span></td>
						<td align="right">
							<span class="ansi-address-countryCode ansi-address-value"></span>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div>
 