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
			
		}
	}
});
</script>     

<div <%= cssIdString %> <%= cssClassString %> >
	<table id="<%=namespace%>_jobTable">
			<tr>
				<td class="jobTableCell" colspan="2">
					<form name="jobForm">
						JOB: <input type="text" name="jobNbr" id="jobNbr" />
						Status: <input type="text" name="jobStatus" />
						Div: <select name="division"></select>
						<div style="float:right;">
							<input type="button" value="Activate Job" />
							<input type="button" value="Cancel Job" />
						</div>
					</form>
				</td>
			</tr>

	</table>

</div>
 