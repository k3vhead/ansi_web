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

<%@ page import="java.util.Calendar" %>


<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Ticket Status Report
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
			#ticketData {
				width:100%;
				display:none;
			}
			.ansi-stdrpt {
				width:1300px;
			}
			.ansi-stdrpt-header-table {
				border-top:solid 1px #404040;
				border-left:solid 1px #404040;
				border-right:solid 1px #404040;
			}
			.ansi-stdrpt-data-table {
				border-bottom:solid 1px #404040;
				border-left:solid 1px #404040;
				border-right:solid 1px #404040;
			}
			.ansi-stdrpt-title-text {
				font-weight:bold;
			}
			.ansi-stdrpt-title {
				text-align:center;
			}
			.ansi-stdrpt-subtitle {
				text-align:center;
			}
			.ansi-stdrpt-subtitle-text {
			}
			.ansi-stdrpt-notes-text {
				font-size:85%;
				font-family:times roman, serif;
				font-style:italic;
			}
			.ansi-stdrpt-col-header {
				background-color:#000000;
			}		
			.ansi-stdrpt-col-header-text {
				color: #e5e5e5;
				font-weight:bold;
			}
			.ansi-stdrpt-summary-text {
				font-weight:bold;
			}
			.ansi-stdrpt-banner {
				text-align:center;
			}
			.ansi-stdrpt-banner-text {
				font-size:120%;
				font-style:italic;
				font-weight:bold;
			}
			.css-data-header-left {
				text-align:left;
				width:75px;
			}
			.css-data-header-left-text {
				font-weight:bold;
			}
			.css-data-left {
				text-align:left;
				width:120px;
			}
			.css-data-left-text {
			}
			.css-data-header-right {
				text-align:left;
				width:120px;
			}
			.css-data-header-right-text {
				font-weight:bold;
			}
			.css-data-right {
				text-align:right;
			}
			.css-data-right-text {
			}
			.ansi-stdrpt-column-4 {
				text-align:right;
			}
			.ansi-stdrpt-column-6 {
				text-align:center;
			}
<%-- 
	
	private final String CSS_HEADER_ROW = "ansi-stdrpt-header-row";
	private final String CSS_EMPTY_HEADER = "ansi-stdrpt-empty-header";
	private final String CSS_EMPTY_HEADER_TEXT = "ansi-stdrpt-col-header-text";
	private final String CSS_DATA_ROW = "ansi-stdrpt-data-row";
	private final String CSS_SUMMARY_ROW = "ansi-stdrpt-summary-row";
	private final String CSS_SUMMARY = "ansi-stdrpt-summary";

	private final String CSS_NOTES_TEXT = "ansi-stdrpt-notes-text";
--%>
        </style>       
       
        <script type="text/javascript" src="js/ansi_utils.js"></script>
       
        <script type="text/javascript">
        $(document).ready(function() {
			// Populate the division list
        	$divisionList = ANSI_UTILS.getDivisionList();
			$("#divisionId").append(new Option("",""));
			$.each($divisionList, function(index, val) {
				var $displayValue = val.divisionNbr + "-" + val.divisionCode;
				$("#divisionId").append(new Option($displayValue, val.divisionId));
			});

			
			$("select").change(function () {
				var $selectedDiv = $('#divisionId option:selected').val();
        		if ($selectedDiv == '') {
        			$("#ticketReport").html("");
        			$("#ticketData").hide();
        		} else {
        			doPopulate($selectedDiv)
        		}
        	});
        	
			function doPopulate($selectedDiv) {
				var $url = "ticketStatusReport/" + $selectedDiv;
				var jqxhr = $.ajax({
					type: 'GET',
					url: $url,
					statusCode: {
						200: function($data) {
							$("#globalMsg").html("").hide();
							$("#ticketReport").html($data);
							$("#ticketData").fadeIn(3000);
							bindFunctions();
						},
						403: function($data) {
							$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
						}, 
						404: function($data) {
							$("#globalMsg").html("Invalid Division").show().fadeOut(5000);
						}, 
						405: function($data) {
							$("#globalMsg").html("System Error 405; Contact Support").show().fadeOut(5000);
						}, 
						500: function($data) {
							$("#globalMsg").html("System Error 500; Contact Support").show();
						} 
					},
					dataType: 'html'
				});
			}
        	        	

			function bindFunctions() {
	        	$(".ansi-stdrpt-data-row").bind("mouseover", function() {
	                $(this).css('background-color','#CCCCCC');
		        });
	        	$(".ansi-stdrpt-data-row").bind("mouseout", function() {
	                $(this).css('background-color','transparent');
		        });
			}
        	
			$('.ScrollTop').click(function() {
				$('html, body').animate({scrollTop: 0}, 800);
      	  		return false;
      	    });
             
             
             

            

        })
       </script>
        

	</tiles:put>
    <tiles:put name="content" type="string">
    	<h1 >Ticket Status</h1>
		<select id="divisionId">			
		</select>

		
		<div id="ticketData">
			<div id="ticketReport">
			</div>
		    <p align="center">
		    	<br>
		    	<a href="#" title="Scroll to Top" class="ScrollTop">Scroll To Top</a>
		    	</br>
		    </p>
		</div>			
    </tiles:put>
		
</tiles:insert>

