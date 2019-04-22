<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ tag description="" body-content="scriptless" %> 
<%@ attribute name="tableName" required="true" rtexprvalue="true" %>
<%
    String tableName = (String)jspContext.getAttribute("tableName"); 
%>
<div class="lookup-container">
	<span style="float:right;"><ansi:hasPermission permissionRequired="CLAIMS_WRITE"><webthing:addNew styleClass="fa-1x" styleId="new-dl-button">Add Direct Labor</webthing:addNew></ansi:hasPermission></span>
	<span class="table-label-text">Direct Labor</span><br />
	<div class="lookup-table-container">
		<table id="<%= tableName %>">
			<thead></thead>
			<tbody></tbody>
			<tfoot>
				<tr>
					<td>&nbsp;</td>
					<td style="text-align:right;"><span style="font-weight:bold;">Total:</span></td>
					<td style="text-align:right;"></td>
					<td style="text-align:right;"></td>
					<td style="text-align:right;"></td>
					<td>&nbsp;</td>
				</tr>
			</tfoot>
		</table>
	</div>
</div>