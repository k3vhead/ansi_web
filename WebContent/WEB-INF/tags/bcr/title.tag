<%@ tag description="Budget Control Panel 1" body-content="scriptless" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %> 
<table style="width:100%;">
	<colgroup>
		<col span="1" style="width:10%;" />
		<col span="1" style="width:20%;" />
		<col span="1" style="width:15%;" />
		<col span="1" style="width:10%;" />
		<col span="1" style="width:20%;" />
		<col span="1" style="width:25%;" />
	</colgroup>
	<tr>
		<td><span class="form-label">Created:</span></td>
		<td><span class="dateCreated"></span></td>
		<td></td>
		<td><span class="form-label">Division:</span></td>
		<td><span class="div"></span></td>
		<td>&nbsp</td>        						
	</tr>
	<tr>
		<td><span class="form-label">Modified:</span></td>
		<td><span class="dateModified"></span></td>
		<td></td>
		<td><span class="form-label">Manager:</span></td>
		<td><span class="managerFirstName"></span> <span class="managerLastName"></span></td>
	</tr>
	<tr>
		<td><span class="form-label">Year:</span></td>
		<td><span class="workYear"></span></td>
		<td></td>
		<td rowspan="4" style="text-align:center;">
			<webthing:view styleClass="new-bcr fa-3x green">New BCR</webthing:view>
			&nbsp;&nbsp;
			<a href="#" target="_new" class="all-ticket-spreadsheet"><webthing:excel styleClass="fa-3x blue">All Tickets</webthing:excel></a>
		</td>
	</tr>
	<tr>
		<td><span class="form-label">Month:</span></td>
		<td><span class="workMonth"></span> (<span class="workMonthName"></span>)</td>
	</tr>
	<tr>
		<td><span class="form-label">From:</span></td>
		<td><span class="firstOfMonth"></span></td>
	</tr>
	<tr>
		<td><span class="form-label">To:</span></td>
		<td><span class="lastOfMonth"></span></td>
	</tr>        					
</table>