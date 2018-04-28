<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="WEB-INF/theTagThing.tld" prefix="ansi" %>


<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        <bean:message key="menu.label.my" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">			
			#confirmModal {
				display:none;
				text-align:center;
			}
			.reqCol {
				width:12px;
				text-align:center;
			}
        </style>
        
        <script type="text/javascript">
        $(function() {   
        	;MYACCOUNT = {
       			init : function() {
       				MYACCOUNT.getUser();     
       				MYACCOUNT.makeConfirmModal();
       				MYACCOUNT.makeButtons();
       			},
       			
       			
       			clear : function() {
					$(".err").html('');
       			},
       			
       			
       			closeModals : function() {
    	    		$("#confirmModal").dialog("close");
    	    		$("#myAccountForm input[name='password']").val('');
    	    		$("#myAccountForm input[name='newPassword']").val('');
    	    		$("#myAccountForm input[name='confirmPassword']").val('');
    	    		$(".passwordErr").html('');
       			},
       			
       			
       			getUser : function() {
       				var jqxhr = $.ajax({
       					type: 'GET',
       					url: 'myAccount',
       					data: {},       					
       					statusCode: {
       						200: function($data) {
								console.debug($data);
								MYACCOUNT.populateForm($data.data);
    		       			},
    	       				404: function($data) {
    	        	    		$("#globalMsg").html("System Error: Contact Support").show();
    	        	    	},       						
    						403: function($data) {
    							$("#globalMsg").html("Session Timeout. Log in and try again").show();
    						},
    		       			500: function($data) {
    	            	    	$("#globalMsg").html("System Error: Contact Support").show();
    	            		}       						
       					},
       					dataType: 'json'
       				});
       			},
       			

       			makeButtons : function() {
       				$("#cancelButton").click(function() {
       					$("#confirmModal").dialog("open");
       				});
       				$("#cancelConfirmModal").click(function() {
       					MYACCOUNT.clear();
       					MYACCOUNT.closeModals();
       					MYACCOUNT.getUser();       					
       					$("#globalMsg").html("Update Canceled").show().fadeOut(3000);
       				});
       				$("#saveButton").click(function() {
       					MYACCOUNT.save();
       				});       		
       				$("#myAccountForm input").on('focus',function(e) {
       			    	var $name = $(this).attr("name");
       			    	var $errName = "#" + $name + "Err";
   			    		$($errName).html('');       					
       				});
       				$("#myAccountForm input").on('input',function(e) {
       			    	var $name = $(this).attr("name");
       			    	var $required = $(this).attr("data-required");
       			    	var $regex = $(this).attr("data-format");
       			    	if ( $required != null && $required=='true') {
       			    		value = $(this).val();
	       			    	var $errName = "#" + $name + "Err";
	       			    	var re = new RegExp($regex);
	       			    	if ( re.test(value) ) {
	       			    		$($errName).html('');
	       			    	} else {
	       			    		$($errName).html('Required Data is Missing');
	       			    	}    	
       			    	}
       			    });
       			},
       			
       			
       			makeConfirmModal : function() {
       				$("#confirmModal").dialog({
       					title:'Confirm',
       					autoOpen: false,
       					height: 160,
       					width: 300,
       					modal: true,
       					buttons: [
       						{
       							id: "cancelConfirmModal",
       							click: function() {
       								MYACCOUNT.closeModals();
       							}
       						},{
       							id: "saveConfirmModal",
       							click: function($event) {
       								$("#confirmModal").dialog("close");
       							}
       						}
       					],
       					close: function() {
       						MYACCOUNT.closeModals();
       					}
       				});
       	    		$('#saveConfirmModal').button('option', 'label', 'Continue');
       	    		$('#cancelConfirmModal').button('option', 'label', 'Cancel');
       			},
       			
       			
       			
       			
       			populateForm : function($user) {
       				$("#userId").html($user.userId);
       				$("#permissionGroup").html($user.permissionGroup);
       				$("#lastName").html($user.lastName);
       				$("#firstName").html($user.firstName);
       				$("#title").html($user.title);
       				$("#email").html($user.email);
       				$("#phone").html($user.phone);
       				$("#address1").html($user.address1);
       				$("#address2").html($user.address2);
       				$("#city").html($user.city);
       				$("#state").html($user.state);
       				$("#zip").html($user.zip);
       			},
       			
       			
       			processError : function($data) {
       				$.each($data.webMessages, function($index, $value) {
       					console.debug($index + "  " + $value);
     					$id = '#' + $index + 'Err';

						if ( $value != null && $value != '' ) {
							console.debug("Setting " + $id + " to " + $value);
							$($id).html($value);
						}      		
						
       				});	
       			},
       			
       			
       			processSuccess : function($data) {
       				MYACCOUNT.clear();
   					MYACCOUNT.closeModals();
   					MYACCOUNT.getUser();       					
   					$("#globalMsg").html("Update Complete").show().fadeOut(3000);
       			},
       			
       			
       			save : function() {
       				$outbound = {};
   					$outbound['password'] = $("#myAccountForm input[name='password']").val();	
       				$outbound['newPassword'] = $("#myAccountForm input[name='newPassword']").val();
       				$outbound['confirmPassword'] = $("#myAccountForm input[name='confirmPassword']").val();
       				
       				var jqxhr = $.ajax({
       					type: 'POST',
       					url: 'myAccount',
       					data: JSON.stringify($outbound),     					
       					statusCode: {
       						200: function($data) {
       							console.debug($data);
       							if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
       								MYACCOUNT.processError($data.data);
       							} else {
       								MYACCOUNT.processSuccess($data.data)
       							}
    		       			},
    	       				404: function($data) {
    	        	    		$("#globalMsg").html("System Error: Contact Support").show();
    	        	    		MYACCOUNT.clear();
    	        	    	},       						
    						403: function($data) {
    							$("#globalMsg").html("Session Timeout. Log in and try again").show();
    	        	    		MYACCOUNT.clear();
    						},
    		       			500: function($data) {
    	            	    	$("#globalMsg").html("System Error: Contact Support").show();
    	        	    		MYACCOUNT.clear();
    	            		}       						
       					},
       					dataType: 'json'
       				});
       			}
        	}
        	
        	MYACCOUNT.init();
        });
        </script>        
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1><bean:message key="menu.label.my" /></h1>
    	
    	<form id="myAccountForm">
	    	<table class="myAccountFormTable">
	    		<tr>
	    			<td class="reqCol">&nbsp;</td>
	    			<td class="formLabel"><span class="formLabelText"><bean:message key="field.label.userId" />:</span></td>
	    			<td class="formValue"><span class="formValueText" id="userId"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="reqCol">&nbsp;</td>
	    			<td class="formLabel"><span class="formLabelText"><bean:message key="field.label.permissionGroupName" />:</span></td>
	    			<td class="formValue"><span class="formValueText" id="permissionGroup"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="reqCol">&nbsp;</td>
	    			<td class="formLabel"><span class="formLabelText"><bean:message key="field.label.lastName" />:</span></td> 
	    			<td class="formValue"><span id="lastName" class="formValueText"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="reqCol">&nbsp;</td>
	    			<td class="formLabel"><span class="formLabelText"><bean:message key="field.label.firstName" />:</span></td>
	    			<td class="formValue"><span id="firstName" class="formValueText"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="reqCol">&nbsp;</td>
	    			<td class="formLabel"><span class="formLabelText"><bean:message key="field.label.userTitle" />:</span></td>
	    			<td class="formValue"><span id="title" class="formValueText"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="reqCol">&nbsp;</td>
	    			<td class="formLabel"><span class="formLabelText"><bean:message key="field.label.email" />:</span></td>
	    			<td class="formValue"><span id="email" class="formValueText"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="reqCol">&nbsp;</td>
	    			<td class="formLabel"><span class="formLabelText"><bean:message key="field.label.phone" />:</span></td>
	    			<td class="formValue"><span id="phone" class="formValueText"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="reqCol">&nbsp;</td>
	    			<td class="formLabel"><span class="formLabelText"><bean:message key="field.label.address" />:</span></td>
	    			<td class="formValue"><span id="address1" class="formValueText"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="reqCol">&nbsp;</td>
	    			<td class="formLabel"><span class="formLabelText"><bean:message key="field.label.address2" />:</span></td>
	    			<td class="formValue"><span id="address2" class="formValueText"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="reqCol">&nbsp;</td>
	    			<td class="formLabel"><span class="formLabelText"><bean:message key="field.label.city" />/<bean:message key="field.label.state" />/<bean:message key="field.label.zip" />:</span></td>
	    			<td class="formValue">
	    				<span id="city" class="formValueText"></span>,
	    				<span id="state" class="formValueText"></span>
	    				<span id="zip" class="formValueText"></span>
	    			</td>
	    		</tr>
				<tr>
					<td class="reqCol"><span class="required">*</span></td>
    				<td class="formLabel"><span class="formLabelText"><bean:message key="field.label.password" />:</span></td>
	    			<td class="formValue"><input type="password" class="formValueText" name="password" data-format=".+" data-required="true" /></td>
	    			<td><span id="passwordErr" class="err"></span></td>
   				</tr>
   				<tr>
   					<td class="reqCol"><span class="required">*</span></td>
    				<td class="formLabel"><span class="formLabelText"><bean:message key="field.label.password.new" />:</span></td>
	    			<td class="formValue"><input type="password" class="formValueText" name="newPassword" data-format=".+" data-required="true" /></td>
	    			<td><span id="newPasswordErr" class="err"></span></td>
   				</tr>
   				<tr>
   					<td class="reqCol"><span class="required">*</span></td>
    				<td class="formLabel"><span class="formLabelText"><bean:message key="field.label.password.confirm" />:</span></td>
	    			<td class="formValue"><input type="password" class="formValueText" name="confirmPassword" data-format=".+" data-required="true" /></td>
	    			<td><span id="confirmPasswordErr" class="err"></span></td>
   				</tr>	    		
	    		<tr>
	    			<td colspan="4" class="button-row">
	    				<input type="button" value="<bean:message key="page.text.cancel" />" id="cancelButton" />
	    				<input type="button" value="<bean:message key="page.text.save" />" id="saveButton" />
	    			</td>
	    		</tr>
	    	</table>
    	</form>
    	
    	<div id="confirmModal">
    		<bean:message key="page.text.cancelUpdate" />?
    	</div>
    </tiles:put>

</tiles:insert>

