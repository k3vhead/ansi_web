<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>
<div id="${param.id}">
	<div class="timesheet-err err"></div>
	<input type="hidden" name="action" />
	<input type="hidden" name="row" />
	<table class="edit-form">
		<colgroup>
			<col style="width:20%" />
			<col style="width:10%" />
			<col style="width:25%" />
			<col style="width:10%" />
			<col style="width:10%" />
			<col style="width:25%" />
		</colgroup>
		<tr class="employee-edit-row">
			<td><span class="form-label">Division: </span></td>
			<td colspan="3">
				<select name="divisionId" class="update-field">
					<option value=""></option>
					<ansi:selectOrganization active="true" type="DIVISION" />
				</select>
			</td>
			<td colspan="2"><span class="divisionIdErr err"></span></td>
		</tr>
		<tr class="employee-edit-row">
			<td><span class="form-label">Week Ending: </span></td>
			<td colspan="3"><input type="date" name="weekEnding" class="update-field" /></td>
			<td colspan="2"><span class="weekEndingErr err"></span></td>
		</tr>
		<tr class="employee-edit-row">
			<td><span class="form-label">State: </span></td>
			<td colspan="3">
				<select name="state" class="update-field">
					<option value=""></option>
					<ansi:localeStateSelect format="select" />
				</select>
			</td>
			<td colspan="2"><span class="stateErr err"></span></td>
		</tr>
		<tr class="employee-edit-row">
			<td><span class="form-label">City: </span></td>
			<td colspan="3"><input type="text" name="city" class="update-field" /></td>
			<td colspan="2"><span class="cityErr err"></span></td>
		</tr>
		<tr class="employee-edit-row">
			<td><span class="form-label">Employee Code: </span></td>
			<td colspan="3"><input type="text" name="employeeCode" class="update-field" disabled="" /></td>
			<td colspan="2"><span class="employeeCodeErr err"></span></td>
		</tr>
		<tr class="employee-edit-row">
			<td><span class="form-label">Employee Name: </span></td>
			<td colspan="3"><input type="text" name="employeeName" class="update-field" /></td>
			<td colspan="2"><span class="employeeNameErr err"></span></td>
		</tr>
	</table>
</div>