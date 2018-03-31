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
        My Account
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
			#passwordModal {
				display:none;
			}
			#confirmModal {
				display:none;
			}
        </style>
        
        <script type="text/javascript">
        $(function() {   
        	;MYACCOUNT = {
       			init : function() {
       				MYACCOUNT.getUser();     
       				MYACCOUNT.makeConfirmModal();
       				MYACCOUNT.makePasswordModal();
       				MYACCOUNT.makeButtons();
       			},
       			
       			
       			clear : function() {
					$(".err").html('');
       			},
       			
       			
       			closeModals : function() {
   					$("#passwordModal").dialog("close");
    	    		$("#confirmModal").dialog("close");
    	    		$("#passwordForm input[name='password']").val('');
    	    		$("#passwordForm input[name='newPassword']").val('');
    	    		$("#passwordForm input[name='confirmPassword']").val('');
    	    		$("#confirmForm input[name='password']").val('');
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
       					MYACCOUNT.clear();
       					MYACCOUNT.closeModals();
       					MYACCOUNT.getUser();       					
       					$("#globalMsg").html("Update Canceled").show().fadeOut(3000);
       				});
       				$("#saveButton").click(function() {
       					MYACCOUNT.clear();
       					$("#confirmModal").dialog("open");
       				});
       				$("#changePasswordButton").click(function() {
       					MYACCOUNT.clear();
       					$("#passwordModal").dialog("open");
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
       								MYACCOUNT.save()
       							}
       						}
       					],
       					close: function() {
       						MYACCOUNT.closeModals();
       					}
       				});
       	    		$('#saveConfirmModal').button('option', 'label', 'Save');
       	    		$('#cancelConfirmModal').button('option', 'label', 'Cancel');
       			},
       			
       			
       			makePasswordModal : function() {
       				$("#passwordModal").dialog({
       					title:'New Password',
       					autoOpen: false,
       					height: 215,
       					width: 350,
       					modal: true,
       					buttons: [
       						{
       							id: "cancelPasswordModal",
       							click: function() {
       								MYACCOUNT.closeModals();
       							}
       						},{
       							id: "savePasswordModal",
       							click: function($event) {
       								MYACCOUNT.save()
       							}
       						}
       					],
       					close: function() {
       						MYACCOUNT.closeModals();
       					}
       				});
       	    		$('#savePasswordModal').button('option', 'label', 'Save');
       	    		$('#cancelPasswordModal').button('option', 'label', 'Cancel');
       			},
       			
       			populateForm : function($user) {
       				$("#userId").html($user.userId);
       				$("#permissionGroup").html($user.permissionGroup);
       				$("#myAccountForm input[name='lastName']").val($user.lastName);
       				$("#myAccountForm input[name='firstName']").val($user.firstName);
       				$("#myAccountForm input[name='title']").val($user.title);
       				$("#myAccountForm input[name='email']").val($user.email);
       				$("#myAccountForm input[name='phone']").val($user.phone);
       				$("#myAccountForm input[name='address1']").val($user.address1);
       				$("#myAccountForm input[name='address2']").val($user.address2);
       				$("#myAccountForm input[name='city']").val($user.city);
       				$("#myAccountForm input[name='state']").val($user.state);
       				$("#myAccountForm input[name='zip']").val($user.zip);
       			},
       			
       			
       			processError : function($data) {
       				var $messageMap = {
       					'password':'.passwordErr',
       					'newPassword':'.passwordErr',
       					'confirmPassword':'.passwordErr',
       					'city':'#address3Err',
       					'state':'#address3Err',
       					'zip':'#address3Err'
       				};
       				var isPasswordError = false;
       				$.each($data.webMessages, function($index, $value) {
       					console.debug($index + "  " + $value);
       					var $id = $messageMap[$index];
       					if ( $id == null || $id == '') {
       						$id = '#' + $index + 'Err';
       					}
       					if ( $index == 'password' || $index == 'newPassword' || $index == 'confirmPassword') {
       						console.debug("Setting password err to true")
       						isPasswordError = true;
       					}
						if ( $value != null && $value != '' ) {
							console.debug("Setting " + $id + " to " + $value);
							$($id).html($value);
						}      		
						
       				});	
       				console.debug("Pass err: " + isPasswordError);
       				if ( isPasswordError == false ) {
       					MYACCOUNT.closeModals();
       				}
       			},
       			
       			
       			processSuccess : function($data) {
       				MYACCOUNT.clear();
   					MYACCOUNT.closeModals();
   					MYACCOUNT.getUser();       					
   					$("#globalMsg").html("Update Complete").show().fadeOut(3000);
       			},
       			
       			
       			save : function() {
       				$outbound = {};
       				$outbound['lastName'] = $("#myAccountForm input[name='lastName']").val();
       				$outbound['firstName'] = $("#myAccountForm input[name='firstName']").val();
       				$outbound['title'] = $("#myAccountForm input[name='title']").val();
       				$outbound['email'] = $("#myAccountForm input[name='email']").val();
       				$outbound['phone'] = $("#myAccountForm input[name='phone']").val();
       				$outbound['address1'] = $("#myAccountForm input[name='address1']").val();
       				$outbound['address2'] = $("#myAccountForm input[name='address2']").val();
       				$outbound['city'] = $("#myAccountForm input[name='city']").val();
       				$outbound['state'] = $("#myAccountForm input[name='state']").val();
       				$outbound['zip'] = $("#myAccountForm input[name='zip']").val();
       				
       				if ( $("#passwordForm input[name='password']").val() != '' ) {
       					$outbound['password'] = $("#passwordForm input[name='password']").val();	
       				} else {
       					$outbound['password'] = $("#confirmForm input[name='password']").val();
       				}
       				
       				$outbound['newPassword'] = $("#passwordForm input[name='newPassword']").val();
       				$outbound['confirmPassword'] = $("#passwordForm input[name='confirmPassword']").val();
       				
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
    	<h1>My Account</h1>
    	
    	<form id="myAccountForm">
	    	<table class="myAccountFormTable">
	    		<tr>
	    			<td class="formLabel"><span class="formLabelText">User Id:</span></td>
	    			<td class="formValue"><span class="formValueText" id="userId"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="formLabel"><span class="formLabelText">Permission Group:</span></td>
	    			<td class="formValue"><span class="formValueText" id="permissionGroup"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="formLabel"><span class="formLabelText">Last Name:</span></td>
	    			<td class="formValue"><input type="text" class="formValueText" name="lastName" /></td>
	    			<td class="formErr"><span class="err" id="lastNameErr"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="formLabel"><span class="formLabelText">First Name:</span></td>
	    			<td class="formValue"><input type="text" class="formValueText" name="firstName" /></td>
	    			<td class="formErr"><span class="err" id="firstNameErr"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="formLabel"><span class="formLabelText">Title:</span></td>
	    			<td class="formValue"><input type="text" class="formValueText" name="title" /></td>
	    			<td class="formErr"><span class="err" id="titleErr"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="formLabel"><span class="formLabelText">EMail:</span></td>
	    			<td class="formValue"><input type="text" class="formValueText" name="email" /></span></td>
	    			<td class="formErr"><span class="err" id="lastNameErr"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="formLabel"><span class="formLabelText">Phone:</span></td>
	    			<td class="formValue"><input type="text" class="formValueText" name="phone" /></td>
	    			<td class="formErr"><span class="err" id="phoneErr"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="formLabel"><span class="formLabelText">Address:</span></td>
	    			<td class="formValue"><input type="text" class="formValueText" name="address1" /></td>
	    			<td class="formErr"><span class="err" id="addressErr"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="formLabel"><span class="formLabelText">Address2:</span></td>
	    			<td class="formValue"><input type="text" class="formValueText" name="address2" /></td>
	    			<td class="formErr"><span class="err" id="address2Err"></span></td>
	    		</tr>
	    		<tr>
	    			<td class="formLabel"><span class="formLabelText">City/State/Zip:</span></td>
	    			<td class="formValue">
	    				<input type="text" class="formValueText" name="city" style="width:115px"; />
	    				<input type="text" class="formValueText" name="state" style="width:25px"; />
	    				<input type="text" class="formValueText" name="zip" style="width:50px"; />
	    			</td>
	    			<td class="formErr"><span class="err" id="address3Err"></span></td>
	    		</tr>
	    		<tr>
	    			<td colspan="3" class="button-row">
	    				<input type="button" value="Change Password" id="changePasswordButton" />
	    				<input type="button" value="Cancel" id="cancelButton" />
	    				<input type="button" value="Save" id="saveButton" />
	    			</td>
	    		</tr>
	    	</table>
    	</form>
    	
    	
    	
    	<div id="confirmModal">
    		<span class="err passwordErr"></span>
    		<form id="confirmForm">
    			<table id="confirmFormTable">
    				<tr>
	    				<td class="formLabel"><span class="formLabelText">Password</span></td>
		    			<td class="formValue">
		    				<input type="password" class="formValueText" name="password" />
		    			</td>
    				</tr>
    			</table>
    		</form>
    	</div>
    	
    	
    	<div id="passwordModal">
    		<span class="err passwordErr"></span>
    		<form id="passwordForm">
    			<table id="passwordFormTable">
    				<tr>
	    				<td class="formLabel"><span class="formLabelText">Password</span></td>
		    			<td class="formValue">
		    				<input type="password" class="formValueText" name="password" />
		    			</td>
    				</tr>
    				<tr>
	    				<td class="formLabel"><span class="formLabelText">New Password</span></td>
		    			<td class="formValue">
		    				<input type="password" class="formValueText" name="newPassword" />
		    			</td>
    				</tr>
    				<tr>
	    				<td class="formLabel"><span class="formLabelText">Confirm</span></td>
		    			<td class="formValue">
		    				<input type="password" class="formValueText" name="confirmPassword" />
		    			</td>
    				</tr>
    			</table>
    		</form>
    	</div>
    </tiles:put>

</tiles:insert>

