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
		myFunction: function() {
			console.log('running MYNAMESPACE.myFunction...');
		}  
	}
});
</script>     

<div <%= cssIdString %> <%= cssClassString %> >
	<table>
		<tr>
			<td>Job #:</td>
			<td><input type="text" name="jobNbr" style="width:40px;" />
			<td>Price Per Cleaning:</td>
			<td><input type="text" name="ppc" style="width:100px;" /></td>
		</tr>
		<tr>
			<td colspan="4">Service Description</td>
		</tr>
		<tr>
			<td colspan="4">
				<textarea cols="40" rows="8" name="serviceDescription"></textarea>
			</td>
		</tr>
	</table>
</div>
 