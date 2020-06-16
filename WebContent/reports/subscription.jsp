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
        	#thinking {
        		width:100%;
        	}
        	.thinking {
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
        			var $table = $("<table>").css('text-align','center');
        			var $hdrRow = $("<tr>");
        			
        			$hdrRow.append($("<td>"));  // blank box in the corner
        			$hdrRow.append($('<td class ="hdr">').append("All")); //row label
    				$.each($divisionList, function($divIdx, $division) {
    					$divTD = $("<td>"); //separates divisions into columns
    					$divTD.addClass("div-" + $division.divisionId);
    					$divTD.append($division.divisionDisplay) //adds display value to divTD
    					$hdrRow.append($divTD); //places divTD columns into hdrRow
    				});
        		//	$hdrRow.addClass($allLabel);
        			$table.append($hdrRow);
        			
        			var $allRow = $("<tr>");  //creates allRow columns  	
        		//	$.each( $reportList, function($reportIdx, $report) {        				
        		//		$.each($divisionList, function($divIdx, $division) {
        					$selector = $allRow;
        					$($selector).mouseover(function($event) {
        					//	$(".report-" + $report.reportId).css('background-color','#F9F9F9');
        					//	$(".div-"+$division.divisionId).css('background-color','#F9F9F9');
        						$($allRow).css('background-color','#F9F9F9');
            					$($allLabel).css('background-color','#F9F9F9');
        					});
        					$($selector).mouseout(function($event) {
        					//	$(".report-" + $report.reportId).css('background-color','transparent');
        					//	$(".div-"+$division.divisionId).css('background-color','transparent');
        						$($allRow).css('background-color','transparent');
            					$($allLabel).css('background-color','transparent');
        					});
        		//		});
        		//	});    		
        			
        			var $allLabel = $("<td>").append("All").css('text-align','left'); //row label
        			var $allTD = $("<td>").append($('<input type="checkbox" class="all-division-report-selector" />')); //corner select all button
        			
       		/*  	//	$allTD.css('background-color','#F9F9F9');
					//highlight division column alone
	    					$.each( $reportList, function($reportIdx, $report) {        				
	                			//	$.each($divisionList, function($divIdx, $division) {
	                					$selector = $allRow;
	                					$($selector).mouseover(function($event) {
	                			//			$(".report-" + $report.reportId).css('background-color','#F9F9F9');
	                			//			$(".div-"+$division.divisionId).css('background-color','#F9F9F9');
	                						$($reportTD).css('background-color','#F9F9F9');
	                    					$($allRow).css('background-color','#F9F9F9');
	                					});
	                					$($selector).mouseout(function($event) {
	                				//		$(".report-" + $report.reportId).css('background-color','transparent');
	                				//		$(".div-"+$division.divisionId).css('background-color','transparent');
	                						$($reportTD).css('background-color','transparent');
	                    					$($allRow).css('background-color','transparent');
	                					});
	                			//	});
	                			});   */

        			
        			//hilghlight all-all selections
					$.each( $reportList, function($reportIdx, $report) {        				
            				$.each($divisionList, function($divIdx, $division) {
            					$selector = $allTD;
            					$($selector).mouseover(function($event) {
            						$(".report-" + $report.reportId).css('background-color','#F9F9F9');
            						$(".div-"+$division.divisionId).css('background-color','#F9F9F9');
            						$($allLabel).css('background-color','#F9F9F9');
                					$($allTD).css('background-color','#F9F9F9');
            					//	$(".all").css('background-color','#F9F9F9');
                					$(".hdr").css('background-color','#F9F9F9');
            					});
            					$($selector).mouseout(function($event) {
            						$(".report-" + $report.reportId).css('background-color','transparent');
            						$(".div-"+$division.divisionId).css('background-color','transparent');
            						$($allLabel).css('background-color','transparent');
                					$($allTD).css('background-color','transparent');
            					//	$(".all").css('background-color','transparent');
                					$(".hdr").css('background-color','transparent');
            					});
            				});
            			});   
        			
        			
        			$allRow.append($allLabel); //places column label into allRow
        			$allRow.append($allTD); //places allTD checkbox into allRow
					$allRow.addClass($allRow);
        			
        			

        			$.each($divisionList, function($divIdx, $division) {
    					$divTD = $("<td>"); //places division in separate columns
    					$divTD.addClass("div-" + $division.divisionId);
    					$divTD.append($('<input type="checkbox" class="all-report-selector" data-division="'+$division.divisionId+'" />' )); //selects all reports in a division

	   					//highlight division column alone
           					$selector = $divTD;
           					$($selector).mouseover(function($event) {
           						$(".div-"+$division.divisionId).css('background-color','#F9F9F9');
           						$($divTD).css('background-color','#F9F9F9');
           					});
           					$($selector).mouseout(function($event) {
           						$(".div-"+$division.divisionId).css('background-color','transparent');
           						$($divTD).css('background-color','transparent');
           					});
  					
    					
    					$allRow.append($divTD); //assigns divTD column to all row buttons
    				});
        			$table.append($allRow);
        			
        			$.each( $reportList, function($reportIdx, $report) {
        				var $tr = $("<tr>"); //creates report rows
        				$tr.addClass("report-" + $report.reportId); //adds report to tr
        				$reportTD = $("<td>"); //places report into td
    					$reportTD.append($report.description); //assigns description to report in td
    					$tr.append($reportTD).css('text-align','left'); //adds reportTD to tr
    					

            			$allColumn = $('<td class="all">');
    					$allColumn.append($('<input type="checkbox" class="all-division-selector" data-report="'+$report.reportId+'" />'));
    				
    					//highlights all column
        				$selector = $allColumn;
    					$($selector).mouseover(function($event) {    			
    						$($allTD).css('background-color','#F9F9F9');
    						$(".all").css('background-color','#F9F9F9');
        					$(".hdr").css('background-color','#F9F9F9');
    					});
    					$($selector).mouseout(function($event) {
    						$($allTD).css('background-color','transparent');
    						$(".all").css('background-color','transparent');
        					$(".hdr").css('background-color','transparent');
    					});		
            		
    					$tr.append($allColumn);
    					
    					
        				$.each($divisionList, function($divIdx, $division) {
        					$divTD = $("<td>").css('text-align','center');
        					$divTD.addClass("div-" + $division.divisionId);
        					$divTD.append($('<input type="checkbox" name="'+$report.reportId+"-" + $division.divisionId +'" class="report-selector" data-report="'+$report.reportId+'" data-division="'+$division.divisionId+'"/>'));
        					$tr.append($divTD);
        				});
        				$table.append($tr);	
        				
        				
        			});
        			
        			
        			$("#division-selection-container").html("");
        			$("#division-selection-container").append($table);
        			
        			//highlight report row + division column
        			$.each( $reportList, function($reportIdx, $report) {        				
        				$.each($divisionList, function($divIdx, $division) {
        					$selector = ".report-" + $report.reportId + " .div-"+$division.divisionId;
        					$($selector).mouseover(function($event) {
        						$(".report-" + $report.reportId).css('background-color','#F9F9F9');
        						$(".div-"+$division.divisionId).css('background-color','#F9F9F9');
        					//	$($allRow).css('background-color','#F9F9F9');
            					//$($allTR).css('background-color','#F9F9F9');
        					});
        					$($selector).mouseout(function($event) {
        						$(".report-" + $report.reportId).css('background-color','transparent');
        						$(".div-"+$division.divisionId).css('background-color','transparent');
        						//$($allRow).css('background-color','transparent');
            					//$($allTR).css('background-color','transparent');
        					});
        				});
        			});   

					//highlight division column alone
        			$.each( $divisionList, function($divIdx, $division) {        				
        			//	$.each($reportList, function($reportIdx, $report)  {
        					$selector = ".div-"+$division.divisionId;
        					$($selector).mouseover(function($event) {
        						$(".div-"+$division.divisionId).css('background-color','#F9F9F9');
        					//	$($allRow).css('background-color','#F9F9F9');
            					//$($allTR).css('background-color','#F9F9F9');
        					});
        					$($selector).mouseout(function($event) {
        						//$(".report-" + $report.reportId).css('background-color','transparent');
        						$(".div-"+$division.divisionId).css('background-color','transparent');
        					//	$($allRow).css('background-color','transparent');
            					//$($allTR).css('background-color','transparent');
        					});
        			//	});
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
        			var $table = $("<table>").css('text-align','center');
        			var $hdrRow = $("<tr>");   
        			$hdrRow.append( $("<td>"));   
        			//var $allColumn = $("<td>");
        			$hdrRow.append( $('<td class ="hdr">')); 
        			$table.append($hdrRow);
        			

        		
        		
        			
        		//	var $allRow = $("<tr>");
        		//	var $allLabel = $("<td>").append("All");
        		//	var $allTD = $("<td>");
        			var $allTD = $('<td class ="allselector">');
        			
        		
        		//	$table.append($allTD);
        			
        			
        			$.each( $reportList, function($reportIdx, $report) {
        				if ( $report["executiveReport"] == true ) {
        					var $tr = $("<tr>"); //creates report rows
            				$tr.addClass("report-" + $report.reportId); //adds report to tr
            				$reportTD = $("<td>"); //places report into td
        					$reportTD.append($report.description); //assigns description to report in td
        					$tr.append($reportTD).css('text-align','left'); //adds reportTD to tr
        					
        					//$hdrRow.append($('<input type="checkbox" class="all" data-report="'+$selector+ '-' +$report.reportId+'" />'));
        					$allTD.append($('<input type="checkbox" name="'+$report.reportId+'" class="allselector" data-report="'+$selector+'"/>'));

                			$allColumn = $('<td class="all">').append($('<input type="checkbox" class="executive-selector" data-report="'+$report.reportId+'" />'));
        				//	$allColumn.append($('<input type="checkbox" class="all" data-report="'+$selector+ '-' +$report.reportId+'" />'));
        				
        					//highlights all column
            				$selector = $allColumn;
        					$($selector).mouseover(function($event) {    			
        						$($allColumn).css('background-color','#F9F9F9');
        						$(".all").css('background-color','#F9F9F9');
            					$(".hdr").css('background-color','#F9F9F9');
        					});
        					$($selector).mouseout(function($event) {
        						$($allColumn).css('background-color','transparent');
        						$(".all").css('background-color','transparent');
            					$(".hdr").css('background-color','transparent');
        					});		
        					//$tr.append($('<input type="checkbox" class="executive-selector" data-report="'+$report.reportId+'" />'));
        					$tr.append($allColumn);
        					
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
        					//	$($allTD).css('background-color','#F9F9F9');
        						//$($allTR).css('background-color','#F9F9F9');
        					});
        					$($selector).mouseout(function($event) {
        						$(".report-" + $report.reportId).css('background-color','transparent');
        					//	$($allTD).css('background-color','transparent');
        					//	$($allTR).css('background-color','transparent');
        					});
        				});
        			});       
        			$("#executive-selection-container .executive-selector").click(function($event) {
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
        			$("#executive-selection-container .all-selector").click(function($event) {
        				var $subscribe = $(this).prop("checked");
        				console.log("all for all " + $subscribe);
        				//$("#executive-selection-container .all-division-selector").prop("checked", $subscribe);
        				$("#executive-selection-container .all-selector").prop("checked", $subscribe);
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
        		//	}); 
        			
        
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
        			
        			$hdrRow.append($("<td>")).css('text-align','center');  // blank box in the corner
        			$hdrRow.append($('<td class ="hdr">').append("All"));
    				$.each($companyList, function($companyIdx, $company) {
    					$companyTD=$("<td>");
    					$companyTD.addClass("company-" + $company.id);
    					$companyTD.append($company.name);
    					$hdrRow.append($companyTD);
    				//	$hdrRow.append($("<td>").append($company.name));
    				});
        			$table.append($hdrRow);
        			
        			var $allRow = $("<tr>").css('text-align','center');
	        			$selector = $allRow;
						$($selector).mouseover(function($event) {
						//	$(".report-" + $report.reportId).css('background-color','#F9F9F9');
						//	$(".div-"+$division.divisionId).css('background-color','#F9F9F9');
							$($allRow).css('background-color','#F9F9F9');
	    					$($allLabel).css('background-color','#F9F9F9');
						});
						$($selector).mouseout(function($event) {
						//	$(".report-" + $report.reportId).css('background-color','transparent');
						//	$(".div-"+$division.divisionId).css('background-color','transparent');
							$($allRow).css('background-color','transparent');
	    					$($allLabel).css('background-color','transparent');
						});
        			
        			var $allLabel = $("<td>").append("All").css('text-align','left');
        			var $allTD = $("<td>").append($('<input type="checkbox" class="groupTable-all-company-report-selector" />'));
        			
        			

        			//hilghlight all-all selections
					$.each( $reportList, function($reportIdx, $report) {        				
            				$.each($companyList, function($companyIdx, $company) {
            					$selector = $allTD;
            					$($selector).mouseover(function($event) {
            						$(".report-" + $report.reportId).css('background-color','#F9F9F9');
            						$(".company-"+$company.id).css('background-color','#F9F9F9');
            						$($allLabel).css('background-color','#F9F9F9');
                					$($allTD).css('background-color','#F9F9F9');
            					//	$(".all").css('background-color','#F9F9F9');
                					$(".hdr").css('background-color','#F9F9F9');
            					});
            					$($selector).mouseout(function($event) {
            						$(".report-" + $report.reportId).css('background-color','transparent');
            						$(".company-"+$company.id).css('background-color','transparent');
            						$($allLabel).css('background-color','transparent');
                					$($allTD).css('background-color','transparent');
            					//	$(".all").css('background-color','transparent');
                					$(".hdr").css('background-color','transparent');
            					});
            				});
            			});   
        			
        			
        			
        			
        			$allRow.append($allLabel);
        			$allRow.append($allTD);	
        			$allRow.addClass($allTD);
        			
        			$.each($companyList, function($companyIdx, $company) {
    					$companyTD=$("<td>");
    					$companyTD.addClass("company-" + $company.id);
    					$companyTD.append($('<input type="checkbox" class="groupTable-all-report-selector" data-company="'+$company.id+'"/>'));
    					

   						//highlight division column alone
        					$selector = $companyTD;
        					$($selector).mouseover(function($event) {
        						$(".company-"+$company.id).css('background-color','#F9F9F9');
        						$($companyTD).css('background-color','#F9F9F9');
        					});
        					$($selector).mouseout(function($event) {
        						$(".company-"+$company.id).css('background-color','transparent');
        						$($companyTD).css('background-color','transparent');
        					});
    					
    					
    					$allRow.append($companyTD);            			
        			
        			});
        			$table.append($allRow);
        			
        			$.each( $reportList, function($reportIdx, $report) {
        				if ( $report[$filter] == true ) {
	        				var $tr = $("<tr>");
	        				$tr.addClass("report-" + $report.reportId);
	        				$reportTD = $("<td>");
	    					$reportTD.append($report.description).css('text-align','left');
	    					$tr.append($reportTD).css('text-align','left');
	    					
	    					
	    					$allColumn = $('<td class="all">');
	    					$allColumn.append($('<input type="checkbox" class="groupTable-all-company-selector" data-report="'+$report.reportId+'" />'));
	        				
	    					//highlights all column
	        				$selector = $allColumn;
	    					$($selector).mouseover(function($event) {    			
	    						$($allTD).css('background-color','#F9F9F9');
	    						$(".all").css('background-color','#F9F9F9');
	        					$(".hdr").css('background-color','#F9F9F9');
	    					});
	    					$($selector).mouseout(function($event) {
	    						$($allTD).css('background-color','transparent');
	    						$(".all").css('background-color','transparent');
	        					$(".hdr").css('background-color','transparent');
	    					});		
	    					//$allTD.append("All");
	    					//$allTD.addClass($tr);
	    					$tr.append($allColumn);
	    					$.each($companyList, function($companyIdx, $company) {
	        					$companyTD=$("<td>");
	        					$companyTD.addClass("company-" + $company.id);
	        					$companyTD.append($('<input type="checkbox" name="'+$report.reportId+"-" + $company.id +'" class="groupTable-report-selector" data-report="'+$report.reportId+'" data-company="'+$company.id+'"/>'));
	        					$tr.append($companyTD).css('text-align','center');
	        				});
	        				$table.append($tr);
        				}
        			});    
        			
        			
        			
            			$($destination).html("");
            			$($destination).append($table);            			
            			
            			$.each( $reportList, function($reportIdx, $report) {        				
            				$.each($companyList, function($companyIdx, $company) {
            					$selector = ".report-" + $report.reportId + " .company-" + $company.id;
            					$($selector).mouseover(function($event) {
            						$(".report-" + $report.reportId).css('background-color','#F9F9F9');
            						$(".company-" + $company.id).css('background-color','#F9F9F9');
            					//	$($allRow).css('background-color','#F9F9F9');
            					});
            					$($selector).mouseout(function($event) {
            						$(".report-" + $report.reportId).css('background-color','transparent');
            						$(".company-" + $company.id).css('background-color','transparent');
            					//	$($allRow).css('background-color','transparent');
            					});
            				});
            			});  

    					$($destination+" .groupTable-report-selector").click(function($event) {
    						var $reportId = $(this).attr("data-report");
    						var $id = $(this).attr("data-company");
    						var $subscribe = $(this).prop("checked");
    						REPORT_SUBSCRIPTION.doSubscription($reportId, $id, $subscribe);
    					}); 
            			
    					$($destination+" .groupTable-all-report-selector").click(function($event) {
    						var $id = $(this).attr("data-company");
    						var $subscribe = $(this).prop("checked");
    						console.log("all for " + $id + " " + $subscribe);
    						$.each( REPORT_SUBSCRIPTION.reportList, function($index, $report) {
    							$selector = $destination+ ' input[name="'+$report.reportId+'-'+ $id+'"]';
    							$subscribed = $($selector).prop("checked");
    							if ( $subscribe != $subscribed ) {
    								$($selector).prop("checked", $subscribe);
    								REPORT_SUBSCRIPTION.doSubscription($report.reportId, $id, $subscribe);	
    							}
    						});        				
    					});      	
            			$($destination+" .groupTable-all-company-selector").click(function($event) {
            				var $reportId = $(this).attr("data-report");
            				var $subscribe = $(this).prop("checked");
            				console.log("all for " + $reportId + " " + $subscribe);
            				$.each( REPORT_SUBSCRIPTION.companyList, function($index, $company) {
            					$selector = $destination+ ' input[name="'+$reportId+'-'+ $company.id+'"]';
            					$subscribed = $($selector).prop("checked");
            					if ( $subscribe != $subscribed ) {
            						$($selector).prop("checked", $subscribe);
            						REPORT_SUBSCRIPTION.doSubscription($reportId, $company.id, $subscribe);	
            					}
            				});        				
            			});        		
						$($destination+" .groupTable-all-company-report-selector").click(function($event) {
							var $subscribe = $(this).prop("checked");
							console.log("all for all " + $subscribe);
							$($destination+" .groupTable-all-company-selector").prop("checked", $subscribe);
							$($destination+" .groupTable-all-report-selector").prop("checked", $subscribe);
							$.each( $reportList, function($reportIdx, $report) {        				
			    				$.each($companyList, function($companyIdx, $company) {
			    					$selector =$destination+ ' input[name="'+$report.reportId+'-'+ $company.id+'"]';
			    					$subscribed = $($selector).prop("checked");
			    					if ( $subscribe != $subscribed ) {
			    						$($selector).prop("checked", $subscribe);
			    						REPORT_SUBSCRIPTION.doSubscription($report.reportId, $company.id, $subscribe);	
			    					}
			    				});
			    			});
		    			});    
	        			 
        			
        
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
			        			$("#thinking").hide();
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
        	
        	
        	
        	
      	  	

        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.reportSubscription" /></h1>
    	<div id="thinking"><webthing:thinking></webthing:thinking></div>
    	    	
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
    		
    		<ansi:hasPermission permissionRequired="REPORT_SUBSCRIPTION_READ">
    		<div style="text-align: center">
    		<a href="reports/subscriptionCSV"><webthing:csv>Subscriptions</webthing:csv></a>
    		</div>
    		</ansi:hasPermission>
    		
    	</div>
    	
    	<div id="subscription-container">
    		
			<div id="division-selection-container" class="selection-container" ">
			</div>
	     
	     	<div id="region-selection-container" class="selection-container" ">
	     	</div>
	     	
	     	<div id="company-selection-container" class="selection-container" ">
	     	</div>
	    
	    	<div id="executive-selection-container" class="selection-container" ">
	    	</div>
	    	
	    	<webthing:scrolltop />
    	</div>
    	<div style="clear:both; height:1px; width:100%;">&nbsp;</div>
    
    	
    </tiles:put>
		
</tiles:insert>

