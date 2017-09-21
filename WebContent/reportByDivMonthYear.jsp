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
        <c:out value="${com_ansi_scilla_report_title}" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
  	    <link rel="stylesheet" href="css/datepicker.css" type="text/css" />
  	    <link rel="stylesheet" href="css/accordion.css" type="text/css" />
    	
        <style type="text/css">
			#resultsDiv {
				width:95%;
				display:none;
				margin-top:20px;
				border:solid 1px #000000;
			}
			#xlsDiv {
				float:right;
				display:none;
				margin-right:100px;
			}
        </style>
        
        <script type="text/javascript">
        
		$(document).ready(function() {
        	
        	;REPORT_BY_DIV_MONTH_YEAR = {
        			
        		reportType : "<c:out value="${com_ansi_scilla_report_type}" />",
        		
       			init : function() {
       	        	$divisionList = ANSI_UTILS.getDivisionList();
       				$("#divisionId").append(new Option("",""));
       				$.each($divisionList, function(index, val) {
       					var $displayValue = val.divisionNbr + "-" + val.divisionCode;
       					$("#divisionId").append(new Option($displayValue, val.divisionId));
       				});
       				REPORT_BY_DIV_MONTH_YEAR.doBindings();
       			},
       			
       			doAccordion : function() {
       				$('ul.accordionList').accordion({
						//autoHeight: true,
						heightStyle: "content",
						alwaysOpen: true,
						header: 'h4',
						fillSpace: false,
						collapsible: false,
						active: true
					});
       			},
       			
       			doBindings : function() {
       	     	  	$('.ScrollTop').click(function() {
						$('html, body').animate({scrollTop: 0}, 800);
       	      	  		return false;
       	     	    });
       				
       	     	  	$('.dateField').datepicker({
                        prevText:'&lt;&lt;',
                        nextText: '&gt;&gt;',
                        showButtonPanel:true
					});
       	     	  	
       	     	  	$('#goButton').click(function($clickEvent) {
       	     	  		REPORT_BY_DIV_MONTH_YEAR.go($clickEvent)
       	     	  	});
       			},
       			
       			go : function($clickEvent) {
       				$("#resultsDiv").fadeIn(2000);
       				// These values must match the 'doAccordion' function
       				var $reportDisplay = {'ul':'accordionList', 'li':'accordionItem', 'titleTag':'h4','titleClass':'accHdr'}; 
       				var $divisionId = $("#divisionId").val();
       				var $month = $("#month").val();
       				var $year = $("#year").val();
       				var $url = "report/" + REPORT_BY_DIV_MONTH_YEAR.reportType;
       				var $outbound = {'divisionId':$divisionId, 'month':$month, 'year':$year, 'reportDisplay':$reportDisplay};
       				var $downloadUrl = $url+"?divisionId=" + $divisionId + "&month="+$month + "&year="+$year;
       				var jqxhr = $.ajax({
    		       		type: 'POST',
    		       		url: $url,
    		       		data: JSON.stringify($outbound),
    		       		statusCode: {
    		       			200: function($data) {
								$("#resultsDiv").html($data); 
								$("#xlsDownload").attr("href", $downloadUrl);
								REPORT_BY_DIV_MONTH_YEAR.doAccordion();
								$("#xlsDiv").show(); 				
    		       			},			       		
    	       				404: function($data) {
    	        	    		$("#globalMsg").html("System Error: Contact Support").show();
    	        	    	},
    						403: function($data) {
    							$("#globalMsg").html("Session Timeout. Log in and try again");
    						},
    		       			500: function($data) {
    	            	    	$("#globalMsg").html("System Error: Contact Support").show();
    	            		},
    		       		},
    		       		dataType: 'html'
    		       	});
       			}
        	}
            	       	
        	REPORT_BY_DIV_MONTH_YEAR.init();
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><c:out value="${com_ansi_scilla_report_title}" /></h1>
    	
    	<div id="xlsDiv" class="tooltip">
			<span class="tooltiptext">Download</span>
			<a href="#"  id="xlsDownload"><i class="fa fa-file-excel-o fa-2x" aria-hidden="true" style="cursor:pointer"></i></a>
		</div>
    	
    	<table>
    		<tr> 
    			<td>Div ID:</td>
    			<td><select id="divisionId">		</select></td>
    		</tr>
    		<tr>
    			<select id="month">  
					<option value=""></option>
					<option value="1">January</option>
					<option value="2">February</option>
					<option value="3">March</option>
					<option value="4">April</option>
					<option value="5">May</option>
					<option value="6">June</option>
					<option value="7">July</option>
					<option value="8">August</option>
					<option value="9">September</option>
					<option value="10">October</option>
					<option value="11">November</option>
					<option value="12">December</option>
 		</select>
    		</tr>
    		<tr>
    			<td>Year:</td>
    			<td><input type="text" name="year" id="year" class="dateField" /></td>
    		</tr>    		
    		<tr>
    			<td colspan="2" style="text-align:center;"><input type="button" value="Go" id="goButton" /></td>
    		</tr>
    	</table>
		
		<div id="resultsDiv">
			.... Thinking ...<br />
			<div style="width:80px;">
			<i class="fa fa-spinner fa-pulse fa-fw fa-5x"></i>
			</div>
		</div>
		
		
    <p align="center">
    	<br>
    	<a href="#" title="Scroll to Top" class="ScrollTop">Scroll To Top</a>
    	</br>
    </p>
    
    </tiles:put>
		
</tiles:insert>

