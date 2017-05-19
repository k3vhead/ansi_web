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
	
	String disabled = "";
	if ( page.equalsIgnoreCase("JOB")) { 
		disabled = "disabled=\"disabled\"";
	}
%>


<div <%= cssIdString %> <%= cssClassString %> >
<% if ( page.equalsIgnoreCase("JOB")) { %> 
	<div style="float:right;margin-right:6px; margin-top:6px;">
		<span id="<%=namespace %>_invoiceEdit" style="cursor:pointer;" class="green fa fa-pencil" ari-hidden="true"></span>
	</div>
<% } %>
	<form id="<%=namespace%>_jobInvoiceForm">
		<table>
			<tr>
				<td colspan="1" style="width:80px;">
					Invoice Style:
				</td>
				<td colspan="1" style="width:200px">
					<select name="<%=namespace%>_invoiceStyle" id="<%=namespace%>_invoiceStyle" style="width:40px;" <%=disabled%>>
						<option value=""></option>
					</select>
				</td>
				<td colspan="1" style="width:80px;vertical-align:middle;" valign="middle">
					Batch: <input type="checkbox" name="<%=namespace%>_invoiceBatch" id="<%=namespace%>_invoiceBatch" <%=disabled%>/>
				</td>
				<td colspan="1" style="width:200px;vertical-align:middle;" valign="middle">
					Tax Exempt: <input type="checkbox" name="<%=namespace%>_invoiceTaxExempt" id="<%=namespace%>_invoiceTaxExempt" <%=disabled%>/>
				</td>
			</tr>
			<tr>
				<td colspan="1" style="width:80px;">
					Grouping:
				</td>
				<td colspan="1" style="width:200px">
						<select name="<%=namespace%>_invoiceGrouping" id="<%=namespace%>_invoiceGrouping" <%=disabled%>>
							<option value=""></option>
						</select>
				</td>
				<td colspan="1" style="width:80px;">
					Terms:
				</td>
				<td colspan="1" style="width:200px">
						<select name="<%=namespace%>_invoiceTerms" id="<%=namespace%>_invoiceTerms" <%=disabled%>>
							<option value=""></option>
						</select>
				</td>
			</tr>
			<tr>
				<td colspan="1" style="width:80px;">
					PO #: 
				</td>
				<td colspan="1" style="width:200px">
						<input type="text"  name="<%=namespace%>_invoicePO" id="<%=namespace%>_invoicePO" <%=disabled%>/>
				</td>
				<td colspan="1" style="width:80px;">
					Our Vendor #: 
					</td>
					<td colspan="1" style="width:200px">
						<input type="text" name="<%=namespace%>_invoiceOurVendorNbr" id="<%=namespace%>_invoiceOurVendorNbr"  <%=disabled%>/>
				</td>
			</tr>
			<tr>
				<td colspan="1" style="width:80px;">
					Expire: 
				</td>
				<td colspan="1" style="width:200px">
						<input type="text" name="<%=namespace%>_invoiceExpire" id="<%=namespace%>_invoiceExpire" <%=disabled%>/>
				</td>
				<td colspan="1" style="width:80px;">
					Exp. Reason: 
				</td>
				<td colspan="1" style="width:200px">
						<input type="text" name="<%=namespace%>_invoiceExpireReason" id="<%=namespace%>_invoiceExpireReason" <%=disabled%>/>
				</td>
			</tr>
		</table>
	</form>
</div>
 