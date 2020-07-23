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
        		allDivReportList : [],
        	//	subscriptionList : [],
        		//allReportType : [],
        		
        		
        		init : function() {
        			REPORT_SUBSCRIPTION.makeLists();
        			REPORT_SUBSCRIPTION.makeClickers();
        		//	REPORT_SUBSCRIPTION.getSubscriptions();
        		},
                
        		
        		doSubscription : function($reportId, $divisionId, $subscribe, $allDivisions, $allReportType, $allDivReportList, $subscriptionId) {
        			if ( $subscribe ) {
        				$word = "Subscribe"
        			} else {
        				$word = "Unsubscribe"
        			}
        			
            		var $outbound = {"reportId":$reportId, "divisionId":$divisionId, "subscribe":$subscribe, "allDivisions":$allDivisions, "allReportType":$allReportType, "subscriptionId":$subscriptionId, "allDivReportList":$allDivReportList};
            				
	       			var jqxhr = $.ajax({
						type: 'POST',
						url: "reports/subscription",
          				$outbound : [{"reportId":null, "allReportType":$allReportType, "allDivReportList":$allDivReportList}, {"divisionId":null, "allDivisions":$allDivisions, "allDivReportList":$allDivReportList}],
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
          								REPORT_SUBSCRIPTION.makeLists();
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
        		
        		makeDivisionTable : function($reportList, $divisionList, $subscriptionList, $allDivReportList, $subscriptionId) {
        			
        			console.log("makeDivisionTable");
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
					
					$.each( $reportList, function($reportIdx, $report) {        				
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
        			
        			$.each( $reportList, function($reportIdx, $report) {
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
        			
        			$("#division-selection-container").html("");
        			$("#division-selection-container").append($table);
        			
					$.each($subscriptionList, function($index, $value) {
						var $checkboxName = $value.reportId + "-" + $value.divisionId;
						var $checkbox = "#division-selection-container input[name='" + $checkboxName +"']";
						console.log($checkbox);
						$($checkbox).prop(":checked", true);	
						
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
        			
        			$.each( $reportList, function($reportIdx, $report) {        				
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
        			$("#division-selection-container .report-selector").click(function($event) {
        				var $reportId = $(this).attr("data-report");
        				var $divisionId = $(this).attr("data-division");
        				var $subscribe = $(this).prop("checked");
        				var $subscriptionId = $(this).attr("data-subscriptionId");
        				REPORT_SUBSCRIPTION.doSubscription($reportId, $divisionId, $subscribe, $subscriptionId);
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

        					if ($(this).is(':checked')) {
        	                    $($allTD).val("True");
        	                } else {
        	                    $($allTD).val("False");
        	                } 
        					
                			$allColumn = $('<td class="all">').append($('<input type="checkbox" class="executive-selector" data-report="'+$report.reportId+'" />'));
        				
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
        			
					$.each($subscriptionList, function($index, $value) {
						var $checkboxName = $value.reportId + "-" + $value.divisionId;
						var $checkbox = "#division-selection-container input[name='" + $checkboxName +"']";
						console.log($checkbox);
						$($checkbox).prop(":checked", true);	
						
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
        			
        			$.each( $("#selection-menu-container input[name='subscription-selector']"), function($index, $value) {
        				if ( $value.value == 'report' ) {
        					$(this).click();
        				}        				
        			});
        		},
        	
        		
        		makeGroupTable : function($reportList, $companyList, $subscriptionList, $allDivReportList, $filter, $destination, $reportId, $divisionId) {
        			console.log("makeGroupTable");
        			var $table = $("<table>");
        			var $hdrRow = $("<tr>");
        			
        			$hdrRow.append($("<td>")).css('text-align','center');  
    				$.each($companyList, function($companyIdx, $company) {
    					$companyTD=$("<td>");
    					$companyTD.addClass("company-" + $company.id);
    					$companyTD.append($company.name);
    					$hdrRow.append($companyTD);
    				});
        			$table.append($hdrRow);
					
					$.each( $reportList, function($reportIdx, $report) {        				
            				$.each($companyList, function($companyIdx, $company) {
            					$selector = ".report-" + $report.reportId;
            					$($selector).mouseover(function($event) {
            						$(".report-" + $report.reportId).css('background-color','#F9F9F9');
            						$(".company-"+$company.id).css('background-color','#F9F9F9');
            					//	$($allLabel).css('background-color','#F9F9F9');
                				//	$($allTD).css('background-color','#F9F9F9');
                					$(".hdr").css('background-color','#F9F9F9');
            					});
            					$($selector).mouseout(function($event) {
            						$(".report-" + $report.reportId).css('background-color','transparent');
            						$(".company-"+$company.id).css('background-color','transparent');
            					//	$($allLabel).css('background-color','transparent');
                				//	$($allTD).css('background-color','transparent');
                					$(".hdr").css('background-color','transparent');
            					});
            				});
            			});   
        			
        			$.each( $reportList, function($reportIdx, $report) {
        				if ( $report[$filter] == true ) {
	        				var $tr = $("<tr>");
	        				$tr.addClass("report-" + $report.reportId);
	        				$reportTD = $("<td>");
	    					$reportTD.append($report.description).css('text-align','left');
	    					$tr.append($reportTD).css('text-align','left');
	    					
	    					$.each($companyList, function($companyIdx, $company) {
	        					$companyTD=$("<td>");
	        					$companyTD.addClass("company-" + $company.id);
	        					$companyTD.append($('<input type="checkbox" name="'+$report.reportId+"-" + $company.id +'" class="groupTable-report-selector" data-report="'+$report.reportId+'" data-company="'+$company.id+'"/>'));
	        					
	        					$companyTD.prop('checked','true');
	        					$companyTD.prop('checked','false');
	        					
	        					$tr.append($companyTD).css('text-align','center');
	        				});
	        				$table.append($tr);
        				}
        			});    
        			
           			$($destination).html("");
           			$($destination).append($table);   

					$.each($subscriptionList, function($index, $value) {
						var $checkboxName = $value.reportId + "-" + $value.divisionId;
						var $checkbox = "#division-selection-container input[name='" + $checkboxName +"']";
						console.log($checkbox);
						$($checkbox).prop(":checked", true);		
						
                	}); 
           			
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
           			
   					$($destination+" .groupTable-report-selector").click(function($event) {
   						var $reportId = $(this).attr("data-report");
   						var $id = $(this).attr("data-company");
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
       		
       		makeLists : function($reportId, $divisionId, $allDivReportList, $subscriptionId) {
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
							REPORT_SUBSCRIPTION.allDivReportList = $data.data.allDivReportList;
						//	REPORT_SUBSCRIPTION.allDivisions = $data.data.allDivisions;
						//	REPORT_SUBSCRIPTION.subscribe = $data.data.subscribe;
							REPORT_SUBSCRIPTION.makeDivisionTable($data.data.reportList, $data.data.divisionList, $data.data.allDivReportList, $data.data.subscriptionList, $reportId, $divisionId, $allDivReportList, $subscriptionId);
							REPORT_SUBSCRIPTION.makeGroupTable($data.data.reportList, $data.data.companyList, $data.data.allDivReportList, $data.data.subscriptionList, 'summaryReport', "#company-selection-container", $reportId, $divisionId, $allDivReportList, $subscriptionId);
							REPORT_SUBSCRIPTION.makeGroupTable($data.data.reportList, $data.data.regionList, $data.data.allDivReportList, $data.data.subscriptionList, 'summaryReport', "#region-selection-container", $reportId, $divisionId, $allDivReportList, $subscriptionId);
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

