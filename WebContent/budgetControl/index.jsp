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
<%@ taglib tagdir="/WEB-INF/tags/bcr" prefix="bcr" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>




<tiles:insert page="../layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Budget Control
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<link rel="stylesheet" href="css/callNote.css" />
    	<link rel="stylesheet" href="css/accordion.css" />
    	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.9.4/Chart.min.js"></script>
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/ticket.js"></script> 
        <style type="text/css">
        	#bcr_delete_confirmation_modal {
        		display:none;
        	}
        	#bcr_edit_modal {
        		display:none;
        	}
        	#bcr_new_claim_modal {
        		display:none;
        	}
        	#bcr_title_prompt {
        		display:none;
        	}
			#bcr_panels .display {
				display:none;
			}
			#session_expire_modal {
				display:none;
			}
			.action-link {
				cursor:pointer;
			}
			.actual-saving {
				display:none;
			}
        	.aligned-center {
        		text-align:center;
        	}
        	.aligned-right {
        		text-align:right;
        	}
        	.all-ticket-spreadsheet {
        		text-decoration:none;
        	}
        	.bcr_employees_display .column-header {
				font-weight:bold;
			}
			.bcr_employees_display th {
				text-align:right;
			}
			.bcr_employees_display .border-set {
				border-right:solid 1px #404040;
				padding-right:4px;
			}
			.bcr_totals_display .column-header {
				font-weight:bold;
			}
			.bcr_totals_display th {
				text-align:right;
			}
			.button_is_active {
				text-align:center; 
				border:solid 1px #000000; 
				padding:5px; 
				background-color:#CCCCCC;"
			}
			.button_is_inactive {
				text-align:center; 
				border:solid 1px #000000; 
				padding:5px; 
				background-color:#FFFFFF;
			}
			.field-container {
				cursor:pointer;
			}
        	.form-label {
				font-weight:bold;
			}	
			.expenseDetails {
				display:none;
			}	
			.new-bcr {
				cursor:pointer;
			}
			.newExpenseItem {
				display:none;
			}	
			.table-header {
				text-align:center;
				font-weight:bold;
			}
			.ticket-week-display {
				display:none;
			} 
			.ticket-note {
				text-decoration:underline;
				cursor:pointer;
			}
			
			
        	#filter-container {
        		width:402px;
        		float:right;
        	}
        	#ndl-crud-form {
        		display:none;
        		background-color:#FFFFFF;
				color:#000000;
				width:400px;
				padding:15px;
        	}
			
			
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			
			#ticket-modal {
				display:none;	
			}
			.ticket-clicker {
				color:#000000;
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;BUDGETCONTROL = {      
				// these hold the values selected from the init modal
        		divisionId : null,
    			workYear : null, 
    			workWeek : null,
    			workCalendar : null,
    			title : null,   // this holds all of the data from the original "title" call (a duplicate of divId, work year/week/calendar)
    			// this holds the ticket panel datatables
        		ticketTable : {},
        		// these are valid values for posts to bcr/ticket and bcr/ticketClaim
        		changeType : {
        			"CLAIM_WEEK":"claimWeek",
        			"TOTAL_VOLUME":"totalVolume",
        			"DL":"dl",
        			"EXPENSE":"expense"
        		},
        		// these are the valid expense types when adding a new pass-thru expense (populated by init call)
        		expenseTypes : [],
        		lastEmployeeEntered : null,
    			lastNoteEntered : null,
    			lastWorkWeekEntered : null,
        		
        		
        		
        		
        		init : function() {
        			BUDGETCONTROL.makeSelectionLists();
        			BUDGETCONTROL.makeClickers();
        			BUDGETCONTROL.makeAccordion();
        			BUDGETCONTROL.makeEditModal();
        		},
        		
        		
        		bcrError : function($data) {
        			console.log("bcrError");
        			$("#globalMsg").html("Unknown system error. Contact Support").show();
        		},
        		
        		
        		
        		
        		// when total volume or claimed volume is changed, figure out how much is left
        		calculateRemainingVolume : function() {
        			console.log("calculateRemainingVolume");
        			if ( $("#totalVolumeField").val() == null || $("#totalVolumeField").val() == "" ) {
        				$("#totalVolumeField").val("0.00");   // it's a string so we get the pennies
        			} else {
        				$("#totalVolumeField").val( parseFloat($("#totalVolumeField").val()).toFixed(2) );
        			} 
        			if ( $("#volumeClaimedField").val() == null || $("#volumeClaimedField").val() == "" ) {
        				$("#volumeClaimedField").val("0.00");  // it's a string so we get the pennies
        			} else {
        				$("#volumeClaimedField").val( parseFloat($("#volumeClaimedField").val()).toFixed(2) );
        			} 
        			var $totalVolume = parseFloat( $("#totalVolumeField").val() );
        			var $volumeClaimed = parseFloat( $("#volumeClaimedField").val() );
        			var $remainingVolume = $totalVolume - $volumeClaimed;
        			$("#bcr_edit_modal .volumeRemaining").html( $remainingVolume.toFixed(2) );
        		},
        		
        		
        		
        		
        		// when claimed volume or billed is changed, figure out the difference
				calculateDiffClaimedBilled : function() {
					console.log("calculateDiffClaimedBilled");
					if ( $("#billedAmountField").val() == null || $("#billedAmountField").val() == "" ) {
        				$("#billedAmountField").val("0.00");   // it's a string so we get the pennies
        			} else {
        				$("#billedAmountField").val( parseFloat($("#billedAmountField").val()).toFixed(2) );
        			} 
        			if ( $("#volumeClaimedField").val() == null || $("#volumeClaimedField").val() == "" ) {
        				$("#volumeClaimedField").val("0.00");  // it's a string so we get the pennies
        			} else {
        				$("#volumeClaimedField").val( parseFloat($("#volumeClaimedField").val()).toFixed(2) );
        			} 
        			var $billedAmount = parseFloat( $("#billedAmountField").val() );
        			var $volumeClaimed = parseFloat( $("#volumeClaimedField").val() );
        			var $diffClaimeBilled = $volumeClaimed - $billedAmount;
        			$("#bcr_edit_modal .claimedVsBilled").html( $diffClaimeBilled.toFixed(2) );
				},
				
				
				
				
				claimUpdateFail : function($data) {
					console.log("claimUpdateFail");
					$("#bcr_edit_modal .bcr_edit_message").html("");
					$message = "<ul>";
					$.each( $data.data.webMessages, function($index, $value) {
						$message = $message + "<li>" + $value[0] + "</li>";
					});
					$message = $message + "</ul>";
					$("#bcr_edit_modal .bcr_edit_message").html($message);
					$("#bcr_edit_modal .bcr_edit_message").show().fadeOut(6000);
				},
				
				
				
				
				claimWeekEditSuccess : function($data) {
					console.log("claimWeekEditSuccess");
					$("#bcr_edit_modal .bcr_edit_message").html("Update Complete").show().fadeOut(4000);
					BUDGETCONTROL.refreshTicketTables();
				},
				
				
				
				deleteClaim : function() {
					var $claimId = $("#bcr_delete_confirmation_modal").attr("claimId");
					var $claimType = $("#bcr_delete_confirmation_modal").attr("claimType");
					console.log("Delete claim: " + $claimId + " " + $claimType);
					if ( $claimType == "expense") {
						$successFunction = BUDGETCONTROL.expenseSaveSuccess;
					} else if ( $claimType = "labor" ) {
						$successFunction = BUDGETCONTROL.laborSaveSuccess;
					} else {
						$successFunction = "Invalid claim type: " + $claimType
					}

					console.log("deleteClaim: " + $claimId);
					var $url = "bcr/expense/" + $claimId
					
					var $ticketId = $("#bcr_edit_modal").attr("ticketId");
        			var $serviceTagId = $("#bcr_edit_modal").attr("serviceTagId");
        			var $claimWeek = $("#bcr_edit_modal input[name='claimWeek']").val();			
        			
        			// these are needed to create the correct response, not to do the update
        			var $divisionId = BUDGETCONTROL.divisionId
        			var $workYear = BUDGETCONTROL.workYear; 
        			var $workWeeks = BUDGETCONTROL.workWeek;
        			
        			var $outbound = {
        					"divisionId":$divisionId,
        					"ticketId":$ticketId,
                			"serviceTagId":$serviceTagId,
                			"claimWeek":$claimWeek,
                			"workYear":$workYear,
                			"workWeeks":$workWeeks,
        			}
					ANSI_UTILS.doServerCall("DELETE", $url, JSON.stringify($outbound), $successFunction, BUDGETCONTROL.claimUpdateFail);
				},
        		
				
				
				
        		doFunctionBinding : function() {
        			console.log("doFunctionBinding");  
        			// make sure a click only does stuff one time
        			$(".ticket-clicker").off("click");
        			$(".bcr-edit-clicker").off("click");
        			$(".newClaimButton").off("click");
        			$(".zeroClaimButton").off("click");
        			
        			// make sure a click actually does something (but only once)
        			$(".ticket-clicker").on("click", function($clickevent) {
    					$clickevent.preventDefault();
    					var $ticketId = $(this).attr("data-id");
    					TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
    					$("#ticket-modal").dialog("open");
    				});	
        			
        			$(".bcr-edit-clicker").on("click", function($clickevent) {
        				$clickevent.preventDefault();
        				var $claimId = $(this).attr("data-claimid");
        				var $ticketId = $(this).attr("data-ticketid");
        				var $divisionId = $(this).attr("data-divisionid");
        				var $workYear = $(this).attr("data-workYear");
        				var $workWeeks = $(this).attr("data-workWeeks");
        				var $claimId = $(this).attr("data-claimid")
        				var $outbound = {"divisionId":$divisionId, "workYear":$workYear, "workWeeks":$workWeeks, "claimId":$claimId};
        				var $url = "bcr/ticketClaim/" + $claimId;
        				ANSI_UTILS.doServerCall("GET", $url, $outbound, BUDGETCONTROL.getTicketDetailSuccess, BUDGETCONTROL.getTicketDetailFail);
        			});
        			
        			$(".newClaimButton").on("click", function($clickevent) {
        				$clickevent.preventDefault();
        				var $ticketId = $(this).attr("data-ticketid");
        				var $serviceTypeId = $(this).attr("data-servicetypeid");
        				var $serviceTagId = $(this).attr("data-servicetagid");
        				console.log("New claim: " + $ticketId + " " + $serviceTypeId + " " + $serviceTagId); 
        				var $url = "ticket/" + $ticketId;
        				ANSI_UTILS.doServerCall("GET", $url, $outbound, BUDGETCONTROL.getClaimPrefillSuccess, BUDGETCONTROL.getClaimPrefillFail, null, {"ticketId":$ticketId, "serviceTypeId":$serviceTypeId, "serviceTagId":$serviceTagId});
        			});

        			$(".zeroClaimButton").on("click", function($clickevent) {
        				$clickevent.preventDefault();
        				var $ticketId = $(this).attr("data-ticketid");
        				var $serviceTypeId = $(this).attr("data-servicetypeid");
        				var $serviceTagId = $(this).attr("data-servicetagid");
        				console.log("zero claim: " + $ticketId + " " + $serviceTypeId + " " + $serviceTagId); 
        				var $url = "ticket/" + $ticketId;
        				ANSI_UTILS.doServerCall("GET", $url, $outbound, BUDGETCONTROL.getZerofillSuccess, BUDGETCONTROL.getClaimPrefillFail, null, {"ticketId":$ticketId, "serviceTypeId":$serviceTypeId, "serviceTagId":$serviceTagId});
        			});

        		},
        		



        		
        		doMonthlyFilter : function($destination, $url, $outbound) {
        			console.log("doMonthlyFilter ")
        			var $key = 'monthlyFilter';
        			$($destination).DataTable().destroy(false);
        			if ( $key in $outbound ) {
        				if ( $outbound[$key]=="true") {
        					$outbound[$key] = "false";
        				} else {
        					$outbound[$key] = "true";
        				}
        			} else {
        				$outbound[$key] = "true";
        			}
        			BUDGETCONTROL.doTicketLookup($destination, $url, $outbound);
        		},
        		
        		
        		
        		
        		doTicketLookup : function($destination, $url, $outbound) {
        			var $weekNum = $outbound['workWeek'];
        			        			
        			
        			if ( $weekNum==null || $weekNum=='') {
        				$fileName = "All_Tickets_" + BUDGETCONTROL.title.div + "_" + BUDGETCONTROL.title.workYear + "_" + BUDGETCONTROL.workWeek  ;
        			} else {
        				$fileName = "Weekly_Tickets_ " + BUDGETCONTROL.title.div + "_" + BUDGETCONTROL.title.workYear + "_" + $weekNum;
        			}
        			console.log("doTicketLookup " + $fileName + "  " + $destination);
        			
        			var $buttonArray = [
        	        	'pageLength',
        	        	'copy', 
        	        	{extend:'csv', filename:'* ' + $fileName}, 
        	        	{extend:'excel', filename:'* ' + $fileName}, 
        	        	{extend:'pdfHtml5', orientation: 'landscape', filename:'* ' + $fileName}, 
        	        	'print',
        	        	{extend:'colvis', label: function () {doFunctionBinding();$('#ticketTable').draw();}}
        	        ];
        			
        			if ( $weekNum==null || $weekNum=='') {
        				$buttonArray.push({ 
        					text:'Current Month', 
        					action: function(e, dt, node, config) { 
        						BUDGETCONTROL.doMonthlyFilter($destination, $url, $outbound);
       						} 
        				});
        			}
        			
        			
        			
        			
        			var $showClaimModal = []
        			$.each($outbound.workWeeks.split(","), function($index, $value) {
        				if ( $value < 10 ) {
        					$displayValue = "0" + $value;
        				} else {
        					$displayValue = $value;
        				}
        				$showClaimModal.push($outbound.workYear + "-" + $displayValue)
        			});
        			
        			var $jobEditTag = '<webthing:edit>No Services Defined. Revise the Job</webthing:edit>';
        			
        			BUDGETCONTROL.ticketTable[$destination] = $($destination).DataTable( {
            			"aaSorting":		[[0,'asc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        "pageLength":		50,
            	        rowId: 				'dt_RowId',
            	        destroy : 			true,		// this lets us reinitialize the table
            	        dom: 				'Bfrtip',
            	        "searching": 		true,
            	        "searchDelay":		800,
            	        lengthMenu: [
            	        	[ 10, 50, 100, 500, 1000 ],
            	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
            	        ],
            	        buttons: $buttonArray,
            	        "columnDefs": [
             	            { "orderable": true, "targets": -1 },
             	            { className: "dt-head-center", "targets":[0,1,2,3,4,5,6,7,8,9,10,11,12,13]},
            	            { className: "dt-left", "targets": [0,1,2,3,4,9,13] },
            	            { className: "dt-center", "targets": [12] },
            	            { className: "dt-right", "targets": [5,6,7,8,10,11]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": $url,
    			        	"type": "POST",
    			        	"data": $outbound,
    			        	},
    			        columns: [
    			        	{ title: "Account", width:"15%", searchable:true, "defaultContent": "<i>N/A</i>", data:'job_site_name' }, 
    			            { title: "Ticket Number", width:"6%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.ticket_id != null){return ('<a href="#" data-id="'+row.ticket_id+'" class="ticket-clicker">'+row.ticket_id+'</a>');}
    			            } },
    			            { title: "Claim Week", width:"5%", searchable:true, searchFormat: "nnnn-nn", 
    			            	data: function ( row, type, set ) {
    			            		var $claimWeek = "";
    			            		var $display = "";
    			            		
    			            		if (row.claim_id != null ) {
    			            			$claimWeek = row.claim_week;
    			            			if ( $showClaimModal.includes(row.claim_week) ) {
    			            				$claimWeek = '<a href="#" class="bcr-edit-clicker" data-claimid="'+row.claim_id+'" data-ticketid="'+row.ticket_id+'" data-workyear="'+$outbound['workYear']+'" data-workweeks="'+$outbound['workWeeks']+'" data-divisionid="'+$outbound['divisionId']+'">'+row.claim_week+'</a>';
    			            			}
    			            		}

									if ( row.service_tag_id == null ) {
										$addButton = "";
									} else {
    			            			$addButton = '<span class="newClaimButton" data-ticketid="'+row.ticket_id+'" data-servicetypeid="'+row.service_type_id+'" data-servicetagid="'+row.service_tag_id+'"><webthing:addNew>New Claim</webthing:addNew></span>';
									}
									var $zeroButton = '<span class="zeroClaimButton" data-ticketid="'+row.ticket_id+'" data-servicetypeid="'+row.service_type_id+'" data-servicetagid="'+row.service_tag_id+'"><webthing:zero>Zero Claim</webthing:zero></span>';
    			            		return $claimWeek + $addButton + $zeroButton;
    			            	} 
    			            },
    			            { title: "D/L", width:"6%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.dl_amt != null){return (row.dl_amt.toFixed(2)+"");}
    			            } },
    			       //     { title: "+Exp", width:"6%", searchable:true, searchFormat: "First Last Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			       //     	//if(row.ansi_contact != null){return (row.ansi_contact+"");}
    			       //     	return '<input type="text" style="width:20px;"/>';
    			       //     } },
    			       //     { title: "Total D/L", width:"6%", searchable:true, searchFormat: "YYYY-MM-dd hh:mm", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			       //     	//if(row.start_time != null){return (row.start_time+"");}
    			       //     	return 'x';
    			       //     } },
    			            { title: "Total Volume",  width:"6%", searchable:true, searchFormat: "Type Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.total_volume != null){return (row.total_volume.toFixed(2)+"");}
    			            } },		
    			            { title: "Volume Claimed",  width:"6%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.volume_claimed != null){return (row.volume_claimed.toFixed(2)+"");}
    			            } },
    			      //      { title: "Volume Remaining",  width:"6%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			      //      	if(row.volume_remaining != null){return (row.volume_remaining.toFixed(2)+"");}
    			      //      } },
    			      //      { title: "Expense Volume", width:"6%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			      //      	if(row.dl_expenses != null){return (row.dl_expenses.toFixed(2)+"");}
    			      //      } },
        			        { title: "Expense Volume", width:"6%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
        			          	if(row.passthru_volume != null){return (row.passthru_volume.toFixed(2)+"");}
    			            } },
    			            { title: "Volume Remaining",  width:"6%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.volume_remaining != null){return (row.volume_remaining.toFixed(2)+"");}
    			            } },
							{ title: "Notes",  width:"10%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	var $displayNote = '';
    			            	if(row.notes != null && row.notes != ''){$displayNote = '<span class="tooltip ticket-note">'+row.notes_display+'<span class="tooltiptext">'+row.notes+'</span></span>';}
    			            	return $displayNote;
    			            } },
    			            { title: "Billed Amount",  width:"6%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.billed_amount != null){return (row.billed_amount.toFixed(2)+"");}
    			            } },
    			            { title: "Diff Clm/Bld",  width:"6%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.claimed_vs_billed != null){return (row.claimed_vs_billed.toFixed(2)+"");}
    			            } },
    			            { title: "Ticket Status",  width:"4%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.ticket_status != null){return (row.ticket_status+"");}
    			            } },
    			            { title: "Service",  width:"4%", searchable:true, searchFormat: "Name #####", 
    			            	data: function ( row, type, set ) {
    			            		if ( row.service_tag_id == null ) {
    			            			$display = '<a href="jobMaintenance.html?id=' + row.job_id +'">' + $jobEditTag + '</a>';
    			            		} else {
    			            			$display = row.service_tag_id;
    			            		}
    			            		return $display;
    			            	} 
    			            },
    			            { title: "Equipment",  width:"4%", searchable:true, searchFormat: "Equipment #####", 
    			            	data: function ( row, type, set ) {
    			            		var $display = [];
    			            		if ( row.equipment_tags != null ) {
	    			            		var $equipmentList = row.equipment_tags.split(",");
	    			            		var $unclaimedList = row.unclaimed_equipment.split(",");
	    			            		$.each($equipmentList, function($index, $value) {
	    			            			if ( $unclaimedList.includes($value) ) {
	    			            				$display.push('<span class="jobtag-display">'+ $value + '</span>');
	    			            			} else {
	    			            				$display.push('<span class="jobtag-display jobtag-selected">'+ $value + '</span>');
	    			            			}
	    			            		});
    			            		}
    			            		return $display.join("");
    			            	}
    			            },
    			            { title: "Employee",  width:"13%", searchable:true, searchFormat: "Name #####", data:'employee' }
    			            ],
    			            "initComplete": function(settings, json) {
    			            	var myTable = this;
    			            	//LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#ticketTable", CALL_NOTE_LOOKUP.makeTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	BUDGETCONTROL.doFunctionBinding();
    			            }
    			    } );
        		},
        		
        		
        		expenseSave : function() {
        			console.log("expenseSave");
        			var $ticketId = $("#bcr_edit_modal").attr("ticketId");
        			var $serviceTagId = $("#bcr_edit_modal").attr("serviceTagId");
        			var $claimWeek = $("#bcr_edit_modal input[name='claimWeek']").val();
        			var $volume = $("#bcr_edit_modal input[name='expenseVolume']").val();
        			var $expenseType = $("#bcr_edit_modal select[name='expenseType']").val();
        			var $notes = $("#bcr_edit_modal input[name='notes']").val();
        			
        			// these are needed to create the correct response, not to do the update
        			var $divisionId = BUDGETCONTROL.divisionId
        			var $workYear = BUDGETCONTROL.workYear; 
        			var $workWeeks = BUDGETCONTROL.workWeek;
        			
        			var $outbound = {
        					"divisionId":$divisionId,
        					"ticketId":$ticketId,
                			"serviceTagId":$serviceTagId,
                			"claimWeek":$claimWeek,
                			"volume":$volume,
                			"expenseType":$expenseType,
                			"notes":$notes,
                			"workYear":$workYear,
                			"workWeeks":$workWeeks,
        			}
        			ANSI_UTILS.doServerCall("POST", "bcr/expense", JSON.stringify($outbound), BUDGETCONTROL.expenseSaveSuccess, BUDGETCONTROL.claimUpdateFail);
        		},
        		
        		
        		expenseSaveSuccess : function($data) {
        			console.log("expenseSaveSuccess");
        			BUDGETCONTROL.getTicketDetailSuccess($data);
        			BUDGETCONTROL.refreshPanels($data);
        			$("#bcr_delete_confirmation_modal").dialog("close");
        			$("#bcr_edit_modal .bcr_edit_message").html("Update Successful");
        			$("#bcr_edit_modal .bcr_edit_message").show().fadeOut(6000);
        		},
        		
        		
        		
        		getClaimPrefillFail : function($data) {
        			console.log("getClaimPrefillFail");
        			$("#globalMsg").html("System Error retrieving ticket detail. Contact Support")
        		},
        		
        		
        		getClaimPrefillSuccess : function($data, $passThruData) {
        			console.log("getClaimPrefillSuccess");
        			var $ticketId = $passThruData["ticketId"];
        			var $serviceTypeId = $passThruData["serviceTypeId"];
        			var $serviceTagId = $passThruData["serviceTagId"];
        			var $actDlAmt = $data.data.ticketDetail.actDlAmt.replace("$","").replace(",","");
        			var $actPricePerCleaning = $data.data.ticketDetail.actPricePerCleaning.replace("$","").replace(",","");
        			
    				$("#bcr_new_claim_modal input").val("");
    				$("#bcr_new_claim_modal select").val("");
    				$("#bcr_new_claim_modal .err").html("");
    				$("#bcr_new_claim_modal input[name='ticketId']").val($ticketId);
    				$("#bcr_new_claim_modal input[name='dlAmt']").val($actDlAmt);
    				$("#bcr_new_claim_modal input[name='volumeClaimed']").val($actPricePerCleaning);
    				$("#bcr_new_claim_modal .ticketId").html($ticketId);
    				$("#bcr_new_claim_modal input[name='serviceTypeId']").val($serviceTypeId);
    				$("#bcr_new_claim_modal .serviceTagId").html($serviceTagId);
    				
    				if ( BUDGETCONTROL.lastEmployeeEntered != null ) {
    					$("#bcr_new_claim_modal input[name='employee']").val(BUDGETCONTROL.lastEmployeeEntered);
    				}
    				if ( BUDGETCONTROL.lastNoteEntered != null ) {
    					$("#bcr_new_claim_modal input[name='notes']").val(BUDGETCONTROL.lastNoteEntered);
    				}
    				
    				if ( BUDGETCONTROL.lastWorkWeekEntered != null ) {
        				var $thisMonth = false;
        				$.each( $("#bcr_new_claim_modal select[name='claimWeek'] option"), function($index, $value) {
        					if ( $value.value == BUDGETCONTROL.lastWorkWeekEntered ) {
        						$thisMonth = true;
        					}
        				});
        				if ( $thisMonth == true ) {
    						$("#bcr_new_claim_modal select[name='claimWeek']").val(BUDGETCONTROL.lastWorkWeekEntered);
        				}
    				}

    				
    				// this bit handles the display/hide of panels in the new claim modal    				
					$("#bcr_new_claim_modal .err").html("");

    				$("#bcr_new_claim_modal").dialog("open");
    				$("#bcr_new_claim_modal .directLaborDetail select[name='claimWeek']").focus();
        		},
        		
        		
        		
        		getTicketDetailSuccess : function($data) {
					console.log("getTicketDetailSuccess");  
										
					var $select = $("#bcr_edit_modal input[name='claimWeek']");
					$($select).attr("data-ticketid",$data.data.ticket.ticketId);
					$($select).attr("data-oldclaimweek",$data.data.claimWeek);
					$($select).attr("data-changetype",BUDGETCONTROL.changeType["CLAIMWEEK"]);
					
					$("#bcr_edit_modal .err").html("");
					
					$("#bcr_edit_modal").dialog( "option", "title", "Ticket Claim: " + $data.data.ticket.ticketId + " (" + $data.data.ticket.status + ")  " + $data.data.ticket.jobSiteName + ", Service Type: " + $data.data.ticket.serviceTagAbbrev);
					
					$("#bcr_edit_modal .totalVolume").html($data.data.ticket.totalVolume.toFixed(2));
	            	//$("#div-summary .total-volume").html( $data.data.ticket.totalVolume.toFixed(2) );


	            	// set attributes so in-modal updates can happen (stash the values for later use)
	            	$("#bcr_edit_modal").attr("ticketId",$data.data.ticket.ticketId);
	            	$("#bcr_edit_modal").attr("serviceTagId",$data.data.ticket.serviceTagId);
	            	
	            	BUDGETCONTROL.makeDlLaborTable($data);
					BUDGETCONTROL.makeDlExpenseTable($data);	
					BUDGETCONTROL.makeSummaryPanel($data);
						
						            	
       				$("#bcr_edit_modal input[name='claimWeek']").val($data.data.claimWeek);
       				$("#bcr_edit_modal .claim-week").html($data.data.claimWeek);

        			var $select = $("#bcr_edit_modal select[name='expenseType']");
					$('option', $select).remove();
					$select.append(new Option("",""));
					$.each(BUDGETCONTROL.expenseTypes, function(index, val) {
					    $select.append(new Option(val.displayValue, val.value));
					});	
					

        			$("#bcr_edit_modal").dialog("open");
        		}, 
        		
        		
        		
        		getTicketDetailFail : function($data) {
					console.log("getTicketDetailFail");        			
					$("#globalMsg").html("Invalid system state. Reload and try again").show();					
        		},
        		
        		
        		
        		getZerofillSuccess : function($data, $passThruData) {
        			console.log("getClaimPrefillSuccess");
        			var $ticketId = $passThruData["ticketId"];
        			var $serviceTypeId = $passThruData["serviceTypeId"];
        			var $serviceTagId = $passThruData["serviceTagId"];
        			var $actDlAmt = $data.data.ticketDetail.actDlAmt.replace("$","").replace(",","");
        			var $actPricePerCleaning = $data.data.ticketDetail.actPricePerCleaning.replace("$","").replace(",","");
        			
    				$("#bcr_new_claim_modal input").val("");
    				$("#bcr_new_claim_modal select").val("");
    				$("#bcr_new_claim_modal .err").html("");
    				$("#bcr_new_claim_modal input[name='ticketId']").val($ticketId);
    				$("#bcr_new_claim_modal input[name='dlAmt']").val("0.00");
    				$("#bcr_new_claim_modal input[name='volumeClaimed']").val("0.00");
    				$("#bcr_new_claim_modal .ticketId").html($ticketId);
    				$("#bcr_new_claim_modal input[name='serviceTypeId']").val($serviceTypeId);
    				$("#bcr_new_claim_modal .serviceTagId").html($serviceTagId);
    				
    				if ( BUDGETCONTROL.lastEmployeeEntered != null ) {
    					$("#bcr_new_claim_modal input[name='employee']").val(BUDGETCONTROL.lastEmployeeEntered);
    				}
    				if ( BUDGETCONTROL.lastNoteEntered != null ) {
    					$("#bcr_new_claim_modal input[name='notes']").val(BUDGETCONTROL.lastNoteEntered);
    				}
    				
    				if ( BUDGETCONTROL.lastWorkWeekEntered != null ) {
        				var $thisMonth = false;
        				$.each( $("#bcr_new_claim_modal select[name='claimWeek'] option"), function($index, $value) {
        					if ( $value.value == BUDGETCONTROL.lastWorkWeekEntered ) {
        						$thisMonth = true;
        					}
        				});
        				if ( $thisMonth == true ) {
    						$("#bcr_new_claim_modal select[name='claimWeek']").val(BUDGETCONTROL.lastWorkWeekEntered);
        				}
    				}

    				
    				// this bit handles the display/hide of panels in the new claim modal    				
					$("#bcr_new_claim_modal .err").html("");

    				$("#bcr_new_claim_modal").dialog("open");
    				$("#bcr_new_claim_modal .directLaborDetail select[name='claimWeek']").focus();
        		},
        		
        		
        		
        		initializeActualDLPanel : function($data) {
        			console.log("initializeActualDLPanel");
        			var $weekLabel = ["1st","2nd","3rd","4th","5th"];

        			$("#actual_dl_totals tbody").html("");
        			$.each($data.data.workCalendar, function($index, $value) {
						var $row = $("<tr>");
						var $label = $("<td>").append($weekLabel[$index] + " Wk in Mo");
						var $week = $("<td>").append($value.weekOfYear);
						var $firstOfWeek = $("<td>").append($value.firstOfWeek);
						$firstOfWeek.addClass("aligned-center");
						var $lastOfWeek = $("<td>").append($value.lastOfWeek);
						$lastOfWeek.addClass("aligned-center");
						var $actualDL = $('<input type="text" />');
						$actualDL.attr("name","actualDL-" + $value.weekOfYear);	
						$actualDL.addClass("actualDL");
						$actualDL.attr("data-week",$value.weekOfYear);
						$actualDL.attr("data-year",$data.data.workYear);
						$actualDL.val("0.00");
						var $omDL = $('<input type="text" />');
						$omDL.attr("name","omDL-" + $value.weekOfYear);	
						$omDL.addClass("omDL");
						$omDL.attr("data-week",$value.weekOfYear)
						$omDL.attr("data-year",$data.data.workYear);
						$omDL.val("0.00");
						$row.append($label);
						$row.append($week);
						$row.append($firstOfWeek);
						$row.append($lastOfWeek);
						$row.append( $("<td>").append($actualDL));
						$row.append( $("<td>").append($omDL));
						var $workingClass = "actual-saving actual-working-week" + $value.weekOfYear; 
						var $doneClass = "actual-saving actual-done-week" + $value.weekOfYear;
						var $errClass = "err" + $value.weekOfYear;
						var $messageTdText = '<webthing:working styleClass="'+$workingClass+'"/>';
						$messageTdText = $messageTdText + '<webthing:checkmark  styleClass="'+$doneClass+'">Success</webthing:checkmark>';
						$messageTdText = $messageTdText + '<span class="err ' + $errClass + '" style="display:none;">Invalid Value</span>';
						$row.append( $("<td>").append($messageTdText));
						$("#actual_dl_totals tbody").append($row);
					});
        			
        		
        			$("#actual_dl_totals input").blur(function($event) {
        				var $that = $(this);
        				
        				var $claimWeek = $that.attr("data-week");
        				var $claimYear = $that.attr("data-year");            				
        				var $divisionId = $that.attr("data-divisionid");
        				var $claimWeeks = $that.attr("data-claimweeks");        				
        				var $previousValue = $that.attr("data-previous");
        				var $workingSelector = "#actual_dl_totals .actual-working-week" + $claimWeek;
        				var $errSelector = "#actual_dl_totals .err" + $claimWeek;
        				
        				var $value = $that.val().trim().replace(/,/g,"");
        				if ( $value == '' || $value == null) {
        					$value = "0.00";
    					}
        				if ( isNaN($value) ) {
        					$($errSelector).show().fadeOut(3000);
        				} else {        					
	       					$value = parseFloat($value).toFixed(2);
	        				$that.val( $value );
	        				
	        				// only do updates if the number has changed
	        				if ( $value != $previousValue ) {
	        					console.log("Value changed -- do a save: " + $value + " " + $previousValue);        					

	        					
	        					$($workingSelector).show();
	        					var $outbound = {"claimWeek":$claimWeek, "claimYear":$claimYear, "value":$value, "divisionId":$divisionId, "claimWeeks":$claimWeeks};
	        					if ( $that.hasClass("actualDL") ) {
	        						var $selector = "#bcr_totals .actual_dl_week" + $claimWeek;
	        						var $looper = "#bcr_totals .actual_dl";
	        						var $totalCell = "#bcr_totals .actual_dl_total";
	        						$outbound['type'] = "actualDL";
	        					} else if ( $that.hasClass("omDL") ) {
	        						var $selector = "#bcr_totals .actual_om_dl_week" + $claimWeek;
	        						var $looper = "#bcr_totals .actual_om_dl";
	        						var $totalCell = "#bcr_totals .actual_om_dl_total";
	        						$outbound['type'] = 'omDL';
	        					} else {
	        						//$("#globalMsg").html("Invalid system state. Reload and try again").show();
	        						// we're just going to ignore this
	        					}
	        					console.log($outbound);
	        					ANSI_UTILS.doServerCall("POST", "bcr/actualDL", JSON.stringify($outbound), BUDGETCONTROL.updateActualSuccess, BUDGETCONTROL.updateActualFail);
	        					$($selector).html($value);
	        					var $rowTotal = 0.00;
	        					$.each( $($looper), function($index, $value) {
	        						$rowTotal = $rowTotal + parseFloat($($value).html());
	        					});
	        					$($totalCell).html($rowTotal.toFixed(2));
	        				}
        				}
        			});
        		},
        		
        		
        		
        		initializeBudgetControlTotalsPanel : function($data) {
        			$("#bcr_totals .bcr_totals_display thead").html("");
        			$("#bcr_totals .bcr_totals_display tbody").html("");
        			
        			
        			var $headerRow1 = $("<tr>");
					$headerRow1.append($("<td>").append("&nbsp;"));
					$headerRow1.append($("<td>").append("&nbsp;")); 
					var $headerRow2 = $("<tr>");
					$headerRow2.append($("<td>").addClass("column-header").append("Week:"));
					$headerRow2.append($("<td>").addClass("column-header").addClass("aligned-right").append("Unclaimed")); 
					$.each($data.data.workCalendar, function($index, $value) {
						var $startDate = $value.firstOfWeek.substring(0, 5);
						var $endDate = $value.lastOfWeek.substring(0, 5);
						var $dates = $("<td>").append($startDate + "-" + $endDate);
						$dates.addClass("aligned-right");
						$headerRow1.append($dates);

						var $week = $("<td>").addClass("column-header").append($value.weekOfYear);
						$week.addClass("aligned-right");
						$headerRow2.append($week);
					});
					$headerRow1.append( $("<th>").append("Month") );
					$headerRow2.append( $("<th>").append("Total") );
					$("#bcr_totals .bcr_totals_display thead").append($headerRow1);
					$("#bcr_totals .bcr_totals_display thead").append($headerRow2);
					
					
					var $bcRowLabels = [
							{"label":"Volume Claimed:","className":"volume_claimed", "column1":"n/a"},
							{"label":"Claimed Volume Remaining:","className":"claimed_volume_remaining", "column1":"n/a"},
							{"label":"<break>"},
							{"label":"Total Billed:","className":"total_billed", "column1":"n/a"},
							{"label":"Variance:","className":"variance", "column1":"n/a"},
							{"label":"<break>"},
							{"label":"Total D/L Claimed:","className":"total_dl_claimed", "column1":"n/a"},
							{"label":"Actual D/L:","className":"actual_dl", "column1":"n/a"},
							{"label":"Actual OM D/L:","className":"actual_om_dl", "column1":"n/a"},
							{"label":"Total Actual D/L:","className":"total_actual_dl", "column1":"n/a"},
							{"label":"<break>"},
							{"label":"D/L Percentage:", "className":"dl_percentage", "column1":"&nbsp;"},
							{"label":"Actual D/L Percentage:", "className":"actual_dl_percentage", "column1":"&nbsp;"},
						];
					
					var $totalVolRow = $("<tr>");					
					$totalVolRow.append( $("<td>").append("Total Volume:") );
					$totalVolRow.append( $("<td>").addClass("aligned-right").append("0.00") );
					$.each($data.data.workCalendar, function($index, $value) {
						$totalVolRow.append( $("<td>").addClass("aligned-right").addClass("total_volume").addClass("week"+$value.weekOfYear).addClass("total_volume_week"+$value.weekOfYear).append("0.00") );
					});
					$totalVolRow.append( $("<td>").addClass("aligned-right").addClass("total_volume_total").append("0.00") );
					$("#bcr_totals .bcr_totals_display tbody").append($totalVolRow);
					
					$.each($bcRowLabels, function($index, $value) {
						var $bcRow = $("<tr>");
						if ( $value['label'] == "<break>") {
							$bcRow.append( $('<td colspan="6">').append("&nbsp;") );
						} else {
							$bcRow.append( $("<td>").append($value['label']) );
							$bcRow.append( $("<td>").addClass("aligned-right").append($value['column1']) );
							$.each($data.data.workCalendar, function($index, $calendarValue) {
								var $className = $value['className']+"_"+"week"+$calendarValue.weekOfYear;								
								$bcRow.append( $("<td>").addClass($value['className']).addClass('week'+$calendarValue.weekOfYear).addClass($value['className']+"_"+'week'+$calendarValue.weekOfYear).addClass("aligned-right").addClass($className).append("0.00") );
							});
							$bcRow.append( $("<td>").addClass("aligned-right").addClass($value['className']+'_total').append("0.00") );
						}
						$("#bcr_totals .bcr_totals_display tbody").append($bcRow);
					});
					
        		},
        		
        		
        		
        		
        		initializeEmployeePanel : function($data) {
        			$("#bcr_employees .bcr_employees_display thead").html("");
        			$("#bcr_employees .bcr_employees_display tbody").html("");
        			$("#bcr_employees .bcr_employees_display tfoot").html("");
        			
        			var $headerRow1 = $("<tr>");
					$headerRow1.append($("<td>").append("&nbsp;"));
					$headerRow1.append($("<td>").append("&nbsp;")); 
					var $headerRow2 = $("<tr>");
					$headerRow2.append($("<td>").addClass("column-header").append("Week:"));
					$headerRow2.append($("<td>").addClass("column-header").addClass("aligned-right").append("&nbsp;")); 
					$headerRow3 = $("<tr>");
					$headerRow3.append($("<td>").append("&nbsp;"));
					$headerRow3.append($("<td>").append("&nbsp;")); 
					var $footerRow = $("<tr>");
					$footerRow.append( $("<td>").append("Total Assigned D/L - All Employees"));
					$footerRow.append( $("<td>").append("&nbsp;"));  // spacer to account for unclaimed column
					
					$.each($data.data.workCalendar, function($index, $value) {
						var $startDate = $value.firstOfWeek.substring(0, 5);
						var $endDate = $value.lastOfWeek.substring(0, 5);
						var $dates = $("<td>").append($startDate + "-" + $endDate);
						$dates.addClass("aligned-center");
						$dates.addClass("border-set");
						$dates.attr("colspan","2");
						$headerRow1.append($dates);

						var $week = $("<td>").addClass("column-header").append($data.data.workYear + "-" + $value.weekOfYear);
						$week.addClass("aligned-center");
						$week.addClass("border-set");
						$week.attr("colspan",2);
						$headerRow2.append($week);
						
						$headerRow3.append('<td class="column-header aligned-center">Volume</td><td class="column-header aligned-center border-set">D/L</td>')
						
						$footerRow.append( $("<td>").addClass("aligned-right").append("0.00") ); // claimed vol
						$footerRow.append( $("<td>").addClass("aligned-right").append("0.00") ); // claimed dl
					});
					$headerRow1.append( $('<td colspan="2" class="column-header aligned-center">').append("Month") );
					$headerRow2.append( $('<td colspan="2" class="column-header aligned-center">').append("Total") );
					$headerRow3.append('<td class="column-header aligned-center">Volume</td><td class="column-header aligned-center">D/L</td>')
					$("#bcr_employees .bcr_employees_display thead").append($headerRow1);
					$("#bcr_employees .bcr_employees_display thead").append($headerRow2);
					$("#bcr_employees .bcr_employees_display thead").append($headerRow3);
					
					
					$footerRow.append( $("<td>").addClass("aligned-right").append("0.00") );// claimed vol
					$footerRow.append( $("<td>").addClass("aligned-right").append("0.00") );// claimed dl
					$("#bcr_employees .bcr_employees_display tfoot").append($footerRow);

					
        		},

        		keepAliveExpired : function($data) {
        			console.log("keepAliveExpired");
        			$("#globalMsg").html("Session expired").show();
        			$("#session_expire_modal").dialog("open");
        		},
        		
        		keepAliveFail : function($data) {
        			$("#globalMsg").html("Session Error. Contact Support").show();
        		},
        		
        		keepAliveSuccess : function($data) {
        			console.log("keepAliveSuccess");
        			
        		},
        		
        		
        		
        		
        		
        		laborSave : function() {
        			console.log("laborSave");
        			var $ticketId = $("#bcr_edit_modal").attr("ticketId");
        			var $serviceTagId = $("#bcr_edit_modal").attr("serviceTagId");
        			var $claimWeek = $("#bcr_edit_modal input[name='claimWeek']").val();
        			
        			var $dlAmt = $("#bcr_edit_modal input[name='dlAmt']").val();
        			var $volumeClaimed = $("#bcr_edit_modal input[name='volumeClaimed']").val();
        			var $employee = $("#bcr_edit_modal input[name='employee']").val();
        			var $notes = $("#bcr_edit_modal input[name='laborNotes']").val();        			
        			
        			var $selectedEquipment = [];
        			$.each( $("#bcr_edit_modal .equipment-claim.jobtag-selected"), function($index, $value) {
        				$tagId = $(this).attr("data-tagid");
        				$selectedEquipment.push($tagId);
        			});
        			
        			// these are needed to create the correct response, not to do the update
        			var $divisionId = BUDGETCONTROL.divisionId
        			var $workYear = BUDGETCONTROL.workYear; 
        			var $workWeeks = BUDGETCONTROL.workWeek;
        			
        			var $outbound = {
        					"divisionId":$divisionId,
        					"ticketId":$ticketId,
                			"serviceTagId":$serviceTagId,
                			"claimWeek":$claimWeek,
                			"dlAmt":$dlAmt,
                			"volumeClaimed":$volumeClaimed,
                			"employee":$employee,
                			"notes":$notes,
                			"workYear":$workYear,
                			"workWeeks":$workWeeks,
                			"claimedEquipment":$selectedEquipment.join(","),
        			}
        			console.log($outbound);
        			ANSI_UTILS.doServerCall("POST", "bcr/ticketClaim", JSON.stringify($outbound), BUDGETCONTROL.expenseSaveSuccess, BUDGETCONTROL.claimUpdateFail);
        		},
        		
        		
        		laborSaveSuccess : function($data) {
        			console.log("laborSaveSuccess");
        			// this handles expense adds:
        			//var $volume = $("#bcr_edit_modal input[name='expenseVolume']").val("");
        			//var $expenseType = $("#bcr_edit_modal select[name='expenseType']").val("");
        			//var $notes = $("#bcr_edit_modal input[name='notes']").val("");	
        			//$("#bcr_edit_modal .newExpenseItem").hide();
					//$("#bcr_edit_modal .displayExpenseItem").fadeIn(500);
					// this handles expense deletes
					$("#bcr_delete_confirmation_modal").dialog("close");
					// this handles displaying the new data
					$("#bcr_edit_modal .bcr_edit_message").html("Update Successful");
					BUDGETCONTROL.makeDlLaborTable($data);
					BUDGETCONTROL.refreshPanels($data);
					$("#bcr_edit_modal .bcr_edit_message").show().fadeOut(6000);
					
        		},
        		
        		
        		
        		
        		makeAccordion : function() {
        			$('ul.accordionList').accordion({
						//autoHeight: true,
						heightStyle: "content",
						alwaysOpen: true,
						header: 'h4',
						fillSpace: false,
						collapsible: true,
						active: true
					});
        		},
        		
        		
        		
        		
        		
        		
        		makeClaimWeekSelect : function($ticket_id, $workCalendar) {
        			var $select = $("<select>");
        			$select.attr("data-ticketid",$ticket_id);
        			var $defaultOption = $("<option>")
       				$defaultOption.attr("value","");
       				$defaultOption.append("");
       				$select.append($defaultOption);
        			$.each( $workCalendar, function($index, $value) {
        				var $option = $("<option>");
        				$option.attr("value",$value.weekOfYear);
        				$option.append($value.workYear + "-" + $value.weekOfYear);
        				$select.append($option);
        			});
        			return $select[0].outerHTML;
        		},
        		
        		
        		makeClickers : function() {
					$(".accHdr").click(function($clickEvent) {
						ANSI_UTILS.doServerCall("GET", "bcr/keepAlive", null, BUDGETCONTROL.keepAliveSuccess, BUDGETCONTROL.keepAliveFail, BUDGETCONTROL.keepAliveExpired);
					});
					
					
					
					

        		},
        		
        		
        		makeDateList : function($data) {
        			console.log("makeDateList");
        			var $dateField = $("#bcr_title_prompt select[name='workDate']");
        			$dateField.append(new Option("",""));
       				$.each($data.data.displayMonthList, function(index, val) {    
       					var $option = $("<option>")
       					$option.attr("value",val.workDate);
       					$option.attr("data-firstofmonth", val.firstOfMonth);
       					$option.attr("data-lastofmonth",val.lastOfMonth)
       					$option.append(val.displayDate);
       					$dateField.append($option);
       					//$dateField.append(new Option(val.displayDate, val.workDate));
       				});
        		},
        		
        		
        		makeDivList : function($data) {
        			console.log("makeDivList");
        			var $divisionField = $("#bcr_title_prompt select[name='divisionId']");
        			$divisionField.append(new Option("",""));
       				$.each($data.data.divisionList, function(index, val) {
       					var $displayValue = val.divisionNbr + "-" + val.divisionCode;
       					$divisionField.append(new Option($displayValue, val.divisionId));
       				});
        		},
        		
        		
        		
        		makeDlExpenseTable : function($data) {
        			// Make DL Expense Table:
        			var $editTag = '<webthing:edit styleClass="expense-edit">Edit</webthing:edit>';
        			var $editCancelTag = '<webthing:ban styleClass="expense-edit-cancel">Cancel</webthing:ban>';
        			var $actionHeader = $editTag + $editCancelTag;
        			
        			var $columnExpense = 0;
	            	var $columnExpenseEdit = 1;
	            	var $columnType = 2;
	            	var $columnTypeEdit = 3;
	            	var $columnNotes = 4;
	            	var $columnNotesEdit = 5;
	            	var $columnAction = 6;
	            	
	            	var $displayColumns = [$columnExpense,$columnType,$columnNotes];
	            	var $editColumns = [$columnExpenseEdit, $columnTypeEdit,$columnNotesEdit];
	            	
	            	var $tabIndex = 0;
    				
					BUDGETCONTROL.dlExpenseTable = $("#dl-expense-table").DataTable( {
		    			data : $data.data.expenses,
		    			paging : false,
		    			autoWidth : false,
	        	        deferRender : true,
	        	        searching: false, 
	        	        scrollX : false,
	        	        rowId : 'claimId',	// this sets an id for the row: <tr id="123"> ... </tr>   where 123 is the claim id
	        	        destroy : true,		// this lets us reinitialize the table for different tickets
		    			columns : [
		    				{ width:"125px", title:"Expense Vol.", className:"dt-right", orderable:true,
		    					data:function($row,$type,$set) {
		    						return $row.passthruVolume.toFixed(2);
		    					}
		    				},	
		    				{ width:"125px", title:"Expense Vol", className:"dt-right", orderable:true, visible:false,
		    					data:function($row,$type,$set) {
		    						$tabIndex++;
		    						var $edit = '<input type="text" name="expenseVolume" tabindex="'+$tabIndex+'" value="'+$row.passthruVolume.toFixed(2)+'" style="width:80px;" />';
		    						return $edit;
		    					}
		    				},
		    				{ width:"200px", title:"Expense Type", className:"dt-head-left", orderable:true, defaultContent: "<i>N/A</i>", data:'passthruExpenseType'},
		    				{ width:"200px", title:"Expense Type", className:"dt-head-left", orderable:true, defaultContent: "<i>N/A</i>", visible:true,
		    					data:function($row,$type,$set) {
		    						$tabIndex++;
		    						var $edit = '<select name="expenseType" tabindex="'+$tabIndex+'"></select>';
		    						return $edit;
		    					}
		    				
		    				},
		    				{ width:"200px", title:"Notes", className:"dt-head-left", orderable:true, defaultContent: "<i>N/A</i>", data:'notes'},
		    				{ width:"200px", title:"Notes", className:"dt-head-left", orderable:true, visible:false,
		    					data:function($row,$type,$set) {
		    						$tabIndex++;
		    						var $edit = '<input type="text" name="notes" value="'+$row.notes+'" tabindex="'+$tabIndex+'" style="width:80px;" />';
		    						return $edit;
		    					}
		    				},
		    				{ width:"50px", title:$actionHeader, className:"dt-center", orderable:false, defaultContent: "<i>N/A</i>",
		    					data:function($row,$type,$set) {
		    						$tabIndex++;
		    						var $save = '<span class="action-link save-expense" data-claimid="'+$row.claimId+'" tabindex="'+$tabIndex+'"><webthing:checkmark>Save</webthing:checkmark></span>';
		    						var $delete = '<span class="action-link delete-expense" data-claimid="'+$row.claimId+'"><webthing:delete>Delete</webthing:delete></span>';
		    						return $delete + $save;		
		    					}
		    				},
		    			],
		    			"drawCallback": function( settings ) {
		    				var $select = $("#bcr_edit_modal select[name='expenseType']");
							$('option', $select).remove();
							$select.append(new Option("",""));
							$.each(BUDGETCONTROL.expenseTypes, function(index, val) {
							    $select.append(new Option(val.displayValue, val.value));
							});
							$.each($data.data.expenses, function(index, val) {
								var $expenseSelect = "#" + val.claimId + " select[name='expenseType']";
								$($expenseSelect).val(val.passthruExpenseCode);
							});
							// select must be visible in order for options to be populated, so hide it afterwards
	    					$("#dl-expense-table").DataTable().columns($columnTypeEdit).visible(false);		    					
							
							
		    				$(".expense-edit-cancel").hide();
		    				$(".save-expense").hide();
		    				$(".delete-expense").click(function($event) {
		    					var $claimId = $(this).attr("data-claimid");
		    					$("#bcr_delete_confirmation_modal").attr("claimId", $claimId);
		    					$("#bcr_delete_confirmation_modal").attr("claimType", "expense");
		    					$("#bcr_delete_confirmation_modal").dialog("open");
		    				});
		    				$(".expense-edit").click(function($event) {
		    					var myTable = $("#dl-expense-table").DataTable();		    					
	    						$.each($displayColumns, function($index, $value) {myTable.columns($value).visible(false);});
	    						$.each($editColumns, function($index, $value) {myTable.columns($value).visible(true);});
	    						$(".expense-edit").hide();
	    						$(".expense-edit-cancel").show();
	    						$(".delete-expense").hide();
	    						$(".save-expense").show();
	    						$(".newExpenseItem").hide();
	    						$(".displayExpenseItem").hide();
	    						$("#dl-expense-table tbody input[name='expenseVolume']").eq(0).focus();  // set focus to expenseVolume in row 0 in tbody
		    				});
    						$(".expense-edit-cancel").click(function($event) {
		    					var myTable = $("#dl-expense-table").DataTable();		    						    						
	    						$.each($editColumns, function($index, $value) {myTable.columns($value).visible(false);});
	    						$.each($displayColumns, function($index, $value) {myTable.columns($value).visible(true);});
	    						$(".expense-edit").show();
	    						$(".expense-edit-cancel").hide();
	    						$(".save-expense").hide();
	    						$(".delete-expense").show();
	    						$(".newExpenseItem").hide();
	    						$(".displayExpenseItem").show();
		    				});
    						$(".save-expense").click(function($event) {
    							var $claimId = $(this).attr("data-claimid");
    							console.log("Saving expense" + $claimId);
    							var $volume = $("#"+$claimId + " input[name='expenseVolume']").val();
    							var $expenseType = $("#"+$claimId + " select[name='expenseType']").val();
    							var $notes = $("#"+$claimId + " input[name='notes']").val();
    							
    							// these are needed to create the correct response, not to do the update
    		        			var $divisionId = BUDGETCONTROL.divisionId
    							var $ticketId = $("#bcr_edit_modal").attr("ticketId");
        						var $serviceTagId = $("#bcr_edit_modal").attr("serviceTagId");
        						var $claimWeek = $("#bcr_edit_modal input[name='claimWeek']").val(); 
    		        			var $workYear = BUDGETCONTROL.workYear; 
    		        			var $workWeeks = BUDGETCONTROL.workWeek;
    		        			
    		        			var $outbound = {
    		        					"divisionId":$divisionId,
    		        					"ticketId":$ticketId,
    		                			"serviceTagId":$serviceTagId,
    		                			"claimWeek":$claimWeek,
    		                			"volume":$volume,
    		                			"expenseType":$expenseType,
    		                			"notes":$notes,
    		                			"workYear":$workYear,
    		                			"workWeeks":$workWeeks,
    		        			}
    		        			console.log($outbound);
    		        			var $url = "bcr/expense/" + $claimId;
    		        			ANSI_UTILS.doServerCall("POST", $url, JSON.stringify($outbound), BUDGETCONTROL.expenseSaveSuccess, BUDGETCONTROL.claimUpdateFail);
    						});
    						$(".save-expense").keydown(function($event) {
		    					if ( $event.which == 32 ) {
		    						// on space bar, act like you clicked the icon
			 						$event.preventDefault();
		    						$(this).click();
		    					}
		    				});
			            },
			            "footerCallback" : function( row, data, start, end, display ) {
			            	var api = this.api();
			            	//var data;
			            	expenseTotal = api.column($columnExpense).data().reduce( function(a,b) {
			            		var mySum = parseFloat(a) + parseFloat(b);
			            		return mySum;
			            	}, 0);
			            	
			            	$( api.column($columnExpense).footer() ).html( '<span class="newExpenseItem"><input type="text" placeholder="0.00" style="width:80px;" id="newExpenseVolume" name="expenseVolume" tabindex="10" /><br /></span><span class="displayExpenseItem">' + expenseTotal.toFixed(2) + '</span>');
			            	$( api.column($columnType).footer() ).html( '<span class="newExpenseItem"><select name="expenseType" tabindex="11"></select><br /></span>' );
			            	$( api.column($columnNotes).footer() ).html( '<span class="newExpenseItem"><input type="text" style="width:120px;" name="notes" tabindex="12" /><br /></span>');
			            	$( api.column($columnAction).footer() ).html( '<span class="newExpenseItem"><webthing:ban styleClass="cancelExpense" tabindex="13">Cancel</webthing:ban><webthing:checkmark styleClass="saveNewExpense" tabindex="14">Save</webthing:checkmark></span><span class="displayExpenseItem"><webthing:addNew styleClass="newExpenseButton">New Expense</webthing:addNew></span>' );
			            	
							$("#bcr_edit_modal .newExpenseButton").click(function() {
								console.log("New Expense Click");
								$("#bcr_edit_modal .displayExpenseItem").hide();
								$("#bcr_edit_modal .newExpenseItem").fadeIn(250);
								$("#newExpenseVolume").focus();
							});
							$("#bcr_edit_modal .newExpenseButton").keydown(function($event) {
		    					if ( $event.which == 32 ) {
		    						// on space bar, act like you clicked the icon
			 						$event.preventDefault();
		    						$(this).click();
		    					}
		    				});
							
							$("#bcr_edit_modal .cancelExpense").click(function() {
								console.log("Cancel Expense Click");
								$("#bcr_edit_modal .newExpenseItem").hide();
								$("#bcr_edit_modal .displayExpenseItem").fadeIn(250);
							});
							$("#bcr_edit_modal .cancelExpense").keydown(function($event) {
		    					if ( $event.which == 32 ) {
		    						// on space bar, act like you clicked the icon
			 						$event.preventDefault();
		    						$(this).click();
		    					}
		    				});
							$("#bcr_edit_modal .saveNewExpense").click(function() {
								BUDGETCONTROL.expenseSave();						
							});
							$("#bcr_edit_modal .saveNewExpense").keydown(function($event) {
		    					if ( $event.which == 32 ) {
		    						// on space bar, act like you clicked the icon
			 						$event.preventDefault();
		    						$(this).click();
		    					}
		    				});
							
			            	
			            }
		    		});
        		},
        		
        		
        		
        		
        		makeDlLaborTable : function($data) {
	            	var volumeClaimedTotal = 0.0;
	            	var expenseTotal = 0.0;
					
	            	var $editTag = '<webthing:edit styleClass="labor-edit">Edit</webthing:edit>';
        			var $editCancelTag = '<webthing:ban styleClass="labor-edit-cancel">Cancel</webthing:ban>';
        			var $actionHeader = $editTag + $editCancelTag;
        			
        			var $columnLabor = 0;
        			var $columnLaborEdit = 1;
        			var $columnClaimed = 2;
        			var $columnClaimedEdit = 3;
        			var $columnRemaining = 4;
        			var $columnEquipment = 5;
        			var $columnEquipmentEdit = 6;
        			var $columnEmployee = 7;
        			var $columnEmployeeEdit = 8;
        			var $columnNotes = 9;
        			var $columnNotesEdit = 10;
        			var $columnAction = 11;
        			
        			var $displayColumns = [$columnLabor,$columnClaimed, $columnEquipment, $columnEmployee,$columnNotes];
	            	var $editColumns = [$columnLaborEdit, $columnClaimedEdit,$columnEquipmentEdit, $columnEmployeeEdit,$columnNotesEdit];
	            	
	            	var $equipmentList = [];
	            	var $tabIndex = 22; //in the input, DL Amt is 20, volume claimed is 21, so start the equipment at 22
	            	$.each($data.data.equipmentTags, function($index, $value) {
	            		$equipmentList.push('<span class="jobtag-display equipment-claim" style="cursor:pointer;" tabindex="'+$tabIndex+'" data-tagid="'+$value.tagId+'">' + $value.abbrev + "</span>");
	            		$tabIndex = $tabIndex + 1;
	            	});
	            	var $equipmentDisplay = $equipmentList.join("");
        			
	            	var $tabIndex = 0;
	            	
					// Make DL Claim Table:
					BUDGETCONTROL.dlClaimTable = $("#dl-claim-table").DataTable( {
		    			data : $data.data.dlClaims,
		    			paging : false,
		    			autoWidth : false,
	        	        deferRender : true,
	        	        rowId : 'claimId',	// this sets an id for the row: <tr id="123"> ... </tr>   where 123 is the claim id
	        	        destroy : true,		// this lets us reinitialize the table for different tickets
		    			columns : [
		    				{ width:"125px", title:"Direct Labor", className:"dt-right", orderable:true, visible:true,
		    					data:function($row,$type,$set) {
		    						return $row.dlAmt.toFixed(2);
		    					}
		    				},
		    				{ width:"125px", title:"Direct Labor", className:"dt-right", orderable:true, visible:false,
		    					data:function($row,$type,$set) {
		    						$tabIndex++;
		    						var $edit = '<input type="text" name="dlAmt" tabindex="'+$tabIndex+'" value="' + $row.dlAmt.toFixed(2) + '" style="width:80px;" />';		    						
		    						return $edit;
		    					}
		    				},
		    				{ width:"125px", title:"Volume Claimed", className:"dt-right", orderable:true, visible:true,
		    					data:function($row,$type,$set) {
		    						return $row.volumeClaimed.toFixed(2);
		    					}  
		    				},	
		    				{ width:"125px", title:"Volume Claimed", className:"dt-right", orderable:true, visible:false,
		    					data:function($row,$type,$set) {
		    						$tabIndex++;
		    						var $edit = '<input type="text" name="volumeClaimed" tabindex="'+$tabIndex+'" value="' + $row.volumeClaimed.toFixed(2) + '" style="width:80px;" />';
		    						return $edit;
		    					}
		    				},
		    				{ width:"125px", title:"Volume Remaining", className:"dt-right", orderable:false, visible:false,
		    					data:function($row,$type,$set) {
		    						return $row.volumeRemaining.toFixed(2);
		    					}  
		    				},
		    				{ width:"125px", title:"Equipment Type", className:"dt-head-left", orderable:true, visible:true, defaultContent: "", 
	    						data: function ( row, type, set ) {
    			            		var $display = [];
    			            		if ( row.equipmentTags != null ) {
	    			            		var $equipmentList = row.equipmentTags.split(",");
	    			            		$.each($equipmentList, function($index, $value) {
    			            				$display.push('<span class="jobtag-display jobtag-selected">'+ $value + '</span>');
	    			            		});
    			            		}
    			            		return $display.join("");
    			            	}
		    				},
		    				{ width:"125px", title:"Equipment Type", className:"dt-head-left", orderable:true, visible:true, defaultContent: "", 
	    						data: function ( row, type, set ) {
    			            		var $display = [];
    			            		var $rowTags = [];
    			            		if ( row.equipmentTags != null ) {
    			            			$rowTags = row.equipmentTags.split(",");
    			            		}
    			            		$.each($data.data.equipmentTags, function($index, $value) {
    			            			$tabIndex++;
    			            			if ( $rowTags.includes($value.abbrev) ) {
    			            				$display.push('<span class="jobtag-display equipment-claim-updt jobtag-selected" tabindex="'+$tabIndex+'" style="cursor:pointer;" data-tagid="'+$value.tagId+'">' + $value.abbrev + '</span>');
    			            			} else {
    			            				$display.push('<span class="jobtag-display equipment-claim-updt" tabindex="'+$tabIndex+'" style="cursor:pointer;" data-tagid="'+$value.tagId+'">' + $value.abbrev + "</span>");
    			            			}
    			            		});

    			            		return $display.join("");
    			            	}
		    				},
		    				{ width:"300px", title:"Employee", className:"dt-head-left", orderable:true, visible:true, defaultContent: "<i>N/A</i>", data:'employee'},
		    				{ width:"300px", title:"Employee", className:"dt-head-left", orderable:true, visible:true, defaultContent: "<i>N/A</i>", 
		    					data:function($row,$type,$set) {
		    						$tabIndex++;
		    						var $edit = '<input type="text" name="employee" tabindex="'+$tabIndex+'" value="' + $row.employee + '"/>';
		    						return $edit;
		    					} 
		    				},
		    				{ width:"300px", title:"Notes", className:"dt-head-left", orderable:true, visible:true, defaultContent: "<i>N/A</i>", data:'notes'},
		    				{ width:"300px", title:"Notes", className:"dt-head-left", orderable:true, visible:false, defaultContent: "<i>N/A</i>", 
		    					data:function($row,$type,$set) {
		    						$tabIndex++;
		    						var $edit = '<input type="text" name="laborNotes" tabindex="'+$tabIndex+'" value="' + $row.notes + '"/>';
		    						return $edit;
		    					} 
		    				},
		    				{ width:"30px", title:$actionHeader, className:"dt-center", orderable:false, visible:true, defaultContent: "<i>N/A</i>",
		    					data:function($row,$type,$set) {
		    						$tabIndex++;
		    						var $save = '<span class="action-link save-labor" tabindex="'+$tabIndex+'" data-claimid="'+$row.claimId+'"><webthing:checkmark>Save</webthing:checkmark></span>';
		    						var $delete = '<span class="action-link delete-claim" data-claimid="'+$row.claimId+'"><webthing:delete>Delete</webthing:delete></span>';
		    						return $delete + $save;		
		    					}
		    				},
		    			],
		    			//"createdRow" : function($row, $data, $dataIndex, cells ) {
		    			//	$rowNum++;
		    			//	$fieldIndex = 1;
		    			//},
		    			"drawCallback": function( settings ) {
		    				// something to make this do something useful		
		    				$(".labor-edit-cancel").hide();
		    				$(".newLaborItem").hide();
		    				$(".save-labor").hide();
		    				$(".delete-claim").click(function($event) {
		    					var $claimId = $(this).attr("data-claimid");
		    					$("#bcr_delete_confirmation_modal").attr("claimId", $claimId);
		    					$("#bcr_delete_confirmation_modal").attr("claimType", "labor");
		    					$("#bcr_delete_confirmation_modal").dialog("open");
		    				});
		    				$(".labor-edit").click(function($event){
		    					console.log("labor edit");
		    					var myTable = $("#dl-claim-table").DataTable();		    					
	    						$.each($displayColumns, function($index, $value) {myTable.columns($value).visible(false);});
	    						$.each($editColumns, function($index, $value) {myTable.columns($value).visible(true);});
	    						$(".labor-edit").hide();
	    						$(".labor-edit-cancel").show();
	    						$(".delete-claim").hide();
	    						$(".save-labor").show();
	    						$(".newLaborItem").hide();
	    						$(".displayLaborItem").hide();
	    						$("#dl-claim-table tbody input[name='dlAmt']").eq(0).focus();  // set focus to dlAmt in row 0 in tbody
		    				});
		    				$(".labor-edit-cancel").click(function($event) {
		    					var myTable = $("#dl-claim-table").DataTable();		    						    						
	    						$.each($editColumns, function($index, $value) {myTable.columns($value).visible(false);});
	    						$.each($displayColumns, function($index, $value) {myTable.columns($value).visible(true);});
	    						$(".labor-edit").show();
	    						$(".labor-edit-cancel").hide();
	    						$(".save-labor").hide();
	    						$(".delete-claim").show();
	    						$(".newLaborItem").hide();
	    						$(".displayLaborItem").show();
		    				});

		    				// for on-click to work, the field has to be visible. So hide it after the binding
		    				$(".equipment-claim-updt").click(function($event) {
		    					$(this).toggleClass("jobtag-selected");
		    				});
		    				$(".equipment-claim-updt").keydown(function($event) {
		    					console.log("equipment keydown: " + $event.which);
		    					if ( $event.which == 32 ) {
		    						// on space bar, act like you clicked the icon
			 						$event.preventDefault();
		    						$(this).click();
		    					}
		    				});
		    				$("#dl-claim-table").DataTable().columns($columnEquipmentEdit).visible(false);
		    				
		    				// for autocomplete to work, the field has to be visible. So hide it after the binding
		    				BUDGETCONTROL.makeEmployeeAutoComplete("#bcr_edit_modal input[name='employee']");
		    				$("#dl-claim-table").DataTable().columns($columnEmployeeEdit).visible(false);
		    				
		    				$(".save-labor").click(function($event) {
		    					var $claimId = $(this).attr("data-claimid");
    							console.log("Saving d/l " + $claimId);
    							var $dlAmt = $("#"+$claimId + " input[name='dlAmt']").val();
    							var $volumeClaimed = $("#"+$claimId + " input[name='volumeClaimed']").val();
    							var $employee = $("#" + $claimId + " input[name='employee']").val();
    							var $notes = $("#"+$claimId + " input[name='laborNotes']").val();
    							
    							var $claimedEquipment = [];
    							$.each( $("#"+$claimId + " .jobtag-selected"), function($index, $value) {
   									$claimedEquipment.push($($value).attr("data-tagid"));
    							});
    							// these are needed to create the correct response, not to do the update
    		        			var $divisionId = BUDGETCONTROL.divisionId
    							var $ticketId = $("#bcr_edit_modal").attr("ticketId");
        						var $serviceTagId = $("#bcr_edit_modal").attr("serviceTagId");
        						var $claimWeek = $("#bcr_edit_modal input[name='claimWeek']").val(); 
    		        			var $workYear = BUDGETCONTROL.workYear; 
    		        			var $workWeeks = BUDGETCONTROL.workWeek;
    		        			
    		        			var $outbound = {
    		        					"divisionId":$divisionId,
    		        					"ticketId":$ticketId,
    		                			"serviceTagId":$serviceTagId,
    		                			"claimWeek":$claimWeek,
    		                			"dlAmt":$dlAmt,
    		                			"volumeClaimed":$volumeClaimed,
    		                			"employee":$employee,
    		                			"notes":$notes,
    		                			"workYear":$workYear,
    		                			"workWeeks":$workWeeks,
    		                			"claimedEquipment":$claimedEquipment.join(","),
    		        			}
    		        			console.log($outbound);
    		        			var $url = "bcr/ticketClaim/" + $claimId;
    		        			ANSI_UTILS.doServerCall("POST", $url, JSON.stringify($outbound), BUDGETCONTROL.expenseSaveSuccess, BUDGETCONTROL.claimUpdateFail);
		    				});
		    				$(".save-labor").keydown(function($event) {
		    					if ( $event.which == 32 ) {
		    						// on space bar, act like you clicked the icon
			 						$event.preventDefault();
		    						$(this).click();
		    					}
		    				});
			            },
			            "footerCallback" : function( row, data, start, end, display ) {
			            	var api = this.api();
			            	//var data;
			            	dlTotal = api.column($columnLabor).data().reduce( function(a,b) {
			            		var mySum = parseFloat(a) + parseFloat(b);
			            		return mySum;
			            	}, 0);
			            	volumeClaimedTotal = api.column($columnClaimed).data().reduce( function(a,b) {
			            		var mySum = parseFloat(a) + parseFloat(b);
			            		return mySum;
			            	}, 0);			            	

			            	$( api.column($columnLabor).footer() ).html( '<span class="newLaborItem"><input type="text" placeholder="0.00" style="width:80px;" tabindex="20" id="newDlAmt" name="dlAmt"/><br /></span><span class="displayLaborItem">' + dlTotal.toFixed(2) + '</span>');
			            	$( api.column($columnClaimed).footer() ).html( '<span class="newLaborItem"><input type="text" placeholder="0.00" style="width:80px;" tabindex="21" name="volumeClaimed"/><br /></span><span class="displayLaborItem">' + volumeClaimedTotal.toFixed(2) + '</span>');
			            	$( api.column($columnEquipment).footer() ).html( '<span class="newLaborItem">'+$equipmentDisplay+'<br /></span><span class="displayLaborItem">&nbsp;</span>');
			            	$tabIndex++;
			            	$( api.column($columnEmployee).footer() ).html( '<span class="newLaborItem"><input type="text" style="width:120px;" name="employee" tabindex="'+$tabIndex+'" /><br /></span>');
			            	$tabIndex++;
			            	$( api.column($columnNotes).footer() ).html( '<span class="newLaborItem"><input type="text" style="width:120px;" name="laborNotes" tabindex="'+$tabIndex+'"/><br /></span>');
			            	$tabIndex++;
			            	var $cancelString = '<webthing:ban styleClass="cancelLabor" tabindex="'+$tabIndex+'">Cancel</webthing:ban>';
			            	$tabIndex++;
			            	var $saveString = '<webthing:checkmark styleClass="saveNewLabor" tabindex="'+$tabIndex+'">Save</webthing:checkmark>';
			            	$( api.column($columnAction).footer() ).html( '<span class="newLaborItem">'+$cancelString+$saveString + '</span><span class="displayLaborItem"><webthing:addNew styleClass="newLaborButton">New Claim</webthing:addNew></span>' );
			            	$tabIndex++;
		 					$("#bcr_edit_modal").attr("volumeClaimedTotal", volumeClaimedTotal);
		 					
		 					BUDGETCONTROL.makeEmployeeAutoComplete("#bcr_edit_modal input[name='employee']");
		 					
		 					$("#bcr_edit_modal .equipment-claim").click(function() {
		 						console.log("equipment click");
		 						$(this).toggleClass("jobtag-selected");
		 					});
		 					$("#bcr_edit_modal .equipment-claim").keydown(function($event) {
		    					if ( $event.which == 32 ) {
		    						// on space bar, act like you clicked the icon
			 						$event.preventDefault();
		    						$(this).click();
		    					}
		    				});
							$("#bcr_edit_modal .newLaborButton").click(function() {
								console.log("New Labor Click");
								$("#bcr_edit_modal .displayLaborItem").hide();
								$("#bcr_edit_modal .newLaborItem").fadeIn(250);
								$("#newDlAmt").focus();
							});
							$("#bcr_edit_modal .newLaborButton").keydown(function($event) {
		    					if ( $event.which == 32 ) {
		    						// on space bar, act like you clicked the icon
			 						$event.preventDefault();
		    						$(this).click();
		    					}
		    				});
							$("#bcr_edit_modal .cancelLabor").click(function() {
								console.log("Cancel Labor Click");
								$("#bcr_edit_modal .newLaborItem").hide();
								$("#bcr_edit_modal .displayLaborItem").fadeIn(250);
							});
							$("#bcr_edit_modal .cancelLabor").keydown(function($event) {
		    					if ( $event.which == 32 ) {
		    						// on space bar, act like you clicked the icon
			 						$event.preventDefault();
		    						$(this).click();
		    					}
		    				});
							$("#bcr_edit_modal .saveNewLabor").click(function() {
								BUDGETCONTROL.laborSave();						
							});
							$("#bcr_edit_modal .saveNewLabor").keydown(function($event) {
		    					if ( $event.which == 32 ) {
		    						// on space bar, act like you clicked the icon
			 						$event.preventDefault();
		    						$(this).click();
		    					}
		    				});
			            }
		    		});
        		},
        		
        		
        		
        		
        		
        		
        		makeEditModal : function() {
        			$( "#bcr_edit_modal" ).dialog({
        				title:'Edit Claim',
        				autoOpen: false,
        				height: 650,
        				width: 1200,
        				modal: true,
        				closeOnEscape:true,
        				open: function(event, ui) {
        					// we need this because the modal that is displayed on page load hides it
        					$(".ui-dialog-titlebar-close", ui.dialog | ui).show();
        				},
        				buttons: [
        					{
        						id:  "bcr_claim_edit_cancel",
        						click: function($event) {
        							$( "#bcr_edit_modal" ).dialog("close");    							
        						}
        					}
        				]
        			});	
        			$("#bcr_claim_edit_cancel").button('option', 'label', 'Done');
        			
        			$("#dlAmtField").blur( function() {
	        			if ( $("#dlAmtField").val() == null || $("#dlAmtField").val() == "" ) {
	        				$("#dlAmtField").val("0.00");   // it's a string so we get the pennies
	        			} else {
	        				$("#dlAmtField").val( parseFloat($("#dlAmtField").val()).toFixed(2) );
	        			} 
        			});
        			
        			$("#totalVolumeField").blur( function() {
        				BUDGETCONTROL.calculateRemainingVolume();
        			});
        			
        			$("#volumeClaimedField").blur(function($event) {
        				BUDGETCONTROL.calculateRemainingVolume();
        				BUDGETCONTROL.calculateDiffClaimedBilled();
        			});
        			$("#billedAmountField").blur(function($event) {
        				BUDGETCONTROL.calculateDiffClaimedBilled();
        			});
        			
        			
        			
        			BUDGETCONTROL.makeEmployeeAutoComplete('#bcr_edit_modal input[name="employee"]')        			
        		},
        		
        		
        		
        		makeEmployeeAutoComplete : function($employeeSelector) {
            		$( $employeeSelector ).autocomplete({
						//'source':"bcr/employee?",
						'source':function(request, response) {
							jQuery.get(
								"bcr/employee", 
								{term:request.term, divisionId:BUDGETCONTROL.divisionId}, 
								function($data) {
									response($data);
								}
							)
						},
						position:{my:"left top", at:"left bottom",collision:"none"},
						appendTo:"#bcr_edit_form",
						select: function( event, ui ) {
							$('#bcr_edit_form input[name="employee"]').val(ui.item.label);							
							if ( ui.item.value == null || ui.item.value.trim() == "" ) {
								$($employeeSelector).val(ui.item.label)
							}
       			      	}
       			 	});
        		},
        		
        		
        		
				

        		
        		
        		
        		makeModals : function() {
        			TICKETUTILS.makeTicketViewModal("#ticket-modal");
        			
        			
        			$( "#bcr_title_prompt" ).dialog({
        				title:'Budget Control',
        				autoOpen: false,
        				height: 200,
        				width: 500,
        				modal: true,
        				closeOnEscape:false,
        				open: function(event, ui) {
        					$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
        				},
        				buttons: [
        					{
        						id:  "bcr_title_prompt_cancel",
        						click: function($event) {
        							if ( BUDGETCONTROL.workWeek == null ) {
        								location.href="dashboard.html";
        							} else {
        								$( "#bcr_title_prompt" ).dialog("close");
        							}
        						}
        					},{
        						id:  "bcr_title_prompt_save",
        						click: function($event) {
        							BUDGETCONTROL.titleSave();        							
        						}
        					}
        				]
        			});	
        			$("#bcr_title_prompt_cancel").button('option', 'label', 'Cancel');
        			$("#bcr_title_prompt_save").button('option', 'label', 'Go');
        			
        			
        			
        			
        			$("#bcr_delete_confirmation_modal").dialog({
        				title:'Confirm Delete',
        				autoOpen: false,
        				height: 200,
        				width: 300,
        				modal: true,
        				closeOnEscape:true,
        				open: function(event, ui) {
        					$(".ui-dialog-titlebar-close", ui.dialog | ui).show();
        				},
        				buttons: [
        					{
        						id:  "bcr_delete_cancel",
        						click: function($event) {
        							$("#bcr_delete_confirmation_modal").dialog("close");      							
        						}
        					},{
        						id:  "bcr_delete_save",
        						click: function($event) {
        							BUDGETCONTROL.deleteClaim();        							
        						}
        					}
        				]
        			});
        			$("#bcr_delete_cancel").button('option', 'label', 'No');
        			$("#bcr_delete_save").button('option', 'label', 'Yes');
        			
        			
        			
        			$("#session_expire_modal").dialog({
        				title:'Session Has Expired',
        				autoOpen: false,
        				height: 200,
        				width: 300,
        				modal: true,
        				closeOnEscape:false,
        				open: function(event, ui) {
        					$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
        				},
        				buttons: [
        					{
        						id:  "session_expire_ok",
        						click: function($event) {
        							location.href="dashboard.html";      							
        						}
        					}
        				]
        			});
        			$("#session_expire_ok").button('option', 'label', 'OK');
        			
        			
        			$("#bcr_new_claim_modal").dialog({
        				title:'New Claim',
        				autoOpen: false,
        				height: 420,
        				width: 750,
        				modal: true,
        				closeOnEscape:true,
        				open: function(event, ui) {
        					$(".ui-dialog-titlebar-close", ui.dialog | ui).show();
        				},
        				buttons: [
        					{
        						id:  "bcr_new_claim_cancel",
        						click: function($event) {
        							$("#bcr_new_claim_modal").dialog("close");      							
        						}
        					},{
        						id:  "bcr_new_claim_save",
        						click: function($event) {
        							BUDGETCONTROL.makeNewClaim();
        						}
        					}
        				]
        			});
        			$("#bcr_new_claim_cancel").button('option', 'label', 'Cancel');
        			$("#bcr_new_claim_save").button('option', 'label', 'Save');
        			$("#bcr_new_claim_cancel").attr("tabIndex", "9");
        			$("#bcr_new_claim_save").attr("tabIndex", "10");
        			BUDGETCONTROL.makeEmployeeAutoComplete("#bcr_new_claim_modal input[name='employee']");
        			BUDGETCONTROL.saveOnEnter();        			
        		},
        		
        		
        		
        		// insert new Labor and/or expense claim, based on "new claim" modal from ticket panels
        		makeNewClaim : function() {
        			console.log("makeNewClaim");
        			
        			$("#bcr_new_claim_modal .err").html("");
        			
        			var $ticketId = $("#bcr_edit_modal").attr("ticketId");
        			var $serviceTagId = $("#bcr_edit_modal").attr("serviceTagId");
        			var $claimWeek = $("#bcr_edit_modal input[name='claimWeek']").val();
        			var $volume = $("#bcr_edit_modal input[name='expenseVolume']").val();
        			var $expenseType = $("#bcr_edit_modal select[name='expenseType']").val();
        			var $notes = $("#bcr_edit_modal input[name='notes']").val();
        			
        			// these are needed to create the correct response, not to do the update
        			var $divisionId = BUDGETCONTROL.divisionId
        			var $workYear = BUDGETCONTROL.workYear; 
        			var $workWeeks = BUDGETCONTROL.workWeek;
        			
        			
        			
        			var $outbound = {
        				"divisionId":BUDGETCONTROL.divisionId,
                		"workYear":BUDGETCONTROL.workYear, 
                		"workWeeks":BUDGETCONTROL.workWeek,	
        					
        				"ticketId":$("#bcr_new_claim_modal input[name='ticketId']").val(),
        				"serviceTypeId":$("#bcr_new_claim_modal input[name='serviceTypeId']").val(),
        				"claimWeek":$("#bcr_new_claim_modal select[name='claimWeek']").val(),
        				"dlAmt":$("#bcr_new_claim_modal input[name='dlAmt']").val(),
        				"expenseVolume":$("#bcr_new_claim_modal input[name='expenseVolume']").val(),
        				"volumeClaimed":$("#bcr_new_claim_modal input[name='volumeClaimed']").val(),
        				"expenseType":$("#bcr_new_claim_modal select[name='expenseType']").val(),
        				"employee":$("#bcr_new_claim_modal input[name='employee']").val(),
        				"laborNotes":$("#bcr_new_claim_modal input[name='laborNotes']").val(),
        				"expenseNotes":$("#bcr_new_claim_modal input[name='expenseNotes']").val(), 
        			};
        			console.log($outbound);
        			ANSI_UTILS.doServerCall("POST", "bcr/newClaim", JSON.stringify($outbound), BUDGETCONTROL.makeNewClaimSuccess, BUDGETCONTROL.makeNewClaimFail);
        		},
        		
        		makeNewClaimFail : function($data) {
        			console.log("makeNewClaimFail");
        			$.each($data.data.webMessages, function($index, $value) {
        				var $loc = "#bcr_new_claim_modal ." + $index + "Err";
        				$($loc).html($value[0]);
        			});
        		},
        		
        		
        		makeNewClaimSuccess : function($data) {
        			console.log("makeNewClaimSuccess");
        			//BUDGETCONTROL.getTicketDetailSuccess($data);
        			BUDGETCONTROL.refreshPanels($data);
        			$("#bcr_new_claim_modal").dialog("close");
        			$("#globalMsg").html("Update Successful").fadeOut(6000);
        		}, 
        		
        		
        		
        		// get a list of divisions, and calendars for initial modal
        		makeSelectionLists : function() {
        			ANSI_UTILS.doServerCall("GET", "bcr/init", null, BUDGETCONTROL.makeSelectionListSuccess, BUDGETCONTROL.makeSelectionListFail);
        		},
        		
        		
        		// process a failure in getting initial div list and calendar
        		makeSelectionListFail : function($data) {
        			console.log("makeSelectionListFail");
        			$("#globalMsg").html("Failure intializing page. Contact Support").show();
        		},
        		
        		
        		// process a successful retrieval of initial div list and calendar
        		makeSelectionListSuccess : function($data) {
        			console.log("makeSelectionListSuccess");
        			BUDGETCONTROL.expenseTypes = $data.data.expenseTypeList;
        			BUDGETCONTROL.makeDivList($data);
        			BUDGETCONTROL.makeDateList($data);
        			
        			BUDGETCONTROL.makeModals();
        			$( "#bcr_title_prompt" ).dialog("open");
        			$("#bcr_title_prompt .workDayDisplay").html("");
        			$("#workDaySelector").change(function() {
        				var $workDay = $("#workDaySelector").val();
        				if ( $workDay == null || $workDay == "") {
           					$("#bcr_title_prompt .workDateDisplay").html("");
            			} else {
            				var $selected = $("#workDaySelector option:selected");
            				$("#bcr_title_prompt .workDateDisplay").html($selected.attr("data-firstofmonth") + " - " + $selected.attr("data-lastofmonth"));
            			}
        			});      
        			
        			
					// populate the new claim expense type selector
					console.log("Populating new claim expense selector");

					var $select = $("#bcr_new_claim_modal select[name='expenseType']");
					$('option', $select).remove();
					$select.append(new Option("",""));
					$.each(BUDGETCONTROL.expenseTypes, function(index, val) {
					    $select.append(new Option(val.displayValue, val.value));
					});
					// this may have been some extra copy on the copy & Paste. Re-examine if something starts going wrong
					//$.each($data.data.expenses, function(index, val) {
					//	var $expenseSelect = "#" + val.claimId + " select[name='expenseType']";
					//	$($expenseSelect).val(val.passthruExpenseCode);
					//});
					
        		},
        		
        		
        		
        		
        		makeSummaryPanel : function($data) {
        			console.log("makeSummaryPanel");
        			var $totalVolume = $data.data.summary.totalVolume.toFixed(2);
        			var $volumeClaimed = $data.data.summary.volumeClaimed.toFixed(2);
        			var $volumeRemaining = $data.data.summary.volumeRemaining.toFixed(2);
        			var $expenseVolume = $data.data.summary.expenseVolume.toFixed(2);
        			
        			$("#div-summary .total-volume").html( $totalVolume );
        			$("#div-summary .volume-claimed").html( $volumeClaimed );
        			$("#div-summary .volume-remaining").html( $volumeRemaining );
        			$("#div-summary .expense-volume").html( $expenseVolume );
        			
        			var red = 'rgba(255, 99, 132, 1)';
        			var blue = 'rgba(54, 162, 235, 1)';
        			var orange = 'rgba(255, 206, 86, 1)';
					var ctx = $("#myChart");
					var myChart = new Chart(ctx, {
						type : 'pie',
						data : {
							labels: ['Volume Claimed', 'Expense Volume','Volume Remaining'],							
							datasets : [{
								label : "",
								data : [ $volumeClaimed, $expenseVolume, $volumeRemaining ],
								backgroundColor : [orange, blue, red],
								borderColor : [orange, blue, red],
								borderWidth: 1
							}]
						},
						options : {
							legend : { display:false, position:"bottom", boxWidth:12 }
						}
					});
        			//var volumeClaimedTotal = parseFloat($("#bcr_edit_modal").attr("volumeClaimedTotal"));
	            	//$("#div-summary .volume-claimed").html( volumeClaimedTotal.toFixed(2) );
	            	//$("#div-summary .volume-remaining").html( $data.data.ticket.volumeRemaining.toFixed(2) );
	            	//$("#div-summary .expense-volume").html( expenseTotal.toFixed(2) );
 					//BUDGETCONTROL.makeChart(volumeClaimedTotal, expenseTotal, $data.data.ticket.volumeRemaining);
        		},
        		
        		
        		
        		
        		populateActualDLPanel : function($data, $divisionId, $claimWeeks) {
        			console.log("populateActualDLPanel");
        			var $claimWeekArray = null;
        			$.each($claimWeeks, function($index, $value) {
        				var $weekNum = $value.split("-")[1];
        				if ( $claimWeekArray == null ) {
        					$claimWeekArray = $weekNum;
        				} else {
        					$claimWeekArray = $claimWeekArray + "," + $weekNum
        				}
        			});
        			$("#actual_dl_totals input").attr("data-divisionid",$divisionId);
        			$("#actual_dl_totals input").attr("data-previous","0.00");
        			$("#actual_dl_totals input").attr("data-claimweeks",$claimWeekArray);
        			$.each($data.weekActualDL, function($index, $value) {
        				var $actualDL = parseFloat($value.actualDL).toFixed(2);
        				var $actualOM = parseFloat($value.omDL).toFixed(2);
        				
        				// populate actual dl panel
        				var $actual = 'input[name="actualDL-'+$index+'"]';
        				var $om = 'input[name="omDL-'+$index+'"]';
        				$($actual).attr("data-previous",$actualDL);
        				//$($actual).attr("data-divisionid", $divisionId);
        				$($om).attr("data-previous",$actualOM);
        				//$($om).attr("data-divisionid", $divisionId);
        				$($actual).val($actualDL);
        				$($om).val($actualOM);
        			});        			
        			$(".actual-saving").hide();  // putting this in the CSS doesn't help b/c they don't exist yet
        		},
        		
        		
        		
        		
        		
        		populateBudgetControlTotalsPanel : function($data) {
        			console.log("populateBudgetControlTotalsPanel");
        			
        			// populate budget control totals panel with weekly data
        			var $dataToTable = {
        				"totalVolume":"total_volume",
        				"volumeClaimed":"volume_claimed",
        				//"dlAmt":"actual_dl",  This gets populated from "actual direct labor" totals panel
        				// "dlTotal":"",
        				// "passthruVolume":"",
        				"dlTotal":"total_dl_claimed",
        				"volumeRemaining":"claimed_volume_remaining",
        				"billedAmount":"total_billed",
        				"claimedVsBilled":"variance",
        				"dlPercentage":"dl_percentage",
        				"actualDlPercentage":"actual_dl_percentage",
        			};
        			
        			$.each($data.data.weekTotals, function($indexWk, $weekTotal) {
        				var $weekNum = $weekTotal['claimWeek'].split("-")[1];
        				$weekNum = parseInt($weekNum); //claimWeek is nnnn-nn (2-digit week number). Cast to int so "03" becomes "3"
        				$.each($dataToTable, function($source, $destination) {
        					var $weekSelector = "#bcr_totals ." + $destination +"_week" + $weekNum;
        					try {
        						$($weekSelector).html( $weekTotal[$source].toFixed(2) );
        					} catch(err) {
        						$($weekSelector).html( "0.00" );
        					}
        				});
        			});
    				$.each($dataToTable, function($source, $destination) {
        				var $monthSelector = "#bcr_totals ." + $destination + "_total";
    					try {
    						$($monthSelector).html( $data.data.monthTotal[$source].toFixed(2) );
    					} catch(err) {
    						$($monthSelector).html( "0.00" );
    					}
    				});
        			
        			
        			
        			// populate actual DL panel with actual dl data
        			BUDGETCONTROL.populateActualDLPanel($data.data.actualDl, $data.data.divisionId, $data.data.claimWeeks);
        			// populate budget control panel actual dl rows
        			$.each($data.data.actualDl.weekActualDL, function($weekNum, $value) {
        				var $actualDL = parseFloat($value.actualDL).toFixed(2);
        				var $dlSelector = "#bcr_totals .actual_dl_week" + $weekNum;
        				$($dlSelector).html($actualDL);
        				var $actualOM = parseFloat($value.omDL).toFixed(2);
        				var $omSelector = "#bcr_totals .actual_om_dl_week" + $weekNum;
        				$($omSelector).html($actualOM);
        				var $totalDL = parseFloat($value.totalDL).toFixed(2);
        				var $totSelector = "#bcr_totals .total_actual_dl_week" + $weekNum;
        				$($totSelector).html($totalDL);
        			});
        			// populate budget control panel actual dl totals
        			$("#bcr_totals .actual_dl_total").html($data.data.actualDl.totalActualDL.actualDL.toFixed(2));
        			$("#bcr_totals .actual_om_dl_total").html($data.data.actualDl.totalActualDL.omDL.toFixed(2));
        			$("#bcr_totals .total_actual_dl_total").html($data.data.actualDl.totalActualDL.totalDL.toFixed(2));
        			
        			$("#bcr_totals .dl_percentage_total").html($data.data.monthTotal.dlPercentage.toFixed(2)); 
        			
        			try {
        				$("#bcr_totals .actual_dl_percentage_total").html($data.data.monthTotal.actualDlPercentage.toFixed(2));
        			} catch(err) {
        				$("#bcr_totals .actual_dl_percentage_total").html("0.00");
        			}
        		},
        		
        		
        		
        		
        		populateEmployeePanel : function($data) {
        			console.log("populateEmployeePanel");
        			
        			$("#bcr_employees tbody").html(""); // this is so the refresh works
        			
        			$.each($data.data.employees, function($index, $value) {
        				var $employeeRow = $('<tr class="employee-row">');
        				$employeeRow.append( $("<td>").append($value.employee) );
        				$employeeRow.append( $("<td>").append("&nbsp;") );  // spacer to account for unclaimed column in budget control total panel
        				$.each($data.data.claimWeeks, function($index, $claimWeek) {
        					if ( $claimWeek in $value.weeklyClaimedVolume ) {
        						$employeeRow.append( $("<td>").addClass("aligned-right").append($value.weeklyClaimedVolume[$claimWeek].toFixed(2)) );
        					} else {
        						$employeeRow.append( $("<td>").addClass("aligned-right").append("0.00") );
        					}
        					if ( $claimWeek in $value.weeklyClaimedDL ) {
        						$employeeRow.append( $('<td class="border-set">').addClass("aligned-right").append($value.weeklyClaimedDL[$claimWeek].toFixed(2)) );
        					} else {
        						$employeeRow.append( $('<td class="border-set">').addClass("aligned-right").append("0.00") );
        					}
        				});
        				$employeeRow.append( $("<td>").addClass("aligned-right").append($value.totalClaimedVolume.toFixed(2)) );
        				$employeeRow.append( $("<td>").addClass("aligned-right").append($value.totalClaimedDL.toFixed(2)) );
        				$("#bcr_employees tbody").append($employeeRow);
        			});
        			
        			var $footerRow = $('<tr class="employee-row" style="border-top:solid 1px #404040;">');
					$footerRow.append( $("<td>").append("Total Assigned D/L - All Employees"));
					$footerRow.append( $("<td>").append("&nbsp;"));  // spacer to account for unclaimed column
					$.each($data.data.claimWeeks, function($index, $claimWeek) {
						if ( $claimWeek in $data.data.monthlyTotal.weeklyClaimedVolume ) {
							$footerRow.append( $("<td>").addClass("aligned-right").append($data.data.monthlyTotal.weeklyClaimedVolume[$claimWeek].toFixed(2)) );
						} else {
							$footerRow.append( $("<td>").addClass("aligned-right").append("0.00") );
						}
						if ( $claimWeek in $data.data.monthlyTotal.weeklyClaimedDL ) {
							$footerRow.append( $('<td class="border-set">').addClass("aligned-right").append($data.data.monthlyTotal.weeklyClaimedDL[$claimWeek].toFixed(2)) );
						} else {
							$footerRow.append( $('<td class="border-set">').addClass("aligned-right").append("0.00") );
						}
					});
					$footerRow.append( $("<td>").addClass("aligned-right").append($data.data.monthlyTotal.totalClaimedVolume.toFixed(2)) );
    				$footerRow.append( $("<td>").addClass("aligned-right").append($data.data.monthlyTotal.totalClaimedDL.toFixed(2)) );
    				$("#bcr_employees tfoot").html($footerRow);
    				
    				
    				$(".employee-row").mouseover(function($event) {
						$(this).css('background-color','#E5E5E5');						
					});
					$(".employee-row").mouseout(function($event) {
						$(this).css('background-color','transparent');
					});
        		},
        		
        		
        		
        		populateTicketTables : function($data) {
        			console.log("populateTicketTables");
        			var $weekList = []
        			$.each($data.data.workCalendar, function($index, $value) {
        				$weekList.push($value.weekOfYear);
        			});
        			var $workWeeks = $weekList.join(",");
        			$.each($data.data.workCalendar, function($index, $value) {
        				var $destination = "#ticketTable" + $value.weekOfMonth;
        				var $outbound = {"divisionId":$data.data.divisionId, "workYear":$data.data.workYear,"workWeeks":$workWeeks,"workWeek":$value.weekOfYear};
        				BUDGETCONTROL.doTicketLookup($destination, "bcr/weeklyTicketList", $outbound);
        			});
        			var $outbound = {"divisionId":$data.data.divisionId, "workYear":$data.data.workYear, "workWeeks":$workWeeks};
        			BUDGETCONTROL.doTicketLookup("#ticketTable","bcr/ticketList", $outbound);
        			console.log("ticket table map:");
        			console.log(BUDGETCONTROL.ticketTable);
        		},
        		
        		
        		populateTitlePanel : function($data) {
        			$("#titleHeader").html($data.data.workMonthName + ", " + $data.data.workYear + " -- " + $data.data.div);        			
					$("#bcr_summary .dateCreated").html($data.data.dateCreated);
					$("#bcr_summary .dateModified").html($data.data.dateModified);
					$("#bcr_summary .workYear").html($data.data.workYear);
					$("#bcr_summary .workMonth").html($data.data.workMonth);
					$("#bcr_summary .workMonthName").html($data.data.workMonthName);
					$("#bcr_summary .firstOfMonth").html($data.data.firstOfMonth);
					$("#bcr_summary .lastOfMonth").html($data.data.lastOfMonth);
					$("#bcr_summary .div").html($data.data.div);
					$("#bcr_summary .managerFirstName").html($data.data.managerFirstName);
					$("#bcr_summary .managerLastName").html($data.data.managerLastName);
					
					$("#bcr_summary .new-bcr").click(function($event) {
						$( "#bcr_title_prompt" ).dialog("open");
					});
					
					var $workWeeks = [];
					$.each($data.data.workCalendar, function($index, $value) {
						$workWeeks.push($value.weekOfYear);
					});
					var $parms = [];
					$parms.push("divisionId="+$data.data.divisionId);
					$parms.push("workYear=" + $data.data.workYear);
					$parms.push("workWeeks=" + $workWeeks.join(","));
					
					var $url = "bcr/ticketXls?" + $parms.join("&");
					console.log($url);
					$("#bcr_summary .all-ticket-spreadsheet").attr("href",$url);
        		},
        		
        		
        		
        		refreshPanels : function($data) {
        			console.log("refreshPanels");
        			var $workWeeks = BUDGETCONTROL.workWeek;
        			var $outbound = {"divisionId":BUDGETCONTROL.divisionId, "workYear":$data.data.claimYear, "workWeek":$workWeeks};
        			ANSI_UTILS.doServerCall("GET", "bcr/bcTotals", $outbound, BUDGETCONTROL.populateBudgetControlTotalsPanel, BUDGETCONTROL.bcrError);
        			ANSI_UTILS.doServerCall("GET", "bcr/employees", $outbound, BUDGETCONTROL.populateEmployeePanel, BUDGETCONTROL.bcrError);
        			BUDGETCONTROL.refreshTicketTables();
        		},
        		
        		
        		
        		
        		refreshTicketTables : function() {
        			console.log("refreshTicketTables");
        			$.each(BUDGETCONTROL.ticketTable, function($index, $value) {
						$($index).DataTable().ajax.reload();        				
        			});
        		},

        		
        		
        		saveNewClaim : function() {
					var $claimType = $("#bcr_new_claim_modal").attr("data-claimtype");
        			console.log("saveNewClaim: " + $claimType);
        			$("#bcr_new_claim_modal .err").html("");
        			
        			var $ticketId = $("#bcr_new_claim_modal .directLaborDetails input[name='ticketId']").val();
        			var $serviceTagId = $("#bcr_new_claim_modal .directLaborDetails input[name='serviceTypeId']").val();
        			
        			if ( $claimType == "labor") {        			
            			var $claimWeek = $("#bcr_new_claim_modal .directLaborDetails select[name='claimWeek']").val();
            			
            			var $dlAmt = $("#bcr_new_claim_modal .directLaborDetails input[name='dlAmt']").val();
            			var $volumeClaimed = $("#bcr_new_claim_modal .directLaborDetails input[name='volumeClaimed']").val();
            			var $employee = $("#bcr_new_claim_modal .directLaborDetails input[name='employee']").val();
            			var $notes = $("#bcr_new_claim_modal .directLaborDetails input[name='notes']").val();
            			
            			// these are needed to create the correct response, not to do the update
            			var $divisionId = BUDGETCONTROL.divisionId
            			var $workYear = BUDGETCONTROL.workYear; 
            			var $workWeeks = BUDGETCONTROL.workWeek;
            			
            			var $outbound = {
            					"divisionId":$divisionId,
            					"ticketId":$ticketId,
                    			"serviceTagId":$serviceTagId,
                    			"claimWeek":$claimWeek,
                    			"dlAmt":$dlAmt,
                    			"volumeClaimed":$volumeClaimed,
                    			"employee":$employee,
                    			"notes":$notes,
                    			"workYear":$workYear,
                    			"workWeeks":$workWeeks,
            			}
            			console.log($outbound);
            			ANSI_UTILS.doServerCall("POST", "bcr/ticketClaim", JSON.stringify($outbound), BUDGETCONTROL.saveNewClaimSuccess, BUDGETCONTROL.saveNewClaimFail);
        			} else if ( $claimType == "expense" ) {
						var $claimWeek = $("#bcr_new_claim_modal .expenseDetails select[name='claimWeek']").val();            			
            			var $volume = $("#bcr_new_claim_modal .expenseDetails input[name='volume']").val();
            			var $expenseType = $("#bcr_new_claim_modal .expenseDetails select[name='expenseType']").val();
            			var $notes = $("#bcr_new_claim_modal .expenseDetails input[name='notes']").val();
            			
            			console.log($claimWeek + "\t" + $volume + "\t" + $expenseType + "\t" + $notes);
            			
            			// these are needed to create the correct response, not to do the update
            			var $divisionId = BUDGETCONTROL.divisionId
            			var $workYear = BUDGETCONTROL.workYear; 
            			var $workWeeks = BUDGETCONTROL.workWeek;

            			var $outbound = {
            					"divisionId":$divisionId,
            					"ticketId":$ticketId,
                    			"serviceTagId":$serviceTagId,
                    			"claimWeek":$claimWeek,
                    			"volume":$volume,
                    			"expenseType":$expenseType,
                    			"notes":$notes,
                    			"workYear":$workYear,
                    			"workWeeks":$workWeeks,
            			}
            			console.log($outbound);
            			ANSI_UTILS.doServerCall("POST", "bcr/expense", JSON.stringify($outbound), BUDGETCONTROL.saveNewClaimSuccess, BUDGETCONTROL.saveNewClaimFail);
        			} else {
        				$("#bcr_new_claim_modal .newClaimErr").html("Invalid system state. (Claim Type: " + $claimType + ")  Reload page and try again");
        			}

        		},
        		
        		
        		saveNewClaimFail : function($data) {
        			console.log("saveNewClaimFail");
        			$.each($data.data.webMessages, function($index, $value) {
        				var $selector = "#bcr_new_claim_modal ." + $index + "Err";
        				console.log($selector);
        				$($selector).html($value[0]);
        			});
        		},
        		
        		
        		saveNewClaimSuccess : function($data) {
        			console.log("saveNewClaimSuccess");
        			$("#bcr_new_claim_modal").dialog("close");
        			$("#globalMsg").html("Update Complete").show().fadeOut(4000);
        			BUDGETCONTROL.lastEmployeeEntered = $("#bcr_new_claim_modal input[name='employee']").val();
        			BUDGETCONTROL.lastNoteEntered = $("#bcr_new_claim_modal input[name='notes']").val();
        			BUDGETCONTROL.lastWorkWeekEntered = $("#bcr_new_claim_modal select[name='claimWeek']").val();
        			BUDGETCONTROL.refreshTicketTables();
        		},
        		
        		
        		
        		
        		saveOnEnter : function() {
	    			$("#bcr_new_claim_modal input").keydown(function($event) {
						if ( $event.which == 13 ) {
							// when you hit "enter" on an input, click the go button
	 						$event.preventDefault();
	 						$("#bcr_new_claim_save").click();
						}
					});
	    			$("#bcr_new_claim_modal select").keydown(function($event) {
						if ( $event.which == 13 ) {
							// when you hit "enter" on an input, click the go button
	 						$event.preventDefault();
	 						$("#bcr_new_claim_save").click();
						}
					});
        		},
    			
    			
    			
        		ticketEditSave : function() {
        			console.log("ticketEditSave");
        			var $outbound = ANSI_UTILS.form2outbound("#bcr_edit_modal",{});
        			console.log(JSON.stringify($outbound));
        			var $ticketId = $outbound['ticketId'];
        			var $url = "bcr/ticketClaim/" + $ticketId;
        			$url = $url + "?divisionId=" +BUDGETCONTROL.divisionId+ "&workYear="+BUDGETCONTROL.workYear+"&workWeeks="+BUDGETCONTROL.workWeek;
        			ANSI_UTILS.doServerCall("POST", $url, JSON.stringify($outbound), BUDGETCONTROL.ticketEditSuccess, BUDGETCONTROL.ticketEditFail);
        		},
        		
        		
        		
        		ticketEditFail : function($data) {
        			console.log("ticketEditFail");
        			$.each($data.data.webMessages, function($index, $value) {
        				var $selector = "#bcr_edit_modal ." + $index + "Err";
        				console.log($selector);
        				console.log($value[0]);
        				$($selector).html($value[0]);
        			});
        		},
        		
        		
        		
        		ticketEditSuccess : function($data) {
        			console.log("ticketEditSuccess");
     				
     				$.each( BUDGETCONTROL.ticketTable, function($index, $value) {
     					console.log("reloading: " + $index);
     					$($index).DataTable().ajax.reload();
     				});
     				var $outbound = {"divisionId":BUDGETCONTROL.divisionId, "workYear":BUDGETCONTROL.workYear, "workWeek":BUDGETCONTROL.workWeek};        			        			
        			ANSI_UTILS.doServerCall("GET", "bcr/bcTotals", $outbound, BUDGETCONTROL.populateBudgetControlTotalsPanel, BUDGETCONTROL.bcrError);
        			ANSI_UTILS.doServerCall("GET", "bcr/employees", $outbound, BUDGETCONTROL.populateEmployeePanel, BUDGETCONTROL.bcrError);
        			$("#bcr_edit_modal").dialog("close");
        		},
        		
        		

        		
        		titleSave : function() {
        			console.log("titleSave");
        			$("#bcr_title_prompt .err").html("");
        			var $divisionId = $("#bcr_title_prompt select[name='divisionId']").val();
        			$outbound = {"divisionId":$divisionId, "workDate":$("#workDaySelector").val()};
        			ANSI_UTILS.doServerCall("POST", "bcr/title", JSON.stringify($outbound), BUDGETCONTROL.titleSaveSuccess, BUDGETCONTROL.titleSaveFail);
        		},
        		
        		
        		
        		
        		
        		
        		titleSaveFail : function($data) {
        			console.log("titleSaveFail");
        			$.each($data.data.webMessages, function($key, $value) {        				
        				$("#bcr_title_prompt ."+$key+"Err").html($value[0]);
        			});
        		},
        		
        		
        		titleSaveSuccess : function($data) {
        			console.log("titleSaveSuccess");
        			BUDGETCONTROL.title = $data.data; // this is a duplicate of some of the BUDGETCONTROL.* items, some cleanup would be a good idea but ... priorities
        			
					$weekList = [];        			
        			$.each($data.data.workCalendar, function($index, $value) {
        				$weekList.push($value.weekOfYear);
        			});
        			
        			var $workWeeks = $weekList.join(",");
        			BUDGETCONTROL.divisionId = $data.data.divisionId;
        			BUDGETCONTROL.workYear = $data.data.workYear; 
        			BUDGETCONTROL.workWeek = $workWeeks;
        			BUDGETCONTROL.workCalendar = $data.data.workCalendar;
        			
        			BUDGETCONTROL.initializeActualDLPanel($data);        			
        			BUDGETCONTROL.initializeBudgetControlTotalsPanel($data);
        			BUDGETCONTROL.initializeEmployeePanel($data);
        			BUDGETCONTROL.populateTicketTables($data);
        			BUDGETCONTROL.populateTitlePanel($data);
        			var $outbound = {"divisionId":$data.data.divisionId, "workYear":$data.data.workYear, "workWeek":$workWeeks};        			
        			// ANSI_UTILS.doServerCall("GET", "bcr/actualDL", $outbound, BUDGETCONTROL.populateActualDLPanel, BUDGETCONTROL.bcrError);
        			ANSI_UTILS.doServerCall("GET", "bcr/bcTotals", $outbound, BUDGETCONTROL.populateBudgetControlTotalsPanel, BUDGETCONTROL.bcrError);
        			ANSI_UTILS.doServerCall("GET", "bcr/employees", $outbound, BUDGETCONTROL.populateEmployeePanel, BUDGETCONTROL.bcrError);


					$.each($data.data.workCalendar, function($index, $value) {
						var $panelId = "#bcr_panels .ticket-display-week" + $value.weekOfMonth;
						var $labelId = "h4 .week" + $value.weekOfMonth;
						$($labelId).html("Week " + $value.weekOfYear + " | " + $value.firstOfWeek + "-" + $value.lastOfWeek);
						$($panelId).show();
					});
        			
					var $select = $("#bcr_new_claim_modal select[name='claimWeek']");
					$('option', $select).remove();
					$select.append(new Option("",""));
					console.log("Populating new claim week selector");
					$.each(BUDGETCONTROL.workCalendar, function($index, $val) {
						var $displayWeek = String($val.weekOfYear);
						if ( $val.weekOfYear < 10 ) {
							$displayWeek = "0" + String($val.weekOfYear);
						}
						var $displayValue = BUDGETCONTROL.workYear + "-" + $displayWeek;
						console.log($displayValue);
					    $select.append(new Option($displayValue, $displayValue));
					});

					
					$("#bcr_panels .thinking").hide();
					$("#bcr_panels .display").show();
        			$("#bcr_title_prompt").dialog("close");
        			$("#titleHeader").click();
        			
        		},
        		
        		
        		updateActualFail : function($data) {
        			console.log("updateActualFail");
        			$("#globalMsg").html($data.data.webMessages["GLOBAL_MESSAGE"]).show();
        		},
        		
        		updateActualSuccess : function($data) {
        			console.log("updateActualSuccess");
        			BUDGETCONTROL.populateBudgetControlTotalsPanel($data);
        			$("#globalMsg").html("D/L Updated").show().fadeOut(3000);
        		}
        	};
        	

        	BUDGETCONTROL.init();
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Budget Control</h1>
    	
    	<ul id="bcr_panels" class="accordionList">
    		<li class="accordionItem">    			
        		<h4 class="accHdr" id="titleHeader">Title</h4>
        		<div id="bcr_summary">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
        			<div class="display">
        				<bcr:title />        				
        			</div>
        		</div>
       		</li>
    		<li class="accordionItem">    			
        		<h4 class="accHdr">Actual Direct Labor Totals</h4>
        		<div id="actual_dl_totals">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
        			<div class="display">
        				<bcr:actualDLTotals />
        			</div>
        		</div>
       		</li>
    		<li class="accordionItem">    			
        		<h4 class="accHdr">Budget Control Totals</h4>
        		<div id="bcr_totals">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	       				<bcr:budgetControlTotals />
	       			</div>
        		</div>
       		</li>
    		<li class="accordionItem">    			
        		<h4 class="accHdr">Employees</h4>
        		<div id="bcr_employees">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	       				<bcr:employees />
	       			</div>
        		</div>
       		</li>
       		<li class="accordionItem">    			
        		<h4 class="accHdr">All Tickets</h4>
        		<div id="bcr_tickets">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	        			<bcr:ticketTable id="ticketTable" />	       				
	       			</div>
        		</div>
       		</li>
       		<li class="accordionItem ticket-week-display ticket-display-week1">    			
        		<h4 class="accHdr"><span class="week1">Week 1</span></h4>
        		<div id="bcr_tickets_week1">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	        			<bcr:ticketTable id="ticketTable1" />	       				
	       			</div>
        		</div>
       		</li>
       		<li class="accordionItem ticket-week-display ticket-display-week2">    			
        		<h4 class="accHdr"><span class="week2">Week 2</span></h4>
        		<div id="bcr_tickets_week2">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	        			<bcr:ticketTable id="ticketTable2" />	       				
	       			</div>
        		</div>
       		</li>
       		<li class="accordionItem ticket-week-display ticket-display-week3">    			
        		<h4 class="accHdr"><span class="week3">Week 3</span></h4>
        		<div id="bcr_tickets_week3">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	        			<bcr:ticketTable id="ticketTable3" />	       				
	       			</div>
        		</div>
       		</li>
       		<li class="accordionItem ticket-week-display ticket-display-week4">    			
        		<h4 class="accHdr"><span class="week4">Week 4</span></h4>
        		<div id="bcr_tickets_week4">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	        			<bcr:ticketTable id="ticketTable4" />	       				
	       			</div>
        		</div>
       		</li>
       		<li class="accordionItem ticket-week-display ticket-display-week5">    			
        		<h4 class="accHdr"><span class="week5">Week 5</span></h4>
        		<div id="bcr_tickets_week1">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	        			<bcr:ticketTable id="ticketTable5"  />	       				
	       			</div>
        		</div>
       		</li>
   		</ul>
   		
   	    <webthing:lookupFilter filterContainer="filter-container" />
    	
	    <webthing:scrolltop />
    
	    <webthing:ticketModal ticketContainer="ticket-modal" />
	    
	    
	    <div id="bcr_title_prompt">
	    	<table>
	    		<tr>
	    			<td><span class="form-label">Division:</span></td>
	    			<td><select name="divisionId"></select></td>
	    			<td><span class="err divisionIdErr"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Date:</span></td>
	    			<td><select id="workDaySelector" name="workDate"></select></td>
	    			<td><span class="err workDateErr"></span></td>	    			
	    		</tr>
	    		<tr>
	    			<td>&nbsp;</td>
	    			<td><span class="workDateDisplay"></span></td>
	    		</tr>
	    	</table>
	    </div>
	    
	    
	    <div id="bcr_delete_confirmation_modal">
	    	<div style="width:100%; text-align:center;">
	    		<h2>Are you sure?</h2>
	    	</div>
	    </div>
	    
	    <div id="session_expire_modal">
	    	<div style="width:100%; text-align:center;">
	    		<h3>Session Expired.</h3>
	    	</div>
	    </div>
	    
	    
	    <div id="bcr_edit_modal">
	    	<div style="width:45%; float:right;">
	    		<span class="err bcr_edit_message"></span>
	    	</div>
	    	<table>
	    		<tr>
	    			<td><span class="form-label">Claim Week:</span></td>
	    			<td><span class="claim-week"></span><input type="hidden" name="claimWeek" /></td>
	    			<td>&nbsp;</td>
	    			<td><span class="form-label">Total Volume:</span></td>
	    			<td><span class="totalVolume"></span></td>
    			</tr>
	    	</table>
	    	
	    	<div style="margin-top:12px; border-top:solid 2px #000000; border-bottom:solid 2px #000000; width:100%;">
	    		<div style="background-color:#CCCCCC; font-weight:bold;padding-top:3px; padding-bottom:3px;">Direct Labor Claims</div>
	    		<table id="dl-claim-table">
	    			<thead></thead>
	    			<tbody></tbody>
	    			<tfoot>
	    				<tr>
	    					<th></th>
	    					<th></th>
	    					<th></th>
	    					<th></th>
	    					<th></th>
	    					<th></th>
	    					<th></th>
	    					<th></th>
	    					<th></th>
	    					<th></th>
	    					<th></th>
	    					<th></th>
	    				</tr>
	    			</tfoot>
	    		</table>
	    	</div>
	    	
	    	<div style="clear:both; width:100%; border-top:solid 2px #000000;">&nbsp;</div>
	    	
	    	<div id="div-summary" style="float:right; margin-top:12px; border-top:solid 2px #000000; border-bottom:solid 2px #000000; width:48%;">
	    		<div style="background-color:#CCCCCC; font-weight:bold;padding-top:3px; padding-bottom:3px;">Summary</div>
	    		
	    		<div style="width:35%; float:right; margin-right:10%;">
	    			<canvas id="myChart" style="width:100px; height:100px;"></canvas>
	    		</div>
	    		
	    		<table style="width:48%; float:left;">
	    			<tr>
	    				<td style="width:75%; text-align:left; padding:3px;"><span class="form-label">Total Volume:</span></td>
	    				<td style="width:25%; text-align:right; padding:3px;"><span class="total-volume"></span></td>
	    			</tr>
	    			<tr>
	    				<td style="width:75%; text-align:left; padding:3px;"><span class="form-label">Volume Claimed:</span></td>
	    				<td style="width:25%; text-align:right; padding:3px; background:rgb(255,206,86)"><span class="volume-claimed"></span></td>
	    			</tr>
	    			<tr>
	    				<td style="width:75%; text-align:left; padding:3px;"><span class="form-label">Expense Volume:</span></td>
	    				<td style="width:25%; text-align:right; padding:3px; background:rgb(54, 162, 235)"><span class="expense-volume"></span></td>
	    			</tr>
	    			<tr>
	    				<td style="width:75%; text-align:left; padding:3px;"><span class="form-label">Volume Remaining:</span></td>
	    				<td style="width:25%; text-align:right; padding:3px; background:rgb(255,99,132)"><span class="volume-remaining"></span></td>
	    			</tr>
	    		</table>
	    	</div>
	    	
	    	<div style="float:left; margin-top:12px; border-top:solid 2px #000000; border-bottom:solid 2px #000000; width:48%;">
	    		<div style="background-color:#CCCCCC; font-weight:bold;padding-top:3px; padding-bottom:3px;">Expense Volume</div>
	    		<table id="dl-expense-table">
	    			<thead></thead>
	    			<tbody></tbody>
	    			<tfoot>
	    				<tr>
	    					<th></th>
	    					<th></th>
	    					<th></th>
	    					<th></th>
	    					<th></th>
							<th></th>
							<th></th>
	    				</tr>
	    			</tfoot>
	    		</table>
	    	</div>
	    		    	
	    </div>
	    
	    
	    <div id="bcr_new_claim_modal">
	    	<div style="width:100%; height:25px;">
	    		<span class="newClaimErr err"></span>
	    	</div>	    	
			<table style="width:100%;">   
   				<tr>
   					<td style="width:20%;" rowspan="3">&nbsp;</td>
   					<td style="width:20%;" class="form-label">Ticket:</td>
   					<td style="width:20%;" ><span class="ticketId"></span></td>
   					<td style="width:20%;" ><input type="hidden" name="ticketId" /></td>
   					<td style="width:20%;" rowspan="3">&nbsp;</td>
   				</tr>
   				<tr>
   					<td class="form-label">Service Type:</td>
   					<td><span class="serviceTagId"></span></td>
   					<td><input type="hidden" name="serviceTypeId" /></td>
   				</tr>
   				<tr>
   					<td class="form-label">Claim Week:</td>
   					<td><select name="claimWeek" tabindex="1"></select></td>
   					<td><span class="claimWeekErr err"></span></td>
   				</tr>
			</table>
			
			<table style="width:100%;border-top:solid 1px #404040;">
				<colgroup>
		        	<col style="width:16%;" />
		        	<col style="width:16%;" />
		        	<col style="width:16%;" />
		        	<col style="width:2%;" />
		        	<col style="width:2%;" />
		        	<col style="width:16%;" />
		        	<col style="width:16%;" />
		        	<col style="width:16%;" />
				</colgroup>
   				<tr>
   					<td colspan="3" style="text-align:center; width:48%;"><span class="form-label">Direct Labor</span></td>
   					<td rowspan="5" style="width:2%;">&nbsp;</td>
   					<td rowspan="5" style="width:2%; border-left:solid 1px #404040;">&nbsp;</td>
   					<td colspan="3" style="text-align:center; width:48%;"><span class="form-label">Expense</span></td>
   				</tr>
   				<tr>
   					<td class="form-label">Direct Labor:</td>
   					<td><input type="text" name="dlAmt" tabindex="1" /></td>
   					<td><span class="dlAmtErr err"></span></td>
   					
   					<td class="form-label">Expense Volume Claimed:</td>
   					<td><input type="text" name="expenseVolume"  tabindex="5"/></td>
   					<td><span class="volumeErr err"></span></td>
   				</tr>
   				<tr>
   					<td class="form-label">Volume Claimed:</td>
   					<td><input type="text" name="volumeClaimed" tabindex="2" /></td>
   					<td><span class="volumeClaimedErr err"></span></td>
   					
   					<td class="form-label">Expense Type:</td>
   					<td><select name="expenseType" tabindex="6"></select></td>
   					<td><span class="expenseTypeErr err"></span></td>
   				</tr>
   				<tr>
   					<td class="form-label">Employee:</td>
   					<td><input type="text" name="employee" tabindex="3" /></td>
   					<td><span class="employeeErr err"></span></td>
   					<td>&nbsp;</td>
   					<td>&nbsp;</td>
   					<td>&nbsp;</td>
   				</tr>
   				<tr>
   					<td class="form-label">Notes:</td>
   					<td><input type="text" name="laborNotes" tabindex="4" /></td>
   					<td><span class="laborNotesErr err"></span></td>
   					
   					<td class="form-label">Notes:</td>
   					<td><input type="text" name="expenseNotes" tabindex="7" /></td>
   					<td><span class="expenseNotesErr err"></span></td>
   				</tr>
   			</table>
			
			
	    </div>
    </tiles:put>
		
</tiles:insert>

