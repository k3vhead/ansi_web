<%@ tag 
    description="" 
    body-content="empty" 
    
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>

<%@ attribute name="namespace" required="true" rtexprvalue="true" %>
<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="page" required="false" rtexprvalue="true" %>



<%
    String namespace = (String)jspContext.getAttribute("namespace");
	String cssClass = (String)jspContext.getAttribute("cssClass");
	
	String cssClassString = cssClass == null || cssClass.length()==0 ? "" : "class=\"" + cssClass + "\"";
%>



<div id="<%=namespace %>_activateJobForm" title="Activate Job" class="ui-widget" style="display:none;">
	<p class="validateTips">All fields are required.</p>
 
	<form id="jobActivationForm">
  		<table>
  			<tr>
  				<td><bean:message key="form.hdr.activationdate" />:</td>
  				<td><input type="text" class="<%=namespace %>_datefield" name="<%=namespace %>_activationDate" id="<%=namespace %>_activationDate" /></td>
  			</tr>
  			<tr>
  				<td><bean:message key="form.hdr.startdate" />:</td>
  				<td><input type="text" class="<%=namespace %>_datefield" name="<%=namespace %>_startDate" id="<%=namespace %>_startDate" /></td>
  			</tr>
  		</table>
	</form>
</div>

<div id="<%=namespace %>_cancelJobForm" title="Cancel Job" class="ui-widget" style="display:none;">
	<p class="validateTips">All fields are required.</p>
 
	<form id="<%= namespace %>_jobCancelForm">
  		<table>
  			<tr>
  				<td><bean:message key="form.hdr.canceldate" />:</td>
  				<td><input type="text" class="<%=namespace %>_datefield" name="<%=namespace %>_cancelDate" id="<%=namespace %>_cancelDate" /></td>
  			</tr>
  			<tr>
  				<td><bean:message key="form.hdr.cancelreason" />:</td>
  				<td><input type="text" name="<%=namespace %>_cancelReason" id="<%=namespace %>_cancelReason" /></td>
  			</tr>
  		</table>
	</form>
</div>




 