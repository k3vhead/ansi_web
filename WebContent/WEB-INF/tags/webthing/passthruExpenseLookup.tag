<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ tag description="" body-content="scriptless" %> 
<%@ attribute name="tableName" required="true" rtexprvalue="true" %>
<%
    String tableName = (String)jspContext.getAttribute("tableName"); 
%>
<div class="lookup-container">
	<span style="float:right;"><ansi:hasPermission permissionRequired="CLAIMS_WRITE"><webthing:addNew styleClass="fa-1x" styleId="new-pe-button">Add Passthru Expense</webthing:addNew></ansi:hasPermission></span>
	<span class="table-label-text">Passthru Expense</span><br />
	<div class="lookup-table-container">
		<table id="<%= tableName %>">
			<thead></thead>
			<tbody></tbody>
			<tfoot>
				<tr>
					<td>&nbsp;</td> <%-- date --%>
					<td style="text-align:right;"><span style="font-weight:bold;">Total:</span></td> <%-- type --%>
					<td style="text-align:right;"></td> <%-- volume --%>
					<td style="text-align:left;">&nbsp;</td> <%-- washer --%>
					<td style="text-align:left;">&nbsp;</td> <%-- notes --%>
				</tr>
			</tfoot>
		</table>
	</div>
</div>