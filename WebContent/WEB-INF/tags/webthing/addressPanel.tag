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
<%@ attribute name="label" required="true" rtexprvalue="true" %>



<%
    String namespace = (String)jspContext.getAttribute("namespace");
	String cssId = (String)jspContext.getAttribute("cssId");
	String cssClass = (String)jspContext.getAttribute("cssClass");
	String label = (String)jspContext.getAttribute("label");
	
	String cssIdString = "id=\"" + cssId + "\"";			 
	String cssClassString = cssClass == null || cssClass.length()==0 ? "" : "class=\"" + cssClass + "\"";
%>
<script type="text/javascript">               
	$(function() {        
		;<%=namespace%> = {
				init: function() {
					$.each($('input'), function () {
				        $(this).css("height","20px");
				        $(this).css("max-height", "20px");
				    });
					$("#<%=namespace %>_address select[name='<%=namespace %>_state']").selectmenu({ width : '150px', maxHeight: '400 !important', style: 'dropdown'});
					$("select[name='<%=namespace %>_city']").addClass("ui-corner-all");
					$("select[name='<%=namespace %>_zip']").addClass("ui-corner-all");
					$("#<%=namespace %>_address select[name='<%=namespace %>_country']").selectmenu({ width : '80px', maxHeight: '400 !important', style: 'dropdown'});
				
							
					console.debug("inits");			
				}, setCountry: function($optionList,$selectedValue) {
					var selectorName = "#<%=namespace%>_address select[name='<%=namespace%>_country']";
					selectorName = "select[name='<%=namespace%>_country']";
					
					var $select = $(selectorName);
					$('option', $select).remove();

					$select.append(new Option("",""));
					$.each($optionList, function(index, val) {
						console.log(val);
					    $select.append(new Option(val.abbrev));
					});
					
					if ( $selectedValue != null ) {
						$select.val($selectedValue);
					}
					$select.selectmenu();
				} , setStates: function($optionList,$selectedValue) {
					var selectorName = "#<%=namespace%>_address select[name='<%=namespace%>_state']";
					selectorName = "select[name='<%=namespace%>_state']";
					
					var $select = $(selectorName);
					$('option', $select).remove();

					$select.append(new Option("",""));
					$.each($optionList, function(index, val) {
						var group = $('<optgroup label="' + val.abbrev + '" />');
							$.each(val.stateList, function(){
								//$('<option />').html(this.display).appendTo(group);
								$(group).append("<option value='"+this.abbreviation+"'>"+this.display+"</option>");
							});
							group.appendTo($select);
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
	<form id="<%=namespace%>_address">
		<table>
			<tr>
				<td><b><%= label %></b></td>
				<td colspan="3"><input type="text" name="<%=namespace %>_name" style="width:315px" /></td>
			</tr>
			<tr>
				<td style="width:85px;">Address:</td>
				<td colspan="3"><input type="text" name="<%=namespace %>_address1" style="width:315px" /></td>
			</tr>
			<tr>
				<td>Address 2:</td>
				<td colspan="3"><input type="text" name="<%=namespace %>_address2" style="width:315px" /></td>
			</tr>
			<tr>
			<td colspan="4" style="padding:0; margin:0;">
				<table style="width:415px;border-collapse: collapse;padding:0; margin:0;">
				<tr>
					<td>City/State/Zip:</td>
					<td><input type="text" name="<%=namespace %>_city" style="width:90px;" /></td>
					<td><select name="<%=namespace %>_state" id="<%=namespace %>_state" style="width:85px !important;max-width:85px !important;"></select></td>
					<td><input type="text" name="<%=namespace %>_zip" style="width:47px !important" /></td>
				</tr>
				</table>
			</td>
			</tr>
			<tr>
				<td>County:</td>
				<td><input type="text" name="<%=namespace %>_county" style="width:90%" /></td>
				<td colspan="2">
					<table style="width:180px">
						<tr>
							<td>Country:</td>
							<td align="right"><select name="<%=namespace %>_country" id="<%=namespace %>_country"></select></td>
						</tr>
					</table>
				
				
				</td>
				
			</tr>
			<tr>
				<td>Job Contact:</td>
				<td style="width:140px;"><input type="text" name="<%=namespace %>_jobContactName" style="width:125px" placeholder="<name>"/></td>
				<td colspan="2"><input type="text" name="<%=namespace %>_jobContactInfo" style="width:170px" placeholder="<phone,mobile,email>"/></td>
			</tr>
			<tr>
				<td>Site Contact:</td>
				<td style="width:140px;"><input type="text" name="<%=namespace %>_siteContactName" style="width:125px" placeholder="<name>"/></td>
				<td colspan="2"><input type="text" name="<%=namespace %>_siteContactInfo" style="width:170px" placeholder="<phone,mobile,email>"/></td>
			</tr>
		</table>
	</form>
</div>
 