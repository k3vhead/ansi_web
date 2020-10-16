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
<%@ taglib tagdir="/WEB-INF/tags/quote" prefix="quote" %>
<%@ taglib uri="WEB-INF/theTagThing.tld" prefix="ansi" %>


<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Quote Maintenance
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/callNote.css" />
    	<link rel="stylesheet" href="css/accordion.css" type="text/css" />
    	<link rel="stylesheet" href="css/sortable.css" type="text/css" />
        <script type="text/javascript" src="js/ansi_utils.js"></script>
        <script type="text/javascript" src="js/quotePrintHistory.js"></script>
        <script type="text/javascript" src="js/quotePrint.js"></script>
        <script type="text/javascript" src="js/addressUtils.js"></script>
        <script type="text/javascript" src="js/callNote.js"></script> 
        <script type="text/javascript" src="js/textExpander.js"></script> 
        
        <script type="text/javascript">        
        
        	$(document).ready(function() {
				; QUOTEMAINTENANCE = {
					quoteId : '<c:out value="${ANSI_QUOTE_ID}" />',
					jobId : '<c:out value="${ANSI_JOB_ID}" />',
					accountTypeList : null,
					countryList : null,
					buildingTypeList : null,
					divisionList : null,
					invoiceGroupingList : null,
					invoiceStyleList : null,
					invoiceTermList : null,
					jobStatusList : null,
					jobFrequencyList : null,
					jobTagList : null,
					jobTagTypeList : null,
					leadTypeList : null,
					managerList : null,
					quote : null,					
									
					joblist : {},
					
					progressbar : $("#progressbar"),
					progressLabel : $("#progress-label"),
					
					// these are used to detect changes in job activiation Direct Labor values
					previousDlPct : null,
	            	previousDlBudget : null,
					
					init : function() {
						console.log("init");
						TEXTEXPANDER.init();
						CALLNOTE.init();
						$("#call-note-link").attr("data-xrefid", QUOTEMAINTENANCE.quoteId);
						CALLNOTE.lookupLink();
						QUOTEMAINTENANCE.makeProgressbar();
						QUOTEMAINTENANCE.init_modal();
						QUOTEMAINTENANCE.makeAutoComplete();
						QUOTEMAINTENANCE.makeOptionLists();
						QUOTEMAINTENANCE.makeButtons();
						QUOTEMAINTENANCE.makeOtherClickables();
						if (QUOTEMAINTENANCE.quoteId != '' ) {
							QUOTEMAINTENANCE.getQuote(QUOTEMAINTENANCE.quoteId);	
						}
						QUOTE_PRINT.init_modal("#printQuoteDiv");
						QUOTE_PRINT_HISTORY.init("#printHistoryDiv", "#viewPrintHistory");
						QUOTEMAINTENANCE.populateNewQuoteJobFields();
						//$("#loading-container").hide();
						//$("#quotePanel").fadeIn(1000);
						//$("#address-container").fadeIn(1000);
						//$("#job-list-container").fadeIn(1000);						
					},
					
					
					
					activateJob : function() {
						console.log("activateJob");
						var $outbound = {};
						$jobId = $("#job-activate-modal").attr("jobid");
						$outbound["jobId"] = $jobId;
						$outbound["action"] = "ACTIVATE_JOB";
						
						$.each($("#job-activate-modal input"), function($index, $val) {
							$outbound[$($val).attr('name')] = $($val).val() 
						});
						
						QUOTEMAINTENANCE.doJobUpdate($jobId, $outbound, QUOTEMAINTENANCE.activateJobSuccess, QUOTEMAINTENANCE.activateJobErr);
					},
					
					
					activateJobErr : function($statusCode) {
						console.log("activateJobErr");
						var $messages = {
								403:"Session Expired. Log in and try again",
								404:"System Error Activate 404. Contact Support",
								500:"System Error Activate 500. Contact Support"
						}
						$("#job-activate-modal").dialog("close");
						$("#globalMsg").html( $messages[$statusCode] );
					},
					
					
					
					activateJobSuccess : function($data) {
						console.log("activateJobSuccess");
						console.log($data);
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {	
							$.each($data.data.webMessages, function($index, $val) {
								console.log($index);
								console.log($val);
								var $fieldName = "." + $index + "Err";
								var $selector = "#job-activate-modal " + $fieldName;
								$($selector).html($val[0]).show().fadeOut(3000);
							});							
						} else {
							var $jobId = $data.data.quote.jobDetail.job.jobId;
							QUOTEMAINTENANCE.joblist[$jobId] = $data.data.quote.jobDetail;
							console.log("populate the job panels after activate");
							$("#globalMsg").html("Job Activated").show().fadeOut(3000);
							$("#job-activate-modal").dialog("close");
							
							QUOTEMAINTENANCE.showJobUpdates($data.data);
						}
					}, 
					
					
					
					
					cancelJob : function() {
						console.log("cancelJob");
						var $outbound = {};
						$jobId = $("#job-cancel-modal").attr("jobid");
						$outbound["jobId"] = $jobId;
						$outbound["action"] = "CANCEL_JOB";
						
						$.each($("#job-cancel-modal input"), function($index, $val) {
							$outbound[$($val).attr('name')] = $($val).val() 
						});
						
						QUOTEMAINTENANCE.doJobUpdate($jobId, $outbound, QUOTEMAINTENANCE.cancelJobSuccess, QUOTEMAINTENANCE.cancelJobErr);
					},
					
					
					cancelJobErr : function($statusCode) {
						console.log("cancelJobErr");
						var $messages = {
								403:"Session Expired. Log in and try again",
								404:"System Error Activate 404. Contact Support",
								500:"System Error Activate 500. Contact Support"
						}
						$("#job-cancel-modal").dialog("close");
						$("#globalMsg").html( $messages[$statusCode] );
					},
					
					
					
					cancelJobSuccess : function($data) {
						console.log("cancelJobSuccess");
						console.log($data);
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {	
							$.each($data.data.webMessages, function($index, $val) {								
								var $fieldName = "." + $index + "Err";
								var $selector = "#job-cancel-modal " + $fieldName;
								$($selector).html($val[0]).show().fadeOut(3000);
							});							
						} else {
							QUOTEMAINTENANCE.quote = $data.data.quote;
							$jobId = $data.data.quote.jobDetail.job.jobId;
							var $destination = "#job" + $jobId + " .job-data-row";
							QUOTEMAINTENANCE.populateJobPanel($jobId, $destination, $data.data);
							$("#job-cancel-modal").dialog("close");
							$("#globalMsg").html("Job Canceled").show().fadeOut(3000);
							
							QUOTEMAINTENANCE.showJobUpdates($data.data);
						}
					}, 
					
					
					
					
					cancelThisJobEdit : function($jobId) {
	    				console.log("clicked a job edit cancel: " + $jobId);
	    				
	    				// hide edit icon & show save/cancel
	    				$editSelector = "#job" + $jobId + " .jobTitleRow .panel-button-container .edit-this-job";
	    				$saveSelector = "#job" + $jobId + " .jobTitleRow .panel-button-container .save-job";
	    				$cancelSelector = "#job" + $jobId + " .jobTitleRow .panel-button-container .cancel-job-edit";
	    				$($editSelector).show();
	    				$($saveSelector).hide();
	    				$($cancelSelector).hide();
	    				
	    				// close the job display panel
	    				$openSelector = "#job" + $jobId + " .jobTitleRow .job-data-open";
						$closedSelector = "#job" + $jobId + " .jobTitleRow .job-data-closed";
						$tableSelector = "#job" + $jobId + " .job-data-row";
						$($tableSelector).hide();
						$($closedSelector).show();
						$($openSelector).hide();
						
						// repopulate the detail so canceled updateds don't get displayed
						var $destination = "#job" + $jobId + " .job-data-row";
						QUOTEMAINTENANCE.populateJobPanel($jobId, $destination, QUOTEMAINTENANCE.joblist[$jobId]);
	            	},
	            	
	            	
	            	
	            	
	            	
	            	
	            	deleteJob : function() {
	            		console.log("deleteJob");
						var $outbound = {};
						$jobId = $("#job-delete-modal").attr("jobid");
						$outbound["jobId"] = $jobId;
						$outbound["action"] = "DELETE_JOB";
						
						QUOTEMAINTENANCE.doJobUpdate($jobId, $outbound, QUOTEMAINTENANCE.deleteJobSuccess, QUOTEMAINTENANCE.deleteJobErr);
					},
					
					
					deleteJobErr : function($statusCode) {
						console.log("deleteJobErr");
						var $messages = {
								403:"Session Expired. Log in and try again",
								404:"System Error Activate 404. Contact Support",
								500:"System Error Activate 500. Contact Support"
						}
						$("#job-delete-modal").dialog("close");
						$("#globalMsg").html( $messages[$statusCode] );
					},
					
					
					
					deleteJobSuccess : function($data) {
						console.log("deleteJobSuccess");
						console.log($data);
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {	
							$.each($data.data.webMessages, function($index, $val) {								
								var $fieldName = "." + $index + "Err";
								var $selector = "#job-delete-modal " + $fieldName;
								$($selector).html($val[0]).show().fadeOut(3000);
							});							
						} else {
							QUOTEMAINTENANCE.quote = $data.data.quote;
							// call this:  populateJobPanel : function($jobId, $destination, $data
							// var $destination = "#job" + $data.data.job.jobId + " .job-data-row";
							//QUOTEMAINTENANCE.populateJobPanel($data.data.job.jobId, $destination, $data.data);
							//QUOTEMAINTENANCE.populateJobHeader($data.quoteList[0].jobHeaderList)
							$("#job-delete-modal").dialog("close");
							$("#globalMsg").html("Job Deleted").show().fadeOut(3000);
							
							QUOTEMAINTENANCE.showJobUpdates($data.data);
						}
					},
	            	
	            	
	            	
					doCopyQuote : function($quoteId) {						
						console.log("Making a copy of " + $quoteId);	
						var $url = "quote/copy/" + $quoteId;
						var jqxhr = $.ajax({
							type: 'POST',
							url: $url,
							data: {},
							statusCode: {
								200: function($data) {
									location.href="quoteMaintenance.html?id=" + $data.data.quote.quote.quoteId;
								},					
								403: function($data) {
									$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
								},
								404: function($data) {
									$("#globalMsg").html("System Error Copy 404. Contact Support");
								},
								500: function($data) {
									$("#globalMsg").html("System Error Copy 500. Contact Support");
								}
							},
							dataType: 'json'
						});
					},
					
					
					
					
					
					doJobUpdate : function($jobId, $outbound, $successCallback, $errCallback) {
						console.log("doJobUpdate");
						var $url = "job/" + $jobId;
						console.log($outbound);						
						
						var jqxhr3 = $.ajax({
							type: 'POST',
							url: $url,
							data: JSON.stringify($outbound),
							statusCode: {
								200:function($data) {
									$successCallback($data);
								},
								403: function($data) {	
									$errCallback(403);									
								},
								404: function($data) {
									$errCallback(404);
								},
								500: function($data) {
									$errCallback(500);
								}
							},
							dataType: 'json'
						});
					},
					
					
					
					
					doReviseQuote : function($quoteId) {
						console.log("Making a revise of " + $quoteId);	
						var $url = "quote/revise/" + $quoteId;
						var jqxhr = $.ajax({
							type: 'POST',
							url: $url,
							data: {},
							statusCode: {
								200: function($data) {
									location.href="quoteMaintenance.html?id=" + $data.data.quote.quote.quoteId;
								},					
								403: function($data) {
									$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
								},
								404: function($data) {
									$("#globalMsg").html("System Error Revise 404. Contact Support");
								},
								500: function($data) {
									$("#globalMsg").html("System Error Revise 500. Contact Support");
								}
							},
							dataType: 'json'
						});
					},
					
					
					
					doQuoteUpdate : function($quoteId, $data, $successCallback, $errCallback) {
						console.log("doQuoteUpdate");
						var $outbound = JSON.stringify($data);
						var $url = "quote/" + $quoteId;
						console.log($outbound);
						
						
						var jqxhr3 = $.ajax({
							type: 'POST',
							url: $url,
							data: $outbound,
							statusCode: {
								200:function($data) {
									$successCallback($data);
								},
								403: function($data) {	
									$errCallback(403);									
								},
								404: function($data) {
									$errCallback(404);
								},
								500: function($data) {
									$errCallback(500);
								}
							},
							dataType: 'json'
						});
					},
					
					
					
					activateThisJob : function($jobId) {
	            		console.log("clicked a job activateThisJob: " + $jobId);
	            		$("#job-activate-modal").attr("jobid", $jobId);
	            		$("#job-activate-modal input").val("");
	            		$("#job-activate-modal .errMsg").html("");
	            		$("#job-activate-modal").dialog("open");	            		
					},
					
					
					
					cancelThisJob : function($jobId, $type) {
	            		console.log("clicked a job cancel: " + $jobId);
	            		$("#job-cancel-modal").attr("jobid", $jobId);
	            		$("#job-cancel-modal input").val("");
	            		$("#job-cancel-modal .errMsg").html("");
	            		$("#job-cancel-modal").dialog("open");
					},
					
					
					
					deleteThisJob : function($jobId, $type) {
	            		console.log("clicked a job deleteThisJob: " + $jobId);
	            		$("#job-delete-modal").attr("jobid", $jobId);
	            		$( "#job-delete-modal" ).dialog("open");
					},
					
					
					populateTagListEdit : function($job) {
						console.log($job);
						var $display = "N/A";
						if ( QUOTEMAINTENANCE.jobTagList.length > 0 ) {
		            		
		            		var $tagDisplay = $("<div>");
		            		$.each(QUOTEMAINTENANCE.jobTagTypeList, function($typeIndex, $tagType) {		            			
		            			var $label = '<span class="formLabel">' + $tagType.display + ': </span>';
		            			$tagDisplay.append($label);
		            			$display = "";
		            			$.each(QUOTEMAINTENANCE.jobTagList, function($index, $tag) {
		            				if ( $tag.tagType == $tagType.name) {
										var $classList = "";
										var $tagIsActive = true
										var $tagIsSelected = false;
										var $skip = false;
										if ( $tag.status == "INACTIVE") {
											$tagIsActive = false;
										}
										if ( $job != null ) {
											// when we're adding a job, a null is passed in, so nothing will be pre-selected
											$.each($job.jobTagList, function($selectedIndex, $selectedTag) {
												if ( $selectedTag.tagId == $tag.tagId ) {
													$tagIsSelected = true;
												}
											});
										}
										if ( $tagIsActive ) {
											if ( $tagIsSelected ) {
												$classList = $classList + " jobtag-selected";
											} else {
												// nothing to do here
											}
										} else {
											if ( $tagIsSelected ) {
												$classList = $classList + " jobtag-selected jobtag-inactive";
											} else {
												$skip = true;
											}
										}
										if ( $skip == false ) {
				            				$display = $display + '<span style="cursor:pointer;" class="jobtag jobtag-edit tooltip '+$classList+'" data-tagid="'+$tag.tagId+'">' + $tag.longCode + '<span class="tooltiptext">'+$tag.abbrev + '-' + $tag.description+'</span></span>&nbsp;';				            				
										}
		            				}
								});
		            			
		            			$tagDisplay.append($display + "<br />");
		            		});
							
						}
						
						return $tagDisplay;
					},

					
					
					
					editThisJob : function($jobId, $type) {
	            		console.log("clicked a job edit: " + $jobId);
	            		console.log("Edit type: " + $type);
	
	    				// hide edit icon & show save/cancel
	    				//$editSelector = "#job" + $jobId + " .jobTitleRow .panel-button-container .edit-this-job";
	    				//$saveSelector = "#job" + $jobId + " .jobTitleRow .panel-button-container .save-job";
	    				//$cancelSelector = "#job" + $jobId + " .jobTitleRow .panel-button-container .cancel-job-edit";
	    				//$($editSelector).hide();
	    				//$($saveSelector).show();
	    				//$($cancelSelector).show();
	    				
	    				// open the job display panel
	    				$openSelector = "#job" + $jobId + " .jobTitleRow .job-data-open";
						$closedSelector = "#job" + $jobId + " .jobTitleRow .job-data-closed";
						$tableSelector = "#job" + $jobId + " .job-data-row";
						$($tableSelector).show();
						$($closedSelector).hide();
						$($openSelector).show();
						
						$detailSelector = "#job" + $jobId + " .job-data-row .job-detail-display";
						if ( ! $($detailSelector).length ) {
							QUOTEMAINTENANCE.getJobPanel($jobId);								
						}
						
						$("#job-edit-modal").attr("data-jobid", $jobId);
						$("#job-edit-modal").attr("data-type", $type);
						
						$("#job-edit-modal .job-edit-panel").hide();
						var $editPanel = "#job-edit-modal ." + $type;
						$($editPanel).show();
						$("#job-edit-modal").dialog("open");
						
						// populate Proposal edit panel
						console.log(QUOTEMAINTENANCE.joblist[$jobId]);
						//populate frequency select:
						QUOTEMAINTENANCE.populateJobFrequencySelect();
						
						$("#job-edit-modal .proposal input[name='job-proposal-job-nbr']").val(QUOTEMAINTENANCE.joblist[$jobId].job.jobNbr);
						$("#job-edit-modal .proposal input[name='job-proposal-ppc']").val(QUOTEMAINTENANCE.joblist[$jobId].job.pricePerCleaning);
						$("#job-edit-modal .proposal select[name='job-proposal-freq']").val(QUOTEMAINTENANCE.joblist[$jobId].job.jobFrequency);
						$("#job-edit-modal .proposal textarea[name='job-proposal-desc']").val(QUOTEMAINTENANCE.joblist[$jobId].job.serviceDescription);
						$("#job-edit-modal .proposal .job-proposal-jobtag").html(QUOTEMAINTENANCE.populateTagListEdit(QUOTEMAINTENANCE.joblist[$jobId].job));
						
						$(".jobtag-edit").click(function($event) {
							var $tagId = $(this).attr("data-tagid");
							var $selected = $(this).hasClass("jobtag-selected");
							console.log("jobtag click: " + $tagId + " " + $selected);
							if ( $selected ) {
								$(this).removeClass("jobtag-selected");
							} else {
								$(this).addClass("jobtag-selected");
							}
						});

						$("#job-edit-modal .proposal textarea[name='job-proposal-desc']").keyup(function($event) {
							TEXTEXPANDER.keyup($event, $("#job-edit-modal .proposal textarea[name='job-proposal-desc']"))
						});
						$("#job-edit-modal .proposal textarea[name='job-proposal-desc']").blur(function() {
							TEXTEXPANDER.blur($("#job-edit-modal .proposal textarea[name='job-proposal-desc']"))
						});
						
						// populate activation edit panel
						$("#job-edit-modal .activation input[name='job-activation-dl-pct']").val(QUOTEMAINTENANCE.joblist[$jobId].job.directLaborPct);
		            	$("#job-edit-modal .activation input[name='job-activation-dl-budget']").val(QUOTEMAINTENANCE.joblist[$jobId].job.budget);
		            	$("#job-edit-modal .activation input[name='job-activation-floors']").val(QUOTEMAINTENANCE.joblist[$jobId].job.floors);
		            	if ( QUOTEMAINTENANCE.joblist[$jobId].job.requestSpecialScheduling == 1 ) {
		            		$("#job-edit-modal .activation select[name='job-activation-schedule']").val("manual");
		            	} else {
		            		$("#job-edit-modal .activation select[name='job-activation-schedule']").val("auto");
		            	}
		            	$("#job-edit-modal .activation input[name='job-activation-equipment']").val(QUOTEMAINTENANCE.joblist[$jobId].job.equipment);
		            	$("#job-edit-modal .activation input[name='job-activation-washer-notes']").val(QUOTEMAINTENANCE.joblist[$jobId].job.washerNotes);
		            	$("#job-edit-modal .activation input[name='job-activation-om-notes']").val(QUOTEMAINTENANCE.joblist[$jobId].job.omNotes);
		            	$("#job-edit-modal .activation input[name='job-activation-billing-notes']").val(QUOTEMAINTENANCE.joblist[$jobId].job.billingNotes);
	
		            	// calculate DL stufff
		            	QUOTEMAINTENANCE.previousDlPct = $("#job-edit-modal .activation input[name='job-activation-dl-pct']").val();
		            	QUOTEMAINTENANCE.previousDlBudget = $("#job-edit-modal .activation input[name='job-activation-dl-budget']").val();
		            	$("#job-edit-modal .activation input[name='job-activation-dl-pct']").blur(function($event) {
		            		var currentDlPct = $("#job-edit-modal .activation input[name='job-activation-dl-pct']").val();
		            		var ppc = QUOTEMAINTENANCE.joblist[$jobId].job.pricePerCleaning;
		            		if ( QUOTEMAINTENANCE.previousDlPct != currentDlPct ) {
		            			newDlBudget = (currentDlPct / 100 ) * ppc;
		            			if ( newDlBudget != Math.floor(newDlBudget)) { // if new value is not a whole number, limit to 2 decimals
		            				newDlBudget = newDlBudget.toFixed(2);	
		            			}
		            			$("#job-edit-modal .activation input[name='job-activation-dl-budget']").val(newDlBudget);
		            			QUOTEMAINTENANCE.previousDlPct = currentDlPct;
		            			QUOTEMAINTENANCE.previousDlBudget = newDlBudget;
		            		}		            		
		            	});
		            	$("#job-edit-modal .activation input[name='job-activation-dl-budget']").blur(function($event) {
		            		var currentDlBudget = $("#job-edit-modal .activation input[name='job-activation-dl-budget']").val();
		            		var ppc = QUOTEMAINTENANCE.joblist[$jobId].job.pricePerCleaning;
		            		if ( QUOTEMAINTENANCE.previousDlBudget != currentDlBudget ) {
		            			newDlPct = (currentDlBudget/ppc) * 100;
		            			if ( newDlPct != Math.floor(newDlPct)) {    // if new value is not a whole number, limit to 2 decimals
		            				newDlPct = newDlPct.toFixed(2);	
		            			}
		            			$("#job-edit-modal .activation input[name='job-activation-dl-pct']").val(newDlPct);
		            			QUOTEMAINTENANCE.previousDlPct = newDlPct;
		            			QUOTEMAINTENANCE.previousDlBudget = currentDlBudget;
		            		}
		            	});
		            	
		            	
						// populate invoice edit panel
						$("#job-edit-modal .invoice input[name='job-invoice-purchase-order']").val(QUOTEMAINTENANCE.joblist[$jobId].job.poNumber);
		            	$("#job-edit-modal .invoice input[name='job-invoice-vendor-nbr']").val(QUOTEMAINTENANCE.joblist[$jobId].job.ourVendorNbr);
		            	$("#job-edit-modal .invoice input[name='job-invoice-expire-date']").val(QUOTEMAINTENANCE.joblist[$jobId].job.expirationDate);
		            	$("#job-edit-modal .invoice input[name='job-invoice-expire-reason']").val(QUOTEMAINTENANCE.joblist[$jobId].job.expirationReason);
		            	
						// populate schedule edit panel
						$("#job-edit-modal .schedule .job-schedule-last-run").html(QUOTEMAINTENANCE.joblist[$jobId].lastRun.startDate);		            	
		            	if ( QUOTEMAINTENANCE.joblist[$jobId].job.repeatScheduleAnnually == 1 ) {
		            		$("#job-edit-modal .schedule input[name='repeatedAnnually']").prop("checked", true);
		            	} else {
		            		$("#job-edit-modal .schedule input[name='repeatedAnnually']").prop("checked", false);
		            	}
		            	$("#job-edit-modal .schedule .job-schedule-next-due").html(QUOTEMAINTENANCE.joblist[$jobId].nextDue.startDate);
		            	$("#job-edit-modal .schedule .job-schedule-created-thru").html(QUOTEMAINTENANCE.joblist[$jobId].lastCreated.startDate);
		            	$("#job-edit-modal .schedule .job-schedule-ticket-list-link").attr("href", "ticketLookup.html?jobId="+$jobId);
	    			},
	    			
	    			
	    			
	    			
					getDivisionList : function($callback) {
						console.log("getDivisionList");
						var jqxhr3 = $.ajax({
							type: 'GET',
							url: 'division/list',
							data: {},
							statusCode: {
								200:function($data) {
									$callback($data.data);
									QUOTEMAINTENANCE.incrementProgress("Division List");
								},
								403: function($data) {								
									$("#globalMsg").html("Session Expired. Log In and try again").show();
								},
								404: function($data) {
									$("#globalMsg").html("System Error Division 404. Contact Support").show();
								},
								500: function($data) {
									$("#globalMsg").html("System Error Division 500. Contact Support").show();
								}
							},
							dataType: 'json',
							async:false
						});
					},
					
					
					getJobPanel : function($jobId) {
						console.log("getJobPanel");
						var jqxhr1 = $.ajax({
		    				type: 'GET',
		    				url: "jobDisplay.html",
		    				data: null,			    				
		    				statusCode: {
		    					200: function($data) {
		    						QUOTEMAINTENANCE.getJob($jobId, $data);
		    					},			    				
		    					403: function($data) {
		    						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
		    					}, 
		    					404: function($data) {
		    						$("#globalMsg").html("System Error JobDisplay 404. Contact Support").show();
		    					}, 
		    					405: function($data) {
		    						$("#globalMsg").html("System Error JobDisplay 405. Contact Support").show();
		    					}, 
		    					500: function($data) {
		    						$("#globalMsg").html("System Error JobDisplay 500. Contact Support").show();
		    					}, 
		    				},
		    				dataType: 'html'
		    			});
					},
					
					
					
					
					getJob : function($jobId, $html) {
						console.log("getJob");
						var $url = "job/" + $jobId;
						var jqxhr1 = $.ajax({
		    				type: 'GET',
		    				url: $url,
		    				data: null,			    				
		    				statusCode: {
		    					200: function($data) {
		    						QUOTEMAINTENANCE.joblist[$jobId] = $data.data.quote.jobDetail;
		    						var $destination = "#job" + $jobId + " .job-data-row";
		    						$($destination).html($html);
		    						QUOTEMAINTENANCE.populateJobPanel($jobId, $destination, $data.data);		    						
		    					},			    				
		    					403: function($data) {
		    						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
		    					}, 
		    					404: function($data) {
		    						$("#globalMsg").html("System Error Option 404. Contact Support").show();
		    					}, 
		    					405: function($data) {
		    						$("#globalMsg").html("System Error Option 405. Contact Support").show();
		    					}, 
		    					500: function($data) {
		    						$("#globalMsg").html("System Error Option 500. Contact Support").show();
		    					}, 
		    				},
		    				dataType: 'json'
		    			});
					},
		            
		            
		            
		            getQuote : function($quoteId) {
		            	console.log("getQuote");
						if ( $quoteId != null ) {
							var $url = "quote/" + $quoteId
							var jqxhr = $.ajax({
								type: 'GET',
								url: $url,
								data: {},
								statusCode: {
									200: function($data) {
										console.log("Got the quote");
										QUOTEMAINTENANCE.quote = $data.data.quote;
										QUOTEMAINTENANCE.populateQuotePanels($data.data);	
										if ( QUOTEMAINTENANCE.quote.canEdit == true ) {
											$("#edit-this-address").show();
											$(".quote-button-container").show();
											$("#edit-this-quote").show();
										} 
										if ( QUOTEMAINTENANCE.quote.canAddJob == true ) {
											$("#new-job-button").show();
										} else {
											$("#new-job-button").hide();
										}
			//							if ( QUOTEMAINTENANCE.quote.canPropose == true ) {//gag: removed because "propose" is correct symantics for reprint also
			//								$("#propose-button").show();
			//							} else {
			//								$("#propose-button").hide();
			//							}
									},					
									403: function($data) {
										$("#globalMsg").html("Session Expired. Log In and try again").show();
									},
									404: function($data) {
										$("#globalMsg").html("Invalid quote").show().fadeOut(4000);
									},
									500: function($data) {
										$("#globalMsg").html("System Error 500. Contact Support").show();
									}
								},
								dataType: 'json',
								async:false
							});
						}
					},
					

					
					getCodeList: function($tableName, $fieldName, $callback) {
						console.log("getCodeList");
						var $url = "code/" + $tableName;
						if ( $fieldName != null ) {
							$url = $url + "/" + $fieldName;
						}
						var jqxhr2 = $.ajax({
							type: 'GET',
							url: $url,
							data: {},							
							statusCode: {
								200: function($data) {
									$callback($data.data)
								},					
								403: function($data) {
									$("#globalMsg").html("Session Expired. Log In and try again").show();
								},
								404: function($data) {
									$("#globalMsg").html("Invalid quote").show().fadeOut(4000);
								},
								500: function($data) {
									$("#globalMsg").html("System Error 500. Contact Support").show();
								}
							},							
							dataType: 'json'
						});
					},
					
					
					
					incrementProgress : function($label) {
						console.log("incrementProgress");
		            	var val = QUOTEMAINTENANCE.progressbar.progressbar("value") || 0;
						QUOTEMAINTENANCE.progressbar.progressbar("value", val+1);
						QUOTEMAINTENANCE.progressLabel.text( $label );
						console.log($label + ": " + QUOTEMAINTENANCE.progressbar.progressbar("value"));
					},
					
		    		
					
					
					init_modal : function() {
						console.log("init_modal");
						$( "#copy-modal" ).dialog({
							title:'Text Copy',
							autoOpen: false,
							height: 75,
							width: 225,
							modal: true,
							closeOnEscape:true,
							show: { effect:"blind",duration:250},
							hide: { effect:"blind",duration:250},
						});	
						
						
						$( "#contact-edit-modal" ).dialog({
							title:'Edit Contact',
							autoOpen: false,
							height: 225,
							width: 500,
							modal: true,
							closeOnEscape:true,
							//open: function(event, ui) {
							//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
							//},
							buttons: [
								{
									id: "contact-cancel-button",
									click: function($event) {
										$( "#contact-edit-modal" ).dialog("close");
									}
								},
								{
									id: "contact-save-button",
									click: function($event) {
										QUOTEMAINTENANCE.saveContact();
									}
								}
							]
						});	
						$("#contact-save-button").button('option', 'label', 'Save');
						$("#contact-cancel-button").button('option', 'label', 'Cancel');
						
						
						
						
						
						
						$( "#address-edit-modal" ).dialog({
							title:'Edit Address',
							autoOpen: false,
							height: 300,
							width: 500,
							modal: true,
							closeOnEscape:true,
							//open: function(event, ui) {
							//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
							//},
							buttons: [
								{
									id: "address-cancel-button",
									click: function($event) {
										$( "#address-edit-modal" ).dialog("close");
									}
								},
								{
									id: "address-save-button",
									click: function($event) {
										QUOTEMAINTENANCE.saveAddress();
									}
								}
							]
						});	
						$("#address-save-button").button('option', 'label', 'Save');
						$("#address-cancel-button").button('option', 'label', 'Cancel');

						
						
						
						
						
						$( "#job-edit-modal" ).dialog({
							title:'Job Edit',
							autoOpen: false,
							height: 350,
							width: 600,
							modal: true,
							closeOnEscape:true,
							//open: function(event, ui) {
							//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
							//},
							buttons: [
								{
									id: "job-edit-cancel-button",
									click: function($event) {
										$( "#job-edit-modal input").removeClass("edit-err");
										$( "#job-edit-modal select").removeClass("edit-err");
										$( "#job-edit-modal" ).dialog("close");
									}
								},
								{
									id: "job-edit-save-button",
									click: function($event) {
										QUOTEMAINTENANCE.saveJob();
									}
								}
							]
						});	
						$("#job-edit-save-button").button('option', 'label', 'Save');
						$("#job-edit-cancel-button").button('option', 'label', 'Cancel');

						
						
						
						
						
						$( "#job-activate-modal" ).dialog({
							title:'Activate Job',
							autoOpen: false,
							height: 250,
							width: 450,
							modal: true,
							closeOnEscape:true,
							//open: function(event, ui) {
							//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
							//},
							buttons: [
								{
									id: "job-activate-cancel-button",
									click: function($event) {
										$( "#job-activate-modal" ).dialog("close");
									}
								},
								{
									id: "job-activate-save-button",
									click: function($event) {
										QUOTEMAINTENANCE.activateJob();
									}
								}
							]
						});	
						$("#job-activate-save-button").button('option', 'label', 'Save');
						$("#job-activate-cancel-button").button('option', 'label', 'Cancel');	
						
						
						
						$( "#job-cancel-modal" ).dialog({
							title:'Cancel Job',
							autoOpen: false,
							height: 250,
							width: 450,
							modal: true,
							closeOnEscape:true,
							//open: function(event, ui) {
							//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
							//},
							buttons: [
								{
									id: "job-cancel-cancel-button",
									click: function($event) {
										$( "#job-cancel-modal" ).dialog("close");
									}
								},
								{
									id: "job-cancel-save-button",
									click: function($event) {
										QUOTEMAINTENANCE.cancelJob();
									}
								}
							]
						});	
						$("#job-cancel-save-button").button('option', 'label', 'Save');
						$("#job-cancel-cancel-button").button('option', 'label', 'Cancel');						

						
						
						
						$( "#job-delete-modal" ).dialog({
							title:'Delete Job',
							autoOpen: false,
							height: 125,
							width: 450,
							modal: true,
							closeOnEscape:true,
							//open: function(event, ui) {
							//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
							//},
							buttons: [
								{
									id: "job-delete-cancel-button",
									click: function($event) {
										$( "#job-delete-modal" ).dialog("close");
									}
								},
								{
									id: "job-delete-save-button",
									click: function($event) {
										QUOTEMAINTENANCE.deleteJob();
									}
								}
							]
						});	
						$("#job-delete-save-button").button('option', 'label', 'Delete');
						$("#job-delete-cancel-button").button('option', 'label', 'Cancel');
						
						
						
						$( "#job-schedule-modal" ).dialog({
							title:'Schedule Job',
							autoOpen: false,
							height: 250,
							width: 450,
							modal: true,
							closeOnEscape:true,
							//open: function(event, ui) {
							//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
							//},
							buttons: [
								{
									id: "job-schedule-cancel-button",
									click: function($event) {
										$( "#job-schedule-modal" ).dialog("close");
									}
								},
								{
									id: "job-schedule-save-button",
									click: function($event) {
										QUOTEMAINTENANCE.scheduleJob();
									}
								}
							]
						});	
						$("#job-schedule-save-button").button('option', 'label', 'Save');
						$("#job-schedule-cancel-button").button('option', 'label', 'Cancel');	
						
						
						
						
						$( "#reorder-job-modal" ).dialog({
							title:'Reorder Jobs',
							autoOpen: false,
							height: 125,
							width: 450,
							modal: true,
							closeOnEscape:false,
							open: function(event, ui) {
								$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
							},
							buttons: [
								{
									id: "reorder-job-cancel-button",
									click: function($event) {
										$( "#reorder-job-modal" ).dialog("close");
										console.log(QUOTEMAINTENANCE.quote);
										QUOTEMAINTENANCE.populateJobHeader(QUOTEMAINTENANCE.quote.jobHeaderList);
									}
								},
								{
									id: "reorder-job-save-button",
									click: function($event) {
										$( "#reorder-job-modal" ).dialog("close");
										QUOTEMAINTENANCE.reorderJobs();
									}
								}
							]
						});	
						$("#reorder-job-save-button").button('option', 'label', 'Reorder');
						$("#reorder-job-cancel-button").button('option', 'label', 'Undo');
						
					},
					
					
					
					
					
					
					
					
					
					makeAutoComplete : function() {
						console.log("makeAutoComplete");
						QUOTEMAINTENANCE.makeAutoCompleteAddress();
						QUOTEMAINTENANCE.makeAutoCompleteContact();
						QUOTEMAINTENANCE.makeAutoCompleteSignedBy();
					},
					
					
					
					
					makeAutoCompleteAddress : function() {
						console.log("makeAutoCompleteAddress");
						var $selector = $("#address-edit-modal input[name='address-name']");						
				    	
						var $addressAutoComplete = $( $selector ).autocomplete({
							source: "addressTypeAhead?",
							select: function( event, ui ) {
								$( $selector ).val(ui.item.id);								
								console.log(ui.item);	
								ADDRESSUTILS.getAddress(ui.item.id, QUOTEMAINTENANCE.populateAddressModal);
	   				      	},
							response: function(event, ui) {
								if (ui.content.length === 0) {
									$("#address-edit-modal .none-found").show();
						        } else {
									$("#address-edit-display").hide();								
						        	$("#address-edit-modal .none-found").hide();
						        }
							}
						}); 	
					},
					
					
					
					makeAutoCompleteContact : function() {
						console.log("makeAutoCompleteContact");
						$selector = $("#contact-edit-modal input[name='contact-name']");
						$( $selector ).autocomplete({
							source:"contactTypeAhead?term=",
						    select: function( event, ui ) {
								console.log(ui.item);
								$("#contact-edit-modal").data("id",ui.item.id);
						    	
						    	var $contactSelector = "#contact-edit-display";
						    	$($contactSelector + " .ansi-contact-name").html(ui.item.value);
						    	var $preferred = ui.item.preferredContactValue.split(":");
						    	$($contactSelector + " .ansi-contact-number").html($preferred[1]);
						    	
						    	$($contactSelector + " .ansi-contact-method-is-business-phone").hide();
								$($contactSelector + " .ansi-contact-method-is-mobile-phone").hide();
								$($contactSelector + " .ansi-contact-method-is-fax").hide();
								$($contactSelector + " .ansi-contact-method-is-email").hide();
								if ( $preferred[0] == "business_phone") { $($contactSelector + " .ansi-contact-method-is-business-phone").show(); }
								if ( $preferred[0] == "mobile_phone") { $($contactSelector + " .ansi-contact-method-is-mobile-phone").show(); }
								if ( $preferred[0] == "fax") { $($contactSelector + " .ansi-contact-method-is-fax").show(); }
								if ( $preferred[0] == "email") { $($contactSelector + " .ansi-contact-method-is-email").show(); }
								$($contactSelector).show();
							},
						    response: function(event, ui) {
								if (ui.content.length === 0) {
									$("#contact-edit-modal .none-found").show();
						        } else {
									$("#contact-edit-display").hide();								
						        	$("#contact-edit-modal .none-found").hide();
						        }
						    }
						});
					},
					
					
					
					makeAutoCompleteSignedBy : function() {
						$selector = $("#quotePanel input[name='signedBy']");
						$( $selector ).autocomplete({
							source:"contactTypeAhead?term=",
						    select: function( event, ui ) {
								console.log(ui.item);
								//$("#contact-edit-modal").data("id",ui.item.id);
						    	$("#quotePanel input[name='signedBy']").attr("id", ui.item.id);
						    	//var $contactSelector = "#contact-edit-display";
						    	//$($contactSelector + " .ansi-contact-name").html(ui.item.value);
						    	//var $preferred = ui.item.preferredContactValue.split(":");
						    	//$($contactSelector + " .ansi-contact-number").html($preferred[1]);
						    	
						    	//$($contactSelector + " .ansi-contact-method-is-business-phone").hide();
								//$($contactSelector + " .ansi-contact-method-is-mobile-phone").hide();
								//$($contactSelector + " .ansi-contact-method-is-fax").hide();
								//$($contactSelector + " .ansi-contact-method-is-email").hide();
								//if ( $preferred[0] == "business_phone") { $($contactSelector + " .ansi-contact-method-is-business-phone").show(); }
								//if ( $preferred[0] == "mobile_phone") { $($contactSelector + " .ansi-contact-method-is-mobile-phone").show(); }
								//if ( $preferred[0] == "fax") { $($contactSelector + " .ansi-contact-method-is-fax").show(); }
								//if ( $preferred[0] == "email") { $($contactSelector + " .ansi-contact-method-is-email").show(); }
								//$($contactSelector).show();
							},
						    response: function(event, ui) {
								if (ui.content.length === 0) {
									alert("No matches");
						        } else {
									//$("#contact-edit-display").hide();								
						        	//$("#contact-edit-modal .none-found").hide();
						        }
						    }
						});
					},
					
					
					
					makeButtons : function() {
						$('.dateField').datepicker({
			                prevText:'&lt;&lt;',
			                nextText: '&gt;&gt;',
			                showButtonPanel:true
			            });
						
						$("#job-edit-modal .activation input[name='job-activation-floors']").spinner({
							spin : function( $event, $ui ) {
								if ( $ui.value > 200 ) {
									alert("Seriously?");
									return false;
								} else if ( $ui.value < 0 ) {
									$( this ).spinner("value", 0);
									return false;
								}	
							}
						});
					},
					
					
					
					
					
					makeJobClickers : function() {
						$(".edit-this-job").click(function($event) {
		            		//var $jobId = this.parentElement.attributes['data-jobid'].value;
		            		var $jobId = $(this).closest("div.panel-button-container")[0].attributes['data-jobid'].value;
		            		var $type = $(this).attr("data-type");
		            		QUOTEMAINTENANCE.editThisJob($jobId, $type);
		            	});
		            	
						$(".edit-this-job").mouseover(function($event) {
							var $jobId = $(this).closest("div.panel-button-container")[0].attributes['data-jobid'].value;
		            		var $type = $(this).attr("data-type");
		            		if ( $type == "proposal") { $("#job" + $jobId + " .jobProposalDisplayPanel").addClass("pre-edit"); }
		            		if ( $type == "activation") { $("#job" + $jobId + " .jobActivationDisplayPanel").addClass("pre-edit"); }
		            		if ( $type == "invoice") { $("#job" + $jobId + " .jobInvoiceDisplayPanel").addClass("pre-edit"); }
		            		if ( $type == "schedule") { $("#job" + $jobId + " .jobScheduleDisplayPanel").addClass("pre-edit"); }
						});
						
						$(".edit-this-job").mouseout(function($event) {
							var $jobId = $(this).closest("div.panel-button-container")[0].attributes['data-jobid'].value;
		            		var $type = $(this).attr("data-type");
		            		if ( $type == "proposal") { $("#job" + $jobId + " .jobProposalDisplayPanel").removeClass("pre-edit"); }
		            		if ( $type == "activation") { $("#job" + $jobId + " .jobActivationDisplayPanel").removeClass("pre-edit"); }
		            		if ( $type == "invoice") { $("#job" + $jobId + " .jobInvoiceDisplayPanel").removeClass("pre-edit"); }
		            		if ( $type == "schedule") { $("#job" + $jobId + " .jobScheduleDisplayPanel").removeClass("pre-edit"); }
						});
						
						
						
						
						
						
		            	$(".cancel-this-job").click(function($event) {
		            		//var $jobId = this.parentElement.attributes['data-jobid'].value;
		            		var $jobId = $(this).closest("div.panel-button-container")[0].attributes['data-jobid'].value;
		            		QUOTEMAINTENANCE.cancelThisJob($jobId);
		            	});
		            	
		            	$(".activate-this-job").click(function($event) {
		            		//var $jobId = this.parentElement.attributes['data-jobid'].value;
		            		var $jobId = $(this).closest("div.panel-button-container")[0].attributes['data-jobid'].value;
		            		QUOTEMAINTENANCE.activateThisJob($jobId);
		            	});
		            	$(".delete-this-job").click(function($event) {
		            		//var $jobId = this.parentElement.attributes['data-jobid'].value;
		            		var $jobId = $(this).closest("div.panel-button-container")[0].attributes['data-jobid'].value;
		            		QUOTEMAINTENANCE.deleteThisJob($jobId);
		            	});
		            	$(".cancel-job-edit").click(function($event) {
		            		var $jobId = this.parentElement.attributes['data-jobid'].value;
		            		QUOTEMAINTENANCE.cancelThisJobEdit($jobId);
		            	});
		            	$(".schedule-this-job").click(function($event) {
		            		var $jobId = this.parentElement.attributes['data-jobid'].value;
		            		QUOTEMAINTENANCE.scheduleThisJob($jobId);
		            	});
					},
					
					
					
					
					makeJobExpansion : function() {
						$(".job-hider").click(function($event) {
							var $jobId = $(this).data("jobid");
							console.log("Toggling display for job: " + $jobId); 
							$openSelector = "#job" + $jobId + " .jobTitleRow .job-data-open";
							$closedSelector = "#job" + $jobId + " .jobTitleRow .job-data-closed";
							$tableSelector = "#job" + $jobId + " .job-data-row";
							$($tableSelector).toggle();
							$($closedSelector).toggle();
							$($openSelector).toggle();
							
							$detailSelector = "#job" + $jobId + " .job-data-row .job-detail-display";
							if ( ! $($detailSelector).length ) {
								QUOTEMAINTENANCE.getJobPanel($jobId);
								$selector = "#job" + $jobId + " .panel-button-container";
								$($selector).show();
							}
							
							console.log("Scrolling");
							$anchorName = "job" + $jobId;
							$anchor = $("a[name='" + $anchorName + "']");
							$('html,body').animate({scrollTop: $anchor.offset().top},'slow');
						});
					},
					
					
					
		            makeJobHeader : function(
								$jobId, 
								$jobNbr, 
								$abbrDescription, 
								$divisionNbr, 
								$divisionCode, 
								$jobStatus, 
								$jobFrequency, 
								$pricePerCleaning,
								$canEdit,
								$canActivate,
								$canCancel,
								$canDelete,
								$canSchedule) {
		            	$jobHeader = $("<div>");
	            		$jobHeader.attr("data-jobid", $jobId);
	            		$jobHeader.attr("data-jobnbr", $jobNbr);
	            		$jobHeader.attr("class","jobTitleRow");
	            		

	            		
	            		$panelButtonContainer = $("<div>");
	            		$panelButtonContainer.attr("class","panel-button-container");
	            		$panelButtonContainer.attr("data-jobid",$jobId);

	            		if ( $canEdit == true ) {
	            			$panelButtonContainer.append('<quote:editJobMenu />');
	            		}
						if ( $canActivate == true ) {
							$panelButtonContainer.append('<webthing:activate styleClass="activate-this-job">Activate</webthing:activate>');
						}
						if ( $canCancel == true ) {
							$panelButtonContainer.append('<webthing:ban styleClass="cancel-this-job">Cancel</webthing:ban>');
						}
						if ( $canDelete == true ) {
							$panelButtonContainer.append('<webthing:delete styleClass="delete-this-job">Delete</webthing:delete>');
						}
						if ( $canSchedule == true ) {
							$panelButtonContainer.append('<webthing:schedule styleClass="schedule-this-job">Schedule</webthing:schedule>');
						}
	            		$jobHeader.append($panelButtonContainer);
	            		
	            		
	            		$jobHider = $("<div>");
	            		$jobHider.attr("data-jobid", $jobId);
	            		$jobHider.attr("class","job-hider");
	            		
	            		
	            		$jobHider.append('<span class="job-data-closed"><i class="fas fa-caret-right"></i></span>');
	            		$jobHider.append('<span class="job-data-open"><i class="fas fa-caret-down"></i></span>');
	            		$jobHider.append('&nbsp;');
	            		
	            		
	            		$anchorName = "job" + $jobId;
	            		$anchor = $("<a>");
	            		$anchor.attr("name", $anchorName);
	            		$anchor.append('<span class="formLabel">Job#: </span>');
	            		$anchor.append('<span>' + $jobNbr + '</span>');
	            		
	            		$jobDiv = $("<div>");
	            		$jobDiv.attr("class","job-header-job-div");
	            		//$jobDiv.append('<span class="formLabel">Job: </span>');
	            		//$jobDiv.append('<span>' + $value.jobNbr + '</span>');
	            		$jobDiv.append($anchor);
	            		
	            		$descDiv = $("<div>");
	            		$descDiv.attr("class","job-header-job-div");
	            		$descDiv.append('<span class="formLabel">Desc: </span>');
	            		$descDiv.append($abbrDescription);
	            		
	            		$jobIdDiv = $("<div>");
	            		$jobIdDiv.attr("class","job-header-job-div");
	            		$jobIdDiv.append('<span class="formLabel">Job: </span>');
	            		$jobIdDiv.append('<span>' + $jobId +'</span>');
	            		
	            		$divDiv = $("<div>");
	            		$divDiv.attr("class","job-header-job-div");
	            		$divDiv.append('<span class="formLabel">Div: </span>');
	            		$divDiv.append('<span>' + $divisionNbr + '-' + $divisionCode + '</span>');
	            		
	            		$statusDiv = $("<div>");
	            		$statusDiv.attr("class","job-header-job-div");
	            		$statusDiv.append('<span class="formLabel"></span>');
	            		$statusDiv.append('<span>' + $jobStatus +'</span>');
	            		
	            		$freqDiv = $("<div>");
	            		$freqDiv.attr("class","job-header-job-div");
	            		$freqDiv.append('<span class="formLabel">Freq: </span>');
	            		$freqDiv.append('<span>' + $jobFrequency +'</span>');
	            		
	            		$ppcDiv = $("<div>");
	            		$ppcDiv.attr("class","job-header-job-div");
	            		$ppcDiv.append('<span class="formLabel">PPC: </span>');
	            		$ppcDiv.append('<span>$' + $pricePerCleaning +'</span>');
	            		
	            		
	            		
	            		// Now that we've build all the pieces, this bit determines the order they're displayed
	            		$jobHider.append($statusDiv);
	            		$jobHider.append($jobIdDiv);
	            		$jobHider.append($jobDiv);
	            		$jobHider.append($ppcDiv);
	            		$jobHider.append($freqDiv);
	            		$jobHider.append($divDiv);
	            		$jobHider.append($descDiv);

	            		$jobHeader.append($jobHider);	
	            		
	            		return $jobHeader;
		            },
		            
		            
		            
		            makeJobSortLengthCheck : function() {
		            	setTimeout(function() {
		            		var $jobCount = $("#jobList li").length;
		            		if ( $jobCount > 0 ) {
		            			QUOTEMAINTENANCE.makeJobSort();
		            		} else {
		            			QUOTEMAINTENANCE.makeJobSortLengthCheck();
		            		}
		            	},250);
		            },
		            
		            makeJobSort : function() {
		            	var $canReorder=true;
		            	$.each( $("#jobList li"), function(index, val) {
		            		var $jobstatus=$(val).attr("data-jobstatus");
		            		if ( $jobstatus=="P" || $jobstatus=="A" || $jobstatus=="C" ) {
		            			$canReorder=false;	
		            		}
		            	});
		            	if ( $canReorder == true ) {
							$("#jobList").sortable({
								stop:function($event, $ui) {
									console.log("Stopping");
									console.log($ui);
									
									$("#reorder-job-modal").dialog("open");
									
								}
							});	
		            	}
					},
					
					
					
					
					makeJobTagList : function(){
	    				var $url = "jobtag/jobTag/list";
	    				var jqxhr = $.ajax({
	    					type: 'GET',
	    					url: $url,
	    					data: null,
	    					statusCode: {
	    						200: function($data) {
	    							QUOTEMAINTENANCE.jobTagList = $data.data.itemList;
	    							QUOTEMAINTENANCE.incrementProgress("Job Tag List");
	    						},					
	    						403: function($data) {
	    							$("#globalMsg").html("Session Expired. Log In and try again").show();
	    						},
	    						404: function($data) {
	    							$("#globalMsg").html("System Error 404/Manager List. Contact Support").show();
	    						},
	    						500: function($data) {
	    							$("#globalMsg").html("System Error 500. Contact Support").show();
	    						}
	    					},
	    					dataType: 'json'
	    				});		    			
		    		},
		    		
		    		
		    		
					
					makeManagerList : function(){
	    				var $url = "user/manager";
	    				var jqxhr = $.ajax({
	    					type: 'GET',
	    					url: $url,
	    					data: {"sortBy":"firstName"},    // you can do firstName,lastName or email
	    					statusCode: {
	    						200: function($data) {
	    							QUOTEMAINTENANCE.managerList = $data.data.userList;
	    							QUOTEMAINTENANCE.populateManagerList($data.data.userList);
	    							QUOTEMAINTENANCE.incrementProgress("Manager List");
	    						},					
	    						403: function($data) {
	    							$("#globalMsg").html("Session Expired. Log In and try again").show();
	    						},
	    						404: function($data) {
	    							$("#globalMsg").html("System Error 404/Manager List. Contact Support").show();
	    						},
	    						500: function($data) {
	    							$("#globalMsg").html("System Error 500. Contact Support").show();
	    						}
	    					},
	    					dataType: 'json'
	    				});		    			
		    		},
		    		
		    		
		    		
		    		makeOptionLists : function(){
						ANSI_UTILS.getOptionList('JOB_STATUS,JOB_FREQUENCY,COUNTRY,INVOICE_GROUPING,INVOICE_STYLE,INVOICE_TERM,JOBTAG_TYPE', QUOTEMAINTENANCE.populateOptions);
						QUOTEMAINTENANCE.incrementProgress("Job Status List");
						QUOTEMAINTENANCE.incrementProgress("Job Frequency List");
						
						
						QUOTEMAINTENANCE.getDivisionList(QUOTEMAINTENANCE.populateDivisionList);
						
						
						QUOTEMAINTENANCE.getCodeList("job", "building_type", QUOTEMAINTENANCE.populateBuildingType);
						QUOTEMAINTENANCE.incrementProgress("Building Type List");
						
						QUOTEMAINTENANCE.getCodeList("quote","account_type", QUOTEMAINTENANCE.populateAccountType); 
						QUOTEMAINTENANCE.incrementProgress("Account Type List");
						
						QUOTEMAINTENANCE.getCodeList("quote","lead_type", QUOTEMAINTENANCE.populateLeadType); 
						QUOTEMAINTENANCE.incrementProgress("Lead Type List");
						
						QUOTEMAINTENANCE.makeManagerList();	
						QUOTEMAINTENANCE.makeJobTagList();
		            },
		            
		            
		            
		            
		            
		            makeOtherClickables : function() {
						QUOTEMAINTENANCE.makeJobExpansion();

		    			$("#address-panel-hider").click(function($event) {
		    				$("#address-panel-open").toggle();
		    				$("#address-panel-closed").toggle();
		    				$("#addressPanel").toggle();
		    			});
		    			
		    			$("#quote-panel-hider").click(function($event) {
		    				$("#quote-panel-open").toggle();
		    				$("#quote-panel-closed").toggle();
		    				$("#quotePanel").toggle();
		    			});
		    			
		    			$("#copy-button").click(function($event) {
		    				var $quoteId = $(this).attr("data-quoteid");
		    				QUOTEMAINTENANCE.doCopyQuote($quoteId);
		    			});
		    			
		    			$("#revise-button").click(function($event) {
		    				var $quoteId = $(this).attr("data-quoteid");
		    				QUOTEMAINTENANCE.doReviseQuote($quoteId);
		    			});
		    			
		    			$("#preview-button").click(function($event) {
		    				var $quoteId = $(this).attr("data-quoteid");
		    				var $quoteNumber = $(this).attr("data-quotenumber");
		    				//QUOTEMAINTENANCE.doReviseQuote($quoteId);
		    				console.log("Printing " + $quoteId);
		    				QUOTE_PRINT.showQuotePrint("#printQuoteDiv", $quoteId, $quoteNumber,"PREVIEW");
		    			});

		    			$("#propose-button").click(function($event) {
		    				var $quoteId = $(this).attr("data-quoteid");
		    				var $quoteNumber = $(this).attr("data-quotenumber");
		    				//QUOTEMAINTENANCE.doReviseQuote($quoteId);
		    				console.log("Printing " + $quoteId);
		    				QUOTE_PRINT.showQuotePrint("#printQuoteDiv", $quoteId, $quoteNumber, "PROPOSE");
		    			});
		    			
		    			
		    			
		    			$("#new-job-button").click(function($event) {
		    				var $quoteId = $(this).attr("data-quoteid");
		    				var $quoteNumber = $(this).attr("data-quotenumber");
		    				console.debug("Adding job to " + $quoteId);
		    				
		    				//Clear all job forms
		    				$(".job-edit-panel input").val("");
		    				$(".job-edit-panel select").val("");
		    				$(".job-edit-panel textarea").val("");
		    				
		    				//show job tags
		    				if ($("#job-edit-modal .proposal .job-proposal-jobtag").html() == "" ) {
		    					$("#job-edit-modal .proposal .job-proposal-jobtag").html(QUOTEMAINTENANCE.populateTagListEdit(null));
		    					$(".jobtag-edit").click(function($event) {
									var $tagId = $(this).attr("data-tagid");
									var $selected = $(this).hasClass("jobtag-selected");
									console.log("jobtag click: " + $tagId + " " + $selected);
									if ( $selected ) {
										$(this).removeClass("jobtag-selected");
									} else {
										$(this).addClass("jobtag-selected");
									}
								});
		    				} else {
		    					// new job -- nothing pre-selected
		    					$.each($("#job-edit-modal .proposal .job-proposal-jobtag .jobtag-edit"), function($index, $value) {
		    						$(this).removeClass("jobtag-selected");
		    					});
		    				}
		    				
		    				//set all job forms to visible
							$(".job-edit-panel").show();		    				
		    				//Populate frequncy dropdown
		    				QUOTEMAINTENANCE.populateJobFrequencySelect();
		    				console.debug("Set jobid attr to new/add/something");
		    				$("#job-edit-modal").attr("data-jobid", "add");
							$("#job-edit-modal").attr("data-type", "add");
							$("#job-edit-modal input[name='job-activation-equipment']").val("BASIC");
							$("#job-edit-modal select[name='job-activation-schedule']").val("auto");
							var $maxJobNbr = -1;
							$.each( $("#jobList .jobTitleRow"), function($idx, $jobTitle){
								$jobNbr = parseInt($($jobTitle).attr("data-jobnbr"));
								console.log($jobNbr)
								if( $jobNbr > $maxJobNbr ) {
									$maxJobNbr = $jobNbr;
								}
							});
							var $nextJobNbr = $maxJobNbr + 1;
							$("#job-edit-modal input[name='job-proposal-job-nbr']").val($nextJobNbr);
		    				$("#job-edit-modal").dialog("open");
		    			});
	
		    			
		    			
		    			
		    			$("#edit-this-address .edit-address").click(function($event) {
		    				var $type = $(this).data("type");
		    				console.log("Editing addr: " + $type);
		    				var $title = "Edit Address";
		    				if ( $type == "jobsite") { $title = "Job Site"; }
		    				if ( $type == "billto") { $title = "Bill To"; }		    				
		    				$("#address-edit-modal input[name='address-name']").val("");
		    				$("#address-edit-modal").dialog("option","title",$title);
		    				$("#address-edit-modal .none-found").hide();
		    				$("#address-edit-display").hide();
		    				$("#address-edit-modal").data("type",$type);
				        	$("#address-edit-modal").data("id","");
		    				$("#address-edit-modal").dialog("open");
		    			});
		    			
		    			
		    			$("#edit-this-address .edit-address").mouseover(function($event) {
		    				var $type = $(this).data("type");
		    				console.log("mousing addr: " + $type);
		    				if ( $type == "jobsite") { $("#address-job-site").addClass("pre-edit"); }
		    				if ( $type == "billto") { $("#address-bill-to").addClass("pre-edit"); }	
		    			});
		    			
		    			
		    			$("#edit-this-address .edit-address").mouseout(function($event) {
		    				var $type = $(this).data("type");
		    				console.log("mousing addr: " + $type);
		    				if ( $type == "jobsite") { $("#address-job-site").removeClass("pre-edit"); }
		    				if ( $type == "billto") { $("#address-bill-to").removeClass("pre-edit"); }	
		    			});
		    			
		    			
		    			
		    			
		    			$("#edit-this-address .edit-contact").click(function($event) {
		    				var $type = $(this).data("type");
		    				var $title = "Edit Contact";
		    				if ( $type == "job") { $title = "Job Contact"; }
		    				if ( $type == "site") { $title = "Site Contact"; }
		    				if ( $type == "contract") { $title = "Contract Contact"; }
		    				if ( $type == "billing") { $title = "Billing Contact"; }
		    				$("#contact-edit-modal input[name='contact-name']").val("");
		    				$("#contact-edit-modal").dialog("option","title",$title);
		    				$("#contact-edit-modal .none-found").hide();
		    				$("#contact-edit-display").hide();
		    				$("#contact-edit-modal").data("type",$type);
				        	$("#contact-edit-modal").data("id","");
		    				$("#contact-edit-modal").dialog("open");
		    			});
		    			
		    			$("#edit-this-address .edit-contact").mouseover(function($event) {
		    				var $type = $(this).data("type");
		    				var $title = "mouse Contact";
		    				if ( $type == "job") { $("#job-contact").addClass("pre-edit"); }
		    				if ( $type == "site") { $("#site-contact").addClass("pre-edit"); }
		    				if ( $type == "contract") { $("#contract-contact").addClass("pre-edit"); }
		    				if ( $type == "billing") { $("#billing-contact").addClass("pre-edit"); }		    				
		    			});
		    			
		    			
		    			$("#edit-this-address .edit-contact").mouseout(function($event) {
		    				var $type = $(this).data("type");
		    				var $title = "mouse Contact";
		    				if ( $type == "job") { $("#job-contact").removeClass("pre-edit"); }
		    				if ( $type == "site") { $("#site-contact").removeClass("pre-edit"); }
		    				if ( $type == "contract") { $("#contract-contact").removeClass("pre-edit"); }
		    				if ( $type == "billing") { $("#billing-contact").removeClass("pre-edit"); }		    				
		    			});
		    			
		    			
		    			$("#edit-this-quote").click(function($event) {
		    				console.log("Editing a quote");
		    				$("#quotePanel input").prop("disabled", false);
		    				$("#quotePanel select").prop("disabled", false);
		    				$("#edit-this-quote").hide();
		    				$("#quoteDataContainer .managerName").hide();
		    				$("#quoteDataContainer select[name='managerId']").show();
		    				$("#new-quote-button").hide();
		    				$("#lookup-button").hide();
		    				$("#quote-container .quote-button-container .save-quote").show();
		    				$("#quote-container .quote-button-container .cancel-edit").show();
		    			});
		    			
		    			
		    			$("#quote-container .quote-button-container .cancel-edit").click(function($event) {
		    				console.log("Cancel Editing a quote");
		    				QUOTEMAINTENANCE.populateQuotePanel(QUOTEMAINTENANCE.quote);
		    				$("#quotePanel input").prop("disabled", true);
		    				$("#quotePanel select").prop("disabled", true);
		    				$("#quotePanel select").removeClass("edit-err");
						    $("#quotePanel input").removeClass("edit-err");
		    				$("#edit-this-quote").show();
		    				$("#quoteDataContainer .managerName").show();
		    				$("#quoteDataContainer select[name='managerId']").hide();
		    				$("#new-quote-button").show();
		    				$("#lookup-button").show();
		    				$("#quote-container .quote-button-container .save-quote").hide();
		    				$("#quote-container .quote-button-container .cancel-edit").hide();
		    			});
		    			
		    			
		    			$("#quote-container .quote-button-container .save-quote").click(function($event) {
		    				console.log("Saving quote header");
		    				QUOTEMAINTENANCE.saveQuoteHeader();
		    			});
		    		},
		    		
		    		
		    		
		    		makeProgressbar : function() {
		    			//var progressbar = $("#progressbar");
		    			//var progressLabel = $("#progress-label");
		    			QUOTEMAINTENANCE.progressbar.progressbar({
		    				value: false,
		    				change: function() {
		    					//progressLabel.text( progressbar.progressbar("value") + "%" );
		    					//console.log("progress value: " + QUOTEMAINTENANCE.progressbar.progressbar("value"));
		    				},
		    				complete: function() {
		    					console.log("Progress complete");
		    					QUOTEMAINTENANCE.progressLabel.text("Complete");
		    					//$("#progressbar").hide();
		    					QUOTEMAINTENANCE.showQuote();
		    				},
		    				max: 12
		    			});
		    		},
		    		
		    		
		    		
		    		
		    		populateAccountType : function($data) {
						QUOTEMAINTENANCE.accountTypeList = $data.codeList;
						$selectorName = "#quoteDataContainer select[name='accountType']";
						var $select = $($selectorName);
						$('option', $select).remove();

						$select.append(new Option("",""));
						$.each($data.codeList, function(index, val) {
						    $select.append(new Option(val.displayValue, val.value));
						});
					},
					
					
					
					
					populateAddressModal : function($statusCode, $data) {
						console.log("Populating address modal: " + $statusCode);
						console.log($data);
						if ( $statusCode == 200 ) {
							var $addressSelector = "#address-edit-display";
							$("#address-edit-modal").data("id",$data.data.addressList[0].addressId); //ui.item.id);	
							QUOTEMAINTENANCE.populateAddressPanel("#address-edit-display", $data.data.addressList[0]);
							$($addressSelector).show();
						}
						if ( $statusCode == 403 ) {
							$("#globalMsg").html("Session expired. Log in and try again").show();
							$("#address-edit-modal").dialog("close");
						}
						if ( $statusCode == 404 ) {
							$("#globalMsg").html("Address Error 404. Contact Support").show();
							$("#address-edit-modal").dialog("close");
						}
						if ( $statusCode == 500 ) {
							$("#globalMsg").html("Address Error 500. Contact Support").show();
							$("#address-edit-modal").dialog("close");
						}
					},
					
					
					populateAddressPanel : function($selectorId, $address) {
		            	$($selectorId + " .ansi-address-name").html($address.name);
		            	$($selectorId + " .ansi-address-address1").html($address.address1);
            			$($selectorId + " .ansi-address-address2").html($address.address2);
        				$($selectorId + " .ansi-address-city").html($address.city);
      					$($selectorId + " .ansi-address-state").html($address.state);
      					$($selectorId + " .ansi-address-zip").html($address.zip);
						$($selectorId + " .ansi-address-county").html($address.county);
 						$($selectorId + " .ansi-address-countryCode").html($address.countryCode);
		            },
		            
		            
		            
		            
		            populateBuildingType : function($data) {
						QUOTEMAINTENANCE.buildingTypeList = $data.codeList;
						$selectorName = "#quoteDataContainer select[name='buildingType']";
						var $select = $($selectorName);
						$('option', $select).remove();

						$select.append(new Option("",""));
						$.each($data.codeList, function(index, val) {
						    $select.append(new Option(val.displayValue, val.value));
						});
					},
					
					
					
					populateContactPanel : function($selector, $data) {
						$($selector + " .ansi-contact-name").html($data.firstName + " " + $data.lastName);						
						$($selector + " .ansi-contact-number").html($data.method);
						$($selector + " .ansi-contact-method-is-business-phone").hide();
						$($selector + " .ansi-contact-method-is-mobile-phone").hide();
						$($selector + " .ansi-contact-method-is-fax").hide();
						$($selector + " .ansi-contact-method-is-email").hide();
						if ( $data.preferredContact == "business_phone") { $($selector + " .ansi-contact-method-is-business-phone").show(); }
						if ( $data.preferredContact == "mobile_phone") { $($selector + " .ansi-contact-method-is-mobile-phone").show(); }
						if ( $data.preferredContact == "fax") { $($selector + " .ansi-contact-method-is-fax").show(); }
						if ( $data.preferredContact == "email") { $($selector + " .ansi-contact-method-is-email").show(); }
					},
					
					
					
					populateDivisionList : function($data) {
		            	QUOTEMAINTENANCE.divisionList = $data.divisionList
		            	
		            	var $select = $("#quoteDataContainer select[name='divisionId']");
						$('option', $select).remove();

						$select.append(new Option("",""));
						$.each(QUOTEMAINTENANCE.divisionList, function(index, val) {
						    $select.append(new Option(val.divisionNbr + "-" + val.divisionCode, val.divisionId));
						});
		            },
		            
		            
		            
		            populateJobFrequencySelect : function() {
		            	var $select = $("#job-edit-modal .proposal select[name='job-proposal-freq']");
						$('option', $select).remove();
	
						$select.append(new Option("",""));
						$.each(QUOTEMAINTENANCE.jobFrequencyList, function(index, val) {
						    $select.append(new Option(val.display, val.abbrev));
						});
		            },
		            
		            
		            
		            
		            populateJobHeader : function($jobHeaderList) {
		            	console.log("populateJobHeader");
		            	$("#jobList").html("");
		            	$.each($jobHeaderList, function($index, $value) {
		            		$jobListItem = $("<li>");
		            		$jobListItem.attr("data-jobid", $value.jobId);
		            		$jobListItem.attr("id","job" + $value.jobId)
		            		$jobListItem.attr("data-jobstatus", $value.jobStatus);
							
		            		var $jobHeader = QUOTEMAINTENANCE.makeJobHeader(
		            				$value.jobId, 
		            				$value.jobNbr, 
		            				$value.abbrDescription, 
		            				$value.divisionNbr, 
		            				$value.divisionCode,
		            				$value.jobStatus,
		            				$value.jobFrequency,
		            				$value.pricePerCleaning,
									$value.canEdit,
									$value.canActivate,
									$value.canCancel,
									$value.canDelete,
									$value.canSchedule
		            				);
		            		
		            		// make the "loading" thing
		            		$detailDiv = $("#job-loading-pattern .job-data-row").clone()
		            		$detailDiv.attr("data-jobid", $value.jobId);
		            		
		            		$jobListItem.append($jobHeader);
		            		$jobListItem.append($detailDiv);
		            		console.log($jobHeader);
		            		$("#jobList").append($jobListItem);
		            		
		            		
		            	});	
		            	
		            	QUOTEMAINTENANCE.makeJobClickers();
		            	
		            	
		            },
		            
		            
		            
		            makeJobTagDisplay : function($jobTagList) {
		            	var $display = '<span class="formLabel">Tags:</span> N/A<br />';
		            	if ( $jobTagList != null && $jobTagList.length > 0 ) {
		            		$display = "";
		            		$.each(QUOTEMAINTENANCE.jobTagTypeList, function($typeIndex, $tagType) {
		            			$display = $display + '<span class="formLabel">' + $tagType.display + ": </span>";
			            		$.each($jobTagList, function($index, $value) {
			            			if ( $value.tagType == $tagType.name ) {
			            				$display = $display + '<span class="jobtag tooltip" data-tagid="'+$value.tagId+'">' + $value.longCode + '<span class="tooltiptext">'+$value.abbrev + " - " + $value.tagDescription+'</span></span>&nbsp;';
			            			}
			            		});
			            		$display = $display + "<br />";
		            		});
		            	}
		            	return $display;
		            },
		            
		            
		            
		            populateJobPanel : function($jobId, $destination, $data) {	
		            	console.log("Populate Job Panel");
		            	console.log($data);
		            	$jobDetail = $data.quote.jobDetail
		            	$($destination + " .jobtext").dblclick(function() {
		            		var $myText = $(this).html();
		            		var aux =document.createElement("input");
		            		aux.setAttribute("value", $myText);
		            		document.body.appendChild(aux);
		            		aux.focus();
		            		aux.select();
		            		document.execCommand("copy");
		            		document.body.removeChild(aux);	
		            		$("#copy-modal").dialog("open");
		            		setTimeout(function() {
		            			$("#copy-modal").dialog("close");
		            		},250);
		    			});
		            	$($destination + " .jobProposalDisplayPanel .job-proposal-job-nbr").html($jobDetail.job.jobNbr);
		            	$($destination + " .jobProposalDisplayPanel .job-proposal-ppc").html("$" + $jobDetail.job.pricePerCleaning);
		            	$($destination + " .jobProposalDisplayPanel .job-proposal-freq").html($jobDetail.job.jobFrequency);
		            	$($destination + " .jobProposalDisplayPanel .job-proposal-desc").html($jobDetail.job.serviceDescription);
		            	$($destination + " .jobProposalDisplayPanel .job-proposal-jobtag").html(QUOTEMAINTENANCE.makeJobTagDisplay($jobDetail.job.jobTagList));
		            	
		            	$($destination + " .jobActivationDisplayPanel .job-activation-dl-pct").html($jobDetail.job.directLaborPct);
		            	$($destination + " .jobActivationDisplayPanel .job-activation-dl-budget").html($jobDetail.job.budget);
		            	$($destination + " .jobActivationDisplayPanel .job-activation-floors").html($jobDetail.job.floors);
		            	if ( $jobDetail.job.requestSpecialScheduling == 1 ) {
		            		$($destination + " .jobActivationDisplayPanel .job-activation-schedule").html("Manual");
		            	} else {
		            		$($destination + " .jobActivationDisplayPanel .job-activation-schedule").html("Auto");
		            	}
		            	$($destination + " .jobActivationDisplayPanel .job-activation-equipment").html($jobDetail.job.equipment);
		            	$($destination + " .jobActivationDisplayPanel .job-activation-washer-notes").html($jobDetail.job.washerNotes);
		            	$($destination + " .jobActivationDisplayPanel .job-activation-om-notes").html($jobDetail.job.omNotes);
		            	$($destination + " .jobActivationDisplayPanel .job-activation-billing-notes").html($jobDetail.job.billingNotes);

		            	$($destination + " .jobDatesDisplayPanel .job-dates-proposed-date").html($data.quote.jobDetail.quote.proposalDate);
		            	$($destination + " .jobDatesDisplayPanel .job-dates-activation-date").html($jobDetail.job.activationDate);
		            	$($destination + " .jobDatesDisplayPanel .job-dates-start-date").html($jobDetail.job.startDate);
		            	$($destination + " .jobDatesDisplayPanel .job-dates-cancel-date").html($jobDetail.job.cancelDate);
		            	$($destination + " .jobDatesDisplayPanel .job-dates-cancel-reason").html($jobDetail.job.cancelReason);
		            			            	
		            	$($destination + " .jobInvoiceDisplayPanel .job-invoice-purchase-order").html($jobDetail.job.poNumber);
		            	$($destination + " .jobInvoiceDisplayPanel .job-invoice-vendor-nbr").html($jobDetail.job.ourVendorNbr);
		            	$($destination + " .jobInvoiceDisplayPanel .job-invoice-expire-date").html($jobDetail.job.expirationDate);
		            	$($destination + " .jobInvoiceDisplayPanel .job-invoice-expire-reason").html($jobDetail.job.expirationReason);
		            	
		            	var $lastProcessDate = "";
		            	var $lastProcessTicket = "";
		            	if ( $jobDetail.lastRun.processDate != null ) { $lastProcessDate = $jobDetail.lastRun.processDate; }
		            	if ( $jobDetail.lastRun.ticketId != null ) { $lastProcessTicket = $jobDetail.lastRun.ticketId; }
		            	$($destination + " .jobScheduleDisplayPanel .job-schedule-last-run").html($lastProcessDate + " " + $lastProcessTicket);		            	

		            	if ( $jobDetail.job.repeatScheduleAnnually == 1 ) {
		            		$($destination + " input[name='repeatedAnnually']").prop("checked", true);
		            	} else {
		            		$($destination + " input[name='repeatedAnnually']").prop("checked", false);
		            	}

		            	var $startDate = "";
		            	var $nextTicket = "";
		            	if ( $jobDetail.nextDue.startDate != null ) { $startDate = $jobDetail.nextDue.startDate; }
		            	if ( $jobDetail.nextDue.ticketId != null ) { $nextTicket = $jobDetail.nextDue.ticketId; }
		            	$($destination + " .jobScheduleDisplayPanel .job-schedule-next-due").html($startDate + " " + $nextTicket);
		            	$($destination + " .jobScheduleDisplayPanel .job-schedule-created-thru").html($jobDetail.lastCreated.startDate);
		            	$($destination + " .jobScheduleDisplayPanel .job-schedule-ticket-list").attr("href", "ticketLookup.html?jobId="+$jobId);
		            	
		            	if ($jobDetail.job.updatedFirstName==null) {
		            		updatedFirstName='';
		            	} else {
		            		updatedFirstName=$jobDetail.job.updatedFirstName;
		            	}
		            	if ($jobDetail.job.updatedLastName==null) {
		            		updatedLastName='';
		            	} else {
		            		updatedLastName=$jobDetail.job.updatedLastName;
		            	}
		            	if ($jobDetail.job.addedFirstName==null) {
		            		addedFirstName='';
		            	} else {
		            		addedFirstName=$jobDetail.job.addedFirstName;
		            	}
		            	if ($jobDetail.job.addedLastName==null) {
		            		addedLastName='';
		            	} else {
		            		addedLastName=$jobDetail.job.addedLastName;
		            	}
		            	
		            	$($destination + " .jobAuditDisplayPanel .job-audit-created-by").html(addedFirstName + " " + addedLastName);	
		            	$($destination + " .jobAuditDisplayPanel .job-audit-created-date").html($jobDetail.job.addedDate);	
		            	$($destination + " .jobAuditDisplayPanel .job-audit-updated-by").html(updatedFirstName + " " + updatedLastName);	
		            	$($destination + " .jobAuditDisplayPanel .job-audit-updated-date").html($jobDetail.job.updatedDate);
		            	
		            	$anchorName = "job" + $jobDetail.job.jobId;
		            	console.log($anchorName);
						$anchor = $("a[name='" + $anchorName + "']");
						$('html,body').animate({scrollTop: $anchor.offset().top},'slow');
		            },
		            
		            
		            
		            populateLeadType : function($data) {
						QUOTEMAINTENANCE.leadTypeList = $data.codeList;
						$selectorName = "#quoteDataContainer select[name='leadType']";
						var $select = $($selectorName);
						$('option', $select).remove();

						$select.append(new Option("",""));
						$.each($data.codeList, function(index, val) {
						    $select.append(new Option(val.displayValue, val.value));
						});
					},
					
					
					
					
					populateManagerList : function($optionList) {
		            	var $select = $("#quoteDataContainer select[name='managerId']");
						$('option', $select).remove();

						$select.append(new Option("",""));
						$.each($optionList, function(index, val) {
						    $select.append(new Option(val.firstName + " " + val.lastName, val.userId));
						});
		            },
		            
		            
		            
		            populateNewQuoteJobFields : function() {
		            	// when a new quote is created, the action puts this attribute in the request
		            	<c:if test="${not empty newQuoteDisplayForm}">
		            		$("#quoteDataContainer select[name='invoiceGrouping']").val('<c:out value="${newQuoteDisplayForm.invoiceGrouping}" />');
    		            	$("#quoteDataContainer select[name='invoiceStyle']").val('<c:out value="${newQuoteDisplayForm.invoiceStyle}" />');
	            			$("#quoteDataContainer select[name='buildingType']").val('<c:out value="${newQuoteDisplayForm.buildingType}" />');
	            			var $invoiceBatch = '<c:out value="${newQuoteDisplayForm.invoiceBatch}" />' == 'true';
    		            	$("#quoteDataContainer input[name='invoiceBatch']").prop("checked", $invoiceBatch);
    		            	$("#quoteDataContainer select[name='invoiceTerms']").val('<c:out value="${newQuoteDisplayForm.invoiceTerms}" />');
    		            	var $taxExempt = '<c:out value="${newQuoteDisplayForm.taxExempt}" />' == 'true';
    		            	$("#quoteDataContainer input[name='taxExempt']").prop("checked", $taxExempt);
    		            	$("#quoteDataContainer input[name='taxExemptReason']").val('<c:out value="${newQuoteDisplayForm.taxExemptReason}" />');
    		            	$("#new-job-button").click();
		            	</c:if>
		            },
		            
		            
		            
		            populateOptions : function($optionData) {
		            	QUOTEMAINTENANCE.countryList = $optionData.country;
		            	QUOTEMAINTENANCE.invoiceGroupingList = $optionData.invoiceGrouping;
						QUOTEMAINTENANCE.invoiceStyleList = $optionData.invoiceStyle;
						QUOTEMAINTENANCE.invoiceTermList = $optionData.invoiceTerm;
						QUOTEMAINTENANCE.jobStatusList = $optionData.jobStatus;
						QUOTEMAINTENANCE.jobFrequencyList = $optionData.jobFrequency;
						QUOTEMAINTENANCE.jobTagTypeList = $optionData.jobTagType
						
						QUOTEMAINTENANCE.populateOptionSelects();
		            },
		            
		            
		            
		            populateOptionSelects : function() {
		            	QUOTEMAINTENANCE.incrementProgress("Country List");
						
						
						var $select = $("#quoteDataContainer select[name='invoiceTerms']");
						$('option', $select).remove();
						$select.append(new Option("",""));
						$.each(QUOTEMAINTENANCE.invoiceTermList, function(index, val) {
						    $select.append(new Option(val.display, val.abbrev));
						});
						QUOTEMAINTENANCE.incrementProgress("Invoice Terms");
						
						var $select = $("#quoteDataContainer select[name='invoiceStyle']");
						$('option', $select).remove();
						$select.append(new Option("",""));
						$.each(QUOTEMAINTENANCE.invoiceStyleList, function(index, val) {
						    $select.append(new Option(val.display, val.abbrev));
						});
						QUOTEMAINTENANCE.incrementProgress("Invoice Style List");
						
						
						var $select = $("#quoteDataContainer select[name='invoiceGrouping']");
						$('option', $select).remove();
						$select.append(new Option("",""));
						$.each(QUOTEMAINTENANCE.invoiceGroupingList, function(index, val) {
						    $select.append(new Option(val.display, val.abbrev));
						});
						QUOTEMAINTENANCE.incrementProgress("Invoice Grouping List");
		            },
		            
		            
		            populateQuotePanel : function($quote) {
		            	console.log("populating quote panel");
		            	console.log($quote);
		            
		            	$("#printHistoryDiv").attr("data-quoteid", $quote.quote.quoteId);	//this is so the gethistory method has an id to work with
		            
		            	$("#quoteDataContainer input[name='quoteId']").val($quote.quote.quoteId);
		            	$("#quoteDataContainer select[name='managerId']").val($quote.quote.managerId);
		            	$("#quoteDataContainer .managerFirstName").html($quote.quote.managerFirstName);
		            	$("#quoteDataContainer .managerLastName").html($quote.quote.managerLastName);
		            	$("#quoteDataContainer select[name='divisionId']").val($quote.quote.divisionId);
		            	$("#quoteDataContainer .quoteNbrDisplay").html($quote.quote.quoteNumber);
		            	$("#quoteDataContainer .revisionDisplay").html($quote.quote.revision);
		            	
		            	if ( $quote.quote.copiedFromQuoteId != null ) {
		            		var $copyLink = '(Previous: <a href="quoteMaintenance.html?id=' + $quote.quote.copiedFromQuoteId + '" style="color:#404040">'+$quote.quote.copiedFromQuoteNbrRev+'</a>)'
		            		$("#quoteDataContainer .quoteCopyDisplay").html($copyLink);
		            	}
		            	
		            	$("#quoteDataContainer select[name='accountType']").val($quote.quote.accountType);
		            	$("#quoteDataContainer select[name='invoiceTerms']").val($quote.quote.invoiceTerms);
		            	$("#quoteDataContainer .proposedDate").html($quote.quote.proposalDate);
		            	$("#quoteDataContainer select[name='leadType']").val($quote.quote.leadType);
		            	$("#quoteDataContainer select[name='invoiceStyle']").val($quote.quote.invoiceStyle);
		            	if ( $quote.signedBy != null ) {
		            		$("#quoteDataContainer input[name='signedBy']").val($quote.signedBy.firstName + " " + $quote.signedBy.lastName);
		            		$("#quoteDataContainer input[name='signedBy']").attr("id", $quote.signedBy.contactid);
		            	}		            	
		            	$("#quoteDataContainer input[name='signedByContactId']").val($quote.quote.signedByContactId);
		            	$("#quoteDataContainer select[name='buildingType']").val($quote.quote.buildingType);
		            	$("#quoteDataContainer select[name='invoiceGrouping']").val($quote.quote.invoiceGrouping);
		            	
		            	var $invoiceBatch = $quote.quote.invoiceBatch == 1;
		            	$("#quoteDataContainer input[name='invoiceBatch']").prop("checked", $invoiceBatch);
		            	var $taxExempt = $quote.quote.taxExempt == 1;
		            	$("#quoteDataContainer input[name='taxExempt']").prop("checked", $taxExempt);
		            	$("#quoteDataContainer input[name='taxExemptReason']").val($quote.quote.taxExemptReason);
		            	
		            	$("#quoteDataContainer .printCount").html($quote.quote.printCount);		            	
		            },
		            
		            
		            
		            populateQuotePanels : function($data) {
	        			var $canPopulate = true;
	        			
	        			if ( QUOTEMAINTENANCE.accountTypeList == null ) { $canPopulate=false; }
	        			if ( QUOTEMAINTENANCE.countryList == null ) { $canPopulate=false; }
       					if ( QUOTEMAINTENANCE.buildingTypeList == null ) { $canPopulate=false; }
    					if ( QUOTEMAINTENANCE.divisionList == null ) { $canPopulate=false; }
    					if ( QUOTEMAINTENANCE.invoiceGroupingList == null ) { $canPopulate=false; }
    					if ( QUOTEMAINTENANCE.invoiceStyleList == null ) { $canPopulate=false; }
    					if ( QUOTEMAINTENANCE.invoiceTermList == null ) { $canPopulate=false; }
    					if ( QUOTEMAINTENANCE.jobStatusList == null ) { $canPopulate=false; }
    					if ( QUOTEMAINTENANCE.jobFrequencyList == null ) { $canPopulate=false; }
  						if ( QUOTEMAINTENANCE.leadTypeList == null ) { $canPopulate=false; }
						if ( QUOTEMAINTENANCE.managerList == null ) { $canPopulate=false; }
						if ( QUOTEMAINTENANCE.quoteId != null && QUOTEMAINTENANCE.quoteId != "" && QUOTEMAINTENANCE.quote == null ) { $canPopulate = false; }
						
						
	        			if ( $canPopulate == true ) {
	        				$(".action-button").attr("data-quoteid",$data.quote.quote.quoteId); //This is so copy/revise buttons know what to copy/revise
							$(".action-button").attr("data-quotenumber",$data.quote.quote.quoteNumber + $data.quote.quote.revision);
							QUOTEMAINTENANCE.populateQuotePanel($data.quote);
							QUOTEMAINTENANCE.populateAddressPanel( "#address-bill-to", $data.quote.billTo);
							QUOTEMAINTENANCE.populateAddressPanel( "#address-job-site", $data.quote.jobSite);
							if ( $data.quote.jobContact != null) {
								// for new quotes, jobContact could be empty
								QUOTEMAINTENANCE.populateContactPanel( "#job-contact", $data.quote.jobContact.jobContact);
								QUOTEMAINTENANCE.populateContactPanel( "#site-contact", $data.quote.jobContact.siteContact);
								QUOTEMAINTENANCE.populateContactPanel( "#billing-contact", $data.quote.jobContact.billingContact);
								QUOTEMAINTENANCE.populateContactPanel( "#contract-contact", $data.quote.jobContact.contractContact);
							}
							if ( $data.quote.jobHeaderList != null && $data.quote.jobHeaderList.length > 0 ) {
								QUOTEMAINTENANCE.populateJobHeader($data.quote.jobHeaderList)
							}
							QUOTEMAINTENANCE.makeJobExpansion();
							
							// when the page loads the first time, check for a job id
							// if it is there, expand that job
							// then delete the input parameter so it doesn't get accidentally referenced later
							if ( QUOTEMAINTENANCE.jobId != null ) {
								$.each( $("#jobList li .job-hider"), function($index, $value) {
									var $jobid = $($value).attr("data-jobid");
									if ( $jobid == QUOTEMAINTENANCE.jobId ) {
										$($value).click();
									}
								});
								QUOTEMAINTENANCE.jobId = null;
							}
	        			} else {
	        				setTimeout(function() {
	            				QUOTEMAINTENANCE.populateQuotePanels($data);
	            			},1000);
	        			}
					},
					
					
					
					reorderJobs : function() {
						var $jobIdList = []
						$.each( $("#jobList li"), function(index, val) {
						    console.log(val);
						    var $thisJobId = $(val).attr("data-jobid");
						    $jobIdList.push($thisJobId);
						});
						
						var $outbound = {};
						$outbound['jobIdList'] = $jobIdList
						console.log(JSON.stringify($outbound));
						
						var $quoteId = QUOTEMAINTENANCE.quote.quote.quoteId;
						var $url = "reorderJobs/" + $quoteId;
						var jqxhr = $.ajax({
							type: 'POST',
							url: $url,
							data: JSON.stringify($outbound),
							statusCode: {
								200: function($data) {
									console.log($data);
									// clear the list of job headers
									$("#jobList").html("");
									// then fill it back it
									QUOTEMAINTENANCE.populateJobHeader($data.data.quote.jobHeaderList)
									QUOTEMAINTENANCE.makeJobExpansion();
								},					
								403: function($data) {
									$("#globalMsg").html("Session Timeout. Log in and try again");
								},
								404: function($data) {
									$("#globalMsg").html("System Error Reorder 404. Contact Support");
								},
								500: function($data) {
									$("#globalMsg").html("System Error Reorder 500. Contact Support");
								}
							},
							dataType: 'json'
						});
					},
					
					saveAddress : function() {	
						console.log("saveAddress");
						var $addressType = $("#address-edit-modal").data("type");
						var $addressId = $("#address-edit-modal").data("id");
						console.log("Saving an address: " + $addressType + " " + $addressId);

						// this maps the address type we use in the jquery to the field in the quote update request object
						var $addressLabels = {
								"jobsite":"jobSiteAddressId",
								"billto":"billToAddressId"
						}
						console.log("QUOTEMAINTENANCE.quote");
						console.log(QUOTEMAINTENANCE.quote);
						var $quoteId = QUOTEMAINTENANCE.quote.quote.quoteId;
						console.log("QUOTE ID: " + $quoteId);
						var $addressLabel = $addressLabels[$addressType];
						var $outbound = {};
						$outbound["quoteId"]=$quoteId;
						$outbound[$addressLabel]=$addressId;
						QUOTEMAINTENANCE.doQuoteUpdate($quoteId, $outbound, QUOTEMAINTENANCE.saveAddressSuccess, QUOTEMAINTENANCE.saveAddressErr);
					},
					
					
					
					saveAddressErr : function($statusCode) {
						var $messages = {
								403:"Session Expired. Log in and try again",
								404:"System Error Address 404. Contact Support",
								500:"System Error Address 500. Contact Support"
						}
						$("#address-edit-modal").dialog("close");
						$("#globalMsg").html( $messages[$statusCode] );
					},
					
					
					
					saveAddressSuccess : function($data) {
						console.log($data);
						var $type = $("#address-edit-modal").data("type");
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {							
							if ( $type == 'jobsite' ) {
								$message = $data.data.webMessages.jobSiteAddressId[0]; 
							} else if ( $type == 'billto' ) {
								$message = $data.data.webMessages.billToAddressId[0];
							} else {
								$message = "Unexpected Response. Contact Support: " + $type;
							}
							$("#address-edit-modal .errMsg").html($message).show().fadeOut(3000);
						} else {
							console.log("Saving quote stuff from address update");
							console.log($data);
							QUOTEMAINTENANCE.quote = $data.data.quote;
							QUOTEMAINTENANCE.populateAddressPanel( "#address-bill-to", $data.data.quote.billTo);
							QUOTEMAINTENANCE.populateAddressPanel( "#address-job-site", $data.data.quote.jobSite);
							$("#globalMsg").html("Update Successful").fadeOut(3000);
							$("#address-edit-modal").dialog("close");
						}
					}, 
						
						
					saveContact : function() {
						var $contactLabels = {
								"contract":"contractContactId",
								"billing":"billingContactId",
								"job":"jobContactId",
								"site":"siteContact"
						}
					
						var $quoteId = QUOTEMAINTENANCE.quote.quote.quoteId;
						var $contactType = $("#contact-edit-modal").data("type");
						var $contactId = $("#contact-edit-modal").data("id");						
						var $contactLabel = $contactLabels[$contactType];
						var $outbound = {};
						$outbound["quoteId"]=$quoteId;
						$outbound[$contactLabel]=$contactId;
						QUOTEMAINTENANCE.doQuoteUpdate($quoteId, $outbound, QUOTEMAINTENANCE.saveContactSuccess, QUOTEMAINTENANCE.saveContactErr);
					},
					
					
					saveContactErr : function($statusCode) {
						var $messages = {
								403:"Session Expired. Log in and try again",
								404:"System Error Contact 404. Contact Support",
								500:"System Error Contact 500. Contact Support"
						}
						$("#contact-edit-modal").dialog("close");
						$("#globalMsg").html( $messages[$statusCode] );
					},
					
					
					saveContactSuccess : function($data) {
						console.log("save contact success");
						console.log("QUOTEMAINTENANCE.quote");
						console.log(QUOTEMAINTENANCE.quote);
						console.log($data);
						var $type = $("#contact-edit-modal").data("type");
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {							
							if ( $type == 'job' ) {
								$message = $data.data.webMessages.jobContactId[0]; 
							} else if ( $type == 'site' ) {
								$message = $data.data.webMessages.siteContact[0]; 
							} else if ( $type == 'contract' ) {
								$message = $data.data.webMessages.contractContactId[0]; 
							} else if ( $type == 'billing' ) {
								$message = $data.data.webMessages.billingContactId[0]; 
							} else {
								$message = "Unexpected Response. Contact Support: " + $type;
							}
							$("#contact-edit-modal .errMsg").html($message).show().fadeOut(3000);
						} else {
							console.log("Saving quote stuff from contact update");
							console.log($data);
							QUOTEMAINTENANCE.quote = $data.data.quote;
							QUOTEMAINTENANCE.populateContactPanel( "#job-contact", $data.data.quote.jobContact.jobContact);
							QUOTEMAINTENANCE.populateContactPanel( "#site-contact", $data.data.quote.jobContact.siteContact);
							QUOTEMAINTENANCE.populateContactPanel( "#billing-contact", $data.data.quote.jobContact.billingContact);
							QUOTEMAINTENANCE.populateContactPanel( "#contract-contact", $data.data.quote.jobContact.contractContact);
							$("#globalMsg").html("Update Successful").fadeOut(3000);
							$("#contact-edit-modal").dialog("close");							
						}
						
					},
					
					
					
					
					saveJob : function() {
						var $jobId = $("#job-edit-modal").attr("data-jobid");
						var $updateType = $("#job-edit-modal").attr("data-type");
						console.log("If I were saving jobs, it would happen here " + $jobId + "  " + $updateType);
						
						// if we're doing an update, just process the one panel
						// if we're adding a new job, do them all
						if ( $updateType == "add" ) {
							var $typeList = ['proposal','activation','invoice','schedule']
						} else {
							var $typeList = [$updateType]
						}
						var $outbound = {};
						
						
						$.each($typeList, function(index, $type) {
							console.log("Looping thru: " + $type);
							var $panelSelector = "#job-edit-modal ." + $type;
							var $inputSelector = $panelSelector + " select";
							$.each( $($inputSelector), function($index, $value) {
								$selector = $panelSelector + " select[name='" + $value.name + "']";	 
								$apiname = $($selector).attr("data-apiname");
		    					$outbound[$apiname] = $($selector).val();
		    				});
							$inputSelector = $panelSelector + " input";
							$.each( $($inputSelector), function($index, $value) {							
								$selector = $panelSelector + " input[name='" + $value.name + "']";
								$apiname = $($selector).attr("data-apiname");
		    					$outbound[$apiname] = $($selector).val();
		    				});
							$inputSelector = $panelSelector + " textarea";
							$.each( $($inputSelector), function($index, $value) {
								$selector = $panelSelector + " textarea[name='" + $value.name + "']";	
								$apiname = $($selector).attr("data-apiname");
		    					$outbound[$apiname] = $($selector).val();
		    				});
							
							// do some panel-specific fixes:
							if ($type == "activation") {
								if ( $("#job-edit-modal .activation input[name='requestSpecialScheduling']").prop("checked") == true ) {
									$outbound['requestSpecialScheduling'] = 1;
								} else {
									$outbound['requestSpecialScheduling'] = 0;
								}
							}
							if ($type == "schedule") {
								if ( $("#job-edit-modal .schedule input[name='repeatedAnnually']").prop("checked") == true ) {
									$outbound['repeatScheduleAnnually'] = 1;
								} else {
									$outbound['repeatScheduleAnnually'] = 0;
								}
							}
							if ( $type == "proposal" ) {
								console.log("proposal-specific updates");
								var $jobtagList = [];
								$.each( $("#job-edit-modal .proposal .jobtag-selected"), function($tagIndex, $tagValue) {
									var $tagId = $(this).attr("data-tagid");
									$jobtagList.push(parseInt($tagId));
								});
								$outbound['jobtags'] = $jobtagList;
							}
						});

						// if this is a new job -- add all the other stuff, too
						if ( $updateType == 'add') {
							console.log("This is an add:")
							console.log(QUOTEMAINTENANCE.quote);
							console.log("********************)");
							$outbound['billingContactId'] = QUOTEMAINTENANCE.quote.jobContact.billingContact.contactId;
							$outbound['buildingType'] = QUOTEMAINTENANCE.quote.quote.buildingType;
							$outbound['contractContactId'] = QUOTEMAINTENANCE.quote.jobContact.contractContact.contactId;
							$outbound['divisionId'] = QUOTEMAINTENANCE.quote.quote.divisionId;
							$outbound['invoiceBatch'] = QUOTEMAINTENANCE.quote.quote.invoiceBatch;
							$outbound['invoiceGrouping'] = QUOTEMAINTENANCE.quote.quote.invoiceGrouping;
							$outbound['invoiceStyle'] = QUOTEMAINTENANCE.quote.quote.invoiceStyle;
							$outbound['invoiceTerms'] = QUOTEMAINTENANCE.quote.quote.invoiceTerms;
							$outbound['jobContactId'] = QUOTEMAINTENANCE.quote.jobContact.jobContact.contactId;
							//$outbound['jobTypeId'] = QUOTEMAINTENANCE.quote
							$outbound['paymentTerms'] = QUOTEMAINTENANCE.quote.quote.invoiceTerms;
							$outbound['quoteId'] = QUOTEMAINTENANCE.quote.quote.quoteId;
							$outbound['siteContact'] = QUOTEMAINTENANCE.quote.jobContact.siteContact.contactId;
							$outbound['taxExempt'] = QUOTEMAINTENANCE.quote.quote.taxExempt;
							$outbound['taxExemptReason'] = QUOTEMAINTENANCE.quote.quote.taxExemptReason;
						}
						
						$outbound['updateType'] = $updateType;
						$outbound['quoteId'] = QUOTEMAINTENANCE.quote.quote.quoteId;
						console.log(JSON.stringify($outbound) )
						
						
						QUOTEMAINTENANCE.doJobUpdate($jobId, $outbound, QUOTEMAINTENANCE.saveJobSuccess, QUOTEMAINTENANCE.saveJobErr);
					},
					
					
					saveJobErr : function($statusCode) {
						console.log("Job error");
						var $messages = {
								403:"Session Expired. Log in and try again",
								404:"System Error Job 404. Contact Support",
								500:"System Error Job 500. Contact Support"
						}
						$("#globalMsg").html( $messages[$statusCode] ).show();
						$("#job-edit-modal").dialog("close");
					},
					
					
					
					
					saveJobSuccess : function($data) {
						console.log("Job success");
						console.log($data);
						$('#job-edit-modal input').bind("focus", function() {
							$(this).removeClass("edit-err");
						});
						$('#job-edit-modal select').bind("focus", function() {
							$(this).removeClass("edit-err");
						});
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
							$.each($data.data.webMessages, function(index, val) {	
								// index matches up with attr data-apiname in the form
								// loop through the input/selects and apply a class to the input
								$.each( $("#job-edit-modal input"), function(fieldIdx, fieldVal) {
									var $apiName = $(fieldVal).attr("data-apiname");
									if ( index == $apiName ) {
										var $fieldName = $(fieldVal).attr("name");
										var $selector = "#job-edit-modal input[name='"+ $fieldName +"']";
										$($selector).addClass("edit-err");
									}
								});
								$.each( $("#job-edit-modal select"), function(fieldIdx, fieldVal) {
									var $apiName = $(fieldVal).attr("data-apiname");
									if ( index == $apiName ) {
										var $fieldName = $(fieldVal).attr("name");
										var $selector = "#job-edit-modal select[name='"+ $fieldName +"']";
										$($selector).addClass("edit-err");
									}
								});
							});
						} else {
							console.log("Update header success:");
							console.log($data);
							var $jobId = $data.data.quote.jobDetail.job.jobId;
							QUOTEMAINTENANCE.joblist[$jobId] = $data.data.quote.jobDetail;
							console.log("do something to populate the job panels here");
							//var $destination = "#job" + $jobId + " .job-data-row";
    						//QUOTEMAINTENANCE.populateJobPanel($jobId, $destination, $data.data);
							$("#globalMsg").html("Update Successful").show().fadeOut(3000);
							$("#job-edit-modal").dialog("close");
							
							QUOTEMAINTENANCE.showJobUpdates($data.data);
						}
					},
					
					
					saveQuoteHeader : function() {
	    				console.log("saveQuoteHeader");
	    				var $outbound = {};
	    				$.each( $("#quotePanel input"), function($index, $value) {
	    					if ( $value.name == 'signedBy') {
	    						$outbound['signedByContactId'] = $("#quotePanel input[name='signedBy']").attr("id");
	    					} else {
		    					$selector = "#quotePanel input[name='" + $value.name + "']";
		    					console.log($selector);
		    					$outbound[$value.name] = $($selector).val();
	    					}
	    				});
	    				$.each( $("#quotePanel select"), function($index, $value) {
	    					$selector = "#quotePanel select[name='" + $value.name + "']";
	    					$outbound[$value.name] = $($selector).val();
	    				});
	    				$outbound['taxExempt'] = $("#quotePanel input[name='taxExempt']").prop("checked");
	    				$outbound['invoiceBatch'] = $("#quotePanel input[name='invoiceBatch']").prop("checked");
	    				
	    				var $quoteId = QUOTEMAINTENANCE.quote.quote.quoteId;
	    				console.log($outbound);
	    				QUOTEMAINTENANCE.doQuoteUpdate($quoteId, $outbound, QUOTEMAINTENANCE.saveQuoteHeaderSuccess, QUOTEMAINTENANCE.saveQuoteHeaderErr);
					},
					
					
					
					saveQuoteHeaderErr : function($statusCode) {
						var $messages = {
								403:"Session Expired. Log in and try again",
								404:"System Error Quote 404. Contact Support",
								500:"System Error Quote 500. Contact Support"
						}
						$("#globalMsg").html( $messages[$statusCode] );
						$("#quotePanel input").prop("disabled", true);
	    				$("#quotePanel select").prop("disabled", true);
	    				$("#quotePanel select").removeClass("edit-err");
					    $("#quotePanel input").removeClass("edit-err");
	    				$("#edit-this-quote").show();
	    				$("#quote-container .quote-button-container .save-quote").hide();
	    				$("#quote-container .quote-button-container .cancel-edit").hide();
					},
					
					
					saveQuoteHeaderSuccess : function($data) {
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
							console.log($data);
							$.each($data.data.webMessages, function(index, val) {							    
							    // it's too much work to figure out if the field is input or select, so do both
							    $selector = "#quotePanel select[name='"+ index +"']";							    
							    $($selector).addClass("edit-err");
							    $selector = "#quotePanel input[name='"+ index +"']";							    
							    $($selector).addClass("edit-err");
							});
							alert("Invalid input. Correct the indicated fields and resubmit");
						} else {
							console.log("Update header success:");
							console.log($data);
							QUOTEMAINTENANCE.quote = $data.data.quote;
							QUOTEMAINTENANCE.populateQuotePanel(QUOTEMAINTENANCE.quote);
							QUOTEMAINTENANCE.showJobUpdates($data.data);
							$("#globalMsg").html("Update Successful").fadeOut(3000);
							$("#quotePanel input").prop("disabled", true);
		    				$("#quotePanel select").prop("disabled", true);
						    $("#quotePanel select").removeClass("edit-err");
						    $("#quotePanel input").removeClass("edit-err");
		    				$("#edit-this-quote").show();
		    				$("#quote-container .quote-button-container .save-quote").hide();
		    				$("#quote-container .quote-button-container .cancel-edit").hide();
		    				$("#new-quote-button").show();
		    				$("#lookup-button").show();
						}
						
						
					},
					
					
					
					
					scheduleJob : function() {
						var $outbound = {};
						$jobId = $("#job-schedule-modal").attr("jobid");
						$outbound["jobId"] = $jobId;
						$outbound["action"] = "SCHEDULE_JOB";
						
						$.each($("#job-schedule-modal input"), function($index, $val) {
							$outbound[$($val).attr('name')] = $($val).val() 
						});
						
						QUOTEMAINTENANCE.doJobUpdate($jobId, $outbound, QUOTEMAINTENANCE.scheduleJobSuccess, QUOTEMAINTENANCE.scheduleJobErr);
					},
					
					
					scheduleJobErr : function($statusCode) {
						var $messages = {
								403:"Session Expired. Log in and try again",
								404:"System Error Activate 404. Contact Support",
								500:"System Error Activate 500. Contact Support"
						}
						$("#job-schedule-modal").dialog("close");
						$("#globalMsg").html( $messages[$statusCode] );
					},
					
					
					
					scheduleJobSuccess : function($data) {
						console.log($data);
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {	
							$.each($data.data.webMessages, function($index, $val) {
								console.log($index);
								console.log($val);
								var $fieldName = "." + $index + "Err";
								var $selector = "#job-schedule-modal " + $fieldName;
								$($selector).html($val[0]).show().fadeOut(3000);
							});							
						} else {
							QUOTEMAINTENANCE.quote = $data.data.quote;
							$jobId = $data.data.quote.jobDetail.job.jobId;
							var $destination = "#job" + $jobId + " .job-data-row";
							QUOTEMAINTENANCE.populateJobPanel($jobId, $destination, $data.data);
							$("#job-schedule-modal").dialog("close");
							$("#globalMsg").html("Job Scheduled").show().fadeOut(3000);
							
							QUOTEMAINTENANCE.showJobUpdates($data.data);
						}
					}, 
					
					
					
					
					scheduleThisJob : function($jobId) {
	            		console.log("clicked a job scheduleThisJob: " + $jobId);
	            		$("#job-schedule-modal").attr("jobid", $jobId);
	            		$("#job-schedule-modal input").val("");
	            		$("#job-schedule-modal .errMsg").html("");
	            		$("#job-schedule-modal").dialog("open");	            		
					},
					
					
					
					showJobUpdates : function($data) {
						console.log("showJobUpdates");
						console.log(QUOTEMAINTENANCE.quote);
						//repopulate QUOTEMAINTENANCE.quote
						QUOTEMAINTENANCE.quote = $data.quote;
						QUOTEMAINTENANCE.joblist = {};
						QUOTEMAINTENANCE.populateJobHeader($data.quote.jobHeaderList)
						QUOTEMAINTENANCE.makeJobExpansion();
						
						//deletes don't return a job detail -- so check
						if ( $data.quote.jobDetail != null ) {
							QUOTEMAINTENANCE.jobId = $data.quote.jobDetail.job.jobId
							//put job headers in job table
							QUOTEMAINTENANCE.joblist[QUOTEMAINTENANCE.jobId] = $data
							$.each( $("#jobList li .job-hider"), function($index, $value) {
								var $jobid = $($value).attr("data-jobid");
								if ( $jobid == QUOTEMAINTENANCE.jobId ) {
									$($value).click();
								}
							});
						}
						
						QUOTEMAINTENANCE.jobId = null;
					},
					
					
					showQuote : function() {
		            	$("#loading-container").hide();
						$("#quote-container").fadeIn(1000);
						$("#address-container").fadeIn(1000);
						$("#job-list-container").fadeIn(1000);
						$("#quoteButtonContainer").fadeIn(1000);
		            }
				};
				
				QUOTEMAINTENANCE.init();
				
				
        	});
         
        </script>
         
        
        
        <style type="text/css">   
        	#address-container {
        		display:none;
        	}     	
        	#addressPanel {
        		width:100%;
        	}
        	#address-panel-closed {
        		display:none;
        	}
        	#address-panel-open {
        	}
        	#contact-edit-modal .none-found {
        		display:none;
        	}
        	#copy-modal {
        		display:none;
        	}
        	#edit-this-quote {
        		cursor:pointer;
        	}
        	#job-activate-modal {
        		display:none;
        	}
        	#job-cancel-modal {
        		display:none;
        	}
        	#job-edit-modal {
        		display:none;
        	}
        	#job-loading-pattern {
        		display:none;
        	}
        	#job-list-container {
        		display:none;
        	}
        	#job-schedule-modal {
        		display:none;
        	}
        	#jobList li {
        		margin-top:2px;
        	}
			#jobList .job-data-row {
				display:none;
			}
			#jobList .job-data-open {
				display:none;
			}
			#printHistoryDiv {
                display:none;
            }
			#printQuoteDiv {
                display:none;
            }     
			#progressbar .ui-progressbar-value {
				background-color:#C9C9C9;
			}       
            #progress-label {
				position: absolute;
				left:50%;
				top:4px;
				font-weight:bold;
				text-shadow: 1px 1px 0 #fff;
			}
        	#quoteButtonContainer {
        		display:none;
        		float:right;
        		text-align:right;
        	}
        	#quote-container {
        		display:none;
        	}
        	#quoteDataContainer select[name='managerId'] {
        		display:none;
        	}
        	#quote-panel-closed {
        		display:none;
        	}
        	#quote-panel-open {
        	}
			#quotePanel {
				border:solid 1px #000000;
				padding:8px;
				width:1300px;
			}
			
			
			#quote-container .quote-button-container .save-quote {
				display:none;
				cursor:pointer;
			}
	    	#quote-container .quote-button-container .cancel-edit {
				display:none;
				cursor:pointer;
			}		
	    		
	    	#reorder-job-modal {
	    		display:none;
	    	}	
	    			
	    			
            #viewPrintHistory {
                cursor:pointer;
            }
            .action-button-container {
            	margin-top:0px;
            	margin-left:0px;
            	margin-right:0px;
            	margin-bottom:8px;
            	border:0;
            	clear:both;
            }
        	.action-button {
        		cursor:pointer;
        	}
        	.address-button-container {
				float:right; 
				margin-right:8px;
				width:6%; 
				background-color:#e5e5e5; 
				border:solid 1px #404040; 
				text-align:center;
			}
        	.ansi-address-container {
        		width:90%;
        	}
        	.ansi-address-form-label-container {
        		width:125px;
        	}
        	.ansi-contact-container {
        		width:90%;
        	}
        	.ansi-contact-method-is-business-phone { display:none; } 
			.ansi-contact-method-is-mobile-phone { display:none; }
			.ansi-contact-method-is-fax { display:none; }
			.ansi-contact-method-is-email { display:none; }
			.edit-modal { display:none; }
			.edit-this-panel { display:none; }
			.edit-err { 
				background-color:#FF0000; 
				opacity:0.20;
			}
        	.job-data-row { display:none; }
			.jobTitleRow {
				background-color:#404040; 
				cursor:pointer; 
				padding-left:4px;
				color:#FFFFFF;
			}
			.jobTitleRow .panel-button-container .save-job {
				display:none;
			}
			.jobTitleRow .panel-button-container .cancel-job-edit {
				display:none;
			}
			.job-data-closed {
				color:#FFFFFF;
			}
			.job-data-open {
				color:#FFFFFF;
			}
			.job-header-job-div {
				display:inline; 
				margin-right:10px;
			}
			.jobtag {
				border:solid 1px #404040;
				padding:1px;
				spacing:1px;
				-moz-border-radius:3px;
				-webkit-border-radius:3px;
				-khtml-border-radius:3px;
				border-radius:3px;
				cursor:default;
			}
			.jobtag-inactive {
				text-decoration:line-through;
			}
			.jobtag-selected {
				background:#CC6600;
				color:#FFFFFF;
			}
			.panel-button-container {
				float:right; 
				margin-right:8px;
				width:6%; 
				background-color:#e5e5e5; 
				border:solid 1px #404040; 
				text-align:center;
				display:none;
			}
			.pre-edit {
				background-color:#CC6600;
				<%-- border:solid 1px #993300; --%>
			}
			.quote-button {
				margin-top:2px;
				margin-bottom:2px;
				padding-top:2px;
				padding-bottom:2px;
			}
			.quote-button-container {
				float:right; 
				margin-right:8px;
				width:6%; 
				background-color:#e5e5e5; 
				border:solid 1px #404040; 
				text-align:center;
				display:none;
			}
			
			.spacer {
				font-size:1px;
				clear:both;
				width:100%;
			}
			.ui-progressbar {
				position:relative;
			}
        </style>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Quote Maintenance</h1>
    	<%--
    	<div id="loading-container"><webthing:thinking style="width:100%" /></div>
    	<div style="width:1200px;">
    		<div id="progressbar"><div id="progress-label">Loading...</div></div>
    	</div>
    	--%>
    	<div id="loading-container">
    		<div id="progressbar"><div id="progress-label">Loading...</div></div>
    	</div>
    	<div style="width:1300px;">	    	
    		<div id="quoteButtonContainer" style="width:30px;">
    			<%-- <ansi:hasPermission permissionRequired="QUOTE_CREATE"><webthing:edit styleClass="fa-2x quote-button">Edit</webthing:edit></ansi:hasPermission>--%>
 			    <ansi:hasPermission permissionRequired="QUOTE_CREATE"><div class="action-button-container"><webthing:revise styleClass="fa-2x quote-button action-button" styleId="revise-button">Revise</webthing:revise></div></ansi:hasPermission>
    			<ansi:hasPermission permissionRequired="QUOTE_CREATE"><div class="action-button-container"><webthing:copy styleClass="fa-2x quote-button action-button" styleId="copy-button">Copy</webthing:copy></div></ansi:hasPermission>
    			<ansi:hasPermission permissionRequired="QUOTE_READ"><div class="action-button-container"><a href="quoteLookup.html" style="text-decoration:none; color:#404040;"><webthing:view styleClass="fa-2x quote-button" styleId="lookup-button">Lookup</webthing:view></a></div></ansi:hasPermission>
    			<ansi:hasPermission permissionRequired="QUOTE_CREATE"><div class="action-button-container"><a href="newQuote.html"><webthing:addNew styleClass="fa-2x quote-button action-button" styleId="new-quote-button">New Quote</webthing:addNew></a></div></ansi:hasPermission>
    			<ansi:hasPermission permissionRequired="QUOTE_READ"><div class="action-button-container"><webthing:print styleClass="orange fa-2x quote-button action-button" styleId="preview-button">Preview</webthing:print></div></ansi:hasPermission>    			
    			<ansi:hasPermission permissionRequired="QUOTE_PROPOSE"><div class="action-button-container"><webthing:print styleClass="green fa-2x quote-button action-button" styleId="propose-button">Propose</webthing:print></div></ansi:hasPermission>
    			<ansi:hasPermission permissionRequired="QUOTE_CREATE"><div class="action-button-container"><webthing:job styleClass="fa-2x quote-button action-button orange" styleId="new-job-button">New Job</webthing:job></div></ansi:hasPermission>    			
    			<ansi:hasPermission permissionRequired="CALL_NOTE_WRITE"><div class="action-button-container"><webthing:notes styleId="call-note-link" styleClass="fa-2x" xrefType="QUOTE" xrefId="x">Quote Notes</webthing:notes></div></ansi:hasPermission>
    			
    			<%--
    			<input type="button" class="quoteButton" id="buttonModifyQuote" value="Modify" /><br />
    			<input type="button" class="quoteButton" id="buttonCopyQuote" value="Copy" /><br />
    			<input type="button" class="quoteButton" id="buttonNewQuote" value="New" /><br />
    			 --%>
	    	</div>
	    	<div id="address-container">
		    	<div style="color:#FFFFFF; background-color:#404040; cursor:pointer; width:1269px; margin-bottom:1px;">
		    		<div class="address-button-container">
		    			<%-- <webthing:edit styleId="edit-this-address" styleClass="edit-this-panel">Edit</webthing:edit> --%>
		    			<quote:editAddressMenu styleId="edit-this-address" styleClass="edit-this-panel" />
		    		</div>
		    		<div id="address-panel-hider">
			    		Addresses
		    			<div style="float:left; padding-left:4px;">
		    				<span id="address-panel-closed"><i class="fas fa-caret-right" style="color:#FFFFFF;"></i>&nbsp;</span>
		    				<span id="address-panel-open"><i class="fas fa-caret-down" style="color:#FFFFFF;"></i>&nbsp;</span>
		    			</div>
	    			</div>
	    		</div>
		    	<div id="addressPanel" style="width:1269px; float:left;">
		    		<div id="addressContainerJobSite" style="float:right; width:49%; border:solid 1px #404040;">
		    			<quote:addressDisplayPanel label="Job Site" id="address-job-site" />
		    			<div id="jobSiteContactContainer" style="width:80%;">
		    				<quote:addressContact label="Job Contact" id="job-contact" />
		    				<quote:addressContact label="Site Contact" id="site-contact" />
		    			</div>
		    		</div>
		    		<div id="addressContainerBillTo" style="float:left; width:50%; border:solid 1px #404040;">
		    			<quote:addressDisplayPanel label="Bill To" id="address-bill-to" />
		    			<div id="billToContactContainer" style="width:80%;">
		    				<quote:addressContact label="Contract Contact" id="contract-contact" />
		    				<quote:addressContact label="Billing Contact" id="billing-contact" />
		    			</div>
		    		</div>
		    		<div class="spacer">&nbsp;</div>
		    	</div>
	    	</div>  <!-- Address container -->
	    	
	    	
	    	<div id="quote-container" style="width:1260px; clear:left; margin-top:12px;">
	    		<div class="quote-button-container">
	    			<webthing:edit styleId="edit-this-quote" styleClass="edit-this-panel">Edit</webthing:edit>
	    			<webthing:save styleClass="save-quote">Save</webthing:save>
	    			<webthing:ban styleClass="cancel-edit">Cancel</webthing:ban>	    			
	    		</div>
		    	<div id="quote-panel-hider" style="color:#FFFFFF; background-color:#404040; cursor:pointer; width:1269px; margin-bottom:1px;">
		    		Quote
	    			<div style="float:left; padding-left:4px;">
	    				<span id="quote-panel-closed"><i class="fas fa-caret-right" style="color:#FFFFFF;"></i>&nbsp;</span>
	    				<span id="quote-panel-open"><i class="fas fa-caret-down" style="color:#FFFFFF;"></i>&nbsp;</span>
	    			</div>
	    		</div>
		    	<div id="quotePanel" style="width:1251px; clear:left;">
		    		<jsp:include page="quoteMaintenance/quoteDataContainer.jsp">
		    			<jsp:param name="action" value="view" />
		    		</jsp:include>
		    		<div class="spacer">&nbsp;</div>
		    	</div> 
	    	</div>  <!--  quote container -->
	    	
	    	
	    	<div id="job-list-container" style="width:1260px; clear:both; margin-top:12px;">
	    		<ul id="jobList" class="sortable" style="width:100%;">	    			
	    		</ul>
	    	</div> <!--  job list container -->
	    </div>
	    <div class="spacer">&nbsp;</div>
	    
	    
	    <div id="job-loading-pattern">
	    	<div class="job-data-row"><webthing:thinking style="width:100%" /></div>
	    </div>
	    
	    
		<webthing:quotePrint modalName="printQuoteDiv" />
		
	    <webthing:quotePrintHistory modalName="printHistoryDiv" />
	    
	    
	    <ansi:hasPermission permissionRequired="QUOTE_CREATE">
		    <div id="contact-edit-modal" class="edit-modal">		    	
		    	<span class="formLabel">Name:</span> <input type="text" name="contact-name" />
		    	<span class="errMsg err"></span>
		    	<br /><hr /><br />
		    	<quote:addressContact id="contact-edit-display" label="Name" />
		    	<div class="none-found">
		    		<span class="err">No Matching Contacts</span>
		    	</div>
		    </div>
	    </ansi:hasPermission>
	    
	    
	    <jsp:include page="quoteModals.jsp" />
	    
	    
	    <ansi:hasPermission permissionRequired="QUOTE_PROPOSE">
	    	<div id="job-activate-modal" class="edit-modal">
	    		<table>
	    			<tr>
	    				<td><span class="formLabel">Activation Date:</span></td>
	    				<td><input type="text" name="activationDate" class="dateField" /></td>
	    				<td><span class="activationDateErr err errMsg"></span></td>
	    			</tr>
	    			<tr>
	    				<td><span class="formLabel">Start Date:</span></td>
	    				<td><input type="text" name="startDate" class="dateField" /></td>
	    				<td><span class="startDateErr err errMsg"></span><br /></td>
	    			</tr>
	    		</table>
	    	</div>
	    	
	    	
	    	<div id="job-cancel-modal" class="edit-modal">
	    		<table>
	    			<tr>
	    				<td><span class="formLabel">Cancel Reason:</span></td>
	    				<td><input type="text" name="cancelReason"  /></td>
	    				<td><span class="cancelReasonErr err errMsg"></span></td>
	    			</tr>
	    			<tr>
	    				<td><span class="formLabel">Cancel Date:</span></td>
	    				<td><input type="text" name="cancelDate" class="dateField" /></td>
	    				<td><span class="cancelDateErr err errMsg"></span><br /></td>
	    			</tr>
	    		</table>
	    	</div>
	    	
	    	
	    	<div id="job-delete-modal" class="edit-modal">
	    		Are you sure you want to delete this job?
	    	</div>
	    	
	    	
	    	<div id="job-schedule-modal" class="edit-modal">
	    		<table>
	    			<tr>
	    				<td><span class="formLabel">Start Date:</span></td>
	    				<td><input type="text" name="startDate" class="dateField" /></td>
	    				<td><span class="startDateErr err errMsg"></span><br /></td>
	    			</tr>
	    			<tr>
	    				<td><span class="formLabel">Reschedule Reason:</span></td>
	    				<td><input type="text" name="cancelReason"  /></td>
	    				<td><span class="cancelReasonErr err errMsg"></span></td>
	    			</tr>
	    		</table>
	    	</div>
	    </ansi:hasPermission>
	    
	    
	    <ansi:hasPermission permissionRequired="QUOTE_CREATE">
	    <div id="reorder-job-modal">
	    	Reorder Jobs?
	    </div>
	    </ansi:hasPermission>
	    
	    <ansi:hasPermission permissionRequired="QUOTE_CREATE">
	    <script type="text/javascript">
	    $(document).ready(function() {
 			QUOTEMAINTENANCE.makeJobSortLengthCheck();
		});
	    </script>
		</ansi:hasPermission>
	    
	    <div id="copy-modal">
	    	<span style="font-size:110%; font-style:italic;">Copied</span>
	    </div>
	    
	    <webthing:callNoteModals />
    </tiles:put>

</tiles:insert>

