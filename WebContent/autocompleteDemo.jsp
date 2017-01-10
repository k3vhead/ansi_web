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
        AutoComplete Demo
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<script type="text/javascript" src="jQuery/jquery.autocomplete.js"></script>
        <link href="jQuery/jquery.autocomplete.css" type="text/css" rel="stylesheet" />
		<style type="text/css">
			.ui-autocomplete-loading {
			  background: white url("images/ui-anim_basic_16x16.gif") right center no-repeat;
			}
		</style>
        
        <script type="text/javascript">        
        $(function() {                	
            $( "#searchTerm" ).autocomplete({
                source: "autoComplete",
                minLength: 2,
                appendTo: "#someElem",
                select: function( event, ui ) {
                  alert( "Selected: " + ui.item.id + " aka " + ui.item.label + " or " + ui.item.value );
                }
              });
            
            
            $("#testservlet").click(function($event) {   
            	var $httpType = $("#httptype option:selected" ).val();
            	var json = JSON.stringify($("#testjson").val());
            	var $url = $("#testurl").val();
            	console.debug($httpType);
            	console.debug(json);
            	console.debug($url);
            	
            	var jqxhr = $.ajax({
            	    type: $httpType,
            	    url: $url,
            	    data: json,
            	    success: function($data) {
            	    	var $value= JSON.stringify($data);
            	    	$("#dialogStatus").html("Status Code 200");
            	    	$("#dialogResponse").html($value);
            	    	$( "#dialog" ).dialog();
            	     },
            	     statusCode: {
            	    	403: function($data) {
                	    	var $value= JSON.stringify($data);
                	    	$("#dialogStatus").html("Status Code 403");
                	    	$("#dialogResponse").html($value);
                	    	$( "#dialog" ).dialog();
            	    	}, 
	         	    	404: function($data) {
	            	    	var $value= JSON.stringify($data);
	            	    	$("#dialogStatus").html("Status Code 404");
	            	    	$("#dialogResponse").html($value);
	            	    	$( "#dialog" ).dialog();
	        	    	}, 
            	    	401: function($data) {
                	    	var $value= JSON.stringify($data);
                	    	$("#dialogStatus").html("Status Code 401");
                	    	$("#dialogResponse").html($value);
                	    	$( "#dialog" ).dialog();
            	    	}, 
            	    	500: function($data) {
                	    	var $value= JSON.stringify($data);
                	    	$("#dialogStatus").html("Status Code 500");
                	    	$("#dialogResponse").html($value);
                	    	$( "#dialog" ).dialog();
            	    	} 
            	     },
            	     dataType: 'json'
            	});
            });
        });
        </script>        
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Auto Complete Demo</h1>

		<form action="#" id="demoForm">
			Type Here: <input type="text" id="searchTerm" />
		</form>
		<hr />
		<form id="testform">
			HTTP:
			<select id="httptype">
				<option value="GET">GET</option>
				<option value="POST">POST</option>
				<option value="DELETE">DELETE</option>
			</select>
			<br />
			URL: /localhost:8080/ansi_web/<input type="text" id="testurl" />
			<br />
			JSON String: <input type="text" id="testjson" />
			<br />
			<input type="button" id="testservlet" value="Go" />
		</form>
		
		<div id="dialog" title="Servlet Response">
  			<div id="dialogStatus" style="border-bottom:solid 1px #000000;"></div>
  			<div id="dialogResponse" style="border-bottom:solid 1px #000000;"></div>
  			<div style="float:right;">
  				See it pretty at <a href="http://jsonLint.com/" target="_new">jsonlint.com</a>
  			</div>
		</div>
    </tiles:put>

</tiles:insert>

