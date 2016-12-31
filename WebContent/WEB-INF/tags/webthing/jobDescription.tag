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
			console.debug("Doing the jobdesc init");			
		},
		setJobFrequency: function($optionList,$selectedValue) {
			console.debug("Setting jf");
			var myoptions = null;
			var selectorName = "#<%=namespace%>_jobDescriptionForm select[name='<%=namespace%>_jobFrequency']";
			selectorName = "select[name='<%=namespace%>_jobFrequency']";
			console.debug(selectorName);
			
			var $select = $(selectorName);
			$('option', $select).remove();

			$.each($optionList, function(index, val) {
			    $select.append(new Option(val.display, val.abbrev));
			    console.debug(val);
			});
			//if ($select.prop) {
			//	myoptions = $select.prop('options');
			//} else {
			//	myoptions = $select.attr('options');
			//}
			//myoptions[myoptions.length] = new Option("","");
			//$.each($optionList, function(index, type) {
			//	console.debug("Setting " + type.value);
			//	myoptions[myoptions.length] = new Option(type.displayValue, type.value);
			//});
			//options[options.length] = new Option("Fr 1", "1");
			//options[options.length] = new Option("Fr 2", "2");
			if ( $selectedValue != null ) {
				$select.val($selectedValue);
			}
			$select.selectmenu();
		}

	}
});
</script>     

<div <%= cssIdString %> <%= cssClassString %> >
	<form name="<%= namespace %>_jobDescriptionForm">
		<table>
			<tr>
				<td>Job #:</td>
				<td><input type="text" name="<%=namespace%>_jobNbr" style="width:40px;" />
				<td>Price Per Cleaning:</td>
				<td><input type="text" name="<%=namespace%>_ppc" style="width:100px;" /></td>
			</tr>
			<tr>
				<td colspan="2">&nbsp;</td>
				<td>Job Frequency</td>
				<td>
					<select name="<%=namespace%>_jobFrequency">
						<option value=""></option>
						<option value="ff">FF</option>
						<option value="gg">GG</option>
					</select>
				</td>
			</tr>
			<tr>
				<td colspan="4">Service Description</td>
			</tr>
			<tr>
				<td colspan="4">
					<textarea cols="40" rows="8" name="<%=namespace%>_serviceDescription"></textarea>
				</td>
			</tr>
		</table>
	</form>
</div>
 