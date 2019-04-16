<%@ tag description="" body-content="scriptless" %> 
<%@ attribute name="tableName" required="true" rtexprvalue="true" %>
<%
    String tableName = (String)jspContext.getAttribute("tableName"); 
%>
<div class="lookup-container">
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