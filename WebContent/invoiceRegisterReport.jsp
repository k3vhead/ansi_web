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
        Invoice Register Report
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
        	#goButton {
        		display:none;
        	}
        	#xlsDownloadDiv {
        		display:none;
        	}
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

			var $today = new Date();
			$("#startMonth").append(new Option("",""));
			$("#startMonth").append(new Option("January", <%= Calendar.JANUARY %>));
			$("#startMonth").append(new Option("February", <%= Calendar.FEBRUARY %>));
			$("#startMonth").append(new Option("March", <%= Calendar.MARCH %>));
			$("#startMonth").append(new Option("April", <%= Calendar.APRIL %>));
			$("#startMonth").append(new Option("May", <%= Calendar.MAY %>));
			$("#startMonth").append(new Option("June", <%= Calendar.JUNE %>));
			$("#startMonth").append(new Option("July", <%= Calendar.JULY %>));
			$("#startMonth").append(new Option("August", <%= Calendar.AUGUST %>));
			$("#startMonth").append(new Option("September", <%= Calendar.SEPTEMBER %>));
			$("#startMonth").append(new Option("October", <%= Calendar.OCTOBER %>));
			$("#startMonth").append(new Option("November", <%= Calendar.NOVEMBER %>));
			$("#startMonth").append(new Option("December", <%= Calendar.DECEMBER %>));
			$("#startMonth").val($today.getMonth()-2); // last month, using java numbers: JANUARY=0
			
			var $startYear = $today.getFullYear();
			$("#startYear").append(new Option("",""));
			$("#startYear").append(new Option($startYear, $startYear));
			$startYear--;
			$("#startYear").append(new Option($startYear, $startYear));
			$startYear--;
			$("#startYear").append(new Option($startYear, $startYear));
			$startYear--;
			$("#startYear").append(new Option($startYear, $startYear));
			$startYear--;
			$("#startYear").append(new Option($startYear, $startYear));
			$("#startYear").val($today.getFullYear());
			
			
			$("#divisionId").change(function($event) {
				checkData();
			});
			$("#startMonth").change(function($event) {
				checkData();
			});
			$("#startYear").change(function($event) {
				checkData();
			});

			function checkData() {
				var $selectedDiv = $('#divisionId option:selected').val();
				var $selectedMonth = $('#startMonth option:selected').val();
				var $selectedYear = $('#startYear option:selected').val();
				if ( $selectedDiv=='' || $selectedMonth=='' || $selectedYear=='' ) {
					$("#goButton").hide();
				} else {
					$("#goButton").fadeIn(1000);
				}
			}
			
			$("#goButton").click(function () {
				var $selectedDiv = $('#divisionId option:selected').val();
				var $selectedMonth = $('#startMonth option:selected').val();
				var $selectedYear = $('#startYear option:selected').val();
				var $downloadUrl = "invoiceRegisterReport/" + $selectedDiv + "?month=" + $selectedMonth + "&year=" + $selectedYear;
				console.debug($downloadUrl);
				$("#xlsDownload").attr("href", $downloadUrl);
				$("#downloader").click();
			});
			
			
			function xxx() {
				var jqxhr = $.ajax({
					type: 'GET',
					url: $url,
					data: {'month':$selectedMonth,'year':$selectedYear},
					statusCode: {
						200: function($data) {
							var newWindow = window.open("", "new window", "width=200, height=100");							
							newWindow.document.write(data);
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
    	<h1 >Invoice Register</h1>
    	<table>
    		<tr>
    			<td><span class="formLabel">Division:</span></td>
    			<td><select id="divisionId"></select></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Start:</span></td>
    			<td><select id="startMonth"></select><select id="startYear"></select></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">&nbsp;</span></td>
    			<td><input type="button" id="goButton" value=" Go " /></td>
    		</tr>
    	</table>
		
    	<div id="xlsDownloadDiv">
    		<a href="#" id="xlsDownload" target="_new"><span id="downloader">Download</span></a>
    	</div>
		
		
		<%--
		<div id="ticketData">
			<div id="ticketReport">
			</div>
		    <p align="center">
		    	<br>
		    	<a href="#" title="Scroll to Top" class="ScrollTop">Scroll To Top</a>
		    	</br>
		    </p>
		</div>
		 --%>			
    </tiles:put>
		
</tiles:insert>

