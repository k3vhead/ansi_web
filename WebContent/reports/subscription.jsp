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
				display: hidden;
				width:11%;
				float:right;
			}
			.checkbox {
				float: center;
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
        		allAnsiReports : [],
        		subscriptionList : [],
        		        		
        		
        		init : function() {
        			REPORT_SUBSCRIPTION.makeClickers();		
        			REPORT_SUBSCRIPTION.getSubscriptions();
        		},
        		                
        		
        		doGroupSubscription : function($reportId, $groupId, $subscribe, $subscriptionId) {
        			console.log("doSubscription");
        			//console.log($reportId + " " + $groupId + " " + $subscribe + " " + $subscriptionId);
        			if ( $subscribe == true ) {
        				$word = "Subscribe"
        			} else {
        				$word = "Unsubscribe"
        			}

            		var $outbound = {"reportId":$reportId, "groupId":$groupId, "subscribe":$subscribe, "subscriptionId":$subscriptionId, "null":null};
            				
	       			var jqxhr = $.ajax({
						type: 'POST',
						url: "reports/subscription",
      					data: JSON.stringify($outbound),
      					statusCode: {
							200 : function($data) {
          							if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
          								$("#globalMsg").html("Edit Error").show().fadeOut(6000);
                          				console.log("updates failed");
          							}
          							if ( $data.responseHeader.responseCode == 'SUCCESS') {
          								$("#globalMsg").html("Update Successful").show().fadeOut(6000);
          								$("#thinking").hide();
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
      			
        			//$("#globalMsg").html($word + " to " + $reportId + " for " + $groupId).show().fadeOut(6000);
        			//console.log($word + " to " + $reportId + " for " + $groupId);
        			
        		},
        		
                        		
        		doDivisionSubscription : function($reportId, $divisionId, $subscribe, $subscriptionId) {
        			console.log("doSubscription");
        			//console.log($reportId + " " + $divisionId + " " + $subscribe + " " + $subscriptionId);
        			if ( $subscribe == true ) {
        				$word = "Subscribe"
        			} else {
        				$word = "Unsubscribe"
        			}

            		var $outbound = {"reportId":$reportId, "divisionId":$divisionId, "subscribe":$subscribe, "subscriptionId":$subscriptionId};
            				
	       			var jqxhr = $.ajax({
						type: 'POST',
						url: "reports/subscription",
      					data: JSON.stringify($outbound),
      					statusCode: {
							200 : function($data) {
          							if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
          								$("#globalMsg").html("Edit Error").show().fadeOut(6000);
                          				console.log("updates failed");
          							}
          							if ( $data.responseHeader.responseCode == 'SUCCESS') {
          								$("#globalMsg").html("Update Successful").show().fadeOut(6000);
          								$("#thinking").hide();
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
      			
        			//$("#globalMsg").html($word + " to " + $reportId + " for " + $divisionId).show().fadeOut(6000);
        			//console.log($word + " to " + $reportId + " for " + $divisionId);
        			
        		},
        		     		
        		        		
				getSubscriptions : function($reportId, $divisionId, $groupId, $subscriptionId) {           			
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
        			<ansi:hasPermission permissionRequired="PERMISSIONS_WRITE">;
        			var $menuOpt = $("#selection-menu-container input[name='subscription-selector']");
           			$menuOpt.click(function($event, $value) {
           				var $selection = $(this).val();
           				$(".selection-container").hide();
           				var $selector = "#" + $selection + "-selection-container";
           				$($selector).show();
           			});
           			</ansi:hasPermission>;
           			$(".subscription-selector").hide();	
        		},
        		       		
        		
				makeDivisionTable : function($divisionList, $divisionReports, $subscriptionList) {        			
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
            					});
            					$($selector).mouseout(function($event) {
            						$(".report-" + $report.reportId).css('background-color','transparent');
            						$(".div-"+$division.divisionId).css('background-color','transparent');
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

        					$tr.append($divTD);
        				});
        				$table.append($tr);	
        				
        			});

        			$("#division-selection-container").html("");
        			$("#division-selection-container").append($table);
        			
        			//console.log("We have " + $subscriptionList.length + " subscriptions");
					$.each($subscriptionList, function($index, $value) {				
						var $checkbox = "#division-selection-container input[name='" + $value.reportId + "-" + $value.divisionId +"']";
						//console.log("Subscribe" + $subscriptionId);
						$($checkbox).prop("checked", true);							
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
        				REPORT_SUBSCRIPTION.doDivisionSubscription($reportId, $divisionId, $subscribe, $subscriptionId);
        			});
        
        			$.each( $("#selection-menu-container input[name='subscription-selector']"), function($index, $value) {
        				if ( $value.value == 'division' ) {
        					$(this).click();
        				}        				
        			});
        		},
       		
        		        		
       			makeMultiColumnTable : function($container, $reportList, $divisionList, $groupList, $subscriptionList) {
        			console.log("Making container" + $container);
        			var $table = $("<table>").css('text-align','center');
           			var $hdrRow = $("<tr>");
           			$hdrRow.append( $("<td>"));
           			
           			$.each($groupList, function($groupIdx, $group) {
           				var $hdrTD = $("<td>").append($group.name);
    					$hdrTD.addClass("div-" + $group.id);
           				$hdrRow.append($hdrTD);	
           				$hdrTD.addClass("hdr");
           				//console.log("LOOK" + $group.id);
           			}); 
           			
           			$table.append($hdrRow);
           			
           			$.each($reportList, function($index, $value) {
           				var $row = $("<tr>");
           				$row.addClass("reportrow");
        				$row.addClass("report-" + $value.reportId);
           				var $labelTD = $("<td>");
           				$labelTD.append($value.description).css('text-align','left');
           				$row.append($labelTD);
           				$.each($groupList, function($groupIdx, $group) {
           					$checkboxTD = $("<td>");
        					$checkboxTD.addClass("div-" + $group.id);
        					$checkboxTD.append($('<input type="checkbox" name="'+$value.reportId+"-" + $group.id +'" class="group-checkbox" data-report="'+$value.reportId+'" data-group="'+$group.id+'"/>'));
        					$row.append($checkboxTD);
    						//console.log("HERE" + $value.reportId + $group.id );
           				});           				
           				$table.append($row);
           			});
           			
        			$("#"+$container).html("");
           			$("#"+$container).append($table);
        			
        			//console.log("We have " + $subscriptionList.length + " subscriptions");
					$.each($subscriptionList, function($index, $value) {					
						var $checkboxMulti = "#"+$container+".selection-container input[name='" + $value.reportId + "-" + $value.groupId +"']";
						//console.log("Checkbox multi: " + $checkboxMulti);
						$($checkboxMulti).prop("checked",true);							
                	}); 
           			           			
         			$selector = "#"+$container + " .reportrow";
          			$($selector).mouseover(function($event) {
   						$(this).css('background-color','#F9F9F9');
   					});
   					$($selector).mouseout(function($event) {
   						$(this).css('background-color','transparent');
   					}); 
   				
   					$("th, td").mouseover(function(){
						var targetIndex, elements;
						targetIndex = $(this).index() + 1;
						elements = $("th, td");
						elements.filter(":nth-child(" + targetIndex + ")").css("background-color", "#F9F9F9");
						elements.not(":nth-child(" + targetIndex + ")").css("background-color", "transparent");
						elements.filter(":nth-child(1)").css("background-color", "transparent");						
					});
					$("table").mouseleave(function(){
						$("th, td").css("background-color", "transparent");
					}); 
   					
   					$selector = "#"+$container + " .group-checkbox";
					$($selector).click(function($event) {
    						var $reportId = $(this).attr("data-report");
    						var $groupId = $(this).attr("data-group");
        					var $subscribe = $(this).prop("checked");
        					var $subscriptionId = $(this).attr("data-subscriptionId");     
    						REPORT_SUBSCRIPTION.doGroupSubscription($reportId, $groupId, $subscribe, $subscriptionId);
    				}); 
        		},

        		
        		makeOneColumnTable : function($container, $reportList, $subscriptionList) {
           			console.log("Making container " + $container);
           			var $table = $("<table>").css('text-align','center');
           			var $hdrRow = $('<tr><td>&nbsp;</td><td><span style="font-weight:bold;">Subscribe</span></td></tr>');
           			
           			$table.append($hdrRow);
           			
           			$.each($reportList, function($index, $value) {
           				var $row = $("<tr>");
           				$row.addClass("reportrow");
           				$row.addClass("report-" + $value.reportId);
           				var $labelTD = $("<td>");
           				$labelTD.append($value.description);
           				$row.append($labelTD).css('text-align','left');
           				$checkboxTD = $("<td>");
           				$checkboxTD.addClass("report-" + $value.reportId)
           				$checkboxTD.append($('<input type="checkbox" name="'+$value.reportId+'" class="subscription-checkbox" data-report="'+$value.reportId+'" />'));
           				$row.append($checkboxTD);
           				$table.append($row);
						//console.log("VALUE" + $value.reportId);
           			});

        			$("#"+$container).html("");           			
           			$("#"+$container).append($table);
           			
        			//console.log("We have " + $subscriptionList.length + " subscriptions");
					$.each($subscriptionList, function($index, $value) {				
						var $checkboxOne = "#"+$container+".selection-container input[name='" + $value.reportId +"']";
						//console.log("Checkbox single: " + $checkboxOne);
						$($checkboxOne).prop("checked",true);							
                	}); 
           								
         			$selector = "#"+$container + " .reportrow";
          			$($selector).mouseover(function($event) {
   						$(this).css('background-color','#F9F9F9');
   					});
   					$($selector).mouseout(function($event) {
   						$(this).css('background-color','transparent');
   					}); 
   					
   					$selector = "#"+$container + " .subscription-checkbox";
   					$($selector).click(function($event) {
   						var $reportId = $(this).attr("data-report");
        				var $subscribe = $(this).prop("checked");
        				var $subscriptionId = $(this).attr("data-subscriptionId");   
   						REPORT_SUBSCRIPTION.doDivisionSubscription($reportId, null, $subscribe, $subscriptionId);
   					});
       			},
       			
       			
        		makeTables : function($data) {
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
							REPORT_SUBSCRIPTION.makeMultiColumnTable("company-selection-container", REPORT_SUBSCRIPTION.summaryReports, REPORT_SUBSCRIPTION.divisionList, REPORT_SUBSCRIPTION.companyList, $data.data.subscriptionList);
		        		
						} else {
							$("#company-subscription-selector").hide();
						}
						if ( REPORT_SUBSCRIPTION.regionList.length > 0 ) {
							$("#region-subscription-selector").show();
							REPORT_SUBSCRIPTION.makeMultiColumnTable("region-selection-container", REPORT_SUBSCRIPTION.summaryReports, REPORT_SUBSCRIPTION.divisionList, REPORT_SUBSCRIPTION.regionList, $data.data.subscriptionList);
		        		
						} else {
							$("#region-subscription-selector").hide();
						}
						if ( REPORT_SUBSCRIPTION.groupList.length > 0 ) {
							$("#group-subscription-selector").show();
							REPORT_SUBSCRIPTION.makeMultiColumnTable("group-selection-container", REPORT_SUBSCRIPTION.summaryReports, REPORT_SUBSCRIPTION.divisionList, REPORT_SUBSCRIPTION.groupList, $data.data.subscriptionList);
		        		
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
						//console.log("Subscribed to " + $data.data.subscriptionList.length + "reports");
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
    		<div id= "allAnsi-subscription-selector" class="subscription-selector">
    		<input type="radio" name="subscription-selector" value="allAnsi" /> All-ANSI Reports<br />
    		</div>
    		
    		<div id= "company-subscription-selector" class="subscription-selector">		
    		<input type="radio" name="subscription-selector" value="company" /> Company Reports<br />
			</div>
			    		
    		<div id= "region-subscription-selector" class="subscription-selector">
    		<input type="radio" name="subscription-selector" value="region" /> Region Reports<br />
			</div>
			    		
    		<div id= "group-subscription-selector" class="subscription-selector">
    		<input type="radio" name="subscription-selector" value="group" /> Group Reports<br />
			</div>
    		
    		<div id= "division-subscription-selector" class="subscription-selector">
    		<input type="radio" name="subscription-selector" value="division" /> Division Reports<br />
			</div>
    		   		
    		<div id= "trend-subscription-selector" class="subscription-selector">
    		<input type="radio" name="subscription-selector" value="trend" /> Trend Reports<br />
			</div>
			    		
    		<div id= "utility-subscription-selector" class="subscription-selector">
    		<input type="radio" name="subscription-selector" value="utility" /> Utility Reports<br />
    		</div>    	
    		    		
    		<ansi:hasPermission permissionRequired="REPORT_SUBSCRIPTION_READ">
	    		<div style="text-align: center">
		    		<a href="reports/subscriptionCSV"><webthing:csv>Subscriptions</webthing:csv></a>
	    		</div>
    		</ansi:hasPermission>
    		
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

