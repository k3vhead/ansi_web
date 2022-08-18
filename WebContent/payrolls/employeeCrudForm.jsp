<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>

<div id="${param.id}" class="modal-window">
	<input type="hidden" name="rowId" />
	<table>
		<tr>
			<td class="form-label">Employee Code:</td>
			<td>&nbsp;</td>
			<td><input type="text" name="employeeCode" /></td>
			<td><span class="err employeeCodeErr"></span></td>
		</tr>
		<tr>
			<td class="form-label">Company:</td>
			<td><webthing:attention styleClass="attn companyCodeAttn">Change to existing value</webthing:attention></td>
			<td>
				<select name="companyCode">
					<option value=""></option>
					<ansi:selectPayrollCompany active="true" />
				</select>
			</td>
			<td><span class="err companyCodeErr"></span></td>
		</tr>
		<tr>
			<td class="form-label">Division:</td>
			<td><webthing:attention styleClass="attn divisionIdAttn">Change to existing value</webthing:attention></td>
			<td>
				<select name="divisionId">
					<option value=""></option>
					<ansi:selectOrganization active="true" type="DIVISION" />
				</select>
			</td>
			<td><span class="err divisionIdErr"></span></td>
		</tr>
		<tr>
			<td class="form-label">Name:</td>
			<td><webthing:attention styleClass="attn nameAttn">Change to existing value</webthing:attention></td>
			<td>
				<input name="firstName" type="text" placeholder="First" />
				<input name="middleInitial" type="text" placeholder="MI" style="width:15px;" />
				<input name="lastName" type="text" placeholder="Last" />
			</td>
			<td><span class="err nameErr"></span></td>
		</tr>				
		<tr>
			<td class="form-label">Department:</td>
			<td><webthing:attention styleClass="attn departmentAttn">Change to existing value</webthing:attention></td>
			<td><input name="departmentDescription" type="text" /></td>
			<td><span class="err departmentDescriptionErr"></span></td>
		</tr>
		<tr>
			<td class="form-label">Status</td>
			<td><webthing:attention styleClass="attn statusAttn">Change to existing value</webthing:attention></td>
			<td>
				<select name="status">
					<option value=""></option>
					<webthing:employeeStatus />
				</select>
			</td>
			<td><span class="err statusErr"></span></td>
		</tr>
		<tr>
			<td class="form-label">Termination Date:</td>
			<td><webthing:attention styleClass="attn terminationDateAttn">Change to existing value</webthing:attention></td>
			<td><input name="terminationDate" type="date" /></td>
			<td><span class="err terminationDateErr"></span></td>
		</tr>
		<tr>
			<td class="form-label">Union Member:</td>
			<td><webthing:attention styleClass="attn unionMemberAttn">Change to existing value</webthing:attention></td>
			<td><input name="unionMember"  type="checkbox" value="1" /></td>
			<td><span class="err unionMemberErr"></span></td>
		</tr>
		<tr>
			<td class="form-label">Union Code:</td>
			<td><webthing:attention styleClass="attn unionCodeAttn">Change to existing value</webthing:attention></td>
			<td><input name="unionCode" class="unionInput" type="text" /></td>
			<td><span class="err unionCodeErr"></span></td>
		</tr>
		<tr>
			<td class="form-label">Union Rate:</td>
			<td><webthing:attention styleClass="attn unionRateAttn">Change to existing value</webthing:attention></td>
			<td><input name="unionRate" style="height:12px;" class="unionInput" type="text"  placeholder="0.00"  /></td>
			<td><span class="err unionRateErr"></span></td>
		</tr>
		<!-- <tr>
			<td class="form-label">Process Date:</td>
			<td><input name="processDate" type="date" /></td>
			<td><span class="err processDateErr"></span></td>
		</tr> -->
		<tr>
			<td class="form-label">Notes:</td>
			<td><webthing:attention styleClass="attn notesAttn">Change to existing value</webthing:attention></td>
			<td><input name="notes" type="text" /></td>
			<td><span class="err notesErr"></span></td>
		</tr>
	</table>
	<span class="attn footnote"><i class="red fas fa-exclamation-circle"></i> : Value is different than existing</span>
</div>