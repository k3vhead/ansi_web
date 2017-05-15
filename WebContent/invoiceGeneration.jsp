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

<%@ page import="com.ansi.scilla.web.actionForm.MessageForm" %>


<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Invoice Generation
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
        <script type="text/javascript" src="js/jobMaintenance.js"></script>
        <link rel="stylesheet" href="css/datepicker.css" type="text/css" />
        <style type="text/css">
        	#invoiceGenPanel {
        		width:80%;
        		padding-left:40px;
        	}

        </style>
        
        <script type="text/javascript">        
        $( document ).ready(function() {
        	$('.dateField').datepicker({
                prevText:'&lt;&lt;',
                nextText: '&gt;&gt;',
                showButtonPanel:true
            });

        	
        	
        	

        $("#goButton").click(function($event){
        	var $invoiceDate = $("#invoiceDate").val();
        	var $monthlyFlag = $("#monthlyFlag").prop('checked');
        	var $outbound = {'invoiceDate':$invoiceDate, 'monthlyFlag':$monthlyFlag}
            var jqxhr = $.ajax({
    			type: 'POST',
    			url: 'invoiceGeneration/',
    			data: JSON.stringify($outbound),
    			success: function($data) {
    				if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
    					$.each($data.data.webMessages, function (key, value) {
    						var $selectorName = "#" + key + "Err";
    						$($selectorName).show();
    						$($selectorName).html(value[0]).fadeOut(4000);
    					});
    				} else {
    					//$("#globalMsg").html($data.responseHeader.responseMessage).fadeOut(4000);
    		        	//$("#invoiceDate").val("");
    		        	//$("#monthlyFlag").prop('checked', false);
    		        	console.debug("Invoices genned");
    		        	$("#printForm input[name=message]").val("Success! Invoices Generated");
    					console.debug("form submit");
    				    $("#printForm").submit();
    		        	
    				}
    			},
    			statusCode: {
    				403: function($data) {
    					$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
    				},
    				500: function($data) {
         	    		$("#globalMsg").html("System Error: Contact Support").fadeIn(10);
         	    	} 
    			},
    			dataType: 'json'
    		});        	
        });
        
	

      });

		</script>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">    	
    	<h1>Invoice Generation</h1>
    	
    	<div id="invoiceGenPanel">
			<form action="#">
				<table>
					<tr>
						<td>Invoice Date: </td>
						<td><input type="text" class="dateField" id="invoiceDate"/></td>
						<td><span class="err" id="invoiceDateErr"></span></td>
					</tr>
					<tr>
						<td>Monthly: </td>
						<td><input type="checkbox" value="monthly" id="monthlyFlag" /></td>
						<td><span class="err" id="monthlyFlagErr"></span></td>
					</tr>
					<tr>
						<td colspan="2"><input type="button" value="Generate Invoices" id="goButton" /></td>
					</tr>
				</table>
			</form>
    	</div>
    	
    	<html:form action="invoicePrint" styleId="printForm">
    		<html:hidden property="<%= MessageForm.MESSAGE %>" />
    	</html:form>
    </tiles:put>

</tiles:insert>

