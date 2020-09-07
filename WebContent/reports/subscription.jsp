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
			.subscription-selector {
				width:11%;
				float:right;
				display: none;
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
            	companyList : [],
        		divisionList : [],
        		groupList : [],
        		reportList : [],
        		regionList : [],
        		//allAnsiReports : [],
        		//subscriptionList : [],
        		//allReportType : [],
        		
        		
        		init : function() {
        			REPORT_SUBSCRIPTION.makeClickers();	
        			REPORT_SUBSCRIPTION.getSubscriptions();
        		},
                
        		
        		doSubscription : function($reportId, $divisionId, $columnId, $subscribe, $allDivisions, $allAnsiReports, $divisionReports, $subscriptionId) {
        			if ( $subscribe ) {
        				$word = "Subscribe"
        			} else {
        				$word = "Unsubscribe"
        			}
        			
            		var $outbound = {"reportId":$reportId, "divisionId":$divisionId, "columnId":$columnId, "subscribe":$subscribe, "allDivisions":$allDivisions, "allAnsiReports":$allAnsiReports, "subscriptionId":$subscriptionId, "divisionReports":$divisionReports};
            				
	       			var jqxhr = $.ajax({
						type: 'POST',
						url: "reports/subscription",
          				$outbound : [{"reportId":null, "allAnsiReports":$allAnsiReports, "divisionReports":$divisionReports}, {"divisionId":null, "allDivisions":$allDivisions, "divisionReports":$divisionReports}, {"reportId":$reportId, "columnId":$columnId}],
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
        		
        		
        		
        		
				getSubscriptions : function($reportId, $divisionId, $subscriptionId) {           			
           			var jqxhr = $.ajax({
    					type: 'GET',
    					url: "reports/subscription",
    					statusCode: {
    						200 : function($data) {
    							REPORT_SUBSCRIPTION.makeTables($data);
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
           		
           		
        		
        		makeClickers : function() {
        			var $menuOpt = $("#selection-menu-container input[name='subscription-selector']");
           			$menuOpt.click(function($event, $value) {
           				var $selection = $(this).val();
           				$(".selection-container").hide();
           				var $selector = "#" + $selection + "-selection-container";
           				$($selector).show();
           			});
           			
           			$(".subscription-selector").hide();	
        		},
        		
       		
        		
        		
				makeDivisionTable : function($divisionList, $divisionReports, $reportId, $divisionId,  $subscriptionList, $subscriptionId) {        			
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
					
					$.each( $divisionReports, function($reportIdx, $report) {        				
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
				       			
        			$.each( $divisionReports, function($reportIdx, $report) {
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
					
        			$.each( $divisionReports, function($reportIdx, $report) {        				
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
        					$selector = $reportTD;
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
        		
        		
        		
       			makeMultiColumnTable($container, $reportList, $columnList, $subscriptionList) {
        			console.log("Making container" + $container);
        			var $table = $("<table>");
           			var $hdrRow = $("<tr>");
           			$hdrRow.append( $("<td>"));
           			$.each($columnList, function($index, $value) {
           				var $hdrTD = $("<td>").append($value.name);
           				$hdrTD.addClass("hdr"); 
    					$hdrTD.addClass("div-" + $value.name);
           				$hdrRow.append($hdrTD);	
           				$hdrTD.addClass("hdr"); 
           			});
           			
           			$table.append($hdrRow);
           			
           			$.each($reportList, function($index, $report) {
           				var $row = $("<tr>");
        				$row.addClass("report-" + $report.reportId);
           				$row.addClass("reportrow"); 
           				var $labelTD = $("<td>");
           				$labelTD.append($report.description);
           				$row.append($labelTD);
           				$.each($columnList, function($index2, $column) {
           					var $checkboxTD = $("<td>");
        					$checkboxTD.addClass("div-" + $column.id);
               				var $checkbox = $('<input />');
               				$checkbox.addClass("subscription-checkbox");
               				
        					//$divTD.append($('<input type="checkbox" name="'+$report.reportId+"-" + $column.id +'" class="report-selector" data-report="'+$report.reportId+'" data-group="'+$groupId+'"/>'));
        					
               			 	$checkbox.attr("name",$container + '-' + $column.id);
               			 	$checkbox.attr("type","checkbox");
               			 	$checkbox.attr("value", $column.id);
               				$checkboxTD.append($checkbox);
               				$row.append($checkboxTD);
               				//$checkboxTD.addClass("divisioncolumn"); 
           				});           				
           				$table.append($row);
           			});

           			
           			$("#"+$container).append($table);
           			
         			$selector = "#"+$container + " .reportrow";
          			$($selector).mouseover(function($event) {
   						$(this).css('background-color','#F9F9F9');
   					});
   					$($selector).mouseout(function($event) {
   						$(this).css('background-color','transparent');
   					}); 
           			
         			$selector = "#"+$container + " .hdr";
          			$($selector).mouseover(function($event) {
   						$(this).css('background-color','#F9F9F9');
   					});
   					$($selector).mouseout(function($event) {
   						$(this).css('background-color','transparent');
   					});
   					
           			$selector = "#"+$container + " .subscription-checkbox";
   					$($selector).click(function($event) {
   						var $subscribe = $(this).attr("data-column.id" + "data-reportId");
   						var $reportId = $(this).attr("data-reportId");
   						var $columnId = $(this).attr("data-column.id");
   						REPORT_SUBSCRIPTION.doSubscription($subscribe, $reportId, $columnId, null);
   					});
        		},

        		
        		
        		
        		makeOneColumnTable : function($container, $reportList, $subscriptionList) {
           			console.log("Making container " + $container);
           			var $table = $("<table>");
           			var $hdrRow = $('<tr><td>&nbsp;</td><td><span style="font-weight:bold;">Subscribe</span></td></tr>');
           			$table.append($hdrRow);
           			
           			
           			$.each($reportList, function($index, $value) {
           				var $row = $("<tr>");
           				$row.addClass("reportrow");
           				$row.addClass("report-"+$value.reportId);
           				var $labelTD = $("<td>");
           				$labelTD.append($value.description);
           				$row.append($labelTD);
           				var $checkboxTD = $("<td>");
           				$checkboxTD.attr("style","text-align:center;");
           				var $checkbox = $('<input>');
           			 	$checkbox.attr("name",$container + '-' + $value.reportId);
           			 	$checkbox.addClass("subscription-checkbox");
           			 	$checkbox.attr("type","checkbox");
           			 	$checkbox.attr("value", $value.reportId);
           				$checkboxTD.append($checkbox);
           				$row.append($checkboxTD);
           				$table.append($row);
           			});
           			
           			$("#"+$container).append($table);
           			
         			$selector = "#"+$container + " .reportrow";
          			$($selector).mouseover(function($event) {
   						$(this).css('background-color','#F9F9F9');
   					});
   					$($selector).mouseout(function($event) {
   						$(this).css('background-color','transparent');
   					});
   					
   					$selector = "#"+$container + " .subscription-checkbox";
   					$($selector).click(function($event) {
   						var $reportId = $(this).attr("data-reportid");
   						REPORT_SUBSCRIPTION.doSubscription($subscribe, $reportId);
   					});
       			},
       			

        		
        		
        		makeTables : function($data) {
           			//reports
   					/*		
   					$("#allAnsi-subscription-selector").hide();	
   					$("#company-subscription-selector").hide();	
   					$("#region-subscription-selector").hide();	
   					$("#group-subscription-selector").hide();	
   					$("#division-subscription-selector").hide();	
   					$("#trend-subscription-selector").hide();								
   					$("#utility-subscription-selector").hide(); 
   					*/
   					
   					REPORT_SUBSCRIPTION.allAnsiReports = $data.data.allAnsiReports;
   					REPORT_SUBSCRIPTION.divisionReports = $data.data.divisionReports;
   					REPORT_SUBSCRIPTION.summaryReports = $data.data.summaryReports;
   					REPORT_SUBSCRIPTION.trendReports = $data.data.trendReports;
   					REPORT_SUBSCRIPTION.utilityReports = $data.data.utilityReports;

   					REPORT_SUBSCRIPTION.reportList = $data.data.reportList;
   					REPORT_SUBSCRIPTION.reportType = $data.data.reportType;
   					
   					//lists
   					REPORT_SUBSCRIPTION.companyList = $data.data.companyList;
   					REPORT_SUBSCRIPTION.divisionList = $data.data.divisionList;
   					REPORT_SUBSCRIPTION.groupList = $data.data.groupList;					
   					REPORT_SUBSCRIPTION.regionList = $data.data.regionList;
   				//	REPORT_SUBSCRIPTION.orgList = $data.data.reportList;		
   					REPORT_SUBSCRIPTION.subscriptionList = $data.data.subscriptionList;
   					REPORT_SUBSCRIPTION.summaryReportList = $data.data.summaryReportList;
   					
   					if ( REPORT_SUBSCRIPTION.allAnsiReports.length > 0 ) {
						$("#allAnsi-subscription-selector").show();	
						REPORT_SUBSCRIPTION.makeOneColumnTable("allAnsi-selection-container", REPORT_SUBSCRIPTION.allAnsiReports, $data.data.subscriptionList);
					} else {
						$("#allAnsi-subscription-selector").hide();
					}
					
					if ( REPORT_SUBSCRIPTION.trendReports.length > 0 ) {
						$("#trend-subscription-selector").show();	
						REPORT_SUBSCRIPTION.makeOneColumnTable("trend-selection-container", REPORT_SUBSCRIPTION.trendReports, $data.data.subscriptionList);
					} else {
						$("#trend-subscription-selector").hide();
					}
					
					if ( REPORT_SUBSCRIPTION.utilityReports.length > 0 ) {
						$("#utility-subscription-selector").show();	
						REPORT_SUBSCRIPTION.makeOneColumnTable("utility-selection-container", REPORT_SUBSCRIPTION.utilityReports, $data.data.subscriptionList);
					} else {
						$("#utility-subscription-selector").hide();
					}

					if ( REPORT_SUBSCRIPTION.summaryReports.length > 0 ) {
						if ( REPORT_SUBSCRIPTION.companyList.length > 0 ) {
							$("#company-subscription-selector").show();
							REPORT_SUBSCRIPTION.makeMultiColumnTable("company-selection-container", REPORT_SUBSCRIPTION.summaryReports, REPORT_SUBSCRIPTION.companyList, $data.data.subscriptionList);
						} else {
							$("#company-subscription-selector").hide();
						}
						if ( REPORT_SUBSCRIPTION.regionList.length > 0 ) {
							$("#region-subscription-selector").show();
							REPORT_SUBSCRIPTION.makeMultiColumnTable("region-selection-container", REPORT_SUBSCRIPTION.summaryReports, REPORT_SUBSCRIPTION.regionList, $data.data.subscriptionList);
						} else {
							$("#region-subscription-selector").hide();
						}
						if ( REPORT_SUBSCRIPTION.groupList.length > 0 ) {
							$("#group-subscription-selector").show();
							REPORT_SUBSCRIPTION.makeMultiColumnTable("group-selection-container", REPORT_SUBSCRIPTION.summaryReports, REPORT_SUBSCRIPTION.regionList, $data.data.subscriptionList);
						} else {
							$("#group-subscription-selector").hide();
						}
					} else {
						$("#company-subscription-selector").hide();
						$("#region-subscription-selector").hide();
						$("#group-subscription-selector").hide();
					}
					
					if ( $data.data.divisionList.length > 0 && $data.data.divisionReports.length > 0 ) { 
						$("#division-subscription-selector").show();	
						REPORT_SUBSCRIPTION.makeDivisionTable($data.data.divisionList, $data.data.divisionReports, $data.data.subscriptionList);
					} else {  
						$("#division-subscription-selector").hide();								
					}
					
					$("#thinking").hide();
        		},
       		}

	        REPORT_SUBSCRIPTION.init();
        	
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.reportSubscription" /></h1>
    	<div id="thinking"><webthing:thinking></webthing:thinking></div>
    	    	
    	<div id="selection-menu-container">
    		Subscribe to:<br />
    		<div id= "allAnsi-subscription-selector subscription-selector">
    		<input type="radio" name="subscription-selector" value="allAnsi" /> All Ansi Reports<br />
    		</div>
    		
    		<div id= "company-subscription-selector subscription-selector">		
    		<input type="radio" name="subscription-selector" value="company" /> Company Reports<br />
			</div>
			    		
    		<div id= "region-subscription-selector subscription-selector">
    		<input type="radio" name="subscription-selector" value="region" /> Region Reports<br />
			</div>
			    		
    		<div id= "group-subscription-selector subscription-selector">
    		<input type="radio" name="subscription-selector" value="group" /> Group Reports<br />
			</div>
    		
    		<div id= "division-subscription-selector subscription-selector">
    		<input type="radio" name="subscription-selector" value="division" /> Division Reports<br />
			</div>
    		   		
    		<div id= "trend-subscription-selector subscription-selector">
    		<input type="radio" name="subscription-selector" value="trend" /> Trend Reports<br />
			</div>
			    		
    		<div id= "utility-subscription-selector">
    		<input type="radio" name="subscription-selector" value="utility" /> Utility Reports<br />
    		</div>    	
    		    		
    		<div style="text-align: center">
    		<ansi:hasPermission permissionRequired="REPORT_SUBSCRIPTION_READ">
    		<a href="reports/subscriptionCSV"><webthing:csv>Subscriptions</webthing:csv></a>
    		</ansi:hasPermission>
    		</div>
    		
    	</div>
    	
    	<div id="subscription-container">
    		
			<div id="allAnsi-selection-container" class="selection-container" >
			</div>
	     
	     	<div id="region-selection-container" class="selection-container" >
	     	</div>
	     	
	     	<div id="company-selection-container" class="selection-container" >
	     	</div>
	     
	     	<div id="group-selection-container" class="selection-container" >
	     	</div>
    		
			<div id="division-selection-container" class="selection-container" >
			</div>
	    
	    	<div id="trend-selection-container" class="selection-container" >
	    	</div>
	    
	    	<div id="utility-selection-container" class="selection-container" >
	    	</div>
	    	
	    	<webthing:scrolltop />
    	</div>
    	<div style="clear:both; height:1px; width:100%;">&nbsp;</div>
    
    	
    </tiles:put>
		
</tiles:insert>

