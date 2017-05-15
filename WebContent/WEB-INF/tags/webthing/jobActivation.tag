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
<%@ attribute name="page" required="false" rtexprvalue="true" %>



<%
    String namespace = (String)jspContext.getAttribute("namespace");
	String cssId = (String)jspContext.getAttribute("cssId");
	String cssClass = (String)jspContext.getAttribute("cssClass");
	String page = (String)jspContext.getAttribute("page");
	
	String cssIdString = "id=\"" + cssId + "\"";			 
	String cssClassString = cssClass == null || cssClass.length()==0 ? "" : "class=\"" + cssClass + "\"";
	
	String disabled = "";
	if ( page.equalsIgnoreCase("JOB")) { 
		disabled = "disabled=\"disabled\"";
	}
%>


<div <%= cssIdString %> <%= cssClassString %> >
<% if ( page.equalsIgnoreCase("JOB")) { %> 
	<div style="float:right;margin-right:6px; margin-top:6px;">
		<span id="<%=namespace %>_activationEdit" style="cursor:pointer;" class="green fa fa-pencil" ari-hidden="true"></span>
	</div>
<% } %>
	<form id="<%=namespace%>_jobActivationForm">
		<table>
			<tr>
				<td colspan="1">DL %: <input type="text" name="<%=namespace%>_directLaborPct" id="<%=namespace%>_directLaborPct" style="width:40px;"  <%=disabled%>/><i id="directLaborPctErr" aria-hidden="true"></i></td>
				<td colspan="1">DL Budget: <input type="text" name="<%=namespace%>_directLaborBudget" id="<%=namespace%>_directLaborBudget" style="width:40px;"  <%=disabled%>/><i id="budgetErr" aria-hidden="true"></i></td>
				<td colspan="4"># Floors <input type="text" name="<%=namespace%>_nbrFloors" id="<%=namespace%>_nbrFloors" style="width:100px;"  <%=disabled%>/></td>
			</tr>
			<tr>
				<td colspan="3">
					Schedule (Auto/Manual):
					<select name="<%=namespace%>_automanual" id="<%=namespace%>_automanual" <%=disabled%>>
						<option value=""></option>
						<option value="auto">Auto</option>
						<option value="manual">Manual</option>
					</select>
				</td>
				<td colspan="3">
					Building Type:
					<select name="<%=namespace%>_buildingType" id="<%=namespace%>_buildingType" <%=disabled%>>
						<option value=""></option>
					</select>
				</td>
			</tr>
			<tr>
				<td>Equipment:</td>
				<td colspan="5"><input type="text" name="<%=namespace%>_equipment" id="<%=namespace%>_equipment" style="width:90%"  <%=disabled%>/></td>
			</tr>
			<tr>
				<td>Washer Notes:</td>
				<td colspan="5"><input type="text" name="<%=namespace%>_washerNotes" id="<%=namespace%>_washerNotes" style="width:90%"  <%=disabled%>/></td>
			</tr>
			<tr>
				<td>OM Notes:</td>
				<td colspan="5"><input type="text" name="<%=namespace%>_omNotes"  id="<%=namespace%>_omNotes" style="width:90%"  <%=disabled%>/></td>
			</tr>
			<tr>
				<td>Billing Notes:</td>
				<td colspan="5"><input type="text" name="<%=namespace%>_billingNotes"  id="<%=namespace%>_billingNotes" style="width:90%"  <%=disabled%>/></td>
			</tr>
		</table>
	</form>
</div>
 