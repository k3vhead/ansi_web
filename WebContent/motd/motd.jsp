<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>

<tiles:insert page="../layout.jsp" flush="true">
	<tiles:put name="title" type="string">
        <bean:message key="page.label.motd" />
    </tiles:put>
    
    <tiles:put name="headextra" type="string">
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/dashboard.js"></script>
    
        <style type="text/css">
        	#confirm-modal {
        		display:none;
        		text-align:center;
        	}
        	#motd {
        		display:inline;
        		margin-right:10px;
        		border:solid 1px #404040;
        		padding:3px;
        	}
        	#motd-edit-container {
        		display:none;
        	}
        	#motd-edit-container .motd-text {
        		width:400px;
        	}
			.form-label {
				font-weight:bold;
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function(){
			;MOTD = {
				init : function() {
					MOTD.getMotd();
					MOTD.makeClickers();
					MOTD.makeModals();
				},
				
				
				doDeleteCall : function() {
					var jqxhr = $.ajax({
						type: 'DELETE',
						url: 'motd',
						data: {},				
						statusCode: {
							200: function($data) {
								if ( $data.responseHeader.responseCode == 'SUCCESS') {										
									$("#globalMsg").html("Success").show().fadeOut(3000);
									$("#motd").html("");
								} else {
									$("#globalMsg").html("Undefined Error");										
								}
							},
							403: function($data) {
								$("#globalMsg").html("Session Expired. Login and try again").show();
							},
							404: function($data) {
								$("#globalMsg").html("Error 404. Contact Support").show();
							}, 
							405: function($data) {
								$("#globalMsg").html("Error 405. Contact Support").show();
							}, 
							500: function($data) {
								$("#globalMsg").html("Error 500. Contact Support").show();
							}, 
						},
						dataType: 'json'
					});
				},
				
				
				
				getMotd : function() {
					var jqxhr = $.ajax({
						type: 'GET',
						url: 'motd',
						data: {},				
						statusCode: {
							200: function($data) {
								if ( $data.responseHeader.responseCode == 'SUCCESS') {
									if ( $data.data.source == "ANSI") {
										$("#motd").html($data.data.motd);	
									} else {
										$("#motd").html("");
									}										
								}
							},
							403: function($data) {
								$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage).show();
							},
							404: function($data) {
								$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage).show();
							}, 
							405: function($data) {
								$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage).show();
							}, 
							500: function($data) {
								$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage).show();
							}, 
						},
						dataType: 'json'
					});
				},
				
				
				
				
				
				
				doPostCall : function() {
					console.log("doPostCall");
					
					var $outbound = {"message":$("#motd-edit-container input[name='motd']").val()};
					var jqxhr = $.ajax({
						type: 'POST',
						url: "motd",
						data: JSON.stringify($outbound),
						statusCode: {
							200: function($data) {
								if ( $data.responseHeader.responseCode == "SUCCESS") {
									$("#globalMsg").html("Success").show().fadeOut(3000);
									$("#motd").html($data.data.motd);
									$("#motd-edit-container").hide();
									$("#motd-display-container").show();
								} else if ( $data.responseHeader.responseCode = "EDIT_FAILURE") {
									console.log("Bad data, but it worked");
									$("#motd-message").html($data.data.webMessages['message'][0]).show();
								} else {
									$("#globalMsg").html("Bad Response Code " + $data.responseHeader.responseCode + ". Contact Support").show();
									console.log("uh oh");
								}
								
							},
							403: function($data) {
								$("#globalMsg").html("Session Timeout. Log in and try again").show();
							}, 
							404: function($data) {
								$("#globalMsg").html("System Error 404: Contact Support").show();
							}, 
							405: function($data) {
								$("#globalMsg").html("System Error 405: Contact Support").show();
							}, 
							500: function($data) {
								$("#globalMsg").html("System Error 500: Contact Support").show();
							} 
						},
						dataType: 'json'
					});
				},
				
				
				
				
				
				
				
				makeClickers : function() {
					$("#delete-button").click(function() {
						$("#confirm-modal" ).dialog("open");
					});
					
					$("#edit-button").click(function() {
						$("#motd-edit-container input[name='motd']").val($("#motd").html());
						$("#motd-message").html("");
						$("#motd-display-container" ).hide();
						$("#motd-edit-container").show();
					});
					
					$("#motd-cancel-button").click(function() {
						$("#motd-edit-container").hide();
						$("#motd-display-container").show();
					});
					
					$("#motd-go-button").click(function() {
						MOTD.doPostCall();
					});
				},
				
				
				makeModals : function() {
					$("#confirm-modal" ).dialog({
						title:'Confirm Delete',
						autoOpen: false,
						height: 190,
						width: 300,
						modal: true,
						buttons: [
							{
								id: "confirm-is-no-button",
								click: function() {
									$("#confirm-modal").dialog( "close" );
								}
							},{
								id: "confirm-is-yes-button",
								click: function($event) {
									$("#confirm-modal").dialog( "close" );									
									MOTD.doDeleteCall();
								}
							}	      	      
						],
						close: function() {
							$("#confirm-modal").dialog( "close" );
							//allFields.removeClass( "ui-state-error" );
						}
					});
					
					$('#confirm-is-no-button').button('option', 'label', 'No');
					$('#confirm-is-yes-button').button('option', 'label', 'Yes');
				},
				
				
				
				
				
				
				
				
			};
			
			MOTD.init();
		});
        
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1> <bean:message key="page.label.motd" /></h1>
    	<div id="motd-display-container">
    		<div id="motd"></div> <webthing:edit styleId="edit-button">Edit</webthing:edit> <webthing:delete styleId="delete-button">Delete</webthing:delete> 
    	</div>
    	<div id="motd-edit-container">
    		<span class="err" id="motd-message"></span><br />
    		<input type="text" name="motd" class="motd-text" /> <input type="button" id="motd-go-button" value="Save" /> <input type="button" id="motd-cancel-button" value="Cancel" /> 
    	</div>
    	
    	<div id="confirm-modal">
    		<div class="confirm-text">
    			Are you sure?
    		</div>
    	</div>
   </tiles:put>
		
</tiles:insert>

