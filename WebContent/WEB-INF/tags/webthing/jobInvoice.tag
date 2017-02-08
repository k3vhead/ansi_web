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
	<form id="<%=namespace%>_jobInvoiceForm">
		<table>
			<tr>
				<td colspan="1">
					Invoice Style: 
					<select name="<%=namespace %>_invoiceStyle" style="width:40px;">
						<option value=""></option>
					</select>
				</td>
				<td colspan="2">Batch: <input type="checkbox" name="<%=namespace %>_invoiceBatch" /></td>
			</tr>
			<tr>
				<td colspan="1">
					Grouping:
					<select name="<%=namespace %>_invoiceGrouping">
						<option value=""></option>
						<option value="auto">Auto</option>
						<option value="manual">Manual</option>
					</select>
				</td>
				<td colspan="1">
					Terms:
					<select name="<%=namespace %>_invoiceTerms">
						<option value=""></option>
						<option value="auto">Type 1</option>
						<option value="manual">Type 2</option>
					</select>
				</td>
				<td colspan="1">Tax Exempt: <input type="checkbox" name="<%=namespace %>_invoiceTaxExempt" /></td>
			</tr>
			<tr>
				<td>PO #: <input type="text"  name="<%=namespace %>_invoicePO" /></td>
				<td colspan="2">Our Vendor #: <input type="text" name="<%=namespace %>_invoiceOurVendorNbr"  /></td>
			</tr>
			<tr>
				<td>Expire: <input type="text" name="<%=namespace %>_invoiceExpire" /></td>
				<td colspan="2">Exp. Reason: <input type="text" name="<%=namespace %>_invoiceExpireReason"  /></td>
			</tr>
		</table>
	</form>
</div>
 