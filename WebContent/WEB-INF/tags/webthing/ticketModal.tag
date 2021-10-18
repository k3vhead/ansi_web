<%@ tag description="" body-content="scriptless" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %> 
<%@ attribute name="ticketContainer" required="true" rtexprvalue="true" %>
<%@ attribute name="styleId" required="false" rtexprvalue="true" %>
<%@ attribute name="style" required="false" rtexprvalue="true" %>
<%
    String ticketContainer = (String)jspContext.getAttribute("ticketContainer"); 
	String jobsiteAddress = ticketContainer + "-jobsite-address";
	String billtoAddress = ticketContainer + "-billto-address";
%>
<div id="<%= ticketContainer %>">
	<div class="ticketModalContainer">
		<div class="summaryTable">
			<table id="displaySummaryTable">
				<tr>
					<th>Status</th><td><span class="status"></span></td>    		
	        		<th>Division</th><td><span class="divisionDisplay"></span></td>
	        		<th>Job ID</th><td><span class="jobId"></span></td>
	        		<th>Payment Terms:</th><td><span class="invoiceStyle"></span></td>
	        		<th>Frequency</th><td><span class="jobFrequency"></span></td>
	        		<th>PO #</th><td><span class="poNumber"></span></td>
	       		</tr>
	       		<tr>
	        		<th colspan="2">Service Description:</th><td colspan="10"><span class="serviceDescription"></span></td>
	    		</tr>
			</table>
		</div>
	    	
	   	<div class="ticketTable">
		   	<table id="displayTicketTable">
		   		<tr>
		   			<th>Ticket</th>
		   			<th>Ticket Amt</th>
		   			<th>Ticket Paid</th>
		   			<th>Ticket Tax</th>
		   			<th>Tax Paid</th>
		   			<th>Balance</th>
		   		</tr>
		   		<tr>
		   			<td style="border-bottom:solid 1px #000000;"><span class="ticketId"></span></td>
		   			<td style="border-bottom:solid 1px #000000;"><span class="actPricePerCleaning"></span></td>
		   			<td style="border-bottom:solid 1px #000000;"><span class="totalVolPaid"></span></td>
		   			<td style="border-bottom:solid 1px #000000;"><span class="actTax"></span></td>
		   			<td style="border-bottom:solid 1px #000000;"><span class="totalTaxPaid"></span></td>
		   			<td style="border-bottom:solid 1px #000000;"><span class="ticketBalance"></span></td>
		   		</tr>
		   		
		   		<tr>
		   			<th style="border-bottom:solid 1px #000000;" rowspan="2" class="formLabel">Job Tags</th>
		   			<td colspan="3" rowspan="2" style="border-bottom:solid 1px #000000;"><span class="jobTags"></span></td>
		   			<td><span class="formLabel">D/L</span></td>
		   			<td><span class="directLabor">0.00</span></td>
		   		</tr>
		   		<tr>
		   			<td style="border-bottom:solid 1px #000000;"><span class="formLabel">D/L%</span></td>
		   			<td style="border-bottom:solid 1px #000000;"><span class="directLaborPct">0.00%</span></td>
		   		</tr>
		   		
		   		<tr class="processNotesRow">
		   			<td colspan="2"><span class="formLabel processDateLabel"></span> <span class="processDate"></span></td>
		   			<td colspan="4"><span class="formLabel">Process Notes:</span> <span class="processNotes"></span></td>
		   		</tr>
		   		<tr class="completedRow">
		   			<td class="bottomRow" colspan="2"><span class="formLabel">Customer Signature: </span> <i class="customerSignature fa" aria-hidden="true"></i></td>
		   			<td class="bottomRow" colspan="2"><span class="formLabel">Bill Sheet: </span> <i class="billSheet fa" aria-hidden="true"></i></td>
		   			<td class="bottomRow" colspan="2">
		   				<span class="formLabel">Manager Approval: </span> <i class="managerApproval fa" aria-hidden="true"></i>	   				
		   			</td>
		   		</tr>
		   		
		   		<tr>
		   			<td colspan="3" style="border-top:solid 1px #000000; border-right:solid 1px #000000;">
		   				<webthing:addressDisplayPanel cssId="<%= jobsiteAddress %>" label="Job Site" />
		   			</td>
		   			<td colspan="3" style="border-top:solid 1px #00000; border-left:solid 1px #000000;">
						<webthing:addressDisplayPanel cssId="<%= billtoAddress %>" label="Bill To" />
		   			
		   			</td>
		   		</tr>
		   	</table>
		   	
		</div>
			   	
		<div class="invoiceTable">		   	
		   	<table id="displayInvoiceTable">
		   		<tr>
		   			<th>Inv #</th>
		   			<th>Inv Amt</th>
		   			<th>Inv Paid</th>
		   			<th>Tax Amt</th>
		   			<th>Tax Paid</th>
		   			<th>Balance</th>
		   		</tr>
		   		<tr>
		   			<td><span class="invoiceId"></span></td>
		   			<td><span class="sumInvPpc"></span></td>
		   			<td><span class="sumInvPpcPaid"></span></td>
		   			<td><span class="sumInvTax"></span></td>
		   			<td><span class="sumInvTaxPaid"></span></td>
		   			<td><span class="invoiceBalance"></span></td>
		   		</tr>
		   	</table>
		</div>
		
		
	</div>
</div>
    	
    	
    	