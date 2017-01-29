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



<%
    String namespace = (String)jspContext.getAttribute("namespace");
	String cssId = (String)jspContext.getAttribute("cssId");
	String cssClass = (String)jspContext.getAttribute("cssClass");
	
	String cssIdString = "id=\"" + cssId + "\"";			 
	String cssClassString = cssClass == null || cssClass.length()==0 ? "" : "class=\"" + cssClass + "\"";
%>


<div <%= cssIdString %> <%= cssClassString %> >
	<form id="<%=namespace%>_jobActivationForm">
		<table>
			<tr>
				<td colspan="1">DL %: <input type="text" name="<%=namespace %>_directLaborPct" style="width:40px;" />
				<td colspan="1">DL Budget: <input type="text" name="<%=namespace %>_directLaborBudget" style="width:40px;" /></td>
				<td colspan="4"># Floors <input type="text" name="<%=namespace %>_nbrFloors" style="width:100px;" /></td>
			</tr>
			<tr>
				<td colspan="3">
					Schedule (Auto/Manual):
					<select name="<%=namespace %>_automanual">
						<option value=""></option>
						<option value="auto">Auto</option>
						<option value="manual">Manual</option>
					</select>
				</td>
				<td colspan="3">
					Building Type:
					<select name="<%=namespace %>_buildingType">
						<option value=""></option>
						<option value="auto">Type 1</option>
						<option value="manual">Type 2</option>
					</select>
				</td>
			</tr>
			<tr>
				<td>Equipment:</td>
				<td colspan="5"><input type="text" name="<%=namespace %>_equipment" style="width:90%" /></td>
			</tr>
			<tr>
				<td>Washer Notes:</td>
				<td colspan="5"><input type="text" name="<%=namespace %>_washerNotes" style="width:90%" /></td>
			</tr>
			<tr>
				<td>OM Notes:</td>
				<td colspan="5"><input type="text" name="<%=namespace %>_omNotes" style="width:90%" /></td>
			</tr>
			<tr>
				<td>Billing Notes:</td>
				<td colspan="5"><input type="text" name="<%=namespace %>_billingNotes" style="width:90%" /></td>
			</tr>
		</table>
	</form>
</div>
 