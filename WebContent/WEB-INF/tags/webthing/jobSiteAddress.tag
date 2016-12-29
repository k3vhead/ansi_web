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
<script type="text/javascript">        
$(function() {        
	;<%=namespace%> = {
			init: function() {
				console.debug("inits");			
			},  
			
		}
});
</script>     

<div <%= cssIdString %> <%= cssClassString %> >
	<form id="<%=namespace%>_jobSiteAddress">
		<table>
			<tr>
				<td><b>Job Site</b></td>
				<td colspan="3"><input type="text" name="<%=namespace %>_name" style="width:90%" /></td>
			</tr>
			<tr>
				<td>Address:</td>
				<td colspan="3"><input type="text" name="<%=namespace %>_address1" style="width:90%" /></td>
			</tr>
			<tr>
				<td>Address 2:</td>
				<td colspan="3"><input type="text" name="<%=namespace %>_address2" style="width:90%" /></td>
			</tr>
			<tr>
				<td>City/State/Zip:</td>
				<td><input type="text" name="<%=namespace %>_city" style="width:90%" /></td>
				<td><input type="text" name="<%=namespace %>_state" style="width:90%" /></td>
				<td><input type="text" name="<%=namespace %>_zip" style="width:90%" /></td>
			</tr>
			<tr>
				<td>County:</td>
				<td><input type="text" name="<%=namespace %>_counry" style="width:90%" /></td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>Job Contact:</td>
				<td><input type="text" name="<%=namespace %>_jobContactName" style="width:90%" placeholder="<name>"/></td>
				<td colspan="2"><input type="text" name="<%=namespace %>_jobContactInfo" style="width:90%" placeholder="<phone,mobile,email>"/></td>
			</tr>
			<tr>
				<td>Site Contact:</td>
				<td><input type="text" name="<%=namespace %>_siteContactName" style="width:90%" placeholder="<name>"/></td>
				<td colspan="2"><input type="text" name="<%=namespace %>_siteContactInfo" style="width:90%" placeholder="<phone,mobile,email>"/></td>
			</tr>
		</table>
	</form>
</div>
 