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
        <link rel="stylesheet" href="css/report.css" type="text/css" />
    	
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
        	
        	;PAST_DUE_REPORT = {
        			
        		reportType : "<c:out value="${com_ansi_scilla_report_type}" />",
        		
       			init : function() {
       	        	$divisionList = ANSI_UTILS.getDivisionList();
       				$("#divisionId").append(new Option("",""));
       				$.each($divisionList, function(index, val) {
       					var $displayValue = val.divisionNbr + "-" + val.divisionCode;
       					$("#divisionId").append(new Option($displayValue, val.divisionId));
       				});
       				PAST_DUE_REPORT.doBindings();
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
       	     	  	
       	     	  	
       			},
       			
       			
        	}
            	       	
        	PAST_DUE_REPORT.init();
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
		<h1>Past Due Report</h1>
		
		<form action="pastDueReport" method="post" target="_new">    	
    	
	    	<table>
	    		<tr>
	    			<td>Div ID:</td>
	    			<td><select id="divisionId" name="divisionId">	</select></td>
	    		</tr>
	    		<tr>
	    			<td>Start Date:</td>
	    			<td><input type="text" name="startDate" id="startDate" class="dateField" /></td>
	    		</tr>
	    		<tr>
	    			<td colspan="2" style="text-align:center;"><input type="submit" value="Go" id="goButton" /></td>
	    		</tr>
	    	</table>
		</form>
		
		
    
   
    
    </tiles:put>
		
</tiles:insert>

