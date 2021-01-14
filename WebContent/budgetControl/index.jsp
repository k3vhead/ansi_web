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
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/ticket.js"></script> 
        <style type="text/css">
        	#bcr_edit_modal {
        		display:none;
        	}
        	#bcr_title_prompt {
        		display:none;
        	}
			#bcr_panels .display {
				display:none;
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
        	.bcr_employees_display .column-header {
				font-weight:bold;
			}
			.bcr_employees_display th {
				text-align:right;
			}
			.bcr_totals_display .column-header {
				font-weight:bold;
			}
			.bcr_totals_display th {
				text-align:right;
			}
        	.form-label {
				font-weight:bold;
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
    			// this holds the ticket panel datatables
        		ticketTable : {},        		
        		
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
				
				
        		
        		doFunctionBinding : function() {
        			console.log("doFunctionBinding");
        			$(".ticket-clicker").on("click", function($clickevent) {
    					$clickevent.preventDefault();
    					var $ticketId = $(this).attr("data-id");
    					TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
    					$("#ticket-modal").dialog("open");
    				});	
        			
        			$(".bcr-edit-clicker").on("click", function($clickevent) {
        				$clickevent.preventDefault();
        				var $ticketId = $(this).attr("data-ticketid");
        				var $divisionId = $(this).attr("data-divisionid");
        				var $workYear = $(this).attr("data-workYear");
        				var $workWeeks = $(this).attr("data-workWeeks");
        				var $outbound = {"divisionId":$divisionId, "workYear":$workYear, "workWeeks":$workWeeks};
        				console.log("edit clicked: " + $ticketId);
        				console.log($outbound);
        				var $url = "bcr/ticket/" + $ticketId;
        				ANSI_UTILS.doServerCall("GET", $url, $outbound, BUDGETCONTROL.getTicketDetailSuccess, BUDGETCONTROL.getTicketDetailFail);
        			});
        		},
        		
        		
        		
        		doTicketLookup : function($destination,$url, $outbound) {
        			var $weekNum = $outbound['workWeek'];
        			if ( $weekNum==null || $weekNum=='') {
        				$fileName = "All Tickets";
        			} else {
        				$fileName = "Tickets Week " + $weekNum;
        			}
        			console.log("doTicketLookup " + $fileName + "  " + $destination);
        			
        			
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
            	        dom: 				'Bfrtip',
            	        "searching": 		true,
            	        "searchDelay":		800,
            	        lengthMenu: [
            	        	[ 10, 50, 100, 500, 1000 ],
            	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
            	        ],
            	        buttons: [
            	        	'pageLength',
            	        	'copy', 
            	        	{extend:'csv', filename:'* ' + $fileName}, 
            	        	{extend:'excel', filename:'* ' + $fileName}, 
            	        	{extend:'pdfHtml5', orientation: 'landscape', filename:'* ' + $fileName}, 
            	        	'print',
            	        	{extend:'colvis', label: function () {doFunctionBinding();$('#ticketTable').draw();}}
            	        ],
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
    			        	"type": "GET",
    			        	"data": $outbound,
    			        	},
    			        columns: [
    			        	{ title: "Account", width:"15%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.job_site_name != null){return ('<a href="#" class="bcr-edit-clicker" data-ticketid="'+row.ticket_id+'" data-workyear="'+$outbound['workYear']+'" data-workweeks="'+$outbound['workWeeks']+'" data-divisionid="'+$outbound['divisionId']+'">'+row.job_site_name+'</a>');}
    			            } },
    			            { title: "Ticket Number", width:"6%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.ticket_id != null){return ('<a href="#" data-id="'+row.ticket_id+'" class="ticket-clicker">'+row.ticket_id+'</a>');}
    			            } },
    			            { title: "Claim Week", width:"4%", searchable:true, searchFormat: "First Last Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.claim_week != null ){return row.claim_week;}    			            	
    			            	//return BUDGETCONTROL.makeClaimWeekSelect(row.ticket_id, $data.data.workCalendar);
    			            } },
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
    			            { title: "Service",  width:"4%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.service_tag_id != null){return (row.service_tag_id+"");}
    			            } },
    			            { title: "Equipment",  width:"4%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.equipment_tags != null){return (row.equipment_tags+"");}
    			            } },
    			            { title: "Employee",  width:"13%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.employee != null){return (row.employee+"");}
    			            } }
    			            ],
    			            "initComplete": function(settings, json) {
    			            	var myTable = this;
    			            	//LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#ticketTable", CALL_NOTE_LOOKUP.makeTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	//CALL_NOTE_LOOKUP.doFunctionBinding();
    			            	BUDGETCONTROL.doFunctionBinding();
    			            }
    			    } );
        		},
        		
        		
        		
        		getTicketDetailSuccess : function($data) {
					console.log("getTicketDetailSuccess");  
					
					var $select = $("#bcr_edit_modal select[name='claimWeek']");
					$('option', $select).remove();
					$select.append(new Option("",""));
					$.each($data.data.claimWeeks, function(index, val) {
						console.log(val);
					    $select.append(new Option(val, val));
					});	
					
					$("#bcr_edit_modal .err").html("");
					
        			$("#bcr_edit_modal .jobSiteName").html($data.data.ticket.jobSiteName);
        			$("#bcr_edit_modal .ticketId").html($data.data.ticket.ticketId);
        			$("#bcr_edit_modal input[name='ticketId']").val($data.data.ticket.ticketId);
        			$("#bcr_edit_modal select[name='claimWeek']").val($data.data.ticket.claimWeek);
        			$("#bcr_edit_modal input[name='dlAmt']").val($data.data.ticket.dlAmt.toFixed(2));
        			$("#bcr_edit_modal input[name='totalVolume']").val($data.data.ticket.totalVolume.toFixed(2));
        			$("#bcr_edit_modal input[name='volumeClaimed']").val($data.data.ticket.volumeClaimed.toFixed(2));
        			$("#bcr_edit_modal .volumeRemaining").html($data.data.ticket.volumeRemaining.toFixed(2));
        			$("#bcr_edit_modal input[name='notes']").val($data.data.ticket.notes);
        			$("#bcr_edit_modal input[name='billedAmount']").val($data.data.ticket.billedAmount.toFixed(2));
        			$("#bcr_edit_modal .claimedVsBilled").html($data.data.ticket.claimedVsBilled.toFixed(2));
        			$("#bcr_edit_modal .ticketStatus").html($data.data.ticket.ticketStatus);
        			$("#bcr_edit_modal .serviceTagId").html($data.data.ticket.serviceTagId);
        			$("#bcr_edit_modal .equipmentTags").html($data.data.ticket.equipmentTags);
        			$("#bcr_edit_modal input[name='employee']").val($data.data.ticket.employee);
    	    		
        			$("#bcr_edit_modal").dialog("open");
        		}, 
        		
        		
        		
        		getTicketDetailFail : function($data) {
					console.log("getTicketDetailFail");        			
					$("#globalMsg").html("Invalid system state. Reload and try again").show();					
        		},
        		
        		
        		
        		initializeActualDLPanel : function($data) {
        			console.log("initializeActualDLPanel");
        			var $weekLabel = ["1st","2nd","3rd","4th","5th"];

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
						$row.append( $("<td>").append('<webthing:working styleClass="'+$workingClass+'"/><webthing:checkmark  styleClass="'+$doneClass+'">Success</webthing:checkmark>'));
						$("#actual_dl_totals tbody").append($row);
					});
        			
        		
        			$("#actual_dl_totals input").blur(function($event) {
        				var $that = $(this);
        				var $previousValue = $that.attr("data-previous");
        				var $value = $that.val();
        				if ( isNaN($value) || $value == '' || $value == null) {
        					$value = "0.00";
        				}
        				$value = parseFloat($value).toFixed(2);
        				$that.val( $value );
        				
        				// only do updates if the number has changed
        				if ( $value != $previousValue ) {
        					console.log("Value changed -- do a save: " + $value + " " + $previousValue);        					
            				var $claimWeek = $that.attr("data-week");
            				var $claimYear = $that.attr("data-year");            				
            				var $divisionId = $that.attr("data-divisionid");
            				var $claimWeeks = $that.attr("data-claimweeks");
        					var $workingSelector = "#actual_dl_totals .actual-working-week" + $claimWeek;
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
        			});
        		},
        		
        		
        		
        		initializeBudgetControlTotalsPanel : function($data) {
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
        			var $headerRow1 = $("<tr>");
					$headerRow1.append($("<td>").append("&nbsp;"));
					$headerRow1.append($("<td>").append("&nbsp;")); 
					var $headerRow2 = $("<tr>");
					$headerRow2.append($("<td>").addClass("column-header").append("Week:"));
					$headerRow2.append($("<td>").addClass("column-header").addClass("aligned-right").append("&nbsp;")); 
					var $footerRow = $("<tr>");
					$footerRow.append( $("<td>").append("Total Assigned D/L - All Employees"));
					$footerRow.append( $("<td>").append("&nbsp;"));  // spacer to account for unclaimed column
					
					$.each($data.data.workCalendar, function($index, $value) {
						var $startDate = $value.firstOfWeek.substring(0, 5);
						var $endDate = $value.lastOfWeek.substring(0, 5);
						var $dates = $("<td>").append($startDate + "-" + $endDate);
						$dates.addClass("aligned-right");
						$headerRow1.append($dates);

						var $week = $("<td>").addClass("column-header").append($value.weekOfYear);
						$week.addClass("aligned-right");
						$headerRow2.append($week);
						
						$footerRow.append( $("<td>").addClass("aligned-right").append("0.00") );
					});
					$headerRow1.append( $("<th>").append("Month") );
					$headerRow2.append( $("<th>").append("Total") );
					$("#bcr_employees .bcr_employees_display thead").append($headerRow1);
					$("#bcr_employees .bcr_employees_display thead").append($headerRow2);
					
					
					$footerRow.append( $("<td>").addClass("aligned-right").append("0.00") );
					$("#bcr_employees .bcr_employees_display tfoot").append($footerRow);

					
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
        		
        		
        		makeEditModal : function() {
        			$( "#bcr_edit_modal" ).dialog({
        				title:'Edit Claim',
        				autoOpen: false,
        				height: 550,
        				width: 550,
        				modal: true,
        				closeOnEscape:true,
        				//open: function(event, ui) {
        				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
        				//},
        				buttons: [
        					{
        						id:  "bcr_claim_edit_cancel",
        						click: function($event) {
        							$( "#bcr_edit_modal" ).dialog("close");    							
        						}
        					},{
        						id:  "bcr_claim_edit_save",
        						click: function($event) {
        							BUDGETCONTROL.ticketEditSave();        							
        						}
        					}
        				]
        			});	
        			$("#bcr_claim_edit_cancel").button('option', 'label', 'Cancel');
        			$("#bcr_claim_edit_save").button('option', 'label', 'Save');
        			
        			
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
        			
        			
        			
        			var $employeeSelector = '#bcr_edit_modal input[name="employee"]';
            		$( $employeeSelector ).autocomplete({
						'source':"bcr/employee?",
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
        							location.href="dashboard.html";        							
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
        				$.each($dataToTable, function($source, $destination) {
        					var $weekSelector = "#bcr_totals ." + $destination +"_week" + $weekNum;
        					$($weekSelector).html( $weekTotal[$source].toFixed(2) );     
        				});
        			});
    				$.each($dataToTable, function($source, $destination) {
        				var $monthSelector = "#bcr_totals ." + $destination + "_total";
        				$($monthSelector).html( $data.data.monthTotal[$source].toFixed(2) );
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
        			
        		},
        		
        		
        		
        		
        		populateEmployeePanel : function($data) {
        			console.log("populateEmployeePanel");
					
        			$.each($data.data.employees, function($index, $value) {
        				var $employeeRow = $("<tr>");
        				$employeeRow.append( $("<td>").append($value.employee) );
        				$employeeRow.append( $("<td>").append("&nbsp;") );  // spacer to account for unclaimed column in budget control total panel
        				$.each($data.data.claimWeeks, function($index, $claimWeek) {
        					if ( $claimWeek in $value.weeklyDL ) {
        						$employeeRow.append( $("<td>").addClass("aligned-right").append($value.weeklyDL[$claimWeek].toFixed(2)) );
        					} else {
        						$employeeRow.append( $("<td>").addClass("aligned-right").append("0.00") );
        					}
        				});
        				$employeeRow.append( $("<td>").addClass("aligned-right").append($value.totalDL.toFixed(2)) );
        				$("#bcr_employees tbody").append($employeeRow);
        			});
        			
        			var $footerRow = $("<tr>");
					$footerRow.append( $("<td>").append("Total Assigned D/L - All Employees"));
					$footerRow.append( $("<td>").append("&nbsp;"));  // spacer to account for unclaimed column
					$.each($data.data.claimWeeks, function($index, $claimWeek) {
						if ( $claimWeek in $data.data.monthlyTotal.weeklyDL ) {
							$footerRow.append( $("<td>").addClass("aligned-right").append($data.data.monthlyTotal.weeklyDL[$claimWeek].toFixed(2)) );
						} else {
							$footerRow.append( $("<td>").addClass("aligned-right").append("0.00") );
						}
					});
    				$footerRow.append( $("<td>").addClass("aligned-right").append($data.data.monthlyTotal.totalDL.toFixed(2)) );
    				$("#bcr_employees tfoot").html($footerRow);
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
        		},
        		
        		
        		
        		ticketEditSave : function() {
        			console.log("ticketEditSave");
        			var $outbound = ANSI_UTILS.form2outbound("#bcr_edit_modal",{});
        			console.log(JSON.stringify($outbound));
        			var $ticketId = $outbound['ticketId'];
        			var $url = "bcr/ticket/" + $ticketId;
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
        			
					$weekList = [];        			
        			$.each($data.data.workCalendar, function($index, $value) {
        				$weekList.push($value.weekOfYear);
        			});
        			
        			var $workWeeks = $weekList.join(",");
        			BUDGETCONTROL.initializeActualDLPanel($data);        			
        			BUDGETCONTROL.initializeBudgetControlTotalsPanel($data);
        			BUDGETCONTROL.initializeEmployeePanel($data);
        			BUDGETCONTROL.populateTicketTables($data);
        			BUDGETCONTROL.populateTitlePanel($data);
        			BUDGETCONTROL.divisionId = $data.data.divisionId;
        			BUDGETCONTROL.workYear = $data.data.workYear; 
        			BUDGETCONTROL.workWeek = $workWeeks;
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
	    
	    
	    
	    <div id="bcr_edit_modal">
	    	<form id="bcr_edit_form">
	    		<input type="hidden" name="ticketId" />
		    	<table>
		    		<tr>
		    			<td><span class="form-label">Account:</span></td>
		    			<td><span class="jobSiteName"></span></td>
		    			<td><span class="err jobSiteNameErr"></span></td>
		    		</tr>
		    		<tr>
		    			<td><span class="form-label">Ticket Number:</span></td>
		    			<td><span class="ticketId"></span></td>
		    			<td><span class="err ticketIdErr"></span></td>	    			
		    		</tr>
		    		<tr>
		    			<td><span class="form-label">Claim Week:</span></td>
		    			<td><select name="claimWeek"></select></td>
		    			<td><span class="err claimWeekErr"></span></td>	    			
		    		</tr>
		    		<tr>
		    			<td><span class="form-label">D/L:</span></td>
		    			<td><input type="text" name="dlAmt"></input></td>
		    			<td><span class="err dlAmtErr"></span></td>	    			
		    		</tr>
		    		<tr>
		    			<td><span class="form-label">Total Volume:</span></td>
		    			<td><input type="text" name="totalVolume" id="totalVolumeField"></input></td>
		    			<td><span class="err totalVolumeErr"></span></td>	    			
		    		</tr>
		    		<tr>
		    			<td><span class="form-label">Volume Claimed:</span></td>
		    			<td><input type="text" name="volumeClaimed" id="volumeClaimedField"></input></td>
		    			<td><span class="err volumeClaimedErr"></span></td>	    			
		    		</tr>
		    		<tr>
		    			<td><span class="form-label">Volume Remaining:</span></td>
		    			<td><span class="volumeRemaining"></span></td>
		    			<td><span class="err volumeRemainingErr"></span></td>	    			
		    		</tr>
		    		<tr>
		    			<td><span class="form-label">Notes:</span></td>
		    			<td><input type="text" name="notes"></input></td>
		    			<td><span class="err workDateErr"></span></td>	    			
		    		</tr>
		    		<tr>
		    			<td><span class="form-label">Billed Amount:</span></td>
		    			<td><input type="text" name="billedAmount" id="billedAmountField"></input></td>
		    			<td><span class="err billedAmountErr"></span></td>	    			
		    		</tr>
		    		<tr>
		    			<td><span class="form-label">Diff Clm/Bld:</span></td>
		    			<td><span class="claimedVsBilled"></span></td>
		    			<td><span class="err claimedVsBilledErr"></span></td>	    			
		    		</tr>
		    		<tr>
		    			<td><span class="form-label">Ticket Status:</span></td>
		    			<td><span class="ticketStatus"></span></td>
		    			<td><span class="err ticketStatusErr"></span></td>	    			
		    		</tr>
		    		<tr>
		    			<td><span class="form-label">Service:</span></td>
		    			<td><span class="serviceTagId"></span></td>
		    			<td><span class="err serviceTagIdErr"></span></td>	    			
		    		</tr>
		    		<tr>
		    			<td><span class="form-label">Equipment:</span></td>
		    			<td><span class="equipmentTags"></span></td>
		    			<td><span class="err equipmentTagsErr"></span></td>	    			
		    		</tr>
		    		<tr>
		    			<td><span class="form-label">Employee:</span></td>
		    			<td><input type="text" name="employee"></input></td>
		    			<td><span class="err employeeErr"></span></td>	    			
		    		</tr>
		    		
		    	</table>
	    	</form>
	    </div>
    </tiles:put>
		
</tiles:insert>

