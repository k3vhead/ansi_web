<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>
<%@ attribute name="action" required="true" rtexprvalue="true" %>
<%
	String action = (String)jspContext.getAttribute("action");	
%>

<table style="width:100%" class="jobScheduleDisplayPanel">	
	<colgroup>
		<col style="width:20%;" />
		<col style="width:20%;" />
		<col style="width:20%;" />
		<col style="width:20%;" />
		<col style="width:20%;" />
	</colgroup>		    					
	<tr>
		<td><span class="formLabel">Last Run:</span></td>
		<td>99/99/99</td>
		<td><span class="formLabel">Repeated Annually:</span></td>
		<td>
			<ansi:hasPermission permissionRequired="QUOTE">
				<ansi:checkbox name="repeatedAnnually" action="<%= action %>" value="true" />
			</ansi:hasPermission>
		</td>
		<td rowspan="2" style="text-align:center;">
			<i style="cursor:pointer;" class="fa fa-list-alt fa-2x tooltip" aria-hidden="true" id="0_jobSchedule_showTicketList"><span class="tooltiptext">Ticket List</span></i>
		</td>
	</tr>
	<tr>
		<td><span class="formLabel">Next Due:</span></td>
		<td>somebody</td>
		<td><span class="formLabel">Created Thru:</span></td>
		<td>99/99/99</td>
	</tr>
</table>