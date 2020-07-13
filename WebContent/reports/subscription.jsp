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
        		subscriptionList : [],
        		//allReportType : [],
        		
        		
        		init : function() {
        			REPORT_SUBSCRIPTION.makeLists();
        			REPORT_SUBSCRIPTION.makeClickers();
        		//	REPORT_SUBSCRIPTION.getSubscriptions();
        		},
                
        		
        		doSubscription : function($reportId, $divisionId, $subscribe, $allDivisions, $allReportType, $subscriptionId) {
        			if ( $subscribe ) {
        		//		$(this).prop('checked', true);
        				$word = "Subscribe"
        			} else {
        		//		$(this).prop('checked', false);
        				$word = "Unsubscribe"
        			}
        			
        		//	$("#subscription").html($reportId, $divisionId, $subscribe, $allDivisions, $allReportType);
        			
        		
        	/*		if ( $allReportType != null ) {
        				var $reportId = null;
        			} else {
        				var $reportId = $reportId;
        			}
            				
            				
         				
       				if ($allDivisions == true) {
       			        var $divisionId = null;
       			    }
       			    else if ($allDivisions == false){
       			        var $divisionId = $divisionId;
       			    } */
            				//var $allDivisions = boolean;
            				//var $subscribe = boolean;
            				//var $subscribe = $($selector).prop('checked');
            			//	var $isChecked = $($selector).prop('checked');
            		var $outbound = {"reportId":$reportId, "divisionId":$divisionId, "subscribe":$subscribe, "allDivisions":$allDivisions, "allReportType":$allReportType, "subscriptionId":$subscriptionId};
            			//	{"action":"REPEAT_JOB", "annualRepeat":$isChecked};
            				
            			
          			
	            			
	       			var jqxhr = $.ajax({
						type: 'POST',
						url: "reports/subscription",
          				$outbound : [{"reportId":null, "allReportType":$allReportType}, {"divisionId":null, "allDivisions":$allDivisions}],
      					data: JSON.stringify($outbound),
      					statusCode: {
							200 : function($data) {
          							if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
          								$("#globalMsg").html("Edit Error").show().fadeOut(4000);
                          				console.log("updates failed");
          							}
          							if ( $data.responseHeader.responseCode == 'SUCCESS') {
          								$("#globalMsg").html("Update Successful").show().fadeOut(4000);
          								$("#thinking").hide();
          								REPORT_SUBSCRIPTION.makeLists($reportId, $divisionId, $subscriptionId, $subscribe);
          								console.log("updates success");
          							}
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
      			
        			
        			
        			
        			$("#globalMsg").html($word + " to " + $reportId + " for " + $divisionId).show().fadeOut(6000);
        			console.log($word + " to " + $reportId + " for " + $divisionId);
        			
        			
        			
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
        		
        		makeDivisionTable : function($reportList, $divisionList, $subscriptionList, $subscriptionId) {
        		/*	var $selector = REPORT_SUBSCRIPTION
        			$($selector).prop('checked',true);
        			var $isChecked = $($selector).prop('checked'); */
        			
        			
        			console.log("makeDivisionTable");
        			var $table = $("<table>").css('text-align','center');
        			var $hdrRow = $("<tr>");
        			
        			$hdrRow.append($("<td>"));  
        			$hdrRow.append($('<td class ="hdr">').append("All")); 
    				$.each($divisionList, function($divIdx, $division) {
    					$divTD = $("<td>"); 
    					$divTD.addClass("div-" + $division.divisionId);
    					$divTD.append($division.divisionDisplay) 
    					$hdrRow.append($divTD); 
    				});
        		
        			$table.append($hdrRow);
        			
        			var $allRow = $("<tr>");  
        			
       					$selector = $allRow;
       					$($selector).mouseover(function($event) {
       						$($allRow).css('background-color','#F9F9F9');
           					$($allLabel).css('background-color','#F9F9F9');
       					});
       					$($selector).mouseout(function($event) {
       						$($allRow).css('background-color','transparent');
           					$($allLabel).css('background-color','transparent');
       					});
        			
        			var $allLabel = $("<td>").append("All").css('text-align','left'); 
        			var $allTD = $("<td>").append($('<input type="checkbox" class="all-division-report-selector" />')); 
        			
        			$allTD.prop('checked','true');
        			$allTD.prop('checked','false');

				/*	if ($(this).is(':checked')) {
	                    $($allTD).val("True");
	                } else {
	                    $($allTD).val("False");
	                } */
					
					$.each( $reportList, function($reportIdx, $report) {        				
            				$.each($divisionList, function($divIdx, $division) {
            					$selector = $allTD;
            					$($selector).mouseover(function($event) {
            						$(".report-" + $report.reportId).css('background-color','#F9F9F9');
            						$(".div-"+$division.divisionId).css('background-color','#F9F9F9');
            						$($allLabel).css('background-color','#F9F9F9');
                					$($allTD).css('background-color','#F9F9F9');
                					$(".hdr").css('background-color','#F9F9F9');
            					});
            					$($selector).mouseout(function($event) {
            						$(".report-" + $report.reportId).css('background-color','transparent');
            						$(".div-"+$division.divisionId).css('background-color','transparent');
            						$($allLabel).css('background-color','transparent');
                					$($allTD).css('background-color','transparent');
                					$(".hdr").css('background-color','transparent');
            					});
            				});
            			});   
        			
        			$allRow.append($allLabel); 
        			$allRow.append($allTD); 
					$allRow.addClass($allRow);
					
        			$.each($divisionList, function($divIdx, $division) {
    					$divTD = $("<td>"); 
    					$divTD.addClass("div-" + $division.divisionId);
    					$divTD.append($('<input type="checkbox" class="all-report-selector" data-division="'+$division.divisionId+'" />' )); 
    					$divTD.prop('checked','true');
    					$divTD.prop('checked','false');
    				//	var $divTD = $(this).

    				/*	if ($(this).prop(':checked')) {
    	                    $($divTD).val("True");
    	                } else {
    	                    $($divTD).val("False");
    	                } */
    					
           					$selector = $divTD;
           					$($selector).mouseover(function($event) {
           						$(".div-"+$division.divisionId).css('background-color','#F9F9F9');
           						$($divTD).css('background-color','#F9F9F9');
           					});
           					$($selector).mouseout(function($event) {
           						$(".div-"+$division.divisionId).css('background-color','transparent');
           						$($divTD).css('background-color','transparent');
           					});
  					
    					$allRow.append($divTD); 
    				});
        			$table.append($allRow);
        			
        			$.each( $reportList, function($reportIdx, $report) {
        				var $tr = $("<tr>"); 
        				$tr.addClass("report-" + $report.reportId); 
        				$reportTD = $("<td>"); 
    					$reportTD.append($report.description); 
    					$tr.append($reportTD).css('text-align','left'); 
    					
            			$allColumn = $('<td class="all">');
    					$allColumn.append($('<input type="checkbox" class="all-division-selector" data-report="'+$report.reportId+'" />'));
    					$allColumn.prop('checked','true');
    					$allColumn.prop('checked','false');

    				/*	if ($(this).is(':checked')) {
    	                    $($allColumn).val("True");
    	                } else {
    	                    $($allColumn).val("False");
    	                } */
    					
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
        					$divTD.prop('checked','true');
        					$divTD.prop('checked','false');

        			/*		if ($(this).is(':checked')) {
        	                    $($divTD).val("True");
        	                } else {
        	                    $($divTD).val("False");
        	                } */
        					
        					
        					
        					$tr.append($divTD);
        				});
        				$table.append($tr);	
        				
        			});
        			
        			
        			$("#division-selection-container").html("");
        			$("#division-selection-container").append($table);
        			



					$.each($subscriptionList, function($subscriptionIdx, $subscription) {
						$subscriptionId = $("input[type='checkbox']");		
						
                	}); 
        			
        			$.each( $reportList, function($reportIdx, $report) {        				
        				$.each($divisionList, function($divIdx, $division) {
        					$selector = ".report-" + $report.reportId + " .div-"+$division.divisionId;
        					$($selector).mouseover(function($event) {
        						$(".report-" + $report.reportId).css('background-color','#F9F9F9');
        						$(".div-"+$division.divisionId).css('background-color','#F9F9F9');
        					});
        					$($selector).mouseout(function($event) {
        						$(".report-" + $report.reportId).css('background-color','transparent');
        						$(".div-"+$division.divisionId).css('background-color','transparent');
        					});
        				});
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
        			$("#division-selection-container .report-selector").click(function($event) {
        				var $reportId = $(this).attr("data-report");
        				var $divisionId = $(this).attr("data-division");
        				var $subscribe = $(this).prop("checked");
        				var $subscriptionId = $(this).attr("data-subscriptionId");
        				REPORT_SUBSCRIPTION.doSubscription($reportId, $divisionId, $subscribe, $subscriptionId);
        			});
        			$("#division-selection-container .all-report-selector").click(function($event) {
        				var $divisionId = $(this).attr("data-division");
        				var $allReportType = $('.all-report-selector input[name="'+ $divisionId+'"]').prop('checked',true);
        				var $subscribe = $(this).prop("checked");
        				var $subscriptionId = $(this).attr("data-subscriptionId");
        				console.log("all for " + $divisionId + " " + $subscribe);
        				$.each( REPORT_SUBSCRIPTION.reportList, function($index, $report) {
        					$selector = '#division-selection-container input[name="'+$report.reportId+'-'+ $divisionId+'"]';
        					//$selector = '#division-selection-container input[name="'+$report.reportId+'-'+ $divisionId+'"]';
        					$subscribed = $($selector).prop("checked");
        					if ( $subscribe != $subscribed ) {
        						$($selector).prop("checked", $subscribe);
                				
        						REPORT_SUBSCRIPTION.doSubscription($allReportType, $report.reportId, $divisionId, $subscribe, $subscriptionId);	
        					}
        				}); 
        			});
        			$("#division-selection-container .all-division-selector").click(function($event) {
        				var $reportId = $(this).attr("data-report");
        				var $allDivisions = $('.all-division-selector input[name="'+ $reportId+'"]').prop('checked',true);
        				var $subscribe = $(this).prop("checked");
        				var $subscriptionId = $(this).attr("data-subscriptionId");
        				
        				console.log("all for " + $reportId + " " + $subscribe);
        				$.each( REPORT_SUBSCRIPTION.divisionList, function($index, $division) {
        					$selector = '#division-selection-container input[name="'+$reportId+'-'+ $division.divisionId+'"]';
        					$subscribed = $($selector).prop("checked");
        					if ( $subscribe != $subscribed ) {
        						$($selector).prop("checked", $subscribe);
                				
        						REPORT_SUBSCRIPTION.doSubscription($allDivisions, $reportId, $division.divisionId, $subscribe, $subscriptionId);	
        					}
        				});      				
        			});        			
        			$("#division-selection-container .all-division-report-selector").click(function($event) {
        				var $reportId = $(this).attr("data-report");
        				var $divisionId = $(this).attr("data-division");
        				var $subscribe = $(this).prop("checked");
        				var $subscriptionId = $(this).attr("data-subscriptionId");
        				console.log("all for all " + $subscribe);
        				//$("#division-selection-container .all-division-report-selector").prop("checked", $subscribe);
        			//	$("#division-selection-container .all-report-selector").prop("checked", $subscribe);
        				$.each( $reportList, function($reportIdx, $report) {        				
            				$.each($divisionList, function($divIdx, $division) {
        						var $allReportType = $('.all-report-selector input[name="'+ $divisionId+'"]').prop('checked',true);
        						var $allDivisions = $('.all-division-selector input[name="'+ $reportId+'"]').prop('checked',true);
            					$selector = '#division-selection-container input[name="'+$allReportType+'-'+ $allDivisions+'"]';
            					$subscribed = $($selector).prop("checked");
            					if ( $subscribe != $subscribed ) {
            						$($selector).prop("checked", $subscribe);
                    				
            						REPORT_SUBSCRIPTION.doSubscription($allReportType, $allDivisions, $subscribe, $subscriptionId);	
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
        		
        		
        		
        		makeExecutiveTable : function($reportList, $subscriptionList) {
        			console.log("makeExecutiveTable");
        			var $table = $("<table>").css('text-align','center');
        			var $hdrRow = $("<tr>");   
        			$hdrRow.append( $("<td>"));   
        			$hdrRow.append( $('<td class ="hdr">')); 
        			$table.append($hdrRow);
        			
        			var $allTD = $('<td class ="allselector">');
        			
        			$.each( $reportList, function($reportIdx, $report) {
        				if ( $report["executiveReport"] == true ) {
        					var $tr = $("<tr>"); 
            				$tr.addClass("report-" + $report.reportId); 
            				$reportTD = $("<td>"); 
        					$reportTD.append($report.description); 
        					$tr.append($reportTD).css('text-align','left'); 
        					$allTD.append($('<input type="checkbox" name="'+$report.reportId+'" class="allselector" data-report="'+$selector+'"/>'));
        					$allTD.prop('checked','true');
        					$allTD.prop('checked','false');

        			/*		if ($(this).is(':checked')) {
        	                    $($allTD).val("True");
        	                } else {
        	                    $($allTD).val("False");
        	                } */
        					
                		//	$allColumn = $('<td class="all">').append($('<input type="checkbox" class="executive-selector" data-report="'+$report.reportId+'" />'));
        				
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
        					$tr.append($allColumn);
        					
            				$table.append($tr);	
            				
        				}
        			});
        			
        			$("#executive-selection-container").html("");
        			$("#executive-selection-container").append($table);
        			


					$.each($subscriptionList, function($subscriptionIdx, $subscription) {
						$subscriptionId = $("input[type='checkbox']");					
                	}); 
        			
        			$.each( $reportList, function($reportIdx, $report) {        				
        				$.each($reportList, function($reportIdx, $report) {
        					$selector = ".report-" + $report.reportId;
        					$($selector).mouseover(function($event) {
        						$(".report-" + $report.reportId).css('background-color','#F9F9F9');
        					});
        					$($selector).mouseout(function($event) {
        						$(".report-" + $report.reportId).css('background-color','transparent');
        					});
        				});
        			});       
        			$("#executive-selection-container .executive-selector").click(function($event) {
        				var $reportId = $(this).attr("data-report");
        				var $subscribe = $(this).prop("checked");
        				var $subscriptionId = $(this).attr("data-subscriptionId");
        				REPORT_SUBSCRIPTION.doSubscription($reportId, $subscribe, $subscriptionId);
        			});
        			$("#executive-selection-container .all-selector").click(function($event) {
        				var $subscribe = $(this).prop("checked");
        				var $subscriptionId = $(this).attr("data-subscriptionId");
        				console.log("all for all " + $subscribe);
        				$("#executive-selection-container .all-selector").prop("checked", $subscribe);
        				$.each( $reportList, function($reportIdx, $report) {   
            					$selector = '#executive-selection-container input[name="'+$report.reportId+'"]';
            					$subscribed = $($selector).prop("checked");
            					if ( $subscribe != $subscribed ) {
            						$($selector).prop("checked", $subscribe);
            						REPORT_SUBSCRIPTION.doSubscription($report.reportId, $subscribe, $subscriptionId);	
            					}
            				}); 
            			});  
        			
        
        			$.each( $("#selection-menu-container input[name='subscription-selector']"), function($index, $value) {
        				if ( $value.value == 'report' ) {
        					$(this).click();
        				}        				
        			});
        		},
        	
        		
        		makeGroupTable : function($reportList, $companyList, $subscriptionList, $filter, $destination, $reportId, $divisionId) {
        			console.log("makeGroupTable");
        			var $table = $("<table>");
        			var $hdrRow = $("<tr>");
        			
        			$hdrRow.append($("<td>")).css('text-align','center');  
        			$hdrRow.append($('<td class ="hdr">').append("All"));
    				$.each($companyList, function($companyIdx, $company) {
    					$companyTD=$("<td>");
    					$companyTD.addClass("company-" + $company.id);
    					$companyTD.append($company.name);
    					$hdrRow.append($companyTD);
    				});
        			$table.append($hdrRow);
        			
        			var $allRow = $("<tr>").css('text-align','center');
	        			$selector = $allRow;
						$($selector).mouseover(function($event) {
							$($allRow).css('background-color','#F9F9F9');
	    					$($allLabel).css('background-color','#F9F9F9');
						});
						$($selector).mouseout(function($event) {
							$($allRow).css('background-color','transparent');
	    					$($allLabel).css('background-color','transparent');
						});
        			
        			var $allLabel = $("<td>").append("All").css('text-align','left');
        			var $allTD = $("<td>").append($('<input type="checkbox" class="groupTable-all-company-report-selector" />'));
        			
        			$allTD.prop('checked','true');
        			$allTD.prop('checked','false');

				/*	if ($(this).is(':checked')) {
	                    $($allTD).val("True");
	                } else {
	                    $($allTD).val("False");
	                } */
					
        			
					$.each( $reportList, function($reportIdx, $report) {        				
            				$.each($companyList, function($companyIdx, $company) {
            					$selector = $allTD;
            					$($selector).mouseover(function($event) {
            						$(".report-" + $report.reportId).css('background-color','#F9F9F9');
            						$(".company-"+$company.id).css('background-color','#F9F9F9');
            						$($allLabel).css('background-color','#F9F9F9');
                					$($allTD).css('background-color','#F9F9F9');
                					$(".hdr").css('background-color','#F9F9F9');
            					});
            					$($selector).mouseout(function($event) {
            						$(".report-" + $report.reportId).css('background-color','transparent');
            						$(".company-"+$company.id).css('background-color','transparent');
            						$($allLabel).css('background-color','transparent');
                					$($allTD).css('background-color','transparent');
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
    					$companyTD.prop('checked','true');
    					$companyTD.prop('checked','false');

    				/*	if ($(this).is(':checked')) {
    	                    $($companyTD).val("True");
    	                } else {
    	                    $($companyTD).val("False");
    	                } */
    					
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
	        				
	    					$allColumn.prop('checked','true');
	    					$allColumn.prop('checked','false');

        			/*		if ($(this).is(':checked')) {
        	                    $($allColumn).val("True");
        	                } else {
        	                    $($allColumn).val("False");
        	                } */
        					
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
	    					$.each($companyList, function($companyIdx, $company) {
	        					$companyTD=$("<td>");
	        					$companyTD.addClass("company-" + $company.id);
	        					$companyTD.append($('<input type="checkbox" name="'+$report.reportId+"-" + $company.id +'" class="groupTable-report-selector" data-report="'+$report.reportId+'" data-company="'+$company.id+'"/>'));
	        					
	        					$companyTD.prop('checked','true');
	        					$companyTD.prop('checked','false');
	        					
	        				/*	if ($(this).is(':checked')) {
	        	                    $($companyTD).val("True");
	        	                } else {
	        	                    $($companyTD).val("False");
	        	                } */
	        					
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
           					});
           					$($selector).mouseout(function($event) {
           						$(".report-" + $report.reportId).css('background-color','transparent');
           						$(".company-" + $company.id).css('background-color','transparent');
           					});
           				});
           			});  

					$.each($subscriptionList, function($subscriptionIdx, $subscription) {
						$subscriptionId = $("input[type='checkbox']");							
                	}); 
   					$($destination+" .groupTable-report-selector").click(function($event) {
   						var $reportId = $(this).attr("data-report");
   						var $id = $(this).attr("data-company");
   						var $subscribe = $(this).prop("checked");
        				var $subscriptionId = $(this).attr("data-subscriptionId");
   						REPORT_SUBSCRIPTION.doSubscription($reportId, $id, $subscribe, $subscriptionId);
   					}); 
           			
   					$($destination+" .groupTable-all-report-selector").click(function($event) {
   						var $id = $(this).attr("data-company");
   						var $subscribe = $(this).prop("checked");
        				var $subscriptionId = $(this).attr("data-subscriptionId");
   						console.log("all for " + $id + " " + $subscribe);
   						$.each( REPORT_SUBSCRIPTION.reportList, function($index, $report) {
   							$selector = $destination+ ' input[name="'+$report.reportId+'-'+ $id+'"]';
   							$subscribed = $($selector).prop("checked");
   							if ( $subscribe != $subscribed ) {
   								$($selector).prop("checked", $subscribe);
   								REPORT_SUBSCRIPTION.doSubscription($report.reportId, $id, $subscribe, $subscriptionId);	
   							}
   						});        				
   					});      	
           			$($destination+" .groupTable-all-company-selector").click(function($event) {
           				var $reportId = $(this).attr("data-report");
           				var $subscribe = $(this).prop("checked");
        				var $subscriptionId = $(this).attr("data-subscriptionId");
           				console.log("all for " + $reportId + " " + $subscribe);
           				$.each( REPORT_SUBSCRIPTION.companyList, function($index, $company) {
           					$selector = $destination+ ' input[name="'+$reportId+'-'+ $company.id+'"]';
           					$subscribed = $($selector).prop("checked");
           					if ( $subscribe != $subscribed ) {
           						$($selector).prop("checked", $subscribe);
           						REPORT_SUBSCRIPTION.doSubscription($reportId, $company.id, $subscribe, $subscriptionId);	
           					}
           				});        				
           			});        		
					$($destination+" .groupTable-all-company-report-selector").click(function($event) {
						var $subscribe = $(this).prop("checked");
        				var $subscriptionId = $(this).attr("data-subscriptionId");
						console.log("all for all " + $subscribe);
						$($destination+" .groupTable-all-company-selector").prop("checked", $subscribe);
						$($destination+" .groupTable-all-report-selector").prop("checked", $subscribe);
						$.each( $reportList, function($reportIdx, $report) {        				
		    				$.each($companyList, function($companyIdx, $company) {
		    					$selector =$destination+ ' input[name="'+$report.reportId+'-'+ $company.id+'"]';
		    					$subscribed = $($selector).prop("checked");
		    					if ( $subscribe != $subscribed ) {
		    						$($selector).prop("checked", $subscribe);
		    					//	REPORT_SUBSCRIPTION.updateSubscription($report.reportId, $company.id, $subscribe)
		    						REPORT_SUBSCRIPTION.doSubscription($report.reportId, $company.id, $subscribe, $subscriptionId);	
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
       		
       		makeLists : function($reportId, $divisionId, $subscriptionId) {
       			var jqxhr = $.ajax({
					type: 'GET',
					url: "reports/subscription",
					statusCode: {
						200 : function($data) {
							REPORT_SUBSCRIPTION.divisionList = $data.data.divisionList;
							REPORT_SUBSCRIPTION.reportList = $data.data.reportList;
							REPORT_SUBSCRIPTION.companyList = $data.data.companyList;
							REPORT_SUBSCRIPTION.regionList = $data.data.regionList;
							REPORT_SUBSCRIPTION.subscriptionList = $data.data.subscriptionList;
						//	REPORT_SUBSCRIPTION.allDivisions = $data.data.allDivisions;
						//	REPORT_SUBSCRIPTION.subscribe = $data.data.subscribe;
							REPORT_SUBSCRIPTION.makeDivisionTable($data.data.reportList, $data.data.divisionList, $data.data.subscriptionList, $reportId, $divisionId, $subscriptionId);
							REPORT_SUBSCRIPTION.makeGroupTable($data.data.reportList, $data.data.companyList, $data.data.subscriptionList, 'summaryReport', "#company-selection-container", $reportId, $divisionId, $subscriptionId);
							REPORT_SUBSCRIPTION.makeGroupTable($data.data.reportList, $data.data.regionList, $data.data.subscriptionList, 'summaryReport', "#region-selection-container", $reportId, $divisionId, $subscriptionId);
							REPORT_SUBSCRIPTION.makeExecutiveTable($data.data.reportList, $data.data.subscriptionList, $reportId, $subscriptionId);
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

