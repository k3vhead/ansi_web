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
	<form id="<%=namespace%>_jobInvoiceForm">
		<table>
			<tr>
				<td colspan="1">
					Invoice Style: 
					<select name="<%=namespace%>_invoiceStyle" id="<%=namespace%>_invoiceStyle" style="width:40px;">
						<option value=""></option>
					</select>
				</td>
				<td colspan="1">
					Batch: <input type="checkbox" value="1" name="<%=namespace%>_invoiceBatch" id="<%=namespace%>_invoiceBatch"/>
				</td>
				<td colspan="1">
					Tax Exempt: <input type="checkbox" name="<%=namespace%>_invoiceTaxExempt" id="<%=namespace%>_invoiceTaxExempt"/>
				</td>
			</tr>
			<tr>
				<td colspan="1">
					Grouping:
					<select name="<%=namespace%>_invoiceGrouping" id="<%=namespace%>_invoiceGrouping">
						<option value=""></option>
						<option value="auto">Auto</option>
						<option value="manual">Manual</option>
					</select>
				</td>
				<td colspan="2">
					Terms:
					<select name="<%=namespace%>_invoiceTerms" id="<%=namespace%>_invoiceTerms">
						<option value=""></option>
					</select>
				</td>
			</tr>
			<tr>
				<td>PO #: <input type="text"  name="<%=namespace%>_invoicePO" id="<%=namespace%>_invoicePO" /></td>
				<td colspan="2">Our Vendor #: <input type="text" name="<%=namespace%>_invoiceOurVendorNbr" id="<%=namespace%>_invoiceOurVendorNbr"  /></td>
			</tr>
			<tr>
				<td>Expire: <input type="text" name="<%=namespace%>_invoiceExpire" id="<%=namespace%>_invoiceExpire" /></td>
				<td colspan="2">Exp. Reason: <input type="text" name="<%=namespace%>_invoiceExpireReason" id="<%=namespace%>_invoiceExpireReason" /></td>
			</tr>
		</table>
	</form>
</div>
 