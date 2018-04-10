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
			<td class="ansi-address-label-container"><span class="ansi-address-label"><bean:message key="field.label.invoice.style" />:</span></td>	
			<td><span class="ansi-default-invoice-value ansi-invoice-invoiceStyleDefault"></span></td>		
		</tr>
		<tr>
			<td class="ansi-address-label-container"><span class="ansi-address-label"><bean:message key="field.label.invoice.grouping" />:</span></td>
			<td><span class="ansi-default-invoice-value ansi-invoice-invoiceGroupingDefault"></span></td>						
		</tr>
		<tr>
			<td class="ansi-address-label-container"><span class="ansi-address-label"><bean:message key="field.label.invoice.batch" />:</span></td>
			<td><span class="ansi-default-invoice-value ansi-invoice-invoiceBatchDefault"></span></td>					
		</tr>
		<tr>
			<td class="ansi-address-label-container"><span class="ansi-address-label"><bean:message key="field.label.invoice.terms" />:</span></td>
			<td><span class="ansi-default-invoice-value ansi-invoice-invoiceTermsDefault"></span></td>
		</tr>
		<tr>
			<td class="ansi-address-label-container"><span class="ansi-address-label"><bean:message key="field.label.invoice.ourVendorNbr" />:</span></td>
			<td><span class="ansi-default-invoice-value ansi-invoice-ourVendorNbrDefault"></span></td>			
		</tr>
	</table>
</div>
 