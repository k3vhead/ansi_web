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
        <bean:message key="page.label.reportSubscription" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/callNote.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
        <style type="text/css">
			#selection-container {
				width:88%;
				float:left;
        		border:solid 1px #000000;				
			}
			#selection-menu-container {
				width:11%;
				float:right;
			}
			.activeRowCol {
				backround-color:#F9F9F9;
			}
        	.selection-container {
        		width:100%;
        		display:none;
        	}
        	#delete-modal {
        		display:none;
        	}
        	th {
        		text-align:center;
        	}
        	#filter-container {
        		width:402px;
        		float:right;
        	}
        	#new-calendar-modal {
        		display:none;
        	}
        	.action-link {
        		cursor:pointer;
        	}
        	.form-label {
        		font-weight:bold;
        	}
        	.monthLabel {
        		font-weight:bold;
        		background-color:#000000;
        		color:#FFFFFF;
        	}
        	.pre-edit {
				background-color:#CC6600;				
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;REPORT_SUBSCRIPTION = {
        		divisionList : [],
        		reportList : [],
        		companyList : [],
        		regionList : [],
        		
        		init : function() {
        			REPORT_SUBSCRIPTION.makeLists();
        			REPORT_SUBSCRIPTION.makeClickers();
        		},
        		
        		
        		doSubscription : function($reportId, $divisionId, $subscribe) {
        			if ( $subscribe ) {
        				$word = "Subscribe"
        			} else {
        				$word = "Unsubscribe"
        			}
        			console.log($word + " to " + $reportId + " for " + $divisionId);
        			$("#globalMsg").html($word + " to " + $reportId + " for " + $divisionId).show().fadeOut(1000);
        		},
        		
        		
        		
        		makeClickers : function() {
        			var $menuOpt = $("#selection-menu-container input[name='subscription-selector']");
           			$menuOpt.click(function($event, $value) {
           				var $selection = $(this).val();
           				$(".selection-container").hide();
           				var $selector = "#" + $selection + "-selection-container";
           				$($selector).show();
           			});
        		},
        		
        		
        		

        		makeDivisionTable : function($reportList, $divisionList) {
        			console.log("makeDivisionTable");
        			var $table = $("<table>");
        			var $hdrRow = $("<tr>");
        			$hdrRow.append($("<td>"));  // blank box in the corner
        			$hdrRow.append($("<td>").append("All"));
    				$.each($divisionList, function($divIdx, $division) {
    					$divTD = $("<td>");
    					$divTD.addClass("div-" + $division.divisionId);
    					$divTD.append($division.divisionDisplay)
    					$hdrRow.append($divTD);
    				});
        			$table.append($hdrRow);
        			
        			var $allRow = $("<tr>");
        			var $allLabel = $("<td>").append("All");
        			var $allTD = $("<td>").append($('<input type="checkbox" class="all-division-report-selector" />'));
        			$allRow.append($allLabel);
        			$allRow.append($allTD);

        			$.each($divisionList, function($divIdx, $division) {
    					$divTD = $("<td>");
    					$divTD.addClass("div-" + $division.divisionId);
    					$divTD.append($('<input type="checkbox" class="all-report-selector" data-division="'+$division.divisionId+'" />' ));
    					$allRow.append($divTD);
    				});
        			$table.append($allRow);
        			
        			$.each( $reportList, function($reportIdx, $report) {
        				var $tr = $("<tr>");
        				$tr.addClass("report-" + $report.reportId);
        				$reportTD = $("<td>");
    					$reportTD.append($report.description)
    					$tr.append($reportTD);
    					$allTD = $($('<input type="checkbox" class="all-division-selector" data-report="'+$report.reportId+'" />'));
    					$allTD.append("All");
    					$tr.append($allTD);
        				$.each($divisionList, function($divIdx, $division) {
        					$divTD = $("<td>");
        					$divTD.addClass("div-" + $division.divisionId);
        					$divTD.append($('<input type="checkbox" name="'+$report.reportId+"-" + $division.divisionId +'" class="report-selector" data-report="'+$report.reportId+'" data-division="'+$division.divisionId+'"/>'));
        					$tr.append($divTD);
        				});
        				$table.append($tr);	      				
        			});
        			
        			$("#division-selection-container").html("");
        			$("#division-selection-container").append($table);
        			$.each( $reportList, function($reportIdx, $report) {        				
        				$.each($divisionList, function($divIdx, $division) {
        					$selector = ".report-" + $report.reportId + " .div-"+$division.divisionId;
        					$($selector).mouseover(function($event) {
        						$(".report-" + $report.reportId).css('background-color','#F9F9F9');
        						$(".div-"+$division.divisionId).css('background-color','#F9F9F9');
        						//$($division).css('background-color','#F9F9F9');
        					});
        					$($selector).mouseout(function($event) {
        						$(".report-" + $report.reportId).css('background-color','transparent');
        						$(".div-"+$division.divisionId).css('background-color','transparent');
        					//	$($division).css('background-color','transparent');
        					});
        				});
        			});       
        			$("#division-selection-container .report-selector").click(function($event) {
        				var $reportId = $(this).attr("data-report");
        				var $divisionId = $(this).attr("data-division");
        				var $subscribe = $(this).prop("checked");
        				REPORT_SUBSCRIPTION.doSubscription($reportId, $divisionId, $subscribe);
        			});
        			$("#division-selection-container .all-report-selector").click(function($event) {
        				var $divisionId = $(this).attr("data-division");
        				var $subscribe = $(this).prop("checked");
        				console.log("all for " + $divisionId + " " + $subscribe);
        				$.each( REPORT_SUBSCRIPTION.reportList, function($index, $report) {
        					$selector = '#division-selection-container input[name="'+$report.reportId+'-'+ $divisionId+'"]';
        					$subscribed = $($selector).prop("checked");
        					if ( $subscribe != $subscribed ) {
        						$($selector).prop("checked", $subscribe);
        						REPORT_SUBSCRIPTION.doSubscription($report.reportId, $divisionId, $subscribe);	
        					}
        				});        				
        			});
        			$("#division-selection-container .all-division-selector").click(function($event) {
        				var $reportId = $(this).attr("data-report");
        				var $subscribe = $(this).prop("checked");
        				console.log("all for " + $reportId + " " + $subscribe);
        				$.each( REPORT_SUBSCRIPTION.divisionList, function($index, $division) {
        					$selector = '#division-selection-container input[name="'+$reportId+'-'+ $division.divisionId+'"]';
        					$subscribed = $($selector).prop("checked");
        					if ( $subscribe != $subscribed ) {
        						$($selector).prop("checked", $subscribe);
        						REPORT_SUBSCRIPTION.doSubscription($reportId, $division.divisionId, $subscribe);	
        					}
        				});        				
        			});        			
        			$("#division-selection-container .all-division-report-selector").click(function($event) {
        				var $subscribe = $(this).prop("checked");
        				console.log("all for all " + $subscribe);
        				$("#division-selection-container .all-division-selector").prop("checked", $subscribe);
        				$("#division-selection-container .all-report-selector").prop("checked", $subscribe);
        				$.each( $reportList, function($reportIdx, $report) {        				
            				$.each($divisionList, function($divIdx, $division) {
            					$selector = '#division-selection-container input[name="'+$report.reportId+'-'+ $division.divisionId+'"]';
            					$subscribed = $($selector).prop("checked");
            					if ( $subscribe != $subscribed ) {
            						$($selector).prop("checked", $subscribe);
            						REPORT_SUBSCRIPTION.doSubscription($report.reportId, $division.divisionId, $subscribe);	
            					}
            				});
            			});  
        			});
        			
        
        			$.each( $("#selection-menu-container input[name='subscription-selector']"), function($index, $value) {
        				if ( $value.value == 'division' ) {
        					$(this).click();
        				}        				
        			});
        		},
        		
        		
        		
        		
        		makeExecutiveTable : function($reportList) {
        			console.log("makeExecutiveTable");
        			var $table = $("<table>");
        			var $hdrRow = $("<tr>");
        			$hdrRow.append( $("<td>").append("All") );    
        			$hdrRow.append( $("<td>").append($('<input type="checkbox" />')) );   
        			$table.append($hdrRow);
        			

        			
        			var $allRow = $("<tr>");
        			var $allLabel = $("<td>").append("All");
        			var $allTD = $("<td>").append($('<input type="checkbox" class="all-report-selector" />'));
        			$allRow.append($allLabel);
        				
        			//$allRow.append($allTD);
					
        			//$table.append($allRow);
        			
        			$.each( $reportList, function($reportIdx, $report) {
        				if ( $report["executiveReport"] == true ) {
	        				var $tr = $("<tr>");
	        				$tr.addClass("report-" + $report.reportId);
	        				$reportTD = $("<td>");
	    					$reportTD.append($report.description)
	    					$tr.append($reportTD);
        					$td = $("<td>");
        					//$selector = ".report-" + $report.reportId;
        					//$allTD = $($('<input type="checkbox" class="all-report-selector" data-report="'+$selector+'" />'));
        					//$allTD.append("All");
        					//$tr.append($allTD);
        					$td.append($('<input type="checkbox" class="report-selector" data-report="'+$report.reportId+'" />'));
        					$tr.append($td);
	        				$table.append($tr);	        				
        				}
        			});
        			
        			$("#executive-selection-container").html("");
        			$("#executive-selection-container").append($table);
        			
        			
        			$.each( $reportList, function($reportIdx, $report) {        				
        				$.each($reportList, function($reportIdx, $report) {
        					$selector = ".report-" + $report.reportId;
        					$($selector).mouseover(function($event) {
        						$(".report-" + $report.reportId).css('background-color','#F9F9F9');
        						$(".div-"+$report.reportId).css('background-color','#F9F9F9');
        					});
        					$($selector).mouseout(function($event) {
        						$(".report-" + $report.reportId).css('background-color','transparent');
        						$(".div-"+$report.reportId).css('background-color','transparent');
        					});
        				});
        			});       
        			$("#executive-selection-container .report-selector").click(function($event) {
        				var $reportId = $(this).attr("data-report");
        				//var $divisionId = $(this).attr("data-division");
        				var $subscribe = $(this).prop("checked");
        				REPORT_SUBSCRIPTION.doSubscription($reportId, $subscribe);
        			});
        			
        			
        		/*	$("#executive-selection-container .all-report-selector").click(function($event) {
        				//var $reportId = $(this).attr("data-report");
        				var $subscribe = $(this).prop("checked");
        				$("#executive-selection-container .all-report-selector").prop("checked", $subscribe);
        				console.log("all for " + $reportId + " " + $subscribe);
        				$.each( $reportList, function($reportIdx, $report) {
        					$selector = '#executive-selection-container input[name="'+$report.reportId+'-'+ '"]';
        					$subscribed = $($selector).prop("checked");
        					if ( $subscribe != $subscribed ) {
        						$($selector).prop("checked", $subscribe);
        						REPORT_SUBSCRIPTION.doSubscription($report.reportId, $subscribe);	
        					}
        				});        				
        			});
        	/*		$("#executive-selection-container .all-division-selector").click(function($event) {
        				var $reportId = $(this).attr("data-report");
        				var $subscribe = $(this).prop("checked");
        				console.log("all for " + $reportId + " " + $subscribe);
        				$.each( REPORT_SUBSCRIPTION.divisionList, function($index, $division) {
        					$selector = '#executive-selection-container input[name="'+$reportId+'-'+ $division.divisionId+'"]';
        					$subscribed = $($selector).prop("checked");
        					if ( $subscribe != $subscribed ) {
        						$($selector).prop("checked", $subscribe);
        						REPORT_SUBSCRIPTION.doSubscription($reportId, $division.divisionId, $subscribe);	
        					}
        				});        				
        			});        	*/	
        			$("#executive-selection-container .all-report-selector").click(function($event) {
        				var $subscribe = $(this).prop("checked");
        				console.log("all for all " + $subscribe);
        				//$("#executive-selection-container .all-division-selector").prop("checked", $subscribe);
        				$("#executive-selection-container .all-report-selector").prop("checked", $subscribe);
        				$.each( $reportList, function($reportIdx, $report) {        				
            			//	$.each($divisionList, function($divIdx, $division) {
            					$selector = '#executive-selection-container input[name="'+$report.reportId+'"]';
            					$subscribed = $($selector).prop("checked");
            					if ( $subscribe != $subscribed ) {
            						$($selector).prop("checked", $subscribe);
            						REPORT_SUBSCRIPTION.doSubscription($report.reportId, $subscribe);	
            					}
            				}); 
            			});  
        	//		}); 
        			
        
        			$.each( $("#selection-menu-container input[name='subscription-selector']"), function($index, $value) {
        				if ( $value.value == 'report' ) {
        					$(this).click();
        				}        				
        			});
        		},
        	
        		
        		
        		makeGroupTable : function($reportList, $companyList, $filter, $destination) {
        			console.log("makeGroupTable");
        			var $table = $("<table>");
        			var $hdrRow = $("<tr>");
        			$hdrRow.append($("<td>"));  // blank box in the corner
        			$hdrRow.append( $("<td>").append("All"));
    				$.each($companyList, function($companyIdx, $company) {
    					$hdrRow.append($("<td>").append($company.name));
    				});
        			$table.append($hdrRow);
        			
        			var $allRow = $("<tr>");
        			var $allLabel = $("<td>").append("All");
        			var $allTD = $("<td>").append($('<input type="checkbox" class="all-division-report-selector" />'));
        			$allRow.append($allLabel);
        			$allRow.append($allTD);
        			$.each($companyList, function($companyIdx, $company) {
    					$allRow.append($("<td>").append($('<input type="checkbox" class="all-report-selector" data-company="'+$company.name+'" />')));
    				});
        			$table.append($allRow);
        			
        			$.each( $reportList, function($reportIdx, $report) {
        				if ( $report[$filter] == true ) {
	        				var $tr = $("<tr>");
	        				$tr.addClass("report-" + $report.reportId);
	        				$reportTD = $("<td>");
	    					$reportTD.append($report.description);
	    					$tr.append($reportTD);
	    					$allTD = $($('<input type="checkbox" />'));
	    					$allTD.append("All");
	    					$tr.append($allTD);
	    					$.each($companyList, function($companyIdx, $company) {
	        					$td = $("<td>");
	        					$companyTD=$("<td>");
	        					$companyTD.addClass("company-" + $company.name);
	        					$companyTD.append($('<input type="checkbox" name="'+$report.reportId+"-" + $company.name +'" class="report-selector" data-report="'+$report.reportId+'" data-company="'+$company.name+'"/>'));
	        					$tr.append($companyTD);
	        				});
	        				$table.append($tr);
        				}
        			});
        			
        			
        			
        			$($destination).html("");
        			$($destination).append($table);
        			

        			$.each( $reportList, function($reportIdx, $report) {        				
        				$.each($companyList, function($companyIdx, $company) {
        					$selector = ".report-" + $report.reportId /* + " .company-"+$company.name */;
        					$($selector).mouseover(function($event) {
        						$(".report-" + $report.reportId).css('background-color','#F9F9F9');
        					//	$(".company-" + $company.name).css('background-color','#F9F9F9');
        					});
        					$($selector).mouseout(function($event) {
        						$(".report-" + $report.reportId).css('background-color','transparent');
        					//	$(".company-" + $company.name).css('background-color','transparent');
        					});
        				});
        			});       
        			$("#division-selection-container .report-selector").click(function($event) {
        				var $reportId = $(this).attr("data-report");
        				var $divisionId = $(this).attr("data-division");
        				var $subscribe = $(this).prop("checked");
        				REPORT_SUBSCRIPTION.doSubscription($reportId, $divisionId, $subscribe);
        			}); 
        			$("#region-selection-container .all-report-selector").click(function($event) {
        				var $divisionId = $(this).attr("data-division");
        				var $subscribe = $(this).prop("checked");
        				console.log("all for " + $divisionId + " " + $subscribe);
        				$.each( REPORT_SUBSCRIPTION.reportList, function($index, $report) {
        					$selector = '#region-selection-container input[name="'+$report.reportId+'"]';
        					$subscribed = $($selector).prop("checked");
        					if ( $subscribe != $subscribed ) {
        						$($selector).prop("checked", $subscribe);
        						REPORT_SUBSCRIPTION.doSubscription($report.reportId, $destination, $subscribe);	
        					}
        				});        				
        			});
        			$("#company-selection-container .all-report-selector").click(function($event) {
        				var $divisionId = $(this).attr("data-division");
        				var $subscribe = $(this).prop("checked");
        				console.log("all for " + $divisionId + " " + $subscribe);
        				$.each( REPORT_SUBSCRIPTION.reportList, function($index, $report) {
        					$selector = '#company-selection-container input[name="'+$report.reportId+'"]';
        					$subscribed = $($selector).prop("checked");
        					if ( $subscribe != $subscribed ) {
        						$($selector).prop("checked", $subscribe);
        						REPORT_SUBSCRIPTION.doSubscription($report.reportId, $destination, $subscribe);	
        					}
        				});        				
        			});
        	/*		$("#region-selection-container .all-division-selector").click(function($event) {
        				var $reportId = $(this).attr("data-report");
        				var $subscribe = $(this).prop("checked");
        				console.log("all for " + $reportId + " " + $subscribe);
        				$.each( REPORT_SUBSCRIPTION.divisionList, function($index, $division) {
        					$selector = '#region-selection-container input[name="'+$reportId+'"]';
        					$subscribed = $($selector).prop("checked");
        					if ( $subscribe != $subscribed ) {
        						$($selector).prop("checked", $subscribe);
        						REPORT_SUBSCRIPTION.doSubscription($reportId, $destination, $subscribe);	
        					}
        				});        				
        			});   
        			$("#company-selection-container .all-division-selector").click(function($event) {
        				var $reportId = $(this).attr("data-report");
        				var $subscribe = $(this).prop("checked");
        				console.log("all for " + $reportId + " " + $subscribe);
        				$.each( REPORT_SUBSCRIPTION.divisionList, function($index, $division) {
        					$selector = '#company-selection-container input[name="'+$reportId+'"]';
        					$subscribed = $($selector).prop("checked");
        					if ( $subscribe != $subscribed ) {
        						$($selector).prop("checked", $subscribe);
        						REPORT_SUBSCRIPTION.doSubscription($reportId, $destination, $subscribe);	
        					}
        				});        				
        			});           	*/		
        			$("#region-selection-container .all-division-report-selector").click(function($event) {
        				var $subscribe = $(this).prop("checked");
        				console.log("all for all " + $subscribe);
        				$("#region-selection-container .all-division-selector").prop("checked", $subscribe);
        				$("#region-selection-container .all-report-selector").prop("checked", $subscribe);
        				$.each( $reportList, function($reportIdx, $report) {        				
            				//$.each($divisionList, function($divIdx, $division) {
            					$selector = '#region-selection-container  input[name="'+$report.reportId+'"]';
            					$subscribed = $($selector).prop("checked");
            					if ( $subscribe != $subscribed ) {
            						$($selector).prop("checked", $subscribe);
            						REPORT_SUBSCRIPTION.doSubscription($report.reportId, $destination, $subscribe);	
            					}
            				});
            			});          			
            			$("#company-selection-container .all-division-report-selector").click(function($event) {
            				var $subscribe = $(this).prop("checked");
            				console.log("all for all " + $subscribe);
            				$("#company-selection-container .all-division-selector").prop("checked", $subscribe);
            				$("#company-selection-container .all-report-selector").prop("checked", $subscribe);
            				$.each( $reportList, function($reportIdx, $report) {        				
                				//$.each($divisionList, function($divIdx, $division) {
                					$selector = '#company-selection-container  input[name="'+$report.reportId+'"]';
                					$subscribed = $($selector).prop("checked");
                					if ( $subscribe != $subscribed ) {
                						$($selector).prop("checked", $subscribe);
                						REPORT_SUBSCRIPTION.doSubscription($report.reportId, $destination, $subscribe);	
                					}
                				});
                			});  
        			//}); 
        			
        
        			$.each( $("#selection-menu-container input[name='subscription-selector']"), function($index, $value) {
        				if ( $value.value == 'destinationName' ) {
        					$(this).click();
        				}        				
        			}); 
        			
        			
        		},
        		
        		
        		
        		
        		
        		makeLists : function() {
        			var jqxhr = $.ajax({
						type: 'GET',
						url: "reports/subscription",
						statusCode: {
							200 : function($data) {
								REPORT_SUBSCRIPTION.divisionList = $data.data.divisionList;
								REPORT_SUBSCRIPTION.reportList = $data.data.reportList;
								REPORT_SUBSCRIPTION.companyList = $data.data.companyList;
								REPORT_SUBSCRIPTION.regionList = $data.data.regionList;
								REPORT_SUBSCRIPTION.makeDivisionTable($data.data.reportList, $data.data.divisionList);
								//REPORT_SUBSCRIPTION.makeSelectionPanel("XXX",[],[],true);
								REPORT_SUBSCRIPTION.makeGroupTable($data.data.reportList, $data.data.companyList, 'summaryReport', "#company-selection-container");
								REPORT_SUBSCRIPTION.makeGroupTable($data.data.reportList, $data.data.regionList, 'summaryReport', "#region-selection-container");
								REPORT_SUBSCRIPTION.makeExecutiveTable($data.data.reportList);
							},
							403: function($data) {
								$("#globalMsg").html("Session Timeout. Log in and try again").show();
							},
							404: function($data) {
								$("#globalMsg").html("System Error 404. Reload and try again").show();
							},
							405: function($data) {
								$("#globalMsg").html("System Error 405. Contact Support").show();
							},
							500: function($data) {
								$("#globalMsg").html("System Error 500. Contact Support").show();
							},
						},
						dataType: 'json'
					});
        		},
        		
        		
        		
        		makeSelectionPanel : function($destinationName, $rows, $columns, $displayUponComplete) {
        			var $destination = $("<div>").attr("id",$destinationName).attr("class","selection-container");
        			$destination.append("here it is");
        			$("#subscription-container").append($destination);
        			if ( $displayUponComplete == true ) {
        				$(".selectionContainer").hide();
        				$("#"+$destinationName).show();
        			}
        		},
        	},
        	
        	REPORT_SUBSCRIPTION.init();
        	
        	
        	
        	;CALENDAR_LOOKUP = {        			
        		dateTypeList : null,
        		
        		init : function() {
        			CALENDAR_LOOKUP.getCalendar();
        			ANSI_UTILS.getOptionList('CALENDAR_TYPE',CALENDAR_LOOKUP.makeCalendarTypeList);
        			CALENDAR_LOOKUP.makeClickers();
        			CALENDAR_LOOKUP.makeNewCalendarModal();
        			CALENDAR_LOOKUP.makeDeleteModal();
        		},
        		
        		
        		clearForm : function() {
        			console.log("clearForm");
        			$.each( $("#new-calendar-modal input"), function($index, $value) {        				
        				var $selector = '#new-calendar-modal input[name="' + $value.name + '"]';
        				$($selector).val("");        				
        			});
        			$.each( $("#new-calendar-modal select"), function($index, $value) {        				
        				var $selector = '#new-calendar-modal select[name="' + $value.name + '"]';
        				$($selector).val("");        				
        			});
        			
        			$("#new-calendar-modal .err").html("");

        		},
        		
        		
        		
        		deleteDate : function() {
        			var $url = "calendar/ansiCalendar" 
           			var $outbound = {};
           			$.each( $("#delete-modal input"), function($index, $value) {
           				$outbound[$($value).attr("name")] = $($value).val();
           			});
           			$.each( $("#delete-modal select"), function($index, $value) {
           				$outbound[$($value).attr("name")] = $($value).val();
           			});

           			var jqxhr = $.ajax({
   						type: 'DELETE',
   						url: $url,
   						data: JSON.stringify($outbound),
   						statusCode: {
   							200 : function($data) {
   								if ( $data.responseHeader.responseCode == "SUCCESS" ) {
   									$("#delete-modal").dialog("close");
   									CALENDAR_LOOKUP.refreshMonth($data.data);
   									$("#globalMsg").html("Success").show().fadeOut(4000);
   								} else if ($data.responseHeader.responseCode == "EDIT_FAILURE") {
   									$("#delete-modal").dialog("close");
   									$("#globalMsg").html("Invalid system state. Reload and try again").show().fadeOut(4000);
   								} else {
   									$("#globalMsg").html("Invalid Response Code " + $data.responseHeader.responseCode + ". Contact Spport").show();
   									$("#delete-modal").dialog("close");
   									$('html, body').animate({scrollTop: 0}, 800);
   								}
   								
   							},
   							403: function($data) {
								$("#delete-modal").dialog("close");
   								$("#globalMsg").html("Session Timeout. Log in and try again").show();
   							},
   							404: function($data) {
								$("#delete-modal").dialog("close");
   								$("#globalMsg").html("System Error 404. Reload and try again").show();
   							},
   							405: function($data) {
								$("#delete-modal").dialog("close");
   								$("#globalMsg").html("System Error 405. Contact Support").show();
   							},
   							500: function($data) {
								$("#delete-modal").dialog("close");
   								$("#globalMsg").html("System Error 500. Contact Support").show();
   							},
   						},
   						dataType: 'json'
   					});
        		},
        		
        		
        		
        		getCalendar : function($year) {
        			$("#displayTable").html("");
        			var $url = "calendar/ansiCalendar"        				       
        			if ( $year != null ) {
        				$url = $url + "/" + $year;
        			}	
        			var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						statusCode: {
							200 : function($data) {
								CALENDAR_LOOKUP.makeTable($data);
							},
							403: function($data) {
								$("#globalMsg").html("Session Timeout. Log in and try again").show();
							},
							404: function($data) {
								$("#globalMsg").html("System Error 404. Reload and try again").show();
							},
							405: function($data) {
								$("#globalMsg").html("System Error 405. Contact Support").show();
							},
							500: function($data) {
								$("#globalMsg").html("System Error 500. Contact Support").show();
							},
						},
						dataType: 'json'
					});
        		},
        		
        		
        		
        		makeCalendarTypeList : function($data) {
        			CALENDAR_LOOKUP.dateTypeList = $data.calendarType;
        			var $select = $("#calendar-selector select[name='highlighter']");
					$('option', $select).remove();

					$select.append(new Option("",""));
					$.each($data.calendarType, function(index, val) {
					    $select.append(new Option(val.display, val.code));
					});
					
					$($select).change(function() {
						var $type = $("#calendar-selector select[name='highlighter'] option:selected").val();
						$("#displayTable .calendar-row").removeClass("pre-edit");
						if ( $type != null && $type != "") {
							$("#displayTable ." + $type).addClass("pre-edit");
						}
					});							
					
					var $select = $("#new-calendar-modal select[name='dateType']");
					$('option', $select).remove();

					$select.append(new Option("",""));
					$.each($data.calendarType, function(index, val) {
					    $select.append(new Option(val.display, val.code));
					});
        		},
        		
        		
        		makeClickers : function() {
            		$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
              	  		return false;
              	    });
            		
            		$("#calendar-selector input[name='new-calendar-button']").click(function($event) {
            			CALENDAR_LOOKUP.clearForm();
            			$( "#new-calendar-modal" ).dialog("open");
            		});
            		
            		var $select = $("#calendar-selector select[name='calendarYear']");
					$select.change(function() {
						CALENDAR_LOOKUP.getCalendar($select.val());
					});
            	},

            	
            	
            	
            	makeDeleteModal : function() {
            		$( "#delete-modal" ).dialog({
						title:'Delete ANSI Date',
						autoOpen: false,
						height: 200,
						width: 500,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "delete-cancel-button",
								click: function($event) {
									$( "#delete-modal" ).dialog("close");
								}
							},{
								id: "delete-save-button",
								click: function($event) {
									CALENDAR_LOOKUP.deleteDate();
								}
							}
						]
					});	
					$("#delete-save-button").button('option', 'label', 'Yes');
					$("#delete-cancel-button").button('option', 'label', 'No');
            	},
            	
            	
            	
            	makeNewCalendarModal : function() {
            		$( "#new-calendar-modal" ).dialog({
						title:'New ANSI Date',
						autoOpen: false,
						height: 200,
						width: 500,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "calendar-cancel-button",
								click: function($event) {
									$( "#new-calendar-modal" ).dialog("close");
								}
							},{
								id: "calendar-save-button",
								click: function($event) {
									CALENDAR_LOOKUP.saveDate();
								}
							}
						]
					});	
					$("#calendar-save-button").button('option', 'label', 'Save');
					$("#calendar-cancel-button").button('option', 'label', 'Cancel');
            	},
            	
            	
            	
				makeTable : function($data) {
					var $select = $("#calendar-selector select[name='calendarYear']");
					$('option', $select).remove();

					//$select.append(new Option("",""));
					$.each($data.data.years, function(index, val) {
					    $select.append(new Option(val, val));
					});
					$($select).val($data.data.selectedYear);
					
					var $monthList = ["January","February","March","April","May","June","July","August","September","October","November","December"];
					var $columnMax = 3;
					var $columnCount = 0;
					var $columnWidth = 100/eval($columnMax+1);   //+1 because we start at 0
					
					var $trLabel = $("<tr>");
					var $trData = $("<tr>");

					$.each($monthList, function($mondIndex, $monthValue) {
						if ( $columnCount > $columnMax ) {
							$("#displayTable").append($trLabel);
							$("#displayTable").append($trData);
							$trLabel = $("<tr>");
							$trData = $("<tr>");							
							$columnCount = 0;
						}
						$tdLabel = $("<td>");
						$tdLabel.attr("class","monthLabel")
						$tdLabel.attr("style","width:" + $columnWidth + "%;");
						$tdLabel.append($monthValue)
						$trLabel.append($tdLabel);
						
						$tdData = $("<td>");
						$tdData.attr("id",$monthValue)
						$tdData.attr("style","vertical-align:top; width:" + $columnWidth + "%;");
						$trData.append($tdData);
												
						$columnCount = $columnCount + 1;
					});
					$("#displayTable").append($trLabel);
					$("#displayTable").append($trData);
					
					$.each($data.data.dates, function($index, $value) {
						var $table = $('<table style="width:100%;">');
						for ( $i=0; $i < $value.length; $i++ ) {
							$dayOfMonth = $value[$i].dateString.substring(3,5);
							$tr = $("<tr>");
							$tr.attr("class","calendar-row " + $value[$i].dateType);
							$td1 = $("<td>");
							$td1.attr("style","width:10%; text-align:center;");
							$td1.append($dayOfMonth);
							$tr.append($td1);
							
							$td2 = $("<td>");
							$td2.attr("style","width:80%;");
							$td2.append($value[$i].dateTypeDescription);
							$tr.append($td2);
							
							$td3 = $("<td>");
							$td3.attr("style","width:10%;");
							$delSpan = $("<span>");
							$delSpan.attr("class","action-link del-link");
							$delSpan.attr("data-date",$value[$i].dateString);
							$delSpan.attr("data-datetype",$value[$i].dateType);
							$delSpan.append('<webthing:delete>Delete</webthing:delete>');
							$td3.append($delSpan);
							$tr.append($td3);
							
							$table.append($tr);
						}
						$("#" + $index).append($table);
					});
					
					$(".del-link").click(function() {
						var $dateString = $(this).attr("data-date");
						var $dateType = $(this).attr("data-datetype");
						CALENDAR_LOOKUP.openDeleteModal($dateString, $dateType);
					});
            	},
        		
            	
            	
            	openDeleteModal : function($dateString, $dateType) {
            		$("#delete-modal input[name='date']").val($dateString);
            		$("#delete-modal input[name='dateType']").val($dateType);
            		$("#delete-modal").dialog("open");
            	},
            	
            	
            	
            	
            	refreshMonth : function($data) {
            		console.log("Refreshing ");
            		
            		var $yearSelect = $("#calendar-selector select[name='calendarYear']");
            		var $selectedYear = $yearSelect.val();
					$('option', $yearSelect).remove();

					$.each($data.years, function(index, val) {
					    $yearSelect.append(new Option(val, val));
					});
					$($yearSelect).val($selectedYear);
            		
            		
            		
            		$selector = "#" + $data.updated;
            		$($selector).html("");
            		$value = $data.dates[$data.updated];
            		var $table = $('<table style="width:100%;">');
					for ( $i=0; $i < $value.length; $i++ ) {
						$dayOfMonth = $value[$i].dateString.substring(3,5);
						$tr = $("<tr>");
						
						var $highlightedType = $("#calendar-selector select[name='highlighter'] option:selected").val();
						if ( $highlightedType == $value[$i].dateType) {
							$tr.attr("class","pre-edit calendar-row " + $value[$i].dateType);
						} else {
							$tr.attr("class","calendar-row " + $value[$i].dateType);	
						}
						$td1 = $("<td>");
						$td1.attr("style","width:10%; text-align:center;");
						$td1.append($dayOfMonth);
						$tr.append($td1);
						
						$td2 = $("<td>");
						$td2.attr("style","width:80%;");
						$td2.append($value[$i].dateTypeDescription);
						$tr.append($td2);
						
						$td3 = $("<td>");
						$td3.attr("style","width:10%;");
						$delSpan = $("<span>");
						$delSpan.attr("class","action-link del-link");
						$delSpan.attr("data-date",$value[$i].dateString);
						$delSpan.attr("data-datetype",$value[$i].dateType);
						$delSpan.append('<webthing:delete>Delete</webthing:delete>');
						$td3.append($delSpan);
						$tr.append($td3);
						
						$table.append($tr);
					}
					$($selector).append($table);
					
					$($selector + " .del-link").click( function() {
						var $dateString = $(this).attr("data-date");
						var $dateType = $(this).attr("data-datetype");
						CALENDAR_LOOKUP.openDeleteModal($dateString, $dateType);
					});
            	},
            	
            	
        		
            	saveDate : function() {            		
        			var $url = "calendar/ansiCalendar" 
        			var $outbound = {};
        			$.each( $("#new-calendar-modal input"), function($index, $value) {
        				$outbound[$($value).attr("name")] = $($value).val();
        			});
        			$.each( $("#new-calendar-modal select"), function($index, $value) {
        				$outbound[$($value).attr("name")] = $($value).val();
        			});

        			var jqxhr = $.ajax({
						type: 'POST',
						url: $url,
						data: JSON.stringify($outbound),
						statusCode: {
							200 : function($data) {
								if ( $data.responseHeader.responseCode == "SUCCESS" ) {
									$("#new-calendar-modal").dialog("close");
									CALENDAR_LOOKUP.refreshMonth($data.data);
									$("#globalMsg").html("Success").show().fadeOut(4000);
								} else if ($data.responseHeader.responseCode == "EDIT_FAILURE") {
									$.each( $data.data.webMessages, function($index, $value) {
										var $selector = "#new-calendar-modal ." + $index + "-err";
										$($selector).html($value[0]).show();
									});
								} else {
									$("#globalMsg").html("Invalid Response Code " + $data.responseHeader.responseCode + ". Contact Spport").show();
									$("#new-calendar-modal").dialog("close");
									$('html, body').animate({scrollTop: 0}, 800);
								}
								
							},
							403: function($data) {
								$("#new-calendar-modal").dialog("close");
								$("#globalMsg").html("Session Timeout. Log in and try again").show();
							},
							404: function($data) {
								$("#new-calendar-modal").dialog("close");
								$("#globalMsg").html("System Error 404. Reload and try again").show();
							},
							405: function($data) {
								$("#new-calendar-modal").dialog("close");
								$("#globalMsg").html("System Error 405. Contact Support").show();
							},
							500: function($data) {
								$("#new-calendar-modal").dialog("close");
								$("#globalMsg").html("System Error 500. Contact Support").show();
							},
						},
						dataType: 'json'
					});
        		},

        	}
      	  	

        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.reportSubscription" /></h1>
    	    	
    	<div id="selection-menu-container">
    		Subscribe to:<br />
    		<ansi:hasPermission permissionRequired="REPORT_SUBSCRIPTION_READ">
    		<input type="radio" name="subscription-selector" value="division" /> Division Reports<br />
			</ansi:hasPermission>
    		
    		<ansi:hasPermission permissionRequired="REPORT_SUBSCRIPTION_READ">
    		<input type="radio" name="subscription-selector" value="company" /> Company Reports<br />
			</ansi:hasPermission>
			
    		<ansi:hasPermission permissionRequired="REPORT_SUBSCRIPTION_WRITE">
    		<input type="radio" name="subscription-selector" value="region" /> Region Reports<br />
			</ansi:hasPermission>
    		
    		<ansi:hasPermission permissionRequired="REPORT_SUBSCRIPTION_READ">
    		<input type="radio" name="subscription-selector" value="executive" /> Executive Reports<br />
    		</ansi:hasPermission>
    	</div>
    	
    	
    	<div id="subscription-container">
			<div id="division-selection-container" class="selection-container">
				<webthing:thinking></webthing:thinking>
			</div>
	     
	     	<div id="region-selection-container" class="selection-container">
	     		<webthing:thinking></webthing:thinking>
	     	</div>
	     	
	     	<div id="company-selection-container" class="selection-container">
	     		<webthing:thinking></webthing:thinking>
	     	</div>
	    
	    	<div id="executive-selection-container" class="selection-container">
	    		<webthing:thinking></webthing:thinking>
	    	</div>
	    	
	    	<webthing:scrolltop />
    	</div>
    	<div style="clear:both; height:1px; width:100%;">&nbsp;</div>
    
    	
    </tiles:put>
		
</tiles:insert>

