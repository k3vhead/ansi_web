
        		
        		
(document).ready(function() {		
	;REPORTEXTRAS = {
       		
   		makeSelectionPanel : function($destinationName, $rows, $columns, $displayUponComplete) {
   			var $destination = $("<div>").attr("id",$destinationName).attr("class","selection-container");
   			$destination.append("here it is");
   			$("#subscription-container").append($destination);
   			if ( $displayUponComplete == true ) {
   				$(".selectionContainer").hide();
   				$("#"+$destinationName).show();
   			}
   		},







// ************************************************************************

   		makeUtilityType_REMOVE : function($allAnsiReports, $filter, $trendReport, $utilityType, $divisionList, $reportId, $divisionId, $subscriptionList, $subscriptionId) {
    			
    			console.log("makeUtilityTable");
    			var $table = $("<table>").css('text-align','center');
    			var $hdrRow = $("<tr>");
    			
    			$hdrRow.append($("<td>"));  
				$.each($divisionList, function($divIdx, $division) {
					$divTD = $("<td>"); 
					$divTD.addClass("div-" + $division.divisionId);
					$divTD.append($division.divisionDisplay) 
					$hdrRow.append($divTD); 
				});
    		
    			$table.append($hdrRow);
				
				$.each( $['$filter'], function($reportIdx, $report) {        				
        				$.each($divisionList, function($divIdx, $division) {
        					$selector = ".report-" + $report.reportId + ".division" + $division.divisionId;
        					$($selector).mouseover(function($event) {
        						$(".report-" + $report.reportId).css('background-color','#F9F9F9');
        						$(".div-"+$division.divisionId).css('background-color','#F9F9F9');
        					//	$($allLabel).css('background-color','#F9F9F9');
            				//	$($allTD).css('background-color','#F9F9F9');
            				//	$(".hdr").css('background-color','#F9F9F9');
        					});
        					$($selector).mouseout(function($event) {
        						$(".report-" + $report.reportId).css('background-color','transparent');
        						$(".div-"+$division.divisionId).css('background-color','transparent');
        					//	$($allLabel).css('background-color','transparent');
            				//	$($allTD).css('background-color','transparent');
            				//	$(".hdr").css('background-color','transparent');
        					});
        				});
        			});   
    			
    			$.each( $['$filter'], function($reportIdx, $report) {
    				var $tr = $("<tr>"); 
    				$tr.addClass("report-" + $report.reportId); 
    				$reportTD = $("<td>"); 
					$reportTD.append($report.description); 
					$tr.append($reportTD).css('text-align','left'); 
					
    				$.each($divisionList, function($divIdx, $division) {
    					$divTD = $("<td>").css('text-align','center');
    					$divTD.addClass("div-" + $division.divisionId);
    					$divTD.append($('<input type="checkbox" name="'+$report.reportId+"-" + $division.divisionId +'" class="report-selector" data-report="'+$report.reportId+'" data-division="'+$division.divisionId+'"/>'));
    					$divTD.prop('checked','true');
    					$divTD.prop('checked','false');
    					
    					$tr.append($divTD);
    				});
    				$table.append($tr);	
    				
    			});
    			
    			$("#allAnsi-selection-container").html("");
    			$("#allAnsi-selection-container").append($table);
    			
				$.each($subscriptionList, function($index, $value) {
					var $checkboxName = $value.reportId + "-" + $value.divisionId;
					var $checkbox = "#allAnsi-selection-container input[name='" + $checkboxName +"']";
					console.log($checkbox);
					$($checkbox).prop(":checked", true);	
					
            	}); 
    			
    			
    			$.each( $allAnsiReports, function($reportIdx, $report) {        				
    			//	$.each($divisionList, function($divIdx, $division) {
    					$selector = ".report-" + $report.reportId;
    					$($selector).mouseover(function($event) {
    						$(".report-" + $report.reportId).css('background-color','#F9F9F9');
    					//	$(".div-"+$division.divisionId).css('background-color','#F9F9F9');
    					});
    					$($selector).mouseout(function($event) {
    						$(".report-" + $report.reportId).css('background-color','transparent');
    					//	$(".div-"+$division.divisionId).css('background-color','transparent');
    					});
    			//	});
    			});   

    			$.each( $divisionList, function($divIdx, $division) {        
    					$selector = ".div-"+$division.divisionId;
    					$($selector).mouseover(function($event) {
    						$(".div-"+$division.divisionId).css('background-color','#F9F9F9');
    					});
    					$($selector).mouseout(function($event) {
    						$(".div-"+$division.divisionId).css('background-color','transparent');
    					});
    			});    
    			$("#allAnsi-selection-container .report-selector").click(function($event) {
    				var $reportId = $(this).attr("data-report");
    				var $divisionId = $(this).attr("data-division");
    				var $subscribe = $(this).prop("checked");
    				var $subscriptionId = $(this).attr("data-subscriptionId");
    				REPORT_SUBSCRIPTION.doSubscription($reportId, $divisionId, $subscribe, $subscriptionId);
    			});
    
    			$.each( $("#selection-menu-container input[name='subscription-selector']"), function($index, $value) {
    				if ( $value.value == 'allAnsi' ) {
    					$(this).click();
    				}        				
    			});
    		},
    		
    		
    		
    		MOCKmakeGroupTable : function ($reportList, $headerList) {
    			var $table = $("<table>").css('text-align','center');
    			var $hdrRow = $("<tr>");
    			
    			$hdrRow.append($("<td>"));
    			$.each( $headerList, function ($hdrIndex, $hdrValue){
    				var $td = $("<td>");
					$td.addClass("div-" + $hdrValue);
    				$td.append($hdrValue);
    				$hdrRow.append($td);
    			});
    			$table.append($hdrRow);
    			$.each($reportList, function ($rptIndex, $rptValue) {
    				$myRow = $("<tr>");
    				$.each($headerList, function($hdrIndex, $hdrValue) {
    					var $td = $("<td>");
    					$td.append($('<input type="checkbox" name="'+ $hdrValue+'" />'));
    					$hdrRow.append($td);
    				});
    				$table.append($hdrRow);
    			});
    			return $table
    		},
    		
    		
    		
   			makeGroupTable_REMOVE : function($orgList, $reportList, $summaryReports, $subscriptionList, $filter, $destination, $reportId, $divisionId, $groupId, $companyId) {
    			console.log("makeGroupTable");
    			var $table = $("<table>");
    			var $hdrRow = $("<tr>");
    			
    			$hdrRow.append($("<td>")).css('text-align','center');  
				$.each($orgList, function($groupIdx, $group) {
					$groupTD=$("<td>");
					$groupTD.addClass("group-" + $groupId);
					$groupTD.append($group.name);
					$hdrRow.append($groupTD);
				});
    			$table.append($hdrRow);
				
				$.each( $summaryReports, function($reportIdx, $report) {        				
        				$.each($orgList, function($groupIdx, $group) {
        					$selector = ".report-" + $report.reportId;
        					$($selector).mouseover(function($event) {
        						$(".report-" + $report.reportId).css('background-color','#F9F9F9');
        						$(".group-"+$groupId).css('background-color','#F9F9F9');
        					//	$($allLabel).css('background-color','#F9F9F9');
            				//	$($allTD).css('background-color','#F9F9F9');
            					$(".hdr").css('background-color','#F9F9F9');
        					});
        					$($selector).mouseout(function($event) {
        						$(".report-" + $report.reportId).css('background-color','transparent');
        						$(".group-"+$groupId).css('background-color','transparent');
        					//	$($allLabel).css('background-color','transparent');
            				//	$($allTD).css('background-color','transparent');
            					$(".hdr").css('background-color','transparent');
        					});
        				});
        			});   
    			
    			$.each( $summaryReports, function($reportIdx, $report) {
    				if ( $report['$filter'] == true ) {
        				var $tr = $("<tr>");
        				$tr.addClass("report-" + $report.reportId);
        				$reportTD = $("<td>");
    					$reportTD.append($report.description).css('text-align','left');
    					$tr.append($reportTD).css('text-align','left');
    					
    					$.each($orgList, function($groupIdx, $group) {
        					$groupTD=$("<td>");
        					$groupTD.addClass("group-" + $groupId);
        					$groupTD.append($('<input type="checkbox" name="'+$report.reportId+"-" + $groupId +'" class="groupTable-report-selector" data-report="'+$report.reportId+'" data-group="'+$groupId+'"/>'));
        					
        					$groupTD.prop('checked','true');
        					$groupTD.prop('checked','false');
        					
        					$tr.append($groupTD).css('text-align','center');
        				});
        				$table.append($tr);
    				}
    			});    
    			
       			$($destination).html("");
       			$($destination).append($table);   

			/*	$.each($subscriptionList, function($index, $value) {
					var $checkboxName = $value.reportId + "-" + $value.divisionId;
					var $checkbox = $destination+" .selection-container input[name='" + $checkboxName +"']";
					console.log($checkbox);
					$($checkbox).prop(":checked", true);		
					
            	}); */
       			
       			$.each( $summaryReports, function($reportIdx, $report) {        				
       				$.each($orgList, function($groupIdx, $group) {
       					$selector = ".report-" + $report.reportId + " .group-" + $groupId;
       					$($selector).mouseover(function($event) {
       						$(".report-" + $report.reportId).css('background-color','#F9F9F9');
       						$(".group-" + $groupId).css('background-color','#F9F9F9');
       					});
       					$($selector).mouseout(function($event) {
       						$(".report-" + $report.reportId).css('background-color','transparent');
       						$(".group-" + $groupId).css('background-color','transparent');
       					});
       				});
       			});  
       			
					$($destination+" .groupTable-report-selector").click(function($event) {
						var $reportId = $(this).attr("data-report");
						var $groupId = $(this).attr("data-group");
						var $subscribe = $(this).prop("checked");
    				var $subscriptionId = $(this).attr("data-subscriptionId");
						REPORT_SUBSCRIPTION.doSubscription($reportId, $id, $subscribe, $subscriptionId);
					}); 
    			 
   			$.each( $("#selection-menu-container input[name='subscription-selector']"), function($index, $value) {
   				if ( $value.value == 'destinationName' ) {
   					$(this).click();
   				}        				
   			}); 
			
   		},
	}   		