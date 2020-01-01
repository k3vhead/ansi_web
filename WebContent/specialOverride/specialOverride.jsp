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
			#formContainer {
				display:none;
			}
			.form-label {
				font-weight:bold;
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function(){
			;SPECIALOVERRIDE = {
				init : function() {
					SPECIALOVERRIDE.makeClickers();
					SPECIALOVERRIDE.makeSelectCall(null, null, SPECIALOVERRIDE.populateSelector);
				},
				
				
				
				makeClickers : function() {
					$("#specialOverride").change(function() {
						var $selectedScript = $("#specialOverride option:selected").val();
						SPECIALOVERRIDE.makeSelectCall(null, $selectedScript, SPECIALOVERRIDE.makeForm)
					});
					
					$("#goSpecialOverride").click(function() {
						var $selectedScript = $("#specialOverride option:selected").val();
						SPECIALOVERRIDE.makeSelectCall(null, $selectedScript, SPECIALOVERRIDE.makeForm)
					});
				},
				
				
				
				makeForm : function($data) {
					console.log("makeForm");
					$("#formContainer").html("");
					var $formTable = $("<table>");
					$.each($data.itemList, function($index, $value) {
						var $row = $("<tr>");
						var $labelTd = $("<td>");
						$labelTd.append($value.label);
						$labelTd.attr("class","form-label");
						$row.append($labelTd);
						
						$inputTd = $("<td>");
						$inputField = $("<input>");
						$inputField.attr("name",$value.fieldName);
						$inputTd.append($inputField);
						$row.append($inputTd);
						
						$errTd = $("<td>");
						$errTd.attr("class","err");
						$row.append($errTd);
						
						$formTable.append($row);
					});
					$("#formContainer").append($formTable);
					$("#formContainer").append('<input type="button" value="Go" id="selectGoButton" />');
					$("#formContainer").append('<input type="button" value="Cancel" id="selectCancelButton" />')
					
					$("#selectCancelButton").click(function() {
						$("#formContainer").hide();
						$("#selectContainer").show();
					});
					
					$("#selectContainer").hide();
					$("#formContainer").show();
				},
				
				
				
				makeSelectCall : function($outbound, $scriptName, $callback) {
					if ( $outbound == null ) {
						$parms = null;
					} else {
						$parms = JSON.stringify($outbound);
					}

					$url = "specialOverrides";
					if ( $scriptName != null ) {
						$url = $url + "/" + $scriptName;
					}
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						data: $parms,
						statusCode: {
							200: function($data) {
								console.log($data);
								$callback($data.data);
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
				
				
				populateSelector : function($data) {
					console.log("populateSelector");
					$select = $("#specialOverride");
					$('option', $select).remove();
					$select.append(new Option("",""));
					
					$.each($data.itemList, function($index, $value) {
						$select.append(new Option($value.description, $value.name));
					});
				},
			};
			
			SPECIALOVERRIDE.init();
		});
        
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.specialOverride" /></h1>
    	<div id="selectContainer">
    		<select id="specialOverride"></select> <input type="button" value="Go" id="goSpecialOverride" />
    	</div>
    	
    	<div id="formContainer">
    	</div>
   </tiles:put>
		
</tiles:insert>

