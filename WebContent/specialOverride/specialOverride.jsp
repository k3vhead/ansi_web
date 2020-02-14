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
        <bean:message key="page.label.specialOverride" />
    </tiles:put>
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/ticket.js"></script> 
    
        <style type="text/css">
        	#confirm-modal {
        		display:none;
        		text-align:center;
        	}
			#selectResultsContainer {
				display:none;
			}
			#selectResults {
				width:100%;
			}
			#updateFormContent {
				display:none;
				margin-top:20px;
			}
			#updateResults {
				display:none;
				width:100%;
				margin-top:20px;
			}
			.centered-text {
				text-align:center;
			}
			.confirm-text {
				width:100%;
				text-align:center;
				margin-top:20px;
			}
			.form-label {
				font-weight:bold;
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function(){
			;SPECIALOVERRIDE = {
				fieldTypeMapping : {
					"String":"text",
					"Integer":"text",
					"Date":"date",
					"Calendar":"date"
				},
				
				
				init : function() {
					SPECIALOVERRIDE.makeClickers();
					SPECIALOVERRIDE.makeModals();
					SPECIALOVERRIDE.doGetCall(null, null, SPECIALOVERRIDE.populateSelector);
				},
				
				
				
				doGetCall : function($outbound, $scriptName, $callback) {
					console.log("doGetCall");
					$url = "specialOverrides";
					if ( $scriptName != null ) {
						$url = $url + "/" + $scriptName;
					}
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						data: $outbound,
						statusCode: {
							200: function($data) {
								console.log($outbound, $scriptName, $data);
								$callback($outbound, $scriptName, $data);
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
				
				
				
				doPostCall : function() {
					console.log("doPostCall");
					var $scriptName = $("#updateForm").attr("data-scriptname");
					var $url = "specialOverrides/" + $scriptName;
					
					var jqxhr = $.ajax({
						type: 'POST',
						url: $url,
						data: $("#updateForm .updateFormField").serialize(),
						statusCode: {
							200: function($data) {
								if ( $data.responseHeader.responseCode == "SUCCESS") {
									$("#globalMsg").html($data.data.webMessages.GLOBAL_MESSAGE[0]).show().fadeOut(6000);
									$(".updateButton").hide();
									SPECIALOVERRIDE.makeSelectResultsTable("#updateResults", null, $scriptName, $data);
									var $continueButton = $('<input type="button" id="continueButton" value="Continue" />');
									$("#updateResults").append($continueButton);
									$("#continueButton").click(function() {
										$("#scriptSelectContainer").show();
										$(".script-title").html("");
										$("#selectResults").hide();
										$("#updateFormContent").hide();
										$("#updateResults").hide();
									});
								} else if ( $data.responseHeader.responseCode = "EDIT_FAILURE") {
									console.log("Bad data, but it worked");
									$.each($data.data.webMessages, function(key, messageList) {
										var $identifier = "#updateForm ." + key + "_err";
										console.log($identifier);
										$($identifier).html(messageList[0]).show();							
									});	
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
				
				
				
				doSelectCall : function($scriptName) {
					console.log("doSelectCall");
					var $outbound = {};
					$.each($("#selectForm .selectFormField"), function($index, $value) {
						$outbound[$value.name] = $($value).val();
					});
					console.log(JSON.stringify($outbound));
					SPECIALOVERRIDE.doGetCall($outbound, $scriptName, SPECIALOVERRIDE.processSelectCall);
				},
				
				
				
				makeClickers : function() {
					$("#specialOverride").change(function() {
						var $selectedScript = $("#specialOverride option:selected").val();
						$(".script-title").html($("#specialOverride option:selected").text());
						$("#selectResults").html("");
						$("#updateResults").html("");
						SPECIALOVERRIDE.doGetCall(null, $selectedScript, SPECIALOVERRIDE.makeSelectForm)
					});
					
					$("#goSpecialOverride").click(function() {
						var $selectedScript = $("#specialOverride option:selected").val();
						$(".script-title").html($("#specialOverride option:selected").text());
						$("#selectResults").html("");
						$("#updateResults").html("");
						SPECIALOVERRIDE.doGetCall(null, $selectedScript, SPECIALOVERRIDE.makeSelectForm)
					});
				},
				
				
				makeModals : function() {
					$("#confirm-modal" ).dialog({
						title:'Confirm Update',
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
									$("#updateResults").html("");
									$("#updateForm .err").html("");
									var $confirm = $('<input type="hidden" name="confirm" value="y" />');
									$("#updateForm").append($confirm);
									SPECIALOVERRIDE.doPostCall();
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
				
				
				
				makeSelectForm : function($outbound, $scriptName, $data) {
					console.log("makeSelectForm");
					$("#selectFormContent").html("");

					var $formTable = $("<table>");
					//$formTable.attr("data-scriptname",$scriptName);
					$formTable.attr("id","selectForm");
					$.each($data.data.selectList, function($index, $value) {
						var $row = $("<tr>");
						var $labelTd = $("<td>");
						$labelTd.append($value.label);
						$labelTd.attr("class","form-label");
						$row.append($labelTd);
						
						$inputTd = $("<td>");
						$inputField = $("<input>");
						$inputField.attr("name",$value.fieldName);
						$inputField.attr("type", SPECIALOVERRIDE.fieldTypeMapping[$value.fieldType]);
						$inputField.attr("class","selectFormField");
						$inputTd.append($inputField);
						$row.append($inputTd);
						
						$errTd = $("<td>");
						$errTd.attr("class","err " + $value.fieldName + "_err");
						$row.append($errTd);
						
						$formTable.append($row);
					});
					$("#selectFormContent").append($formTable);
					$("#selectFormContent").append('<input type="button" value="Go" id="selectGoButton" />');
					$("#selectFormContent").append('<input type="button" value="Cancel" id="selectCancelButton" />')
					
					$("#selectCancelButton").click(function() {
						$("#scriptSelectContainer").show();
						$(".select-item").hide();
					});
					
					$("#selectGoButton").click(function() {
						$("#selectForm .err").html("");
						SPECIALOVERRIDE.doSelectCall($scriptName);
					});
					
					$("#scriptSelectContainer").hide();
					$(".select-item").show();

				},
				
				
				
				
				makeSelectResultsTable : function($destination, $outbound, $scriptName, $data) {
					console.log("makeSelectResultsTable " +$destination);
					$("#selectFormContent").hide();
					$($destination).html("");
					
					$.each($data.data.resultSet, function($index1, $rowResult) {
						var $row = $("<tr>");
						$.each($rowResult, function($index2, $value) {
							var $col = $("<td>");
							var $text = $("<span>");
							if ( $index1 == 0 ) {								
								$text.attr("class", "form-label");
							}
							$text.append($value);
							$col.append($text);
							$row.append($col);
						});
						$($destination).append($row);
					});
					
					$($destination).show();
				},
				
				
				
				makeUpdateForm : function($outbound, $scriptName, $data) {
					console.log("makeUpdateForm");
					$("#updateFormContent").html("");

					var $selectFields = []
					$.each($data.data.selectList, function($index, $value) {
						$selectFields.push($value.fieldName);
					});
					
					var $formTable = $("<table>");
					$formTable.attr("data-scriptname",$scriptName);
					$formTable.attr("id","updateForm");
					$.each($data.data.updateList, function($index, $value) {
						var $row = $("<tr>");
						var $labelTd = $("<td>");
						$labelTd.append($value.label);
						$labelTd.attr("class","form-label");
						$row.append($labelTd);
						
						$inputTd = $("<td>");
						$inputField = $("<input>");
						$inputField.attr("name",$value.fieldName);
						if ( $selectFields.includes($value.fieldName)) {
							var $selectValue = $("#selectForm input[name='"+$value.fieldName+"']").val();
							$inputField.attr("type", "hidden");
							$inputField.attr("value", $selectValue);
							$inputField.attr("class","updateFormField");
							$inputTd.append($selectValue);
						} else {
							$inputField.attr("type", SPECIALOVERRIDE.fieldTypeMapping[$value.fieldType]);
							$inputField.attr("class","updateFormField");
						}
						$inputTd.append($inputField);
						$row.append($inputTd);
						
						$errTd = $("<td>");
						$errTd.attr("class","err " + $value.fieldName + "_err");
						$row.append($errTd);
						
						$formTable.append($row);
					});
					$("#updateFormContent").append($formTable);
					$("#updateFormContent").append('<input type="button" value="Go" id="updateGoButton" class="updateButton" />');
					$("#updateFormContent").append('<input type="button" value="Cancel" id="updateCancelButton" class="updateButton" />')
					
					$("#updateCancelButton").click(function() {
						$("#scriptSelectContainer").show();
						$(".select-item").hide();
						$(".update-item").hide();
					});
					
					$("#updateGoButton").click(function() {
						$("#confirm-modal").dialog("open");
					});
					
					$(".update-item").show();
				},
				
				
				
				populateSelector : function($outbound, $scriptName, $data) {
					console.log("populateSelector");
					$select = $("#specialOverride");
					$('option', $select).remove();
					$select.append(new Option("",""));
					
					$.each($data.data.scriptList, function($index, $value) {
						$select.append(new Option($value.description, $value.name));
					});
				},
				
				
				processSelectCall : function($outbound, $scriptName, $data ) {
					console.log("processSelectCall");
					if ( $data.responseHeader.responseCode == "SUCCESS") {
						SPECIALOVERRIDE.makeSelectResultsTable("#selectResults", $outbound, $scriptName, $data);
						SPECIALOVERRIDE.makeUpdateForm($outbound, $scriptName, $data);
					} else if ( $data.responseHeader.responseCode = "EDIT_FAILURE") {
						console.log("Bad data, but it worked");
						$.each($data.data.webMessages, function(key, messageList) {
							var $identifier = "#selectForm ." + key + "_err";
							console.log($identifier);
							$($identifier).html(messageList[0]).show();							
						});	
					} else {
						$("#globalMsg").html("Bad Response Code " + $data.responseHeader.responseCode + ". Contact Support").show();
						console.log("uh oh");
					}
				},
			};
			
			SPECIALOVERRIDE.init();
		});
        
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.specialOverride" /></h1>
    	<div id="scriptSelectContainer">
    		<select id="specialOverride"></select> <input type="button" value="Go" id="goSpecialOverride" />
    	</div>
    	
   		<h4 class="select-item"><span class="script-title"></span></h4>
   		<div id="selectFormContent" class="select-item"></div>
   		<table id="selectResults" class="select-item"></table>
   		<div id="updateFormContent" class="update-item"></div>
    	<table id="updateResults" class="update-item"></table>
    	
    	
    	<div id="confirm-modal">
    		<div class="confirm-text">
    			Are you sure?
    		</div>
    	</div>
   </tiles:put>
		
</tiles:insert>

