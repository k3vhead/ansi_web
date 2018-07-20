<%@ page contentType="text/html"%>

<%@ taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/sql.tld" prefix="sql"%>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing"%>
<%@ taglib tagdir="/WEB-INF/tags/quote" prefix="quote"%>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>

<html>
	<head>
	</head>
	<body>
		<table style="width:100%" class="jobActivationEditPanel">
			<tr>
				<td colspan="4">
					<div style="display:inline; margin-right:10px;">
						<span class="formLabel">DL%:</span>
						<input type="text" name="job-activation-dl-pct" data-apiname="directLaborPct" style="width:75px;" />
					</div>
					<div style="display:inline; margin-right:10px;">
						<span class="formLabel">DL Budget:</span>
						<input type="text" name="job-activation-dl-budget" data-apiname="budget" style="width:75px;" />
					</div>
					<div style="display:inline; margin-right:10px;">
						<span class="formLabel"># Floors:</span>
						<input type="text" name="job-activation-floors" data-apiname="floors" style="width:75px;" />
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<span class="formLabel">Schedule:</span>
					<select name="job-activation-schedule" data-apiname="requestSpecialScheduling">
						<option value="auto">Auto</option>
						<option value="manual">manual</option>
					</select>
				</td>
			</tr>
			<tr>
				<td><span class="formLabel">Equipment:</span></td>
				<td colspan="3"><input type="text" name="job-activation-equipment" data-apiname="equipment" style="width:225px;" /></td>
			</tr>
			<tr>
				<td><span class="formLabel">Washer Notes:</span></td>
				<td colspan="3"><input type="text" name="job-activation-washer-notes" data-apiname="washerNotes" style="width:225px;" /></td>
			</tr>
			<tr>
				<td><span class="formLabel">OM Notes:</span></td>
				<td colspan="3"><input type="text" name="job-activation-om-notes" data-apiname="omNotes" style="width:225px;" /></td>
			</tr>
			<tr>
				<td><span class="formLabel">Billing Notes:</span></td>
				<td colspan="3"><input type="text" name="job-activation-billing-notes" data-apiname="billingNotes" /></td>
			</tr>
		</table>
	</body>
</html>