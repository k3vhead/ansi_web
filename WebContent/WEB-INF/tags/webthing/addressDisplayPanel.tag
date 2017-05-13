<%@ tag 
    description="" 
    body-content="empty" 
    
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>

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
			<td><span class="ansi-address-label"><%= labelString %>:</span></td>
			<td colspan="3">
				<span class="ansi-address-name ansi-address-value" style="width:315px"></span>
			</td>
		</tr>
		<tr>
			<td><span class="ansi-address-label">Address:</span></td>
			<td colspan="3">
				<span class="ansi-address-address1 ansi-address-value" style="width:315px"></span>
			</td>						
		</tr>
		<tr>
			<td><span class="ansi-address-label">Address 2:</span></td>
			<td colspan="3">
				<span class="ansi-address-address2 ansi-address-value" style="width:315px"></span>
			</td>						
		</tr>
		<tr>
			<td><span class="ansi-address-label">City/State/Zip:</span></td>
			<td colspan="3" style="padding:0; margin:0;">
				<table style="border-collapse: collapse;padding:0; margin:0;">
					<tr>
						<td>
							<span class="ansi-address-city ansi-address-value" style="width:90px;"></span>
						</td>
						<td>
							<span class="ansi-address-state ansi-address-value" style="width:85px"></span>
						</td>
						<td>
							<span class="ansi-address-zip ansi-address-value" style="width:47px"></span>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td><span class="ansi-address-label">County:</span></td>
			<td>
				<span class="ansi-address-county ansi-address-value" style="width:90%"></span>
			</td>
			<td colspan="2">
				<table>
					<tr>
						<td><span class="ansi-address-label">Country:</span></td>
						<td align="right">
							<span class="ansi-address-countryCode ansi-address-value"></span>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div>
 