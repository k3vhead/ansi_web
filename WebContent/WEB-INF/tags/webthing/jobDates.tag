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
			$("#<%=namespace %>_jobActivationForm select[name='<%=namespace %>_automanual']").selectmenu();
			$("#<%=namespace %>_jobActivationForm select[name='<%=namespace %>_buildingType']").selectmenu();
			$("#<%=namespace %>_jobActivationForm input[name='<%=namespace %>_nbrFloors']").spinner(
				{
					spin: function( event, ui ) {
						if ( ui.value < 1 ) {
							$( this ).spinner( "value", 1 );
								return false;
						}
					}
				}
			);
		},  
		setBuildingType: function ($optionList, $selectedValue) {
			var selectorName = "#<%=namespace %>_jobActivationForm select[name='<%=namespace %>_buildingType']";
			var $select = $(selectorName);
			if($select.prop) {
				var options = $select.prop('options');
			} else {
				var options = $select.attr('options');
			}
			$('option', $select).remove();
			
			options[options.length] = new Option("","");
			$.each($optionList, function(index, type) {
				options[options.length] = new Option(type.displayValue, type.value);
			});

			if ( $selectedValue != null ) {
				$select.val($selectedValue);
			}
			$select.selectmenu();
		}
	}
});
</script>     

<div <%= cssIdString %> <%= cssClassString %> >
	<table>
		<tr>
			<td>Proposed Date:</td>
			<td>MM/DD/YYYY</td>
			<td>Activation Date:</td>
			<td>MM/DD/YYYY</td>
		</tr>
		<tr>
			<td>Start Date:</td>
			<td>MM/DD/YYYY</td>
			<td>Cancel Date:</td>
			<td>MM/DD/YYYY</td>
		</tr>
		<tr>
			<td colspan="4">
				Cancel Reason:
				Reason goes here
			</td>
		</tr>
	</table>
</div>
 