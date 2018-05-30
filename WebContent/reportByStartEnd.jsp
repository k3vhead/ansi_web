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
  	    <link rel="stylesheet" href="css/datepicker.css" type="text/css" />
		<link rel="stylesheet" href="css/accordion.css" type="text/css" />
		<link rel="stylesheet" href="css/report.css" type="text/css" />
    	
        <style type="text/css">
			#resultsDiv {
				width:95%;
				display:none;
				margin-top:20px;
				border:solid 1px #000000;
				clear:both;
			}
			#xlsDiv {
				float:right;
				display:none;
				margin-right:100px;
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	
        	;REPORT_BY_START_END = {
        			
        		reportType : "<c:out value="${com_ansi_scilla_report_type}" />",
        		
       			init : function() {
       				// put init stuff here
       				REPORT_BY_START_END.doBindings();
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
       	     	  		REPORT_BY_START_END.go($clickEvent)
       	     	  	});
       			},
       			
       			go : function($clickEvent) {
       				//$("#resultsDiv").html('<div style="width:100%; text-align:center;">.... Thinking ...<br /><i class="fa fa-spinner fa-pulse fa-fw fa-5x"></i></div>')
       				$("#resultsDiv").fadeIn(2000);
       				// These values must match the 'doAccordion' function
       				var $reportDisplay = {'ul':'accordionList', 'li':'accordionItem', 'titleTag':'h4','titleClass':'accHdr'};
       				var $startDate = $("#startDate").val();
       				var $endDate = $("#endDate").val();
       				var $url = "report/" + REPORT_BY_START_END.reportType;
       				var $outbound = {'startDate':$startDate, 'endDate':$endDate, 'reportDisplay':$reportDisplay};
       				var $downloadUrl = $url+"?startDate=" + $startDate + "&endDate="+$endDate;
       				var jqxhr = $.ajax({
    		       		type: 'POST',
    		       		url: $url,
    		       		data: JSON.stringify($outbound),
    		       		statusCode: {
    		       			200: function($data) {
								$("#resultsDiv").html($data); 
								$("#xlsDownload").attr("href", $downloadUrl);
								REPORT_BY_START_END.doAccordion();
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
            	       	
			REPORT_BY_START_END.init();
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><c:out value="${com_ansi_scilla_report_title}" /></h1>
    	
		<div id="xlsDiv">
			<a href="#"  id="xlsDownload"><webthing:excel styleClass="fa-2x" style="cursor:pointer">Download</webthing:excel></a>
		</div>
		
    	<table>
    		<tr>
    			<td>Start Date:</td>
    			<td><input type="text" name="startDate" id="startDate" class="dateField" /></td>
    		</tr>
    		<tr>
    			<td>End Date:</td>
    			<td><input type="text" name="endDate" id="endDate" class="dateField" /></td>
    		</tr>    		
    		<tr>
    			<td colspan="2" style="text-align:center;"><input type="button" value="Go" id="goButton" /></td>
    		</tr>
    	</table>
		
		<div id="resultsDiv">	
			<webthing:thinking style="width:100%" />		
		</div>
    
	    <p align="center">
	    	<br>
	    	<a href="#" title="Scroll to Top" class="ScrollTop">Scroll To Top</a>
	    	</br>
	    </p>
    
    </tiles:put>
		
</tiles:insert>

