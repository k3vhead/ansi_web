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
<% if ( page.equalsIgnoreCase("JOB")) { %> 
	<div style="float:right;margin-right:6px; margin-top:6px;">
		<span id="<%=namespace %>_invoiceEdit" style="cursor:pointer;" class="green fa fa-pencil" ari-hidden="true"></span>
	</div>
<% } %>
	<form id="<%=namespace%>_jobInvoiceForm">
		<table>
			<tr>
				<td colspan="1">
					Invoice Style:
					<% if ( page.equalsIgnoreCase("JOB")) { %> 
						<span id="<%=namespace%>_invoiceStyle"></span>
					<% } else { %>
						<select name="<%=namespace%>_invoiceStyle" id="<%=namespace%>_invoiceStyle" style="width:40px;">
							<option value=""></option>
						</select>
					<% } %>
				</td>
				<td colspan="1">
					Batch: 
					<% if ( page.equalsIgnoreCase("JOB")) { %>
						<i id="<%=namespace%>_invoiceBatch_is_yes" class="fa fa-check-square-o" aria-hidden="true"></i>
						<i id="<%=namespace%>_invoiceBatch_is_no" class="fa fa-minus-circle" aria-hidden="true"></i>
					<% } else { %>
						<input type="checkbox" value="1" name="<%=namespace%>_invoiceBatch" id="<%=namespace%>_invoiceBatch"/>
					<% } %>
				</td>
				<td colspan="1">
					Tax Exempt: 
					<% if ( page.equalsIgnoreCase("JOB")) { %>
						<i id="<%=namespace%>_invoiceTaxExempt_is_yes" class="fa fa-check-square-o" aria-hidden="true"></i>
						<i id="<%=namespace%>_invoiceTaxExempt_is_no" class="fa fa-minus-circle" aria-hidden="true"></i>
					<% } else { %>
						<input type="checkbox" name="<%=namespace%>_invoiceTaxExempt" id="<%=namespace%>_invoiceTaxExempt"/>
					<% } %>
				</td>
			</tr>
			<tr>
				<td colspan="1">
					Grouping:
					<% if ( page.equalsIgnoreCase("JOB")) { %> 
						<span id="<%=namespace%>_invoiceGrouping"></span>
					<% } else { %>
						<select name="<%=namespace%>_invoiceGrouping" id="<%=namespace%>_invoiceGrouping">
							<option value=""></option>
							<option value="auto">Auto</option>
							<option value="manual">Manual</option>
						</select>
					<% } %>
				</td>
				<td colspan="2">
					Terms:
					<% if ( page.equalsIgnoreCase("JOB")) { %> 
						<span id="<%=namespace%>_invoiceTerms"></span>
					<% } else { %>
						<select name="<%=namespace%>_invoiceTerms" id="<%=namespace%>_invoiceTerms">
							<option value=""></option>
						</select>
					<% } %>
				</td>
			</tr>
			<tr>
				<td>
					PO #: 
					<% if ( page.equalsIgnoreCase("JOB")) { %> 
						<span id="<%=namespace%>_invoicePO"></span>
					<% } else { %>
						<input type="text"  name="<%=namespace%>_invoicePO" id="<%=namespace%>_invoicePO" />
					<% } %>
				</td>
				<td colspan="2">
					Our Vendor #: 
					<% if ( page.equalsIgnoreCase("JOB")) { %> 
						<span id="<%=namespace%>_invoiceOurVendorNbr"></span>
					<% } else { %>
						<input type="text" name="<%=namespace%>_invoiceOurVendorNbr" id="<%=namespace%>_invoiceOurVendorNbr"  />
					<% } %>
				</td>
			</tr>
			<tr>
				<td>
					Expire: 
					<% if ( page.equalsIgnoreCase("JOB")) { %> 
						<span id="<%=namespace%>_invoiceExpire"></span>
					<% } else { %>
						<input type="text" name="<%=namespace%>_invoiceExpire" id="<%=namespace%>_invoiceExpire" />
					<% } %>
				</td>
				<td colspan="2">
					Exp. Reason: 
					<% if ( page.equalsIgnoreCase("JOB")) { %> 
						<span id="<%=namespace%>_invoiceExpireReason"></span>
					<% } else { %>
						<input type="text" name="<%=namespace%>_invoiceExpireReason" id="<%=namespace%>_invoiceExpireReason" />
					<% } %>
				</td>
			</tr>
		</table>
	</form>
</div>
 